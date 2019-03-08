package com.example.spring.ldap.basic.attributemappter;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.spring.ldap.AbstractPersonRepoTest;

public class LdapBasicPersonRepoTest extends AbstractPersonRepoTest {

	@Autowired
	@Qualifier("basic")
	protected PersonRepo personRepo;
	
	@Before
	public void beforeClass() {
		super.setPersonRepo(personRepo);
	}
}
