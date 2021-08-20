package com.everis.springboot.createaccount.service.imp;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.everis.springboot.createaccount.dao.CreateAccountDao;
import com.everis.springboot.createaccount.document.ClientDocument;
import com.everis.springboot.createaccount.document.CreateAccountDocument;
import com.everis.springboot.createaccount.document.CreditDocument;
import com.everis.springboot.createaccount.document.CurrentAccount;
import com.everis.springboot.createaccount.document.FixedTermDocument;
import com.everis.springboot.createaccount.document.SavingAccount;
import com.everis.springboot.createaccount.service.CreateAccountService;
import reactor.core.publisher.Mono;

@Service
public class CreateAccountServiceImpl implements CreateAccountService {
	
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@Autowired
	private CreateAccountDao createAccountDao;
	
	@Value("${everis.precio.mantenimiento}")
	private double costOfMaintenment;
	
	@Value("${everis.cantidad.movimientos}")
	private int movementsPerMonth;
	
	@Value("${everis.dia-retiro.plazo-fijo}")
	private Integer diaRetiro;
	
	@Value("${everis.url.gateway}")
	private String urlGateway;

	@Override
	public Mono<CreateAccountDocument> findAccountsById(String id) {
		return createAccountDao.findById(id);
	}

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> saveAccount(String id, CreateAccountDocument account) {
		Map<String, Object> response = new HashMap<>();
		Date date = Calendar.getInstance(TimeZone.getTimeZone("America/Lima")).getTime();
		
		Mono<ClientDocument> client = webClientBuilder.build().get()
				.uri(urlGateway+"/api/client/client/"+id)
				.retrieve()
				.bodyToMono(ClientDocument.class);
		
		
		return createAccountDao.findByClient(id).collectList().flatMap( accounts -> {
			
			Mono<ResponseEntity<Map<String,Object>>> res = client.flatMap(c -> {
				Integer cAhorro = 0;
				Integer cCorriente = 0;
				Integer cPlazoFijo = 0;
				
				System.out.println(c.toString());
				
				if(!account.getAccount_type().equals("Cuenta de Ahorro")  && !account.getAccount_type().equals("Cuenta Corriente") && !account.getAccount_type().equals("Cuenta Plazo Fijo")) {
					response.put("mensaje", "No se puede crear el tipo de cuenta, los tipos de cuentas solo pueden ser: Cuenta de Ahorro, Cuenta Corriente y Cuenta Plazo Fijo");
					return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
				}
				
				if(c.getClient_type().getDescription().equals("Personal") || c.getClient_type().getDescription().equals("VIP")) {
					for (CreateAccountDocument acc : accounts) {
						if(acc.getAccount_type().equals("Cuenta de Ahorro")) {
							cAhorro++;
						}
						if(acc.getAccount_type().equals("Cuenta Corriente")) {
							cCorriente++;
						}
						if(acc.getAccount_type().equals("Cuenta Plazo Fijo")) {
							cPlazoFijo++;
						}
						if(account.getAccount_type().equals("Cuenta de Ahorro") && cAhorro>0) {
							response.put("mensaje", "No puede crear la cuenta, un cliente no puede tener más de una cuenta de ahorro");
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
						if(account.getAccount_type().equals("Cuenta Corriente") && cCorriente>0) {
							response.put("mensaje", "No puede crear la cuenta, un cliente no puede tener más de una cuenta corriente");
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
						if(account.getAccount_type().equals("Cuenta Plazo Fijo") && cPlazoFijo>0) {
							response.put("mensaje", "No puede crear la cuenta, un cliente no puede tener más de una cuenta a plazo fijo");
							return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
						}
					}
					
				}else if(c.getClient_type().getDescription().equals("Empresarial") || c.getClient_type().getDescription().equals("PYME")) {
					if(account.getAccount_type().equals("Cuenta de Ahorro")) {
						response.put("mensaje", "Un usuario empresarial no puede tener cuenta de ahorro");
						return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
					}
					if(account.getAccount_type().equals("Cuenta Plazo Fijo")) {
						response.put("mensaje", "Un usuario empresarial no puede tener cuenta a plazo fijo");
						return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
					}
				}else if(!c.getClient_type().getDescription().equals("Empresarial") && !c.getClient_type().getDescription().equals("Personal") &&
						!c.getClient_type().getDescription().equals("VIP") && !c.getClient_type().getDescription().equals("PYME")) {
					response.put("mensaje", "Ingreso un tipo de cliente incorrecto");
					return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST));
				}
				
				account.setClient(id);
				
				if(account.getAccount_type().equals("Cuenta Plazo Fijo")) {
					FixedTermDocument fixedTerm = FixedTermDocument.builder()
							.saldo(account.getMount())
							.fechaCreacion(date)
							.idCliente(id)
							.diaRetiro(diaRetiro)
							.build();
					
					
					webClientBuilder.build().post()
					.uri(urlGateway+"/api/fixed-term/saveAccount")
					.body(Mono.just(fixedTerm), FixedTermDocument.class)
					.retrieve().bodyToMono(FixedTermDocument.class).subscribe();
				}
				
				if(account.getAccount_type().equals("Cuenta Corriente")) {
					
					if(c.getClient_type().getDescription().equals("PYME")) {
						System.out.println("El cliente PYME quiere abrir una cuenta corriente");
						Map<String, Object> responseEmpty = new HashMap<>();
						responseEmpty.put("mensaje", "No existen tarjetas de credito para este usuario");
						return webClientBuilder.build().get()
								.uri(urlGateway+"/api/credit/getCreditCards/"+id)
								.retrieve().bodyToMono(CreditDocument.class).flatMap( cre ->{
										CurrentAccount currentAccount = CurrentAccount.builder()
												.amountInAccount(account.getMount())
												.costOfMaintenance(costOfMaintenment)
												.createAt(date)
												.clientId(id)
												.build();
										
									
										webClientBuilder.build().post()
										.uri(urlGateway+"/api/currentAccount")
										.body(Mono.just(currentAccount), CurrentAccount.class)
										.retrieve().bodyToMono(CurrentAccount.class).subscribe();
										
										return createAccountDao.save(account).flatMap( p -> {
											response.put("productSaved", p);
											response.put("mensaje", "Cuenta registrada con exito");
											return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK)); 
										});
									
								}).defaultIfEmpty(new ResponseEntity<>(responseEmpty, HttpStatus.BAD_GATEWAY));
					}else {
						CurrentAccount currentAccount = CurrentAccount.builder()
								.amountInAccount(account.getMount())
								.costOfMaintenance(costOfMaintenment)
								.createAt(date)
								.clientId(id)
								.build();
						
					
						webClientBuilder.build().post()
						.uri(urlGateway+"/api/currentAccount")
						.body(Mono.just(currentAccount), CurrentAccount.class)
						.retrieve().bodyToMono(CurrentAccount.class).subscribe();
					}
					
				}
				
				if(account.getAccount_type().equals("Cuenta de Ahorro")) {
										
					if(c.getClient_type().getDescription().equals("VIP")) {
						System.out.println("El cliente vip quiere abrir una cuenta de ahorro");
						Map<String, Object> responseEmpty = new HashMap<>();
						responseEmpty.put("mensaje", "No existen tarjetas de credito para este usuario");
						return webClientBuilder.build().get()
								.uri(urlGateway+"/api/credit/getCreditCards/"+id)
								.retrieve().bodyToMono(CreditDocument.class).flatMap( cre ->{
										SavingAccount savingAccount = SavingAccount.builder()
												.amountInAccount(account.getMount())
												.createAt(date)
												.clientId(id)
												.movementsPerMonth(movementsPerMonth)
												.build();
										
										webClientBuilder.build().post()
										.uri(urlGateway+"/api/accountSavings")
										.body(Mono.just(savingAccount), SavingAccount.class)
										.retrieve().bodyToMono(SavingAccount.class).subscribe();
										
										return createAccountDao.save(account).flatMap( p -> {
											response.put("productSaved", p);
											response.put("mensaje", "Cuenta registrada con exito");
											return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK)); 
										});
									
								}).defaultIfEmpty(new ResponseEntity<>(responseEmpty, HttpStatus.BAD_GATEWAY));
					}else {
						SavingAccount savingAccount = SavingAccount.builder()
								.amountInAccount(account.getMount())
								.createAt(date)
								.clientId(id)
								.movementsPerMonth(movementsPerMonth)
								.build();
						
						
						webClientBuilder.build().post()
						.uri(urlGateway+"/api/accountSavings")
						.body(Mono.just(savingAccount), SavingAccount.class)
						.retrieve().bodyToMono(SavingAccount.class).subscribe();
					}
		
				}

				
				return createAccountDao.save(account).flatMap( p -> {
					response.put("productSaved", p);
					response.put("mensaje", "Cuenta registrada con exito");
					return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK)); 
				});
				
			});
			
			
			return res;
		});
	}

}
