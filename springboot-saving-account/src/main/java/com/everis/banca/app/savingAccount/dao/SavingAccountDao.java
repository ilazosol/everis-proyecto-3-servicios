package com.everis.banca.app.savingAccount.dao;

import java.util.Date;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.banca.app.savingAccount.models.documents.SavingAccount;

import reactor.core.publisher.Flux;

public interface SavingAccountDao extends ReactiveMongoRepository<SavingAccount, String> {

	@Query(value = "{'createAt':{ $gte: ?0, $lte: ?1}}"   )
	Flux<SavingAccount> findProductsByDate(Date dateInicio, Date dateFin);

}
