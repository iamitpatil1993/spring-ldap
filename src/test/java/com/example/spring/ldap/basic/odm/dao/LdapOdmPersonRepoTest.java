/**
 * 
 */
package com.example.spring.ldap.basic.odm.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LdapOdmPersonRepoTest extends AbstractPersonRepoTest {

	private static String username;
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
		username = UUID.randomUUID().toString().concat("-odm");
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
	
	@Test
	@Override
	public void removeByIdTest() {
		// given
		String personDn =  new StringBuilder("cn=").append(username).append(",ou=engineering,ou=praxify").toString();

		// when
		personRepo.removeById(personDn);

		// then
		Optional<Person> person = personRepo.finById(personDn);
		assertNotNull(person);
		assertThat(person.isPresent(), is(false));
	}
}
