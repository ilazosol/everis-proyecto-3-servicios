package com.everis.springboot.movements.controller;

import java.text.ParseException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.everis.springboot.movements.documents.MovementDocument;
import com.everis.springboot.movements.service.MovementService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class MovementController {
	
	@Autowired
	private MovementService movementService;
	
	@PostMapping("/saveMovement")
	public Mono<MovementDocument> saveMovement(@Valid @RequestBody MovementDocument movement){
		System.out.println("Entro al metodo guardar cuenta");
		return movementService.saveMovement(movement);
	}
	@GetMapping("/numberOfMovements")
	public Mono<Long> numberOfMovements(@RequestParam(name="idCuenta") String idCuenta){
		System.out.println("Entro al metodo guardar cuenta");
		return movementService.getNumberOfMovements(idCuenta);
	}
	
	@GetMapping("/getComissionsAccount/{idCuenta}/{fechaInicio}/{fechaFin}")
	private Flux<MovementDocument> getComissionsByAccount(@PathVariable String idCuenta, @PathVariable String fechaInicio, @PathVariable String fechaFin) throws ParseException{

		return movementService.getComissionsByAccount(idCuenta,fechaInicio,fechaFin).filter( m ->  m.getComission() != 0.0);
	}
	
	@GetMapping("/getLast10CreditDebit")
	private Flux<MovementDocument> getLast10CreditDebit() throws ParseException{
		return movementService.getLast10CreditDebit();
	}


}
