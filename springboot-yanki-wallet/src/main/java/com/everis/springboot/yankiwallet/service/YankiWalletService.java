package com.everis.springboot.yankiwallet.service;

import com.everis.springboot.yankiwallet.document.ClientWalletDocument;
import com.everis.springboot.yankiwallet.document.YankiWalletDocument;
import com.everis.springboot.yankiwallet.exception.ValidatorWalletException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface YankiWalletService {
	
	Mono<YankiWalletDocument> createWalletClient(ClientWalletDocument client) throws ValidatorWalletException;
		
	Flux<YankiWalletDocument> findAllWallet();

	Mono<YankiWalletDocument> getWalletById(Integer id);
}
