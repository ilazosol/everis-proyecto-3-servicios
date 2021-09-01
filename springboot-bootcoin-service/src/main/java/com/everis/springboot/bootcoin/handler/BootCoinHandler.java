package com.everis.springboot.bootcoin.handler;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.everis.springboot.bootcoin.documents.ClientDocument;
import com.everis.springboot.bootcoin.exception.ValidatorWalletException;
import com.everis.springboot.bootcoin.service.BootCoinService;
import com.fasterxml.jackson.core.JsonProcessingException;

import reactor.core.publisher.Mono;

@Component
public class BootCoinHandler {
	
	@Autowired
	private BootCoinService bootCoinService;
	
	
	public Mono<ServerResponse> createWallet(ServerRequest request){
		System.out.println("Entro al metodo de crear una billetera bootcoin");
		return request.bodyToMono(ClientDocument.class)
				.flatMap(c ->  {
					try {
						return bootCoinService.createWalletBootCoin(c);
					} catch (ValidatorWalletException | InterruptedException | ExecutionException | JsonProcessingException | JSONException e) {
						return Mono.just(new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_GATEWAY)) ;
					}
				}).flatMap(nw -> ServerResponse
						.status(HttpStatus.CREATED)
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(nw)));
	}

}
