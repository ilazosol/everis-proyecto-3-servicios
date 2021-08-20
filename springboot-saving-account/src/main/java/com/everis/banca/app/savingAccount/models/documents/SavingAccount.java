package com.everis.banca.app.savingAccount.models.documents;



import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Document(collection="cuentaAhorros")
@Getter
@Setter
@AllArgsConstructor
public class SavingAccount implements Serializable{
	
	@Id
	private String id;
	private double amountInAccount;
	private String clientId;
	private int  movementsPerMonth;
	private String type;
	private Date modifiedAt ;
	private Date createAt;
}
