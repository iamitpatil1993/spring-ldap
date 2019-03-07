/**
 * 
 */
package com.example.spring.ldap.basic.attributemappter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author amit
 *
 */
public class Person {

	private String lastName;
	
	private String username;
	
	private String phone;
	
	private List<String> postalCodes;

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

	public List<String> getPostalCodes() {
		return postalCodes;
	}

	public void setPostalCodes(List<String> postalCodes) {
		this.postalCodes = postalCodes;
	}

	public void addPostalCodes(String postalCode) {
		if (postalCodes == null) {
			this.postalCodes = new ArrayList<>();
		}
		this.postalCodes.add(postalCode);
	}
}
