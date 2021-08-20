package com.everis.banca.app.savingAccount.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.banca.app.savingAccount.models.documents.SavingAccount;

public interface SavingAccountDao extends ReactiveMongoRepository<SavingAccount, String> {

}
