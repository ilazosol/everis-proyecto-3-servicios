package com.everis.springboot.report.documents;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SpecificAccount {
	
	//Atributes for Account
	
	private Double amountInAccount;
	private String clientId;
	private String type;
	private Date modifiedAt;
	private Date createAt;
	private Integer  movementsPerMonth;
	private Double  costOfMaintenance;
	private Integer diaRetiro;
	
	//Attributes for Credit
	
	private String creditType;
	private Double balance;
	private Double creditLimit;
	private String idClient;
	private String creationDate;
	private Double creditPaid;

}
