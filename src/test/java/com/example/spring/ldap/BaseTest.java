package com.example.spring.ldap;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.GenericXmlContextLoader;

/**
 * @author amit
 *
 */
@ContextConfiguration(locations = "classpath:config/app-context.xml", loader = GenericXmlContextLoader.class)
public class BaseTest {

	// Nothing to do here
}
