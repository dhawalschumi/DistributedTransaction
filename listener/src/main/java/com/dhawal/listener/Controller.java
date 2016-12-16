/**
 * 
 */
package com.dhawal.listener;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dhawal
 *
 */
@RestController
public class Controller {
	
	private final JmsTemplate jmsTemplate;
	
	private final JdbcTemplate jdbcTemplate;
	
	public Controller(DataSource dataSource, JmsTemplate jmsTemplate){		
		this.jmsTemplate = jmsTemplate;
		this.jmsTemplate.setSessionTransacted(true);
		this.jdbcTemplate=new JdbcTemplate(dataSource);
	}

}
