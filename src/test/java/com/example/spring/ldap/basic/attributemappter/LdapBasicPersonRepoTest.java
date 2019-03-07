package com.example.spring.ldap.basic.attributemappter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
}
