package com.everis.springboot.report.documents;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDocument {
	
	private String id;
	
	private String idClient;
	
	private String accountType;
	
	private Date creationDate;
	
	private Double initialMount;
	
}
