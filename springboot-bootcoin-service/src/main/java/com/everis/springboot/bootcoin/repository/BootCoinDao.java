package com.everis.springboot.bootcoin.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.springboot.bootcoin.documents.BootCoinDocument;

import reactor.core.publisher.Mono;

public interface BootCoinDao extends ReactiveMongoRepository<BootCoinDocument, String> {

	public Mono<BootCoinDocument> findByIdClient(String idClient);
}
