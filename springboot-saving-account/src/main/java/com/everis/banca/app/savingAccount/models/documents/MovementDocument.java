package com.everis.banca.app.savingAccount.models.documents;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovementDocument {
	
	private String id;
	
	private String tipoMovimiento;
	
	private String tipoProducto;
	
	private Date fechaMovimiento;
	private double comission;
	private String idCuenta;
	
	private String idCliente;
	
}
