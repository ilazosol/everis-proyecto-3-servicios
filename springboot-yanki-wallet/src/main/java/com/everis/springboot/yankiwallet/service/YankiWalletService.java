package com.everis.springboot.yankiwallet.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.springframework.http.ResponseEntity;

import com.everis.springboot.yankiwallet.document.ClientDocument;
import com.everis.springboot.yankiwallet.document.YankiWalletDocument;
import com.everis.springboot.yankiwallet.exception.ValidatorWalletException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface YankiWalletService {
	
	Mono<ResponseEntity<Map<String, Object>>> createWalletClient(ClientDocument client) throws ValidatorWalletException,InterruptedException, ExecutionException,JsonMappingException, JsonProcessingException, JSONException;
		
	Flux<YankiWalletDocument> findAllWallet();

	Mono<YankiWalletDocument> getWalletById(Integer id);
}
