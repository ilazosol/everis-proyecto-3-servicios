package com.everis.springboot.credits.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientDocument {

	private String id;
	
	private String firstName;
	
	private String lastName;
	
	private String clientType;
	
	private Integer dniClient;
	
	private String CEXClient;
	
	private String passportClient;

	private Integer phoneNumber;
	
	private Integer IMEI;
	
	private String email;

}
