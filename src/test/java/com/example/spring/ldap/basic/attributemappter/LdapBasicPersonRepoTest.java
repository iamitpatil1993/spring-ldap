package com.example.spring.ldap.basic.attributemappter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.naming.Name;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.InvalidNameException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.spring.ldap.BaseTest;

@RunWith(SpringJUnit4ClassRunner.class)
public class LdapBasicPersonRepoTest extends BaseTest {

	@Autowired
	private LdapBasicPersonRepo personRepo;

	@Test
	public void getAllPersonNamesTest() {
		// when
		List<String> personNames = personRepo.getAllPersonNames();

		// then
		assertNotNull(personNames);
		assertThat(personNames.isEmpty(), is(false));
	}
	
	@Test
	public void getAllPersonsTest() {
		// when
		List<Person> persons = personRepo.getAllPersons();

		// then
		assertNotNull(persons);
		assertThat(persons.isEmpty(), is(false));
	}
	
	@Test
	public void finByIdWithIdFoundTest() {

		// NOTE: we have specified dn relative to suffix, suffix is omitted from dn,
		// since we already have configured 'base' in context-source.
		// So, all our queries will/should be relative to base (dc=example,dc=com).
		// given
		final String personDn = "cn=amipatil,ou=engineering,ou=praxify";

		// when
		Optional<Person> person = personRepo.finById(personDn);

		// then
		assertNotNull(person);
		assertThat(person.isPresent(), is(true));
	}
	
	@Test
	public void finByIdWithIdNotFoundTest() {

		final String personDn = "cn=sam,ou=engineering,ou=praxify";

		// when
		Optional<Person> person = personRepo.finById(personDn);

		// then
		assertNotNull(person);
		assertThat(person.isPresent(), is(false));
	}
	
	@Test(expected = InvalidNameException.class)
	public void finByIdWithInvalidDnTest() {

		final String personDn = "casdn=sam,ou=engineering,ou=praxify";

		// when
		Optional<Person> person = personRepo.finById(personDn);

		// then
		assertNotNull(person);
		assertThat(person.isPresent(), is(false));
	}
	
	@Test
	public void findByLastNameWithFoundByNameTest() {
		// given
		String lastName = "patil";
		
		// when
		Optional<Person> person = personRepo.findByLastName(lastName);
		
		// then
		assertNotNull(person);
		assertThat(person.isPresent(), is(true));
		assertNotNull(person.get().getLastName());
		assertTrue(person.get().getLastName().equalsIgnoreCase(lastName));
	}
	
	@Test
	public void findByLastNameWithNotFoundByNameTest() {
		// given
		String lastName = "asd";
		
		// when
		Optional<Person> person = personRepo.findByLastName(lastName);
		
		// then
		assertNotNull(person);
		assertThat(person.isPresent(), is(false));
	}
	
	@Test
	public void buildDnTest() throws Exception {
		// given
		Person person = new Person();
		person.setUsername("amipatil");
		
		// when
		Name name = Whitebox.invokeMethod(personRepo, "buildDn", person);
		
		// then
		assertNotNull(name);
		assertEquals("cn=amipatil,ou=engineering,ou=praxify", name.toString());
	}
	
	@Test
	public void getUsernmeFromDnTest() throws Exception {
		// given
		Person person = new Person();
		person.setUsername("amipatil");
		Name name = Whitebox.invokeMethod(personRepo, "buildDn", person);
		
		// when
		String personUsername = Whitebox.invokeMethod(personRepo, "getUsernmeFromDn", name);
		
		// then
		assertNotNull(personUsername);
		assertEquals(person.getUsername(), personUsername);
	}
}
