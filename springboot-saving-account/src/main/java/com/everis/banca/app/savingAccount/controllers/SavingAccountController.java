package com.everis.banca.app.savingAccount.controllers;



import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.everis.banca.app.savingAccount.models.documents.SavingAccount;
import com.everis.banca.app.savingAccount.services.interfaces.SavingAccountService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping()
@Slf4j
public class SavingAccountController {



		@Autowired
		private SavingAccountService savingAccountService;
		
		@GetMapping()
		public Flux<SavingAccount> getAllSavingAccount() {
			
			Flux<SavingAccount> currentAccounts = savingAccountService.getAllSavingAccount().map(currentAccount -> {
				return currentAccount;
			});
			return currentAccounts;
		}
		
		@PostMapping()
		public Mono<SavingAccount> saveSavingAccount(@RequestBody SavingAccount currentAccount) {
			try {
				currentAccount.setCreateAt(new Date());
				Mono<SavingAccount> save = savingAccountService.save(currentAccount);
				log.info("Se ingresó correctamente");
				
				
				
				return save;
			
			
			} catch (Exception e) {
				log.error("Error: " + e);
				return null;
			}}
			@PutMapping()
			public Mono<ResponseEntity<Mono<SavingAccount>>> updateSavingAccount(@RequestBody SavingAccount currentAccount) {
				try {
					Mono<SavingAccount> updatedSavingAccount= savingAccountService.findById(currentAccount.getId()).flatMap(c ->{ 
						
						
						currentAccount.setModifiedAt(new Date ());
						
						
						return savingAccountService.save(currentAccount);})
							.cast(SavingAccount.class) ;
					if ( updatedSavingAccount ==null) {
						log.info("No se econtró el registro de la cuenta corriente");

						Map<String, Object> params = new HashMap<String, Object>();
						params.put("mensaje", "No existe la cuenta");
						
						return Mono.just(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
						}else {
							log.info("Se modificó correctamente");
							Map<String, Object> params = new HashMap<String, Object>();
							
							params.put("mensaje", "No existe la cuenta");
							
							return Mono.just(new ResponseEntity<>(updatedSavingAccount, HttpStatus.NOT_FOUND));
								}
					
					
					
				}
				 catch (Exception e) {
					log.error("Error: " + e);
					return Mono.just(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
				}
				
				
				}
			
			@DeleteMapping()
			public Mono<ResponseEntity<Void>> deleteSavingAccount(@RequestParam(name="idSavingAccount",required = true) String idSavingAccount) {
				
				return savingAccountService.findById(idSavingAccount).flatMap(c ->{ 
					return savingAccountService.deleteById(idSavingAccount).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))) ;	
				}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
			}
			@PostMapping("/deposit/{id}/{amount}")
			public Mono<ResponseEntity<Map<String,Object>>> deposit(@PathVariable String id, @PathVariable Double amount){
				System.out.println("Entro al metodo guardar cuenta");
				return savingAccountService.depositar(id, amount);
			}
			
			@PostMapping("/retirement/{id}/{amount}")
			public Mono<ResponseEntity<Map<String,Object>>> retirement(@PathVariable String id, @PathVariable Double amount){
				System.out.println("Entro al metodo guardar cuenta");
				return savingAccountService.retirar(id, amount);
			}
			
			@GetMapping("/getBalance/{id}")
			public Mono<ResponseEntity<Map<String,Object>>> getBalance(@PathVariable("id") String id) {
				return savingAccountService.consultarSaldo(id);
			}
			
		}
	


