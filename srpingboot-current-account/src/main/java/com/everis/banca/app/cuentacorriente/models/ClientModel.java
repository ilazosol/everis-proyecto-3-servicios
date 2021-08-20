package com.everis.banca.app.cuentacorriente.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientModel {
	
	private String id;
	private String name;
	private TypeModel type;
	private Date createAt;
}
