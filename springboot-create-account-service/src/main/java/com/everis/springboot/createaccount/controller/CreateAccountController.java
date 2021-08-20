package com.everis.springboot.createaccount.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.everis.springboot.createaccount.document.CreateAccountDocument;
import com.everis.springboot.createaccount.service.CreateAccountService;

import reactor.core.publisher.Mono;

@RefreshScope
@RestController
public class CreateAccountController {
	
	@Autowired
	private CreateAccountService accountService;
	
	@PostMapping("/saveAccount/{id}")
	public Mono<ResponseEntity<Map<String,Object>>> saveAccount(@PathVariable String id,@Valid @RequestBody CreateAccountDocument document){
		System.out.println("Entro al metodo guardar cuenta");
		return accountService.saveAccount(id,document);
	}

	@GetMapping("/findAccount/{id}")
	public Mono<ResponseEntity<?>> getProduct(@PathVariable("id") String id) {
		Map<String,Object> response = new HashMap<>();
		return accountService.findAccountsById(id).flatMap( a -> {
			response.put("account", a);
			return Mono.just(new ResponseEntity<>(response,HttpStatus.OK));
		});
	}
	

}
