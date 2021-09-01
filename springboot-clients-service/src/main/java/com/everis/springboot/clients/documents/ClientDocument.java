package com.everis.springboot.clients.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "clients")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientDocument {

	@Id
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
