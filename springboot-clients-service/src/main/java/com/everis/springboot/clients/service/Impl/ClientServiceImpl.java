package com.everis.springboot.clients.service.Impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.everis.springboot.clients.dao.ClientDao;
import com.everis.springboot.clients.documents.ClientDocument;
import com.everis.springboot.clients.service.ClientService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClientServiceImpl implements ClientService {
	
	@Autowired
	private ClientDao clientDao;

	@Override
	public Mono<ClientDocument> saveClient(ClientDocument client) {
		return clientDao.save(client);
	}

	@Override
	public Flux<ClientDocument> findClients() {
		return clientDao.findAll();
	}

	@Override
	public Mono<ClientDocument> findClient(String id) {
		return clientDao.findById(id);
	}

	@Override
	public Mono<ResponseEntity<Map<String,Object>>> updateClient(String id, ClientDocument client) {
		Map<String, Object> response = new HashMap<>();
	
		return clientDao.findById(id).flatMap(c -> {
			c.setFirstName(client.getFirstName());
			c.setLastName(client.getLastName());
			c.setClientType(client.getClientType());
			return clientDao.save(c);
		}).map(clientUpdated -> {
			response.put("client", clientUpdated);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
			
	}

	@Override
	public ResponseEntity<String> deleteClient(String id) {
		try {
			clientDao.deleteById(id).subscribe();
		} catch (Exception e) {
			return new ResponseEntity<>("Error al eliminar cliente", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("Cliente eliminado con éxito", HttpStatus.OK);
	}

}
