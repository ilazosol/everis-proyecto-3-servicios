package com.everis.springboot.credits.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "credits")
public class CreditDocument {
	@Id
	private String id;
	
	private String creditType;
	
	private Double balance;
	
	private Double creditLimit;
	
	private String idClient;
	
	private String creationDate;
	
	private Double creditPaid;
}
