package com.everis.springboot.createaccount.document;

import java.util.Date;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FixedTermDocument {
	
	@Id
	private String id;
	private Double amountInAccount;
	private String clientId;
	private Integer diaRetiro;
	private String type;
	private Date modifiedAt ;
	private Date createAt;
}
