/**
 * 
 */
package com.dhawal.distributed.jms.message.processor;

import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.core.SessionCallback;
import org.springframework.jms.support.JmsUtils;
import org.springframework.jms.support.destination.DestinationResolver;

/**
 * @author dhawal
 *
 */
public class MessageProducerConsumer implements SessionCallback<Message>{
	
	private static final int TIMEOUT = 5000;
	 
    private final String msg;

    private final DestinationResolver destinationResolver;

    private final String requestQueueName;
    
    private final String responseQueueName;

	public MessageProducerConsumer(final String msg, String requestQueue,
			String responseQueue,final DestinationResolver destinationResolver) {
		this.msg = msg;
		this.requestQueueName = requestQueue;
		this.responseQueueName = responseQueue;
		this.destinationResolver = destinationResolver;

	}

	@Override
	public Message doInJms(Session session) throws JMSException {
		 MessageConsumer consumer = null;
         MessageProducer producer = null;
         try {
             final String correlationId = UUID.randomUUID().toString();
             final Destination requestQueue =
                     destinationResolver.resolveDestinationName( session, requestQueueName, false );
             final Destination replyQueue =
                     destinationResolver.resolveDestinationName( session, responseQueueName, false );
             consumer = session.createConsumer( replyQueue, "JMSCorrelationID = '" + correlationId + "'" );
             final TextMessage textMessage = session.createTextMessage( msg );
             textMessage.setJMSCorrelationID( correlationId );
             textMessage.setJMSReplyTo( replyQueue );
             producer = session.createProducer( requestQueue );
             producer.send( requestQueue, textMessage );
             return consumer.receive(TIMEOUT);
         }
         finally {
             JmsUtils.closeMessageConsumer( consumer );
             JmsUtils.closeMessageProducer( producer );
         }
	}
}
