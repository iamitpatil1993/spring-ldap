package com.example.spring.ldap;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class ExampleConfigurationTests extends BaseTest {
	
	@Autowired
	private HelloWorldService service;

	@Test
	public void testSimpleProperties() throws Exception {
		assertNotNull(service);
	}
	
}
