package com.everis.springboot.bootcoin.documents;

import java.util.Date;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BootCoinDocument {
	
	@Id
	private String id;
	
	private Date createdAt;
	
	private Date modifiedAt;
	
	private Double balance;
	
	private String idDebitCard;
	
	private String idClient;

}
