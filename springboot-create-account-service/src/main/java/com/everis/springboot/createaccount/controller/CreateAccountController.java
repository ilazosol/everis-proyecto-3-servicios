package com.everis.springboot.createaccount.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.everis.springboot.createaccount.document.CreateAccountDocument;
import com.everis.springboot.createaccount.service.CreateAccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RefreshScope
@RestController
public class CreateAccountController {
	
	@Autowired
	private CreateAccountService accountService;
	
	@Autowired
	private BlockingQueue<Map<String, String>> unbounded;
	
	@PostMapping("/saveAccount")
	public void saveAccount(@Valid @RequestBody CreateAccountDocument document) throws JsonProcessingException{
		System.out.println("Entro al metodo guardar cuenta");
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> sendMap = new HashMap<>();
		sendMap.put("account", mapper.writeValueAsString(document));
		sendMap.put("idClient", document.getIdClient());
		unbounded.offer(sendMap);
	}

	@GetMapping("/findAccount/{id}")
	public Mono<ResponseEntity<?>> getProduct(@PathVariable("id") String id) {
		Map<String,Object> response = new HashMap<>();
		return accountService.findAccountsById(id).flatMap( a -> {
			response.put("account", a);
			return Mono.just(new ResponseEntity<>(response,HttpStatus.OK));
		});
	}
	
	@GetMapping("/findAccounts/client/{idClient}")
	public Flux<CreateAccountDocument> getProductByClient(@PathVariable("idClient") String idClient) {
		return accountService.findAccountsByClient(idClient);
	}
	

}
