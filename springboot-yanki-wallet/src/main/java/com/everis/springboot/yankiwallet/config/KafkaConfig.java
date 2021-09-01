package com.everis.springboot.yankiwallet.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class KafkaConfig {
	
	@Value("${kafka.group.id}")
	private String groupId;
	
	@Value("${kafka.group.id2}")
	private String groupId2;
	
	@Value("${kafka.reply.topic}")
	private String topicNewClient;
	
	@Value("${kafka.reply.topic2}")
	private String topicExistingClient;
	
	@Bean
	public ReplyingKafkaTemplate<String, Map<String, Object>, Map<String, String>> createNewClientTemplate(ProducerFactory<String, Map<String, Object>> pf,ConcurrentKafkaListenerContainerFactory<String, Map<String, String>> factory){
		ConcurrentMessageListenerContainer<String, Map<String, String>> replyContainer = factory.createContainer(topicNewClient);
		replyContainer.getContainerProperties().setGroupId(groupId);
		replyContainer.getContainerProperties().setMissingTopicsFatal(false);
		return new ReplyingKafkaTemplate<>(pf, replyContainer);
	}
	
	@Bean
	public ReplyingKafkaTemplate<String, Map<String, Object>, Map<String, String>> findClientTemplate(ProducerFactory<String, Map<String, Object>> pf,ConcurrentKafkaListenerContainerFactory<String, Map<String, String>> factory){
		ConcurrentMessageListenerContainer<String, Map<String, String>> replyContainer = factory.createContainer(topicExistingClient);
		replyContainer.getContainerProperties().setMissingTopicsFatal(false);
		replyContainer.getContainerProperties().setGroupId(groupId2);
		return new ReplyingKafkaTemplate<>(pf, replyContainer);
	}
	
	@Bean
	public KafkaTemplate<String, Map<String, String>> replyTemplate(ProducerFactory<String, Map<String, String>> pf,ConcurrentKafkaListenerContainerFactory<String, Map<String, String>> factory){
		KafkaTemplate<String, Map<String, String>> kafkaTemplate = new KafkaTemplate<>(pf);
		factory.getContainerProperties().setMissingTopicsFatal(false);
		factory.setReplyTemplate(kafkaTemplate);
		return kafkaTemplate;
	}

}
