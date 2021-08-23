package com.example.everis.springboot.debitcard.document;

import java.util.Date;
import java.util.List;

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
@Document(collection = "debit_card")
public class DebitCardDocument {

	@Id
	private String id;
	
	private String cardNumber;
	
	private Date createdAt;
	
	private Date modifiedAt;
	
	private String clientId;
	
	private List<AccountDocument> asociatedAccounts;
	
	
}
