package com.everis.banca.app.savingAccount.services.interfaces;


import java.text.ParseException;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.everis.banca.app.savingAccount.models.documents.SavingAccount;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SavingAccountService {

	Flux<SavingAccount> getAllSavingAccount();
	
	Mono<SavingAccount> save(SavingAccount creditCard);
	

	Mono<SavingAccount> findById(String idSavingAccount);

	Mono<Void> deleteById(String idSavingAccount);

	Mono<ResponseEntity<Map<String, Object>>> consultarSaldo(String idAccount);

	Mono<ResponseEntity<Map<String, Object>>> retirar(String idCuenta, Double cantidad);

	Mono<ResponseEntity<Map<String, Object>>> depositar(String idCuenta, Double cantidad);

	Flux<SavingAccount> getProductByDates(String fechaInicio, String fechaFin) throws ParseException;

	Mono<SavingAccount> getSavingAccount(String idAccount);

}
