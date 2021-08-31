package com.everis.springboot.yankiwallet.config;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.everis.springboot.yankiwallet.service.YankiWalletService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Configuration
public class KafkaStreamConfig {
	
	@Autowired
	YankiWalletService service;

	BlockingQueue<Map<String, String>> unbounded(){
		return new LinkedBlockingQueue<>();
	}
	
	@Bean
	Supplier<Map<String, String>> producerCreate(){
		return () -> unbounded().poll();
	}
	
	@Bean
	Consumer<Flux<Map<String, String>>> consumerCreate(){
		return (flux) -> flux.subscribe();
	}

}
