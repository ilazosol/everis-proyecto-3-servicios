package com.everis.springboot.bootcoin.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;

import com.everis.springboot.bootcoin.documents.BootCoinDocument;
import com.everis.springboot.bootcoin.documents.ClientDocument;
import com.everis.springboot.bootcoin.exception.ValidatorWalletException;
import com.everis.springboot.bootcoin.repository.BootCoinDao;
import com.everis.springboot.bootcoin.service.BootCoinService;
import com.everis.springboot.bootcoin.util.Validations;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Service
public class BootCoinServiceImpl implements BootCoinService {
	
	@Autowired
	private BootCoinDao bootCoinDao;
	
	@Autowired
	private Validations validations;
	
	@Value("${valor.preciocompra.bootcoin}")
	private Double precioCompraBootCoin;
	
	@Value("${valor.precioventa.bootcoin}")
	private Double precioVentaBootCoin;
	
	@Value("${kafka.request.topic3}")
	private String topicNewClient;
	
	@Value("${kafka.request.topic4}")
	private String topicExistingClient;
	
	@Value("${kafka.request.topic5}")
	private String topicTransferMovement;
	
	@Autowired
	@Qualifier("createNewClientTemplate")
	private ReplyingKafkaTemplate<String,Map<String, Object>,Map<String, String>> createNewClientTemplate;
	
	@Autowired
	@Qualifier("findClientTemplate")
	private ReplyingKafkaTemplate<String,Map<String, Object>,Map<String, String>> findClientTemplate;
	
	@Autowired
	@Qualifier("createMovementTransfer")
	private ReplyingKafkaTemplate<String,Map<String, Object>,Map<String, String>> createMovementTransfer;

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> createWalletBootCoin(ClientDocument client)
			throws ValidatorWalletException, InterruptedException, ExecutionException, JsonMappingException,
			JsonProcessingException, JSONException {
		Map<String, Object> response = new HashMap<>();
		System.out.println("Entro a la implementacion de la creacion de la billetera");
		if(client.getId() == null) {
			System.out.println("Entro aca no hay id");
			try {
				validations.validateClientWallet(client);
				Map<String, Object> request = new HashMap<>();
				ObjectMapper mapper = new ObjectMapper();
				request.put("client", client);
				ProducerRecord<String, Map<String, Object>> record = new ProducerRecord<>(topicTransferMovement, request);
				RequestReplyFuture<String, Map<String, Object>, Map<String, String>> future = createNewClientTemplate.sendAndReceive(record);
				ConsumerRecord<String, Map<String, String>> res = future.get();
				ClientDocument nc = mapper.readValue(res.value().get("client").toString(), ClientDocument.class);
				
				BootCoinDocument wallet = BootCoinDocument.builder()
						.createdAt(new Date())
						.balance(0.0)
						.idClient(nc.getId())
						.build();
				
				return bootCoinDao.save(wallet).flatMap( w ->{
					response.put("mensaje", "se registro correctamente la billetera bootcoin para el cliente: "+nc.getFirstName()+" "+nc.getLastName());
					response.put("wallet", w);
					return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
				});				
				
			} catch (ValidatorWalletException e) {
				response.put("mensaje", e.getMensajeValidacion());
				return Mono.just(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
			}
		}else {
			Map<String, Object> request = new HashMap<>();
			request.put("idClient", client.getId());
			ObjectMapper mapper = new ObjectMapper();
			ProducerRecord<String, Map<String, Object>> record = new ProducerRecord<>(topicExistingClient, request);
			RequestReplyFuture<String, Map<String, Object>, Map<String, String>> future = findClientTemplate.sendAndReceive(record);
			ConsumerRecord<String, Map<String, String>> res = future.get();
			System.out.println(res.value());
			ClientDocument ec = mapper.readValue(res.value().get("client").toString(), ClientDocument.class);
			BootCoinDocument wallet = BootCoinDocument.builder()
					.createdAt(new Date())
					.balance(0.0)
					.idClient(ec.getId())
					.build();
			
			return bootCoinDao.save(wallet).flatMap( w ->{
				response.put("mensaje", "se registro correctamente la billetera bootcoin para el cliente: "+ec.getFirstName()+" "+ec.getLastName());
				response.put("wallet", w);
				return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
			});
		}
	}

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> solicitarCompraBootCoin(String idSolicitante, String idVendedor,
			Double mount, String modalidad) throws ValidatorWalletException, InterruptedException, ExecutionException,
			JsonMappingException, JsonProcessingException, JSONException {
			Map<String, Object> request = new HashMap<>();
		
		return bootCoinDao.findByIdClient(idSolicitante).flatMap( b -> {
			Map<String, Object> response = new HashMap<>();
			try {
				
				ProducerRecord<String, Map<String, Object>> record = new ProducerRecord<>(topicExistingClient, request);
				RequestReplyFuture<String, Map<String, Object>, Map<String, String>> future = createMovementTransfer.sendAndReceive(record);
				ConsumerRecord<String, Map<String, String>> res = future.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
		}).defaultIfEmpty(new ResponseEntity<>(Map.of("mensaje","El cliente solicitante no dispone de un monedero Bootcoin"),HttpStatus.BAD_GATEWAY));
	}

}
