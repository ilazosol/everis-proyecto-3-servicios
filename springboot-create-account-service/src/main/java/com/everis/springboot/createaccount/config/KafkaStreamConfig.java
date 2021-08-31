package com.everis.springboot.createaccount.config;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.everis.springboot.createaccount.document.ClientDocument;
import com.everis.springboot.createaccount.document.CreateAccountDocument;
import com.everis.springboot.createaccount.service.CreateAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Configuration
public class KafkaStreamConfig {
	
	@Autowired
	CreateAccountService service;

	@Bean("unbounded")
	BlockingQueue<Map<String, String>> unbounded(){
		return new LinkedBlockingQueue<>();
	}
	
	@Bean("unboundedFixed")
	BlockingQueue<Map<String, String>> unboundedFixed(){
		return new LinkedBlockingQueue<>();
	}
	
	@Bean
	Supplier<Map<String, String>> producerCreate(){
		return () -> unbounded().poll();
	}
	
	@Bean
	Supplier<Map<String, String>> producerCreateFixed(){
		return () -> unboundedFixed().poll();
	}
	
	@Bean
	Consumer<Flux<Map<String, String>>> consumerCreate(){
		return (flux) -> flux.subscribe(map -> {
			if(map.get("error") == null) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					CreateAccountDocument document = mapper.readValue(map.get("account"), CreateAccountDocument.class);
					ClientDocument client = mapper.readValue(map.get("client"), ClientDocument.class);
					service.saveAccount(document,client);
				} catch (Exception e) {
					log.info(e.getMessage());
				}
			}else {
				throw new RuntimeException(map.get("error"));
			}
			
		});
	}
	
}
