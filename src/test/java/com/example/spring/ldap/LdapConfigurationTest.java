package com.example.spring.ldap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This test ensure ldap configuration is correct and connectivity to configured ldap server exists.
 * @author amipatil
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class LdapConfigurationTest extends BaseTest{

	private static final Logger LOGGER = LoggerFactory.getLogger(LdapConfigurationTest.class);

	@Autowired
	private LdapTemplate ldapTemplate;

	@Test
	public void configurationTest() {
		assertNotNull(ldapTemplate);
	}

	@Test
	public void sampleQueryTest() {
		// when
		List<String> domainComponents = ldapTemplate.search(LdapQueryBuilder.query().where("objectclass").is("top"),
				new AttributesMapper<String>() {
			@Override
			public String mapFromAttributes(Attributes attributes) throws NamingException {
				return attributes.get("objectclass").get().toString();
			}
		});
		// then
		assertNotNull(domainComponents);
		assertTrue(domainComponents.size() > 0);

		domainComponents.stream().forEach(LOGGER::info);
	}
}
