/**
 * 
 */
package com.example.spring.ldap.basic.odm.auth;

import javax.naming.Name;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Component;

/**
 * @author amit
 *
 */

@Component
public class LdapBasicAuthProvider implements AuthProvider {

	private LdapTemplate ldapTemplate;

	@Autowired
	public LdapBasicAuthProvider(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	/**
	 * <p>Authenticate user against ldap server. Uses basic/bind ldap authentication.</p>
	 * 
	 * <p>Since this only provides basic authentication and no capabilities for authorization, we should consider using Spring Security using ldap
	 * if we need more than basic authentication demonstrated below. </p>
	 * 
	 * <p>NOTE: It does not throws any exception provided user not found in dit. It considers that case as invalid credentials</p>
	 * 
	 * @param password This password needs to be plain text password, irrespective of HASH method used to store password in ldap server.
	 * @return true if successfully authenticated otherwise false. 
	 * 
	 */
	@Override
	public boolean authenticate(String username, String password) {

		// build filter to determine dn of user to be authenticate
		Filter filter = new AndFilter().and(new EqualsFilter("objectclass", "organizationalPerson"))
				.and(new EqualsFilter("cn", username));
		
		// base, where to find user.
		Name base = LdapUtils.newLdapName("ou=praxify");

		// spring first finds dn using other API, and then uses dn of user and password
		// to authenticate user using below api
		// we can directly use below method to authenticatre user if we already have dn
		// of user.
		// ldapTemplate.getContextSource().getContext(dn, password);

		return ldapTemplate.authenticate(base, filter.toString(), password);
	}

}
