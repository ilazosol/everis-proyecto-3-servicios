package com.everis.banca.app.savingAccount.services.implementations;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import com.everis.banca.app.savingAccount.dao.SavingAccountDao;
import com.everis.banca.app.savingAccount.models.documents.MovementDocument;
import com.everis.banca.app.savingAccount.models.documents.SavingAccount;
import com.everis.banca.app.savingAccount.services.interfaces.SavingAccountService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SavingAccountImpl implements SavingAccountService {

	@Autowired
	private SavingAccountDao savingAccountDao;
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@Value("${everis.cantidad.movimientos}")
	private Integer amountOfMovements;
	@Value("${everis.comision.movimientos}")
	private Integer comissionPerMovement;
	@Value("${everis.url.gateway}")
	private String urlGateway;
	
	@Override
	public Flux<SavingAccount> getAllSavingAccount() {
		// TODO Auto-generated method stub
		Flux<SavingAccount> lsavingAccount = savingAccountDao.findAll();
		return lsavingAccount;
	}

	@Override
	public Mono<SavingAccount> save(SavingAccount savingAccount) {
		if(savingAccount.getId() != null) {
			return Mono.error(new IllegalArgumentException("Id of New SavingAccount be null"));
		}
		return savingAccountDao.save(savingAccount);
	}

	@Override
	public Mono<SavingAccount> findById(String idSavingAccount) {
		// TODO Auto-generated method stub
		return savingAccountDao.findById(idSavingAccount);
	}

	@Override
	public Mono<Void> deleteById(String idSavingAccount) {
		// TODO Auto-generated method stub
		return savingAccountDao.deleteById(idSavingAccount);
	}
	@Override
	public Mono<ResponseEntity<Map<String,Object>>> depositar(String idCuenta,Double cantidad) {
		Map<String, Object> response = new HashMap<>();
		//TODO: Change this to make general "localhost"
		return webClientBuilder.build().get()
			    .uri(urlGateway+"/api/movement/numberOfMovements?idCuenta="+idCuenta)
		.retrieve().bodyToMono(Long.class).flatMap(number->{
		//TODO: put movements per month static
			if (number<=amountOfMovements) {
				return savingAccountDao.findById(idCuenta).flatMap( c -> {
					c.setAmountInAccount(c.getAmountInAccount() + cantidad);
					return savingAccountDao.save(c).flatMap(acc -> {
							
							Date date = Calendar.getInstance().getTime();
							MovementDocument movement = MovementDocument.builder()
									.tipoMovimiento("Deposito")
									.tipoProducto("Cuenta Ahorros")
									.fechaMovimiento(new Timestamp(date.getTime()))
									.idCuenta(idCuenta)
									.idCliente(acc.getClientId())
									.build();
							
							webClientBuilder.build().post()
							.uri("http://localhost:8090/api/movement/saveMovement")
							.body(Mono.just(movement), MovementDocument.class)
							.retrieve().bodyToMono(MovementDocument.class).subscribe();
						
							
							response.put("mensaje", "Se hizo el deposito exitosamente");
							response.put("cuenta", acc);
							return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
						});
					
				}).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
			}
			else {
				
				
				return  savingAccountDao.findById(idCuenta).flatMap( c -> {
					c.setAmountInAccount(c.getAmountInAccount() + cantidad -comissionPerMovement);
					return savingAccountDao.save(c).flatMap(acc -> {
							
							Date date = Calendar.getInstance().getTime();
							MovementDocument movement = MovementDocument.builder()
									.tipoMovimiento("Deposito")
									.tipoProducto("Cuenta Ahorros")
									.fechaMovimiento(new Timestamp(date.getTime()))
									.idCuenta(idCuenta)
									.comission(comissionPerMovement)
									.idCliente(acc.getClientId())
									.build();
							
							webClientBuilder.build().post()
							.uri("http://localhost:8090/api/movement/saveMovement")
							.body(Mono.just(movement), MovementDocument.class)
							.retrieve().bodyToMono(MovementDocument.class).subscribe();
						
							
							response.put("mensaje", "Se hizo el deposito exitosamente");
							response.put("cuenta", acc);
							return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
						});
					
				}).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
			
			}
		});
		
		
	
	}


	
	@Override
	public Mono<ResponseEntity<Map<String, Object>>> retirar(String idCuenta, Double cantidad) {
		Map<String, Object> response = new HashMap<>();
		Calendar calendar = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
		return savingAccountDao.findById(idCuenta).flatMap( c -> {
			
			
				if(c.getAmountInAccount() - cantidad - comissionPerMovement< 0) {
					response.put("mensaje", "No puede realizar este retiro ya que no cuenta con el saldo suficiente");
					return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
				}else {
					
					
					
					c.setAmountInAccount(c.getAmountInAccount() - cantidad - comissionPerMovement);
					return savingAccountDao.save(c).flatMap(acc -> {
						
						Date date = Calendar.getInstance().getTime();
						MovementDocument movement = MovementDocument.builder()
								.tipoMovimiento("Retiro")
								.tipoProducto("Cuenta Corriente")
								.fechaMovimiento(new Timestamp(date.getTime()))
								.comission(comissionPerMovement)
								.idCuenta(idCuenta)
								.idCliente(acc.getClientId())
								.build();
						
						webClientBuilder.build().post()
						.uri(urlGateway+"/api/movement/saveMovement")
						.body(Mono.just(movement), MovementDocument.class)
						.retrieve().bodyToMono(MovementDocument.class).subscribe();
						
						response.put("mensaje", "Se hizo el retiro exitosamente");
						response.put("cuenta", acc);
						return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
					});
				}
		
		}).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> consultarSaldo(String idAccount) {
		Map<String, Object> response = new HashMap<>();
		
		return savingAccountDao.findById(idAccount).flatMap( c -> {
			
			
			response.put("mensaje", "El saldo de la cuenta es: S/."+c.getAmountInAccount());
			return Mono.just(new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK));
			
		}).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	

}
