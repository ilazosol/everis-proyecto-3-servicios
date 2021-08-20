package com.everis.springboot.report.documents;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementReport {
	private String id;
	
	private String tipoMovimiento;
	
	private String tipoProducto;
	
	private String fechaMovimiento;
	
	private String idCuenta;
	
	private double comission;
	
	private String idCliente;
}
