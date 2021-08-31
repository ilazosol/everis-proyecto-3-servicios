package com.everis.springboot.clients.config;

import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.everis.springboot.clients.service.ClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Configuration
public class KafkaStreamConfig {
	
	@Autowired
	ClientService clientService;
	
	@Bean
	public Function<Flux<Map<String, String>>,Flux<Map<String, String>>> processorCreate(){
		ObjectMapper mapper = new ObjectMapper();
		return flux -> flux.flatMap( mapa -> {
			return clientService.findClient((String)mapa.get("idClient")).map( c -> {
				try {
					mapa.put("client", mapper.writeValueAsString(c));
					System.out.println(mapa.toString());
					return mapa;
				} catch (JsonProcessingException e) {
					log.info(e.getMessage());
					return Map.of("error",e.getMessage());
				}
			});
		});
	}

}
