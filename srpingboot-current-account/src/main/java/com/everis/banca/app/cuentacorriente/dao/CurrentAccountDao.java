package com.everis.banca.app.cuentacorriente.dao;

import java.util.Date;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


import com.everis.banca.app.cuentacorriente.models.documents.CurrentAccount;

import reactor.core.publisher.Flux;

public interface CurrentAccountDao extends ReactiveMongoRepository<CurrentAccount, String> {
	
	Flux<CurrentAccount> findByClientId (String clientId);

	@Query(value = "{'createAt':{ $gte: ?0, $lte: ?1}}"   )
	Flux<CurrentAccount> findProductsByDate(Date dateInicio, Date dateFin);
}
