package com.everis.springboot.yankiwallet.document;

import java.util.Date;

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
@Document("yanki_wallet")
public class YankiWalletDocument {
	
	@Id
	private String id;
	
	private Date createdAt;
	
	private Date modifiedAt;
	
	private Double balance;
	
	private String idDebitCard;
	
	private String idClient;

}
