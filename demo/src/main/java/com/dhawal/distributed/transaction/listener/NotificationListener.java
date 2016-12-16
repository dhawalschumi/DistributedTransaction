/**
 * 
 */
package com.dhawal.distributed.transaction.listener;

import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

import com.dhawal.distributed.application.DistributedApplication;
import com.dhawal.distributed.event.BeforeCommitEvent;

/**
 * @author dhawal
 *
 */

@Service
public class NotificationListener {

	// @Transactional(propagation=Propagation.NEVER)
	//@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void listenMessage(BeforeCommitEvent event) {
		String jmsMessageId = "JMSCorrelationID='" + UUID.randomUUID().toString() + "'";
		event.getJmsTemplate().convertAndSend(DistributedApplication.PRODUCER_QUEUE, "Hello " + "!",
				new MessagePostProcessor() {
					@Override
					public Message postProcessMessage(Message message) throws JMSException {
						message.setJMSCorrelationID(jmsMessageId);
						return message;
					}
				});
		String message = recieveMessage(event, jmsMessageId);
		System.out.println(message);
	}

	// @Transactional(propagation=Propagation.REQUIRES_NEW)
	public String recieveMessage(BeforeCommitEvent event, String jmsMessageId) {
		String message = (String) event.getJmsTemplate()
				.receiveSelectedAndConvert(DistributedApplication.CONSUMER_QUEUE, jmsMessageId);
		return message;
	}
}
