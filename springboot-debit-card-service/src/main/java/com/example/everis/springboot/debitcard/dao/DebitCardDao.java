package com.example.everis.springboot.debitcard.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.example.everis.springboot.debitcard.document.DebitCardDocument;

public interface DebitCardDao extends ReactiveMongoRepository<DebitCardDocument, String> {

}
