package com.everis.springboot.movements.dao;

import java.util.Date;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.springboot.movements.documents.MovementDocument;

import reactor.core.publisher.Flux;

public interface MovementDao extends ReactiveMongoRepository<MovementDocument, String> {
	
	Flux<MovementDocument> findByIdCuenta(String idCuenta);
	
	@Query(value = "{'fechaMovimiento':{ $gte: ?0, $lte: ?1}, 'idCuenta': ?2}")
	Flux<MovementDocument> findMovementDocumentByFechaMovimientoAndIdCliente(Date fechaInicio, Date fechaFin, String idCuenta);

	@Query(value = "{'tipoMovimiento': 'Pago Tarjeta Debito' }")
	Flux<MovementDocument> findLast10MovementDebitCredit(Sort sort);
}
