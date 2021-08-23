package com.example.everis.springboot.debitcard.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.everis.springboot.debitcard.document.AccountDocument;
import com.example.everis.springboot.debitcard.document.DebitCardDocument;
import com.example.everis.springboot.debitcard.service.DebitCardService;

import reactor.core.publisher.Mono;

@RestController
public class DebitCardController {
	
	@Autowired
	private DebitCardService debitCardService;
	
	@PostMapping("/save/{idClient}")
	public Mono<DebitCardDocument> saveDebitCard(@PathVariable String idClient){
		return debitCardService.createDebitCard(idClient);
	}
	
	@PostMapping("/associate/{idDebitCard}")
	public Mono<ResponseEntity<Map<String, Object>>> associateAccount(@PathVariable String idDebitCard,@RequestBody AccountDocument account){
		return debitCardService.associateAccounttoDebitCard(idDebitCard,account);
	}
	
	@PostMapping("/pay/{idDebitCard}/{amount}")
	public Mono<ResponseEntity<Map<String, Object>>> payWithAccount(@PathVariable String idDebitCard,@PathVariable Double amount){
		return debitCardService.payWithAccount(idDebitCard,amount);
	}

}
