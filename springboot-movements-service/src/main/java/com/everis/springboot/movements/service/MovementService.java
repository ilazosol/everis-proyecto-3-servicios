package com.everis.springboot.movements.service;

import java.text.ParseException;

import com.everis.springboot.movements.documents.MovementDocument;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementService {
	
	Mono<MovementDocument> saveMovement(MovementDocument movement);
	Mono<Long> getNumberOfMovements(String idCuenta);
	Flux<MovementDocument> getComissionsByAccount(String idCuenta, String fechaInicio, String fechaFin) throws ParseException;
	Flux<MovementDocument> getLast10CreditDebit();
}
