package com.everis.springboot.yankiwallet.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.everis.springboot.yankiwallet.document.ClientWalletDocument;
import com.everis.springboot.yankiwallet.document.YankiWalletDocument;
import com.everis.springboot.yankiwallet.exception.ValidatorWalletException;
import com.everis.springboot.yankiwallet.service.YankiWalletService;

import reactor.core.publisher.Mono;

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
		
		return request.bodyToMono(ClientWalletDocument.class)
				.flatMap(c ->  {
					try {
						return walletService.createWalletClient(c);
					} catch (ValidatorWalletException e) {
						return ServerResponse.badRequest().body(BodyInserters.fromValue(e.getMensajeValidacion()));
					}
				})
				.flatMap(nw -> ServerResponse
						.status(HttpStatus.CREATED)
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(nw)));
	}
	
}
