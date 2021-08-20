package com.everis.springboot.report.controller;

import java.io.IOException;

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

	@GetMapping("/reportComission/{idCuenta}/{fechaInicio}/{fechaFin}")
	public Mono<ResponseEntity<?>> getReportComission(@PathVariable String idCuenta, @PathVariable String fechaInicio, @PathVariable String fechaFin) throws IOException{
		System.out.println("Entro a imprimir el pdf de comisiones");
		
		return reportService.getComissionReport(idCuenta, fechaInicio, fechaFin);
		
	}
}
