package com.everis.springboot.report.documents;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementDocument {
	
	private String id;
	
	private String tipoMovimiento;
	
	private String tipoProducto;
	
	private Date fechaMovimiento;
	
	private String idCuenta;
	
	private double comission;
	
	private String idCliente;
	
}
