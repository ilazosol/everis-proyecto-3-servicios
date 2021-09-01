package com.everis.springboot.yankiwallet.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;
import com.everis.springboot.yankiwallet.dao.YankiWalletDao;
import com.everis.springboot.yankiwallet.document.ClientDocument;
import com.everis.springboot.yankiwallet.document.YankiWalletDocument;
import com.everis.springboot.yankiwallet.exception.ValidatorWalletException;
import com.everis.springboot.yankiwallet.service.YankiWalletService;
import com.everis.springboot.yankiwallet.util.Validations;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class YankiWalletServiceImpl implements YankiWalletService {
	
	@Autowired
	private YankiWalletDao yankiWalletDao;
	
	@Autowired
	private Validations validations;
	
	@Value("${kafka.request.topic}")
	private String topicNewClient;
	
	@Value("${kafka.request.topic2}")
	private String topicExistingClient;
	
	@Autowired
	@Qualifier("createNewClientTemplate")
	private ReplyingKafkaTemplate<String,Map<String, Object>,Map<String, String>> createNewClientTemplate;
	
	@Autowired
	@Qualifier("findClientTemplate")
	private ReplyingKafkaTemplate<String,Map<String, Object>,Map<String, String>> findClientTemplate;

	@Override
	public Mono<ResponseEntity<Map<String, Object>>> createWalletClient(ClientDocument client) throws InterruptedException, ExecutionException, JsonMappingException, JsonProcessingException, JSONException {
		Map<String, Object> response = new HashMap<>();
		System.out.println("Entro a la implementacion de la creacion de la billetera");
		if(client.getId() == null) {
			System.out.println("Entro aca no hay id");
			try {
				validations.validateClientWallet(client);
				Map<String, Object> request = new HashMap<>();
				request.put("client", client);
				ProducerRecord<String, Map<String, Object>> record = new ProducerRecord<>(topicNewClient, request);
				RequestReplyFuture<String, Map<String, Object>, Map<String, String>> future = createNewClientTemplate.sendAndReceive(record);
				ConsumerRecord<String, Map<String, String>> res = future.get();
				
				
				/*return res.value().flatMap( nc -> {
					YankiWalletDocument wallet = YankiWalletDocument.builder()
							.createdAt(new Date())
							.balance(0.0)
							.idClient(nc.getId())
							.build();
					
					return yankiWalletDao.save(wallet).flatMap( w ->{
						response.put("mensaje", "se registro correctamente la billetera Tunki para el cliente: "+nc.getFirstName()+" "+nc.getLastName());
						response.put("wallet", w);
						return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
					});
					
				});*/
				
				return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
				
				
			} catch (ValidatorWalletException e) {
				response.put("mensaje", e.getMensajeValidacion());
				return Mono.just(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
			}
		}else {
			System.out.println("Entro aca si hay");
			Map<String, Object> request = new HashMap<>();
			request.put("idClient", client.getId());
			ObjectMapper mapper = new ObjectMapper();
			ProducerRecord<String, Map<String, Object>> record = new ProducerRecord<>(topicExistingClient, request);
			RequestReplyFuture<String, Map<String, Object>, Map<String, String>> future = findClientTemplate.sendAndReceive(record);
			ConsumerRecord<String, Map<String, String>> res = future.get();
			System.out.println("Abajo se va a imprimir el valor del evento");
			System.out.println(res.value().get("client").toString());
			ClientDocument ec = mapper.readValue(res.value().get("client").toString(), ClientDocument.class);
			System.out.println(ec.toString());
			YankiWalletDocument wallet = YankiWalletDocument.builder()
					.createdAt(new Date())
					.balance(0.0)
					.idClient(ec.getId())
					.build();
			
			return yankiWalletDao.save(wallet).flatMap( w ->{
				response.put("mensaje", "se registro correctamente la billetera Tunki para el cliente: "+ec.getFirstName()+" "+ec.getLastName());
				response.put("wallet", w);
				return Mono.just(new ResponseEntity<>(response, HttpStatus.OK));
			});
		}
		
		
	}



	@Override
	public Flux<YankiWalletDocument> findAllWallet() {
		return yankiWalletDao.findAll();
	}

	@Override
	public Mono<YankiWalletDocument> getWalletById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

}
