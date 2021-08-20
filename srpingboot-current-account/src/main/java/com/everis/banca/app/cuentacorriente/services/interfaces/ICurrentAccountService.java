package com.everis.banca.app.cuentacorriente.services.interfaces;



import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.everis.banca.app.cuentacorriente.models.documents.CurrentAccount;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICurrentAccountService {

	Flux<CurrentAccount> getAllCurrentAccount();
	
	Mono<CurrentAccount> createCurrentAccount(CurrentAccount currentAccount);
	
	Mono<CurrentAccount> findById(String  idCurrentAccount);

	Flux<CurrentAccount> findByClientId(String clientId);

	Mono<CurrentAccount> save(CurrentAccount currentAccount);
	
	Mono<Void> deleteById(String  idCurrentAccount);
	
	Mono<ResponseEntity<Map<String,Object>>> depositar(String idCuenta,Double cantidad);
	
	Mono<ResponseEntity<Map<String,Object>>> retirar(String idCuenta,Double cantidad);
	
	Mono<ResponseEntity<Map<String,Object>>> consultarSaldo(String idCliente);

	 


	
}
