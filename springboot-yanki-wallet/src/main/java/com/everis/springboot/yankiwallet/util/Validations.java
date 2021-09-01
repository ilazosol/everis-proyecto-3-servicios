package com.everis.springboot.yankiwallet.util;

import org.springframework.stereotype.Component;
import com.everis.springboot.yankiwallet.document.ClientDocument;
import com.everis.springboot.yankiwallet.document.YankiWalletDocument;
import com.everis.springboot.yankiwallet.exception.ValidatorWalletException;

@Component
public class Validations {
	
	public void validateWallet(String idClient) throws ValidatorWalletException{	
		String mensajeValidacion = "";
		if(idClient == null) {
			mensajeValidacion = "Se necesita el id del cliente para la creacion de la cartera";
			throw new ValidatorWalletException(mensajeValidacion);
		}
	}
	
	public void validateClientWallet(ClientDocument client) throws ValidatorWalletException{	
		String mensajeValidacion = "";
		if(client.getEmail() == null) {
			mensajeValidacion = "Se necesita el email del cliente para la creacion de la cartera";
			throw new ValidatorWalletException(mensajeValidacion);
		}
		if(client.getDniClient() == null && client.getCEXClient() == null && client.getPassportClient() == null) {
			mensajeValidacion = "Se necesita al menos un número de los documentos pedidos";
			throw new ValidatorWalletException(mensajeValidacion);
		}
		if(client.getPhoneNumber() == null) {
			mensajeValidacion = "Se necesita el número de celular para la creacion de la cartera";
			throw new ValidatorWalletException(mensajeValidacion);
		}
		if(client.getIMEI() == null) {
			mensajeValidacion = "Se necesita el IMEI del cliente para la creacion de la cartera";
			throw new ValidatorWalletException(mensajeValidacion);
		}
	}
}
