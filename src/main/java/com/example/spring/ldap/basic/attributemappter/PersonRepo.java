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
	
	Optional<Person> findByLastName(final String lastName);
	
	String create(Person person);
	
	void removeById(final String dn);
}
