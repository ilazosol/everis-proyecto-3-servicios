package com.example.everis.springboot.debitcard.document;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDocument {
	
	private String idAccount;
	private String typeAccount;
	private Boolean principal;
	private Date timeAdded;

}
