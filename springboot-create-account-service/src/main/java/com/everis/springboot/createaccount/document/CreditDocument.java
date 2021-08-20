package com.everis.springboot.createaccount.document;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditDocument {
	private String id;
	
	private String creditType;
	
	private Double balance;
	
	private Double creditLimit;
	
	private String idClient;
	
	private Date creationDate;
	
	private Double creditPaid;
}
