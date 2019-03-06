package com.example.spring.ldap;


import org.springframework.stereotype.Component;

/**
 * Spring bean to demonstrate project setup
 * @author amit
 *
 */
@Component
public class ExampleHelloWorldService implements HelloWorldService {
	
	/**
	 * Reads next record from input
	 */
	public String getMessage() {
		return "Hello world!";	
	}

}
