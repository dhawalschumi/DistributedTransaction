package com.dhawal.distributed.application;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = { "com.dhawal" })
@EnableJms
@EnableTransactionManagement
@Configuration
public class DistributedApplication {  

	public static final String PRODUCER_QUEUE = "producer";
	
	public static final String CONSUMER_QUEUE = "consumer";

	public static void main(String[] args) {
		SpringApplication.run(DistributedApplication.class, args);
	}

	@Bean
	public DefaultJmsListenerContainerFactory myFactory(@Autowired ActiveMQConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setSessionTransacted(true);
		JmsTransactionManager transactionManager =  new JmsTransactionManager();
		transactionManager.setRollbackOnCommitFailure(true);
		transactionManager.setNestedTransactionAllowed(true);
		transactionManager.setFailEarlyOnGlobalRollbackOnly(true);
		transactionManager.setTransactionSynchronization(0);
		transactionManager.setConnectionFactory(connectionFactory);
		factory.setTransactionManager(transactionManager);
		factory.setReceiveTimeout(10000L);
		return factory;
	}
	
	@Bean
	public JmsTemplate jmsTemplate(@Autowired ActiveMQConnectionFactory connectionFactory){
		JmsTemplate template = new JmsTemplate(connectionFactory);
		template.setDefaultDestinationName(PRODUCER_QUEUE);
		//template.setSessionTransacted(true);
		return template;
	}
	
	@Bean(name="recieverJmsTemplate")
	public JmsTemplate recieverJmsTemplate(@Autowired ActiveMQConnectionFactory connectionFactory){
		JmsTemplate template = new JmsTemplate(connectionFactory);
		template.setDefaultDestinationName(PRODUCER_QUEUE);
		//template.setSessionTransacted(true);
		return template;
	}
	
	@Bean
	public JmsMessagingTemplate jmsMessagingTemplate(@Autowired JmsTemplate jmsTemplate){
		JmsMessagingTemplate messagingTemplate = new JmsMessagingTemplate(jmsTemplate);
		return messagingTemplate;
	}
}
