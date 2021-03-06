package com.example.everis.springboot.debitcard.service.Impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.everis.springboot.debitcard.dao.DebitCardDao;
import com.example.everis.springboot.debitcard.document.AccountDocument;
import com.example.everis.springboot.debitcard.document.CreatedAccountDocument;
import com.example.everis.springboot.debitcard.document.DebitCardDocument;
import com.example.everis.springboot.debitcard.service.DebitCardService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DebitCardServiceImpl implements DebitCardService {
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@Autowired
	private DebitCardDao debitCardDao;
	
	@Value("${everis.url.gateway}")
	private String urlGateway;

	@Override
	public Mono<DebitCardDocument> createDebitCard(String idClient) {
		Random rand = new Random();
	    Integer counter = 0;
	    String cardNumber = "";
	    while(counter<=3){
	    	Integer generate = rand.nextInt(10000); 
	    	cardNumber += generate.toString();
	    	if(counter != 3) {
	    		cardNumber += "-";
	    	}
	    	counter++;
	    }
	    
	    DebitCardDocument debitCard = DebitCardDocument.builder()
				.cardNumber(cardNumber)
				.createdAt(new Date())
				.clientId(idClient)
				.asociatedAccounts(new ArrayList<>())
				.build();
	    
		return debitCardDao.save(debitCard);
	}

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> associateAccounttoDebitCard(String idDebitCard, AccountDocument account) {
		
		Map<String, Object> response = new HashMap<>();
		
		Map<String, Object> responseIfEmptyAccount = new HashMap<>();
		responseIfEmptyAccount.put("mensaje", "La cuenta ingresada es incorrecta ya que no existe un registro");
		
		Map<String, Object> responseIfEmptyCredit = new HashMap<>();
		responseIfEmptyCredit.put("mensaje", "La tarjeta de debito ingresada es incorrecta ya que no existe un registro");
		
		if(!account.getTypeAccount().equals("Cuenta de Ahorro")  && !account.getTypeAccount().equals("Cuenta Corriente") && !account.getTypeAccount().equals("Cuenta Plazo Fijo")) {
			response.put("mensaje", "El tipo de cuenta a asociar es incorrecto");
			return Mono.just(new ResponseEntity<>(response,HttpStatus.BAD_GATEWAY));
		}
		
		String typeUrl = account.getTypeAccount().equals("Cuenta Corriente") 
				? "currentAccount" 
				: account.getTypeAccount().equals("Cuenta de Ahorro") 
				? "accountSavings" 
				: account.getTypeAccount().equals("Cuenta Plazo Fijo")
				? "fixed-term"
				: "";
		
		return webClientBuilder.build().get()
				.uri(urlGateway+"/api/"+typeUrl+"/getAccount/"+account.getIdAccount())
				.retrieve().bodyToMono(CreatedAccountDocument.class).flatMap(acc -> {
					
					log.info(acc.toString());
					System.out.println(acc.toString());
					return debitCardDao.findById(idDebitCard).flatMap( dc -> {
						log.info(dc.getClientId());
						log.info(acc.getClientId());
						if(!dc.getClientId().equals(acc.getClientId())) {
							response.put("mensaje", "No puede asignar una tarjeta de debito de un cliente a la cuenta de otro");
							return Mono.just(new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY));
						}else {
							Integer sizeAccFil = dc.getAsociatedAccounts().stream()
								.filter( a -> a.getIdAccount().equals(account.getIdAccount()))
								.collect(Collectors.toList()).size();
							
							Integer sizeAcc = dc.getAsociatedAccounts().size();
							
							System.out.println(sizeAccFil);
							
							if(sizeAccFil > 0) {
								response.put("mensaje", "Esta cuenta ya ha sido asignada a esta tarjeta de debito");
								return Mono.just(new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY));
							}else {
								account.setTimeAdded(new Date());
								
								if(sizeAcc == 0) {
									account.setPrincipal(true);
								}else {
									account.setPrincipal(false);
								}
								
								dc.getAsociatedAccounts().add(account);
								
								return debitCardDao.save(dc).flatMap( dcs -> {
									response.put("mensaje", "Se registro la cuenta con exito en la tarjeta de debito");
									response.put("debit-card", dcs);
									return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
								});	
							}
						}
					}).defaultIfEmpty(new ResponseEntity<>(responseIfEmptyCredit, HttpStatus.NOT_FOUND));
				}).defaultIfEmpty(new ResponseEntity<>(responseIfEmptyAccount, HttpStatus.NOT_FOUND));
		
	}

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> payWithAccount(String idDebitCard, Double amount) {
		Map<String, Object> response = new HashMap<>();
		
		return debitCardDao.findById(idDebitCard).flatMap( dc -> {
			
			Integer numberAccounts = dc.getAsociatedAccounts().size();
			
			if(numberAccounts == 0) {
				response.put("mensaje", "No existen cuentas para esta tarjeta de debito");
				return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
			}else {
				
				AccountDocument account = dc.getAsociatedAccounts().stream().filter( acc -> acc.getPrincipal() == true ).collect(Collectors.toList()).get(0);
				
				String typeUrl = account.getTypeAccount().equals("Cuenta Corriente") 
						? "currentAccount" 
						: account.getTypeAccount().equals("Cuenta de Ahorro") 
						? "accountSavings" 
						: account.getTypeAccount().equals("Cuenta Plazo Fijo")
						? "fixed-term"
						: "";
									
				return webClientBuilder.build().get()
						.uri(urlGateway+"/api/"+typeUrl+"/payWithDebit/"+account.getIdAccount()+"/"+amount)
						.retrieve().bodyToMono(Boolean.class).flatMap( b -> {
							if(b) {
								response.put("mensaje", "Se hizo el pago con la cuenta exitosamente");
								return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
							}else {
								List<AccountDocument> secondaryAccounts = dc.getAsociatedAccounts()
										.stream()
										.filter( acc -> acc.getPrincipal() == false )
										.collect(Collectors.toList());
																
								Collections.sort(secondaryAccounts, new Comparator<AccountDocument>() {
									  public int compare(AccountDocument o1, AccountDocument o2) {
									      return o1.getTimeAdded().compareTo(o2.getTimeAdded());
									  }
									});
								
								log.info(secondaryAccounts.toString());
								
								return getResponsePay(secondaryAccounts,0,amount);
								
							}
						});
				
			}
		});
				
		
	}
	
	
	public Mono<ResponseEntity<Map<String, Object>>> getResponsePay(List<AccountDocument> secondaryAccounts,Integer position, Double amount){
		Map<String, Object> response = new HashMap<>();
				
		if(secondaryAccounts.size() < position + 1) {
			response.put("mensaje", "No existen cuentas disponibles para hacer el pago");
			return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
		}else {
			
			log.info("Entro aqui para el segundo bucle");
			String typeUrl = secondaryAccounts.get(position).getTypeAccount().equals("Cuenta Corriente") 
					? "currentAccount" 
					: secondaryAccounts.get(position).getTypeAccount().equals("Cuenta de Ahorro") 
					? "accountSavings" 
					: secondaryAccounts.get(position).getTypeAccount().equals("Cuenta Plazo Fijo")
					? "fixed-term"
					: "";
			
			
			return webClientBuilder.build().get()
					.uri(urlGateway+"/api/"+typeUrl+"/payWithDebit/"+secondaryAccounts.get(position).getIdAccount()+"/"+amount)
					.retrieve().bodyToMono(Boolean.class).flatMap( isOk -> {
						if(isOk) {
							response.put("mensaje", "Se hizo el pago con la cuenta: "+secondaryAccounts.get(position).getTypeAccount() + " exitosamente");
							return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
						}else {
							Integer pos = position + 1;
							return getResponsePay(secondaryAccounts,pos,amount);
						}
					});
		}
		
		
	}

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> consultBalanceDebitCard(String idDebitCard) {
		Map<String, Object> response = new HashMap<>();
		
		return debitCardDao.findById(idDebitCard).flatMap( debit -> {
			
			AccountDocument account = debit.getAsociatedAccounts()
												.stream()
												.filter(a -> a.getPrincipal() == true)
												.collect(Collectors.toList())
												.get(0);
			
			String typeUrl = account.getTypeAccount().equals("Cuenta Corriente") 
					? "currentAccount" 
					: account.getTypeAccount().equals("Cuenta de Ahorro") 
					? "accountSavings" 
					: account.getTypeAccount().equals("Cuenta Plazo Fijo")
					? "fixed-term"
					: "";
			
			return webClientBuilder.build().get()
			.uri(urlGateway+"/api/"+typeUrl+"/getBalance/"+account.getIdAccount())
			.retrieve().bodyToMono(String.class).flatMap( msg -> {
				response.put("mensaje", msg);
				return Mono.just(new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK));
			});
			
		});
	}
	
	
	
	
	
	
	
}
