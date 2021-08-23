package com.everis.banca.app.cuentacorriente.controllers;



import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.everis.banca.app.cuentacorriente.models.documents.CurrentAccount;
import com.everis.banca.app.cuentacorriente.services.interfaces.ICurrentAccountService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping()
@Slf4j
public class CurrentAccountController {



		@Autowired
		private ICurrentAccountService currentAccountService;
		
		@GetMapping("/getAccount/{idAccount}")
		public Mono<CurrentAccount> getCurrentAccount(@PathVariable String idAccount) {
			return currentAccountService.getCurrentAccount(idAccount);
		}
		
		@GetMapping()
		public Flux<CurrentAccount> getAllCurrentAccount() {
			
			Flux<CurrentAccount> currentAccounts = currentAccountService.getAllCurrentAccount().map(currentAccount -> {
				return currentAccount;
			});
			return currentAccounts;
		}
		
		@PostMapping()
		public Mono<CurrentAccount> saveCurrentAccount(@RequestBody CurrentAccount currentAccount) {
			try {
				currentAccount.setCreateAt(new Date());
				Mono<CurrentAccount> save = currentAccountService.save(currentAccount);
				log.info("Se ingresó correctamente");
				
				
				
				return save;
			
			
			} catch (Exception e) {
				log.error("Error: " + e);
				return null;
			}}
			@PutMapping()
			public Mono<ResponseEntity<Mono<CurrentAccount>>> updateCurrentAccount(@RequestBody CurrentAccount currentAccount) {
				try {
					Mono<CurrentAccount> updatedCurrentAccount= currentAccountService.findById(currentAccount.getId()).flatMap(c ->{ 
						
						
						currentAccount.setModifiedAt(new Date ());
						
						
						return currentAccountService.save(currentAccount);})
							.cast(CurrentAccount.class) ;
					if ( updatedCurrentAccount ==null) {
						log.info("No se econtró el registro de la cuenta corriente");

						Map<String, Object> params = new HashMap<String, Object>();
						params.put("mensaje", "No existe la cuenta");
						
						return Mono.just(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
						}else {
							log.info("Se modificó correctamente");
							Map<String, Object> params = new HashMap<String, Object>();
							
							params.put("mensaje", "No existe la cuenta");
							
							return Mono.just(new ResponseEntity<>(updatedCurrentAccount, HttpStatus.NOT_FOUND));
								}
					
					
					
				}
				 catch (Exception e) {
					log.error("Error: " + e);
					return Mono.just(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
				}
				
				
				}
			
			@DeleteMapping()
			public Mono<ResponseEntity<Void>> deleteCurrentAccount(@RequestParam(name="idCurrentAccount",required = true) String idCurrentAccount) {
				
				return currentAccountService.findById(idCurrentAccount).flatMap(c ->{ 
					return currentAccountService.deleteById(idCurrentAccount).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))) ;	
				}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
			}
			
			@PostMapping("/deposit/{id}/{amount}")
			public Mono<ResponseEntity<Map<String,Object>>> deposit(@PathVariable String id, @PathVariable Double amount){
				System.out.println("Entro al metodo guardar cuenta");
				return currentAccountService.depositar(id, amount);
			}
			
			@PostMapping("/retirement/{id}/{amount}")
			public Mono<ResponseEntity<Map<String,Object>>> retirement(@PathVariable String id, @PathVariable Double amount){
				System.out.println("Entro al metodo guardar cuenta");
				return currentAccountService.retirar(id, amount);
			}
			
			@GetMapping("/getBalance/{id}")
			public Mono<String> getBalance(@PathVariable("id") String id) {
				return currentAccountService.consultarSaldo(id);
			}
			
			@GetMapping("/getProductByDates/{fechaInicio}/{fechaFin}")
			public Flux<CurrentAccount> getProductByDates(@PathVariable String fechaInicio, @PathVariable String fechaFin) throws ParseException {
				return currentAccountService.getProductByDates(fechaInicio, fechaFin);
			}
			
			@GetMapping("/payWithDebit/{idAccount}/{mount}")
			public Mono<Boolean> payWithDebitCard(@PathVariable String idAccount,@PathVariable Double mount) {
				return currentAccountService.payWithDebitCard(idAccount,mount);
			}

			
		}
	


