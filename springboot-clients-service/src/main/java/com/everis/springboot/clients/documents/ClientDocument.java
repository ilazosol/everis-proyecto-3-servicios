package com.everis.springboot.clients.documents;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

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
	
	@NotEmpty
	private String firstName;
	
	@NotEmpty
	private String lastName;
	
	@NotEmpty
	private String clientType;
	
	@Min(10000000)
	@Max(99999999)
	private Integer dniClient;
	
	private String CEXClient;
	
	private String passportClient;
	
	@Min(100000000)
	@Max(999999999)
	private Integer phoneNumber;
	
	@NotEmpty
	private Integer IMEI;
	
	@NotEmpty
	@Email
	private String email;
	
	

}
