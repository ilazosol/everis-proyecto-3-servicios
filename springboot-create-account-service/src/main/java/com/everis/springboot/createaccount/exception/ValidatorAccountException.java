package com.everis.springboot.createaccount.exception;

public class ValidatorAccountException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String mensajeValidacion = "";

	public ValidatorAccountException(String mensajeValidacion) {
		super();
		this.mensajeValidacion = mensajeValidacion;
	}

	public String getMensajeValidacion() {
		return mensajeValidacion;
	}

	public void setMensajeValidacion(String mensajeValidacion) {
		this.mensajeValidacion = mensajeValidacion;
	}
	
	

}
