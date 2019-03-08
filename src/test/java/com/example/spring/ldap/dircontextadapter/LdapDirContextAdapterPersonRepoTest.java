/**
 * 
 */
package com.example.spring.ldap.dircontextadapter;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.spring.ldap.AbstractPersonRepoTest;
import com.example.spring.ldap.basic.attributemappter.PersonRepo;

/**
 * @author amit
 *
 */

public class LdapDirContextAdapterPersonRepoTest extends AbstractPersonRepoTest {

	@Autowired
	@Qualifier("dirContextAdapter")
	private PersonRepo personRepo;

	@Before
	public void beforeClass() {
		super.setPersonRepo(personRepo);
	}

}
