package com.everis.springboot.yankiwallet.document;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientWalletDocument {
	
	private String id;
	
	@NotEmpty
	private String firstName;
	
	@NotEmpty
	private String lastName;
	
	@Min(10000000)
	@Max(99999999)
	private Integer dniClient;
	
	private String CEXClient;
	
	private String passportClient;
	
	@NotEmpty
	private Integer phoneNumber;
	
	@NotEmpty
	private Integer IMEI;
	
	@NotEmpty
	@Email
	private String email;
	
	
}
