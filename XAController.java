/**
 * 
 */
package com.dhawal.distributed.transaction.controller;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dhawal.distributed.application.DistributedApplication;

/**
 * @author dhawal
 *
 */
@RestController
public class XAController {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	@Qualifier("recieverJmsTemplate")
	private JmsTemplate recieverJmsTemplate;

	@Autowired
	public XAController(@Autowired DataSource dataSource, @Autowired JmsTemplate jmsTemplate) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.jmsTemplate = jmsTemplate;
	}

	@PostMapping
	@Transactional
	public @ResponseBody String sync(@RequestBody Map<String, String> map, @RequestParam Optional<Boolean> rollback) throws Exception {
		String name = map.get("name");
		this.jdbcTemplate.update("insert into distributed.STUDENT (NAME,AGE) values (?,?)", name, 47);
		String message = sendRecieveMessage();		
		if (rollback.orElse(false)) {
			throw new RuntimeException("Cannot process request");
		}
		return message;
	}
	
	@PostMapping(path="/async")
	@Transactional
	public void async(@RequestBody Map<String, String> map, @RequestParam Optional<Boolean> rollback) throws Exception {
		String name = map.get("name");
		this.jdbcTemplate.update("insert into distributed.STUDENT (NAME,AGE) values (?,?)", name, 47);
		this.jmsTemplate.convertAndSend(DistributedApplication.PRODUCER_QUEUE, "Hello Async" + "!");		
		if (rollback.orElse(false)) {
			throw new RuntimeException("Cannot process request");
		}
	}

	@GetMapping
	public Collection<Map<String, String>> get() {
		return this.jdbcTemplate.query("select * from distributed.STUDENT", (ResultSet rs, int rowNum) -> {
			Map<String, String> map = new HashMap<>();
			map.put("name", rs.getString("NAME"));
			map.put("age", rs.getString("AGE"));
			return map;
		});
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public String sendRecieveMessage() throws Exception {
		String jmsMessageId = "JMSCorrelationID='" + UUID.randomUUID().toString() + "'";
		jmsTemplate.convertAndSend(DistributedApplication.PRODUCER_QUEUE, "Hello " + "!", new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws JMSException {
				message.setJMSCorrelationID(jmsMessageId);
				return message;
			}
		});
		String message = recieveMessage(jmsMessageId);
		System.out.println(message);
		return message;
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public String recieveMessage(String jmsMessageId) throws JMSException {
		String response = null;
		Message message = jmsTemplate.receive(DistributedApplication.CONSUMER_QUEUE);
		if(message instanceof TextMessage){
			TextMessage textMessage = (TextMessage)message;
			response = textMessage.getText();
		}
		System.out.println("Message recieved = "+response);
		return response;
	}
}
