package com.everis.springboot.yankiwallet.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.springboot.yankiwallet.document.YankiWalletDocument;

public interface YankiWalletDao extends ReactiveMongoRepository<YankiWalletDocument, String> {

}
