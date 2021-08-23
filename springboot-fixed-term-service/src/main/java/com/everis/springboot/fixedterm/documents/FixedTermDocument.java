package com.everis.springboot.fixedterm.documents;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "fixed_terms")
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
