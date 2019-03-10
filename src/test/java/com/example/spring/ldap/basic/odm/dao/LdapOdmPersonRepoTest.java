/**
 * 
 */
package com.example.spring.ldap.basic.odm.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.spring.ldap.AbstractPersonRepoTest;
import com.example.spring.ldap.basic.attributemappter.Person;
import com.example.spring.ldap.basic.attributemappter.PersonRepo;

/**
 * @author amit
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class LdapOdmPersonRepoTest extends AbstractPersonRepoTest {

	@Autowired
	@Qualifier("odm")
	private PersonRepo personRepo;
	
	@Before
	public void beforeClass() {
		super.setPersonRepo(personRepo);
	}
	
	/**
	 * Overriding ths one, to avoid same user conflict.
	 * Test method for {@link com.example.spring.ldap.basic.odm.dao.LdapOdmPersonRepo#create(com.example.spring.ldap.basic.attributemappter.Person)}.
	 */
	@Test
	@Override
	public void createTest() {
		// given
		String username = UUID.randomUUID().toString().concat("-odm");
		Person person = new Person();
		person.setUsername(username);
		person.setLastName("Porter");
		person.setPhone("23431234545");
		person.setPostalCodes(Arrays.asList("411033", "12312123"));

		// when
		String personDn = personRepo.create(person);

		// then
		assertNotNull(personDn);
		String expectedDn = new StringBuilder("cn=").append(username).append(",ou=engineering,ou=praxify").toString();
		assertEquals(expectedDn, personDn);
	}
	
	@Test
	@Override
	public void getUsernmeFromDnTest() {
		// Nothing to do here
	}
}
