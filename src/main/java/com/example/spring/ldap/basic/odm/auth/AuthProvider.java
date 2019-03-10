/**
 * 
 */
package com.example.spring.ldap.basic.odm.auth;

/**
 * This interface contract for user's basic authentication.
 *
 * @author amit
 *
 */
public interface AuthProvider {

	public boolean authenticate(final String username, final String password);
}
