package com.everis.banca.app.cuentacorriente.microservices.implementations;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.everis.banca.app.cuentacorriente.microservices.interfaces.IClientRestService;
import com.everis.banca.app.cuentacorriente.models.ClientModel;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ClientRestServiceImpl implements IClientRestService {

    
    private final WebClient webClient;

    public ClientRestServiceImpl() {
        this.webClient = WebClient.builder().baseUrl("http://localhost:8001")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
        .build();
                    
    }

    @Override
    public Mono<ClientModel> findById(String id) {
        Mono<ClientModel> client = webClient.get()
                .uri("/Client/" + id)
                .retrieve()
                .bodyToMono(ClientModel.class)
                .log();
        
        return client;
    }

}