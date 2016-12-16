/**
 * 
 */
package com.dhawal.distributed.jms.message.processor;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.jms.core.MessagePostProcessor;

/**
 * @author Dhawal Patel
 *
 */
public class JMSCorelationIdProcessor implements MessagePostProcessor{
	
	 private final String correlationId;
	 
     public JMSCorelationIdProcessor( final String correlationId ) {
         this.correlationId = correlationId;
     }

	@Override
	public Message postProcessMessage(Message message) throws JMSException {
		message.setJMSCorrelationID(correlationId);
		return message;
	}
	

}
