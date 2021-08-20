package com.everis.banca.app.cuentacorriente.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


import com.everis.banca.app.cuentacorriente.models.documents.CurrentAccount;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CurrentAccountDao extends ReactiveMongoRepository<CurrentAccount, String> {
	
	Flux<CurrentAccount> findByClientId (String clientId);
}
