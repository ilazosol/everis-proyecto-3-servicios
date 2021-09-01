package com.everis.springboot.yankiwallet.handler;

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

import com.everis.springboot.yankiwallet.document.ClientDocument;
import com.everis.springboot.yankiwallet.document.YankiWalletDocument;
import com.everis.springboot.yankiwallet.exception.ValidatorWalletException;
import com.everis.springboot.yankiwallet.service.YankiWalletService;
import com.fasterxml.jackson.core.JsonProcessingException;

import reactor.core.publisher.Mono;

@Component
public class YankiWalletHandler {
	
	@Autowired
	private YankiWalletService walletService;
	
	public Mono<ServerResponse> getAllWallets(ServerRequest request){
		return ServerResponse
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(walletService.findAllWallet(), YankiWalletDocument.class);
	}
	
	public Mono<ServerResponse> getWallet(ServerRequest request){
		Integer id = Integer.parseInt(request.pathVariable("id"));
		return ServerResponse
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(walletService.getWalletById(id)));
	}
	
	public Mono<ServerResponse> createWalletClient(ServerRequest request){
		System.out.println("Entro al metodo de crear una billetera");
		return request.bodyToMono(ClientDocument.class)
				.flatMap(c ->  {
					try {
						return walletService.createWalletClient(c);
					} catch (ValidatorWalletException | InterruptedException | ExecutionException | JsonProcessingException | JSONException e) {
						return Mono.just(new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_GATEWAY)) ;
					}
				})
				.flatMap(nw -> ServerResponse
						.status(HttpStatus.CREATED)
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(nw)));
	}
	
}
