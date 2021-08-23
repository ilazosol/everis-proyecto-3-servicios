package com.everis.springboot.createaccount.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "accounts")
public class CreateAccountDocument {
	
	@Id
	private String id;
	
	private String idSavedAccount;
	
	private String idClient;
	
	private String accountType;
	
	private Date creationDate;
	
	private Double initialMount;
	
}
