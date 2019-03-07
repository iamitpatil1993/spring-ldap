/**
 * 
 */
package com.example.spring.ldap.basic.attributemappter;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.List;

import javax.naming.directory.Attributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Component;

/**Bean shows use of 
 * <p>AtributeMapper: To map Ldap Attributes to custom object</p>
 * <p>Basic ldap search operation using LdapTemplate</p>
 *
 * @author amit
 *
 */

@Component
public class LdapBasicPersonRepo implements PersonRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(LdapBasicPersonRepo.class);

	private LdapTemplate ldapTemplate;

	/**
	 * Always prefer using setter/constructor based auto-wiring
	 */
	public LdapBasicPersonRepo(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	/*
	 * Returns names of all persons in LDAP DIT
	 */
	@Override
	public List<String> getAllPersonNames() {
		return ldapTemplate.search(query().where("objectclass").is("organizationalPerson"),
				(Attributes attributes) -> attributes.get("cn") != null ? attributes.get("cn").toString() : null);
	}

	/*
	 * Returns Person objects of all persons in LDAP DIT
	 */
	@Override
	public List<Person> getAllPersons() {
		return ldapTemplate.search(query().where("objectclass").is("organizationalPerson"), (Attributes attributes) -> {
			Person person = new Person();
			person.setLastName(attributes.get("sn") != null ? attributes.get("sn").toString() : null);
			return person;
		});
	}
}
