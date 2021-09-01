package com.everis.springboot.bootcoin.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.everis.springboot.bootcoin.handler.BootCoinHandler;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {

	@Bean
	public RouterFunction<ServerResponse> routes(BootCoinHandler handler){
		return route()
			.POST("/createWalletBootCoin", handler::createWallet)
			.build();
	}
}
