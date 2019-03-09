/**
 * 
 */
package com.example.spring.ldap;

/**
 * @author amit
 *
 */
public class DnNotExistsException extends RuntimeException {

	private static final long serialVersionUID = -7962872829554643040L;

	public DnNotExistsException() {
		super();
	}
	
	public DnNotExistsException(String message) {
		super(message);
	}
}
