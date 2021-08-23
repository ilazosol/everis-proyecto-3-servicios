package com.example.everis.springboot.debitcard.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.example.everis.springboot.debitcard.document.AccountDocument;
import com.example.everis.springboot.debitcard.document.DebitCardDocument;

import reactor.core.publisher.Mono;

public interface DebitCardService {
	
	Mono<DebitCardDocument> createDebitCard(String document);

	Mono<ResponseEntity<Map<String, Object>>> associateAccounttoDebitCard(String idDebitCard, AccountDocument account);

	Mono<ResponseEntity<Map<String, Object>>> payWithAccount(String idDebitCard, Double amount);

}
