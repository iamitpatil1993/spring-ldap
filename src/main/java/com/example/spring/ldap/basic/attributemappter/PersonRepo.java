/**
 * 
 */
package com.example.spring.ldap.basic.attributemappter;

import java.util.List;

/**
 * @author amit
 *
 */
public interface PersonRepo {
	List<String> getAllPersonNames();

	List<Person> getAllPersons();
}
