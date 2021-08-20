package com.everis.springboot.createaccount.document;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FixedTermDocument {
	
	private String id;
	
	private Double saldo;
	
	private Date fechaCreacion;
	
	private String idCliente;
	
	private Integer diaRetiro;
}
