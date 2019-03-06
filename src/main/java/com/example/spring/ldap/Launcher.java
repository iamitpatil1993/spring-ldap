package com.example.spring.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Launcher class to demonstrate spring project set-up is current.
 * @author amit
 *
 */
public class Launcher {
	private static final Logger LOGER = LoggerFactory.getLogger(Launcher.class);

	public static void main(String args[]) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/app-context.xml");

		HelloWorldService service = applicationContext.getBean(HelloWorldService.class);
		LOGER.info("Service returned :: {}", service.getMessage());

		// close application context
		((ConfigurableApplicationContext) applicationContext).close();
	}

}
