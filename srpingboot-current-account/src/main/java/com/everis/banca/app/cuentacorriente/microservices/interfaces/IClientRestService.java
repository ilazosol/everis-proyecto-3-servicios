package com.everis.banca.app.cuentacorriente.microservices.interfaces;

import com.everis.banca.app.cuentacorriente.models.ClientModel;

import reactor.core.publisher.Mono;

public interface IClientRestService {

    Mono<ClientModel> findById(String id);
}
