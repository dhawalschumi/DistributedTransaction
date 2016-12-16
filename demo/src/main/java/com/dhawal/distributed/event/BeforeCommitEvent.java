/**
 * 
 */
package com.dhawal.distributed.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author dhawal
 *
 */
public class BeforeCommitEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;
	
	private String message;
	
	private JmsTemplate jmsTemplate;

	public BeforeCommitEvent(Object source,String message,JmsTemplate template) {
		super(source);
		this.jmsTemplate=template;
		this.message=message;
		
	}

	public String getMessage() {
		return message;
	}

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}
	

}
