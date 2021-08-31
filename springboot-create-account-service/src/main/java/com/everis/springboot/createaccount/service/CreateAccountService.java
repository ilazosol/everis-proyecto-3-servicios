package com.everis.springboot.createaccount.service;

import com.everis.springboot.createaccount.document.ClientDocument;
import com.everis.springboot.createaccount.document.CreateAccountDocument;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreateAccountService {
	
	void saveAccount(CreateAccountDocument account, ClientDocument client);

	Mono<CreateAccountDocument> findAccountsById(String id);

	Flux<CreateAccountDocument> findAccountsByClient(String idClient);

}
