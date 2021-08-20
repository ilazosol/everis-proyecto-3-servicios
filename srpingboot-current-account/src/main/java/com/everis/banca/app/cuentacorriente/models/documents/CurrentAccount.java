package com.everis.banca.app.cuentacorriente.models.documents;



import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Document(collection="cuentaCorriente")
@Getter
@Setter
@AllArgsConstructor
public class CurrentAccount implements Serializable  {
	
	@Id
	private String id;
	private double amountInAccount;
	private String clientId;
	private double  costOfMaintenance;
	private String type;
	private Date modifiedAt ;
	private Date createAt;
}
