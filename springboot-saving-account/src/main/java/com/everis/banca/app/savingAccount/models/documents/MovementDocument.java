package com.everis.banca.app.savingAccount.models.documents;

import java.sql.Timestamp;

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
	
	private Timestamp fechaMovimiento;
	private double comission;
	private String idCuenta;
	
	private String idCliente;
	
}
