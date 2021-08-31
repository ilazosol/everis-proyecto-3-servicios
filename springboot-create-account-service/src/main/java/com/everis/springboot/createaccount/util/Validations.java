package com.everis.springboot.createaccount.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.everis.springboot.createaccount.document.ClientDocument;
import com.everis.springboot.createaccount.document.CreateAccountDocument;
import com.everis.springboot.createaccount.exception.ValidatorAccountException;

@Component
public class Validations {
	
	public void validateAccount(List<CreateAccountDocument> accounts, CreateAccountDocument account,ClientDocument c) throws ValidatorAccountException{		
		Integer cAhorro = 0;
		Integer cCorriente = 0;
		Integer cPlazoFijo = 0;
		
		String mensajeValidacion = null;
		System.out.println("Entro a la validacion de la cuenta");
		
		if(!account.getAccountType().equals("Cuenta de Ahorro")  && !account.getAccountType().equals("Cuenta Corriente") && !account.getAccountType().equals("Cuenta Plazo Fijo")) {
			mensajeValidacion = "No se puede crear el tipo de cuenta, los tipos de cuentas solo pueden ser: Cuenta de Ahorro, Cuenta Corriente y Cuenta Plazo Fijo";
			System.out.println(mensajeValidacion);
			throw new ValidatorAccountException(mensajeValidacion);
		}
		
		if(c.getClientType().equals("Personal") || c.getClientType().equals("VIP")) {
			for (CreateAccountDocument acc : accounts) {
				if(acc.getAccountType().equals("Cuenta de Ahorro")) {
					cAhorro++;
				}
				if(acc.getAccountType().equals("Cuenta Corriente")) {
					cCorriente++;
				}
				if(acc.getAccountType().equals("Cuenta Plazo Fijo")) {
					cPlazoFijo++;
				}
				if(account.getAccountType().equals("Cuenta de Ahorro") && cAhorro>0) {
					mensajeValidacion = "No puede crear la cuenta, un cliente no puede tener más de una cuenta de ahorro";
					throw new ValidatorAccountException(mensajeValidacion);
				}
				if(account.getAccountType().equals("Cuenta Corriente") && cCorriente>0) {
					mensajeValidacion = "No puede crear la cuenta, un cliente no puede tener más de una cuenta corriente";
					throw new ValidatorAccountException(mensajeValidacion);
				}
				if(account.getAccountType().equals("Cuenta Plazo Fijo") && cPlazoFijo>0) {
					mensajeValidacion = "No puede crear la cuenta, un cliente no puede tener más de una cuenta a plazo fijo";
					throw new ValidatorAccountException(mensajeValidacion);
				}
			}
			
		}else if(c.getClientType().equals("Empresarial") || c.getClientType().equals("PYME")) {
			if(account.getAccountType().equals("Cuenta de Ahorro")) {
				mensajeValidacion = "Un usuario empresarial no puede tener cuenta de ahorro";
				throw new ValidatorAccountException(mensajeValidacion);
			}
			if(account.getAccountType().equals("Cuenta Plazo Fijo")) {
				mensajeValidacion = "Un usuario empresarial no puede tener cuenta a plazo fijo";
				throw new ValidatorAccountException(mensajeValidacion);
			}
		}else if(!c.getClientType().equals("Empresarial") && !c.getClientType().equals("Personal") &&
				!c.getClientType().equals("VIP") && !c.getClientType().equals("PYME")) {
			mensajeValidacion = "Ingreso un tipo de cliente incorrecto";
			throw new ValidatorAccountException(mensajeValidacion);
		}
	}

}
