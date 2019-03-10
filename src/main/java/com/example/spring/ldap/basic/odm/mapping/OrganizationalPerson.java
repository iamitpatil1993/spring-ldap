/**
 * 
 */
package com.example.spring.ldap.basic.odm.mapping;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

/**
 * Represents DIT entry with organizationalPerson, top object classes
 * 
 * @author amit
 *
 */

// We can specify base dn as well, but we won't, because doing so will make position of entries created by this class fixed in DIT hierarchy.
@Entry(objectClasses = { "organizationalPerson", "top" })
public class OrganizationalPerson {

	// private type of this property must be javax.naming.Name or it's sub-class
	@Id
	private Name dn;

	// we can specify index property of this annotation, but we won't, because it
	// will then create dn of entry based in this attribute and index relative to
	// base dn specific in @Entry annotation.
	// we want to manually create dn and pass it to this class, so that this class
	// remains generic and can be used to represent Person at any position in DIT.
	@DnAttribute(value = "cn")
	@Attribute(name = "cn")
	private String username;

	@Attribute(name = "sn")
	private String lastName;

	@Attribute(name = "postalCode")
	private List<String> postalCodes = new ArrayList<>();

	@Attribute(name = "telephoneNumber")
	private String phone;

	public Name getDn() {
		return dn;
	}

	public void setDn(Name dn) {
		this.dn = dn;
	}

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

	public List<String> getPostalCodes() {
		return postalCodes;
	}

	public void setPostalCodes(List<String> postalCodes) {
		this.postalCodes = postalCodes;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public void addPostalCode(final String postalCode) {
		this.postalCodes.add(postalCode);
	}

	public void removePostalCode(final String postalCode) {
		this.postalCodes.remove(postalCode);
	}
}
