package com.example.spring.ldap;

import junit.framework.TestCase;

public class ExampleServiceTests extends TestCase {

	private HelloWorldService service = new ExampleHelloWorldService();
	
	public void testReadOnce() throws Exception {
		assertEquals("Hello world!", service.getMessage());
	}

}
