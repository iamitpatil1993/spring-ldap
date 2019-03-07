/**
 * 
 */
package com.example.spring.ldap.basic.attributemappter;

/**
 * @author amit
 *
 */
public class Person {

	private String lastName;
	
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
