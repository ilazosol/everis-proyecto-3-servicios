package com.everis.springboot.clients.listener;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import com.everis.springboot.clients.documents.ClientDocument;
import com.everis.springboot.clients.service.ClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ClientKafkaListener {
	
	@Autowired
	private ClientService clientService;
	
	@KafkaListener(topics = "${kafka.request.topic}", groupId = "${kafka.group.id}")
	@SendTo
	public Map<String, Object> createClientTunki(Map<String, Object> request) throws JsonMappingException, JsonProcessingException, JSONException{
		Map<String, Object> response = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		JSONObject json = new JSONObject(request);
		ClientDocument client = mapper.readValue(json.get("client").toString(), ClientDocument.class);
		ClientDocument savedClient = clientService.saveClient(client).block();
		response.put("client", savedClient.toString());
		return response;
	}
	
	@KafkaListener(topics = "${kafka.request.topic2}", groupId = "${kafka.group.id2}")
	@SendTo
	public Map<String, String> findClientById(Map<String, Object> request) throws JsonProcessingException{
		Map<String, String> response = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(request);
		ClientDocument existentClient = clientService.findClient(request.get("idClient").toString()).block();
		System.out.println(request.get("idClient").toString());
		String jsonClient = mapper.writeValueAsString(existentClient);
		System.out.println(jsonClient);
		response.put("client", jsonClient);
		return response;
	}
	
	@KafkaListener(topics = "${kafka.request.topic3}", groupId = "${kafka.group.id3}")
	@SendTo
	public Map<String, Object> createClientBootCoin(Map<String, Object> request) throws JsonMappingException, JsonProcessingException, JSONException{
		Map<String, Object> response = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		JSONObject json = new JSONObject(request);
		ClientDocument client = mapper.readValue(json.get("client").toString(), ClientDocument.class);
		System.out.println(client.toString());
		ClientDocument savedClient = clientService.saveClient(client).block();
		response.put("client", mapper.writeValueAsString(savedClient));
		return response;
	}
	
	@KafkaListener(topics = "${kafka.request.topic4}", groupId = "${kafka.group.id4}")
	@SendTo
	public Map<String, String> findClientByIdBootCoin(Map<String, Object> request) throws JsonProcessingException{
		Map<String, String> response = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(request);
		clientService.findClient(request.get("idClient").toString()).doOnSuccess( c -> {
			System.out.println("clieeent");
			System.out.println(c);
		}).subscribe();
		ClientDocument existentClient = clientService.findClient(request.get("idClient").toString()).block();
		System.out.println(request.get("idClient").toString());
		String jsonClient = mapper.writeValueAsString(existentClient);
		System.out.println(jsonClient);
		response.put("client", jsonClient);
		return response;
	}
	
}
