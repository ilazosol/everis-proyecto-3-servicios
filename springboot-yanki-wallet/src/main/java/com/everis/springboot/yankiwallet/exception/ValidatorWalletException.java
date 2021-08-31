package com.everis.springboot.yankiwallet.exception;

public class ValidatorWalletException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String mensajeValidacion = "";

	public ValidatorWalletException(String mensajeValidacion) {
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
