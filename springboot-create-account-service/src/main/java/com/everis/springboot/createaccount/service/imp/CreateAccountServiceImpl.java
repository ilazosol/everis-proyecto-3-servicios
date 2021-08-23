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
import com.everis.springboot.createaccount.exception.ValidatorAccountException;
import com.everis.springboot.createaccount.service.CreateAccountService;
import com.everis.springboot.createaccount.util.Validations;

import reactor.core.publisher.Flux;
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
	
	@Autowired
	private Validations validations;

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
		
		
		return createAccountDao.findByIdClient(id).collectList().flatMap( accounts -> {
			
			Mono<ResponseEntity<Map<String,Object>>> res = client.flatMap(c -> {
				
				try {
					
					validations.validateAccount(accounts, account, c);
					
					account.setIdClient(id);
					account.setCreationDate(date);
					
					if(account.getAccountType().equals("Cuenta Plazo Fijo")) {
						FixedTermDocument fixedTerm = FixedTermDocument.builder()
								.amountInAccount(account.getInitialMount())
								.createAt(date)
								.clientId(id)
								.diaRetiro(diaRetiro)
								.build();
						
						
						return webClientBuilder.build().post()
						.uri(urlGateway+"/api/fixed-term/saveAccount")
						.body(Mono.just(fixedTerm), FixedTermDocument.class)
						.retrieve().bodyToMono(FixedTermDocument.class).flatMap( ft -> {
							account.setIdSavedAccount(ft.getId());
							return createAccountDao.save(account).flatMap( p -> {
								response.put("productSaved", p);
								response.put("mensaje", "Cuenta registrada con exito");
								return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK)); 
							});
						});
					}
					
					if(account.getAccountType().equals("Cuenta Corriente")) {
						
						if(c.getClient_type().getDescription().equals("PYME")) {
							System.out.println("El cliente PYME quiere abrir una cuenta corriente");
							Map<String, Object> responseEmpty = new HashMap<>();
							responseEmpty.put("mensaje", "No existen tarjetas de credito para este usuario");
							return webClientBuilder.build().get()
									.uri(urlGateway+"/api/credit/getCreditCards/"+id)
									.retrieve().bodyToMono(CreditDocument.class).flatMap( cre ->{
											CurrentAccount currentAccount = CurrentAccount.builder()
													.amountInAccount(account.getInitialMount())
													.costOfMaintenance(costOfMaintenment)
													.createAt(date)
													.clientId(id)
													.build();
											
										
											return webClientBuilder.build().post()
											.uri(urlGateway+"/api/currentAccount")
											.body(Mono.just(currentAccount), CurrentAccount.class)
											.retrieve().bodyToMono(CurrentAccount.class).flatMap( cc -> {
												account.setIdSavedAccount(cc.getId());
												return createAccountDao.save(account).flatMap( p -> {
													response.put("productSaved", p);
													response.put("mensaje", "Cuenta registrada con exito");
													return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK)); 
												});
											});
									}).defaultIfEmpty(new ResponseEntity<>(responseEmpty, HttpStatus.BAD_GATEWAY));
						}else {
							CurrentAccount currentAccount = CurrentAccount.builder()
									.amountInAccount(account.getInitialMount())
									.costOfMaintenance(costOfMaintenment)
									.createAt(date)
									.clientId(id)
									.build();
							
						
							return webClientBuilder.build().post()
							.uri(urlGateway+"/api/currentAccount")
							.body(Mono.just(currentAccount), CurrentAccount.class)
							.retrieve().bodyToMono(CurrentAccount.class).flatMap( cc -> {
								account.setIdSavedAccount(cc.getId());
								return createAccountDao.save(account).flatMap( p -> {
									response.put("productSaved", p);
									response.put("mensaje", "Cuenta registrada con exito");
									return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK)); 
								});
							});
						}
						
					}
					
					if(account.getAccountType().equals("Cuenta de Ahorro")) {
											
						if(c.getClient_type().getDescription().equals("VIP")) {
							System.out.println("El cliente vip quiere abrir una cuenta de ahorro");
							Map<String, Object> responseEmpty = new HashMap<>();
							responseEmpty.put("mensaje", "No existen tarjetas de credito para este usuario");
							return webClientBuilder.build().get()
									.uri(urlGateway+"/api/credit/getCreditCards/"+id)
									.retrieve().bodyToMono(CreditDocument.class).flatMap( cre ->{
											SavingAccount savingAccount = SavingAccount.builder()
													.amountInAccount(account.getInitialMount())
													.createAt(date)
													.clientId(id)
													.movementsPerMonth(movementsPerMonth)
													.build();
											
											return webClientBuilder.build().post()
											.uri(urlGateway+"/api/accountSavings")
											.body(Mono.just(savingAccount), SavingAccount.class)
											.retrieve().bodyToMono(SavingAccount.class).flatMap( sa -> {
												account.setIdSavedAccount(sa.getId());
												return createAccountDao.save(account).flatMap( p -> {
													response.put("productSaved", p);
													response.put("mensaje", "Cuenta registrada con exito");
													return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK)); 
												});
											});
											
									}).defaultIfEmpty(new ResponseEntity<>(responseEmpty, HttpStatus.BAD_GATEWAY));
						}else {
							SavingAccount savingAccount = SavingAccount.builder()
									.amountInAccount(account.getInitialMount())
									.createAt(date)
									.clientId(id)
									.movementsPerMonth(movementsPerMonth)
									.build();
							
							
							return webClientBuilder.build().post()
							.uri(urlGateway+"/api/accountSavings")
							.body(Mono.just(savingAccount), SavingAccount.class)
							.retrieve().bodyToMono(SavingAccount.class).flatMap( sa -> {
								account.setIdSavedAccount(sa.getId());
								return createAccountDao.save(account).flatMap( p -> {
									response.put("productSaved", p);
									response.put("mensaje", "Cuenta registrada con exito");
									return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK)); 
								});
							});
						}
			
					}
					
					response.put("mensaje", "Tipo de cuenta ingresado incorrecta");
					return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK));
				
				} catch (ValidatorAccountException e) {
					response.put("mensaje", e.getMensajeValidacion());
					return Mono.just(new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK));
				}
			});
			return res;
		});
	}

	@Override
	public Flux<CreateAccountDocument> findAccountsByClient(String idClient) {
		return createAccountDao.findByIdClient(idClient);
	}

}
