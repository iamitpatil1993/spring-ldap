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
	
	private String phone;

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
