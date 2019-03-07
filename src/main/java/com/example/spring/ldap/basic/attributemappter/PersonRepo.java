/**
 * 
 */
package com.example.spring.ldap.basic.attributemappter;

import java.util.List;
import java.util.Optional;

/**
 * @author amit
 *
 */
public interface PersonRepo {
	List<String> getAllPersonNames();

	List<Person> getAllPersons();
	
	Optional<Person> finById(final String dn);
}