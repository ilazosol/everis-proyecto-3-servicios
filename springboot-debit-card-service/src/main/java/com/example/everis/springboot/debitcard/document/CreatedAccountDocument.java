package com.example.everis.springboot.debitcard.document;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CreatedAccountDocument {
	
	private String id;
	private double amountInAccount;
	private String clientId;
	private String type;
	private Date modifiedAt ;
	private Date createAt;

}
