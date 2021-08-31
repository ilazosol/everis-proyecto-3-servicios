package com.everis.springboot.createaccount.service.imp;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
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
	public void saveAccount(CreateAccountDocument account, ClientDocument client) {
		System.out.println("Entro al metodo de guardar cuenta nueva");
		Date date = Calendar.getInstance(TimeZone.getTimeZone("America/Lima")).getTime();
		createAccountDao.findByIdClient(account.getIdClient()).collectList().doOnSuccess( accounts -> {
				try {
					validations.validateAccount(accounts, account, client);
					
					account.setIdClient(account.getIdClient());
					account.setCreationDate(date);
					
					if(account.getAccountType().equals("Cuenta Plazo Fijo")) {
						System.out.println("Entro a crear una cuenta a plazo fijo");
						FixedTermDocument fixedTerm = FixedTermDocument.builder()
								.amountInAccount(account.getInitialMount())
								.createAt(date)
								.clientId(account.getIdClient())
								.diaRetiro(diaRetiro)
								.build();
						
						
						webClientBuilder.build().post()
						.uri(urlGateway+"/api/fixed-term/saveAccount")
						.body(Mono.just(fixedTerm), FixedTermDocument.class)
						.retrieve().bodyToMono(FixedTermDocument.class).flatMap( ft -> {
							account.setIdSavedAccount(ft.getId());
							return createAccountDao.save(account);
						}).subscribe();
						
					}
					
					if(account.getAccountType().equals("Cuenta Corriente")) {
						
						if(client.getClientType().equals("PYME")) {
							System.out.println("El cliente PYME quiere abrir una cuenta corriente");
							webClientBuilder.build().get()
									.uri(urlGateway+"/api/credit/getCreditCards/"+account.getIdClient())
									.retrieve()
									.bodyToMono(CreditDocument.class)
									.flatMap( cre ->{
											CurrentAccount currentAccount = CurrentAccount.builder()
													.amountInAccount(account.getInitialMount())
													.costOfMaintenance(costOfMaintenment)
													.createAt(date)
													.clientId(account.getIdClient())
													.build();
											
										
											return webClientBuilder.build().post()
											.uri(urlGateway+"/api/currentAccount")
											.body(Mono.just(currentAccount), CurrentAccount.class)
											.retrieve().bodyToMono(CurrentAccount.class).flatMap( cc -> {
												account.setIdSavedAccount(cc.getId());
												return createAccountDao.save(account);
											});
									}).subscribe();
						}else {
							CurrentAccount currentAccount = CurrentAccount.builder()
									.amountInAccount(account.getInitialMount())
									.costOfMaintenance(costOfMaintenment)
									.createAt(date)
									.clientId(account.getIdClient())
									.build();
							
						
							webClientBuilder.build().post()
							.uri(urlGateway+"/api/currentAccount")
							.body(Mono.just(currentAccount), CurrentAccount.class)
							.retrieve().bodyToMono(CurrentAccount.class).flatMap( cc -> {
								account.setIdSavedAccount(cc.getId());
								return createAccountDao.save(account);
							}).subscribe();
						}
						
					}
					
					if(account.getAccountType().equals("Cuenta de Ahorro")) {
											
						if(client.getClientType().equals("VIP")) {
							System.out.println("El cliente vip quiere abrir una cuenta de ahorro");
							webClientBuilder.build().get()
									.uri(urlGateway+"/api/credit/getCreditCards/"+account.getIdClient())
									.retrieve().bodyToMono(CreditDocument.class).flatMap( cre ->{
											SavingAccount savingAccount = SavingAccount.builder()
													.amountInAccount(account.getInitialMount())
													.createAt(date)
													.clientId(account.getIdClient())
													.movementsPerMonth(movementsPerMonth)
													.build();
											
											return webClientBuilder.build().post()
											.uri(urlGateway+"/api/accountSavings")
											.body(Mono.just(savingAccount), SavingAccount.class)
											.retrieve().bodyToMono(SavingAccount.class).flatMap( sa -> {
												account.setIdSavedAccount(sa.getId());
												return createAccountDao.save(account);
											});
											
									}).subscribe();
						}else {
							SavingAccount savingAccount = SavingAccount.builder()
									.amountInAccount(account.getInitialMount())
									.createAt(date)
									.clientId(account.getIdClient())
									.movementsPerMonth(movementsPerMonth)
									.build();
							
							
							webClientBuilder.build().post()
							.uri(urlGateway+"/api/accountSavings")
							.body(Mono.just(savingAccount), SavingAccount.class)
							.retrieve().bodyToMono(SavingAccount.class).flatMap( sa -> {
								account.setIdSavedAccount(sa.getId());
								return createAccountDao.save(account);
							}).subscribe();
						}
			
					}
								
				} catch (ValidatorAccountException e) {
					log.info(e.getMensajeValidacion());
				}
			}).subscribe();
	}

	@Override
	public Flux<CreateAccountDocument> findAccountsByClient(String idClient) {
		return createAccountDao.findByIdClient(idClient);
	}

}
