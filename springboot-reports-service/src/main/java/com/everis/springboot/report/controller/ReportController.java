package com.everis.springboot.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.everis.springboot.report.service.ReportService;

import reactor.core.publisher.Mono;

@RestController
public class ReportController {
	
	@Autowired
	private ReportService reportService;

	@GetMapping("/comission/{idCuenta}/{fechaInicio}/{fechaFin}")
	public Mono<ResponseEntity<?>> getReportComission(@PathVariable String idCuenta, @PathVariable String fechaInicio, @PathVariable String fechaFin){
		System.out.println("Entro a imprimir el pdf de comisiones");
		
		return reportService.getComissionReport(idCuenta, fechaInicio, fechaFin);
		
	}
	
	@GetMapping("/products/{idClient}")
	public Mono<ResponseEntity<?>> getProductsperClientReport(@PathVariable String idClient ){
		System.out.println("Entro a imprimir el pdf de productos por cliente");
		
		return reportService.getProductsperClientReport(idClient);
		
	}
	
	@GetMapping("/product/{typeProduct}/{fechaInicio}/{fechaFin}")
	public Mono<ResponseEntity<?>> getReportGeneralProduct(@PathVariable String typeProduct,@PathVariable String fechaInicio, @PathVariable String fechaFin ){
		System.out.println("Entro a imprimir el pdf general de un producto");
		
		return reportService.getReportGeneralProduct(typeProduct,fechaInicio,fechaFin);
		
	}
	
	@GetMapping("/tenLastMovementCreditDebit/{idClient}")
	public Mono<ResponseEntity<?>> getResportLast10CreditDebit(@PathVariable String idClient ){
		System.out.println("Entro a imprimir el pdf general de un producto");
		
		return reportService.getResportLast10CreditDebit(idClient);
		
	}
}
