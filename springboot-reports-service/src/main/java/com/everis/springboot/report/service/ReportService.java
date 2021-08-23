package com.everis.springboot.report.service;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface ReportService {
	
	Mono<ResponseEntity<?>> getComissionReport(String idCuenta,String fechaInicio,String fechaFin);

	Mono<ResponseEntity<?>> getProductsperClientReport(String idClient);

	Mono<ResponseEntity<?>> getReportGeneralProduct(String typeProduct,String fechaInicio,String fechaFin);

	Mono<ResponseEntity<?>> getResportLast10CreditDebit();

}
