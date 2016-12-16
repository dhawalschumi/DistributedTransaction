/**
 * 
 */
package com.dhawal.message.listener;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

import com.dhawal.listener.ListenerApplication;

/**
 * @author dhawal
 *
 */
@Service
public class MessageListener {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private JmsTemplate jmstemplate;

	@JmsListener(destination = ListenerApplication.PRODUCER_QUEUE)
	public void onMessage(Message message) throws JMSException {
		String messageJmsId = message.getJMSCorrelationID();
		System.out.println("Jms Id = "+messageJmsId);
		try {
			jdbcTemplate.update("insert into distributed.STUDENT (NAME,AGE) values (?,?)", "dhawal123", "1258");
			jmstemplate.convertAndSend(ListenerApplication.CONSUMER_QUEUE,"Recieved Message = "+messageJmsId, new MessagePostProcessor() {		
				@Override
				public Message postProcessMessage(Message message) throws JMSException {
					message.setJMSCorrelationID(messageJmsId);
					//message.setBooleanProperty("failure", failure);
					return message;
				}
			});
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}		
		finally {}
	}
}
