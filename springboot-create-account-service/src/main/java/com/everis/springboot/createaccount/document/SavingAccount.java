package com.everis.springboot.createaccount.document;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingAccount implements Serializable {
	
	private String id;
	private double amountInAccount;
	private String clientId;
	private int  movementsPerMonth;
	private String type;
	private Date modifiedAt ;
	private Date createAt;
	
	private static final long serialVersionUID = 1L;
}
