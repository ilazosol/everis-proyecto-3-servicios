package com.everis.springboot.bootcoin.service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.springframework.http.ResponseEntity;

import com.everis.springboot.bootcoin.documents.ClientDocument;
import com.everis.springboot.bootcoin.exception.ValidatorWalletException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import reactor.core.publisher.Mono;

public interface BootCoinService {
	Mono<ResponseEntity<Map<String, Object>>> createWalletBootCoin(ClientDocument client) throws ValidatorWalletException,InterruptedException, ExecutionException,JsonMappingException, JsonProcessingException, JSONException;
	Mono<ResponseEntity<Map<String, Object>>> solicitarCompraBootCoin(String idSolicitante,String idVendedor,Double mount,String modalidad) throws ValidatorWalletException,InterruptedException, ExecutionException,JsonMappingException, JsonProcessingException, JSONException;

}
