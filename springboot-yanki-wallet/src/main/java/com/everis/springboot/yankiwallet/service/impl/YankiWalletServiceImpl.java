package com.everis.springboot.yankiwallet.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.springboot.yankiwallet.dao.YankiWalletDao;
import com.everis.springboot.yankiwallet.document.ClientWalletDocument;
import com.everis.springboot.yankiwallet.document.YankiWalletDocument;
import com.everis.springboot.yankiwallet.exception.ValidatorWalletException;
import com.everis.springboot.yankiwallet.service.YankiWalletService;
import com.everis.springboot.yankiwallet.util.Validations;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class YankiWalletServiceImpl implements YankiWalletService {
	
	@Autowired
	private YankiWalletDao yankiWalletDao;
	
	@Autowired
	private Validations validations;

	@Override
	public Mono<YankiWalletDocument> createWalletClient(ClientWalletDocument client) throws ValidatorWalletException {	
		
		if(client.getId() == null) {
			validations.validateClientWallet(client);
			
			YankiWalletDocument wallet = YankiWalletDocument.builder()
											.createdAt(new Date())
											.balance(0.0)
											.idClient(client.getId())
											.build();
			return yankiWalletDao.save(wallet);
		}else {
			validations.validateWallet(client.getId());
			
			YankiWalletDocument wallet = YankiWalletDocument.builder()
											.createdAt(new Date())
											.balance(0.0)
											.idClient(client.getId())
											.build();
			return yankiWalletDao.save(wallet);
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
