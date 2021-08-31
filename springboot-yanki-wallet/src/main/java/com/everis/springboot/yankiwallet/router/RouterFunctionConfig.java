package com.everis.springboot.yankiwallet.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.everis.springboot.yankiwallet.handler.YankiWalletHandler;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {

	@Bean
	public RouterFunction<ServerResponse> routes(YankiWalletHandler handler){
		return route()
			.GET("/getAll", handler::getAllWallets)
			.POST("/createWallet", handler::createWalletExistingClient)
			.build();
	}
}
