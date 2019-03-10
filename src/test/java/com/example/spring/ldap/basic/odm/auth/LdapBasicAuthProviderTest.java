/**
 * 
 */
package com.example.spring.ldap.basic.odm.auth;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.spring.ldap.BaseTest;

/**
 * @author amit
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class LdapBasicAuthProviderTest extends BaseTest {

	@Autowired
	private AuthProvider authProvider;
	
	/**
	 * Test method for
	 * {@link com.example.spring.ldap.basic.odm.auth.LdapBasicAuthProvider#authenticate(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAuthenticateWithValidCredentials() {
		// given
		String username = "amipatil";
		String password = "asdf1234";
		
		/// when
		boolean isAuthenticated = authProvider.authenticate(username, password);
		
		// then
		assertTrue(isAuthenticated);
	}
	
	/**
	 * Test method for
	 * {@link com.example.spring.ldap.basic.odm.auth.LdapBasicAuthProvider#authenticate(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAuthenticateWithInvalidCredentials() {
		// given
		String username = "amipatil";
		String password = "asdadadasdasd";
		
		/// when
		boolean isAuthenticated = authProvider.authenticate(username, password);
		
		// then
		assertFalse(isAuthenticated);
	}

}
