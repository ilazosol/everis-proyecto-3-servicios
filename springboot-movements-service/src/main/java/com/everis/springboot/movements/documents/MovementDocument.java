package com.everis.springboot.movements.documents;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "movements")
public class MovementDocument {
	
	@Id
	private String id;
	
	private String tipoMovimiento;
	
	private String tipoProducto;
	
	private Date fechaMovimiento;
	
	private String idCuenta;
	
	private double comission;
	
	private String idCliente;
	
}
