/**
 * 
 */
package com.example.spring.ldap.basic.attributemappter;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.List;
import java.util.Optional;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
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
		return ldapTemplate.search(query().where("objectclass").is("organizationalPerson"), new PersonAttributeMapper());
	}

	/**
	 * It gets Person by it's unique dn.
	 * <p>
	 * Use lookup method/operation on LdapTemplate if, dn of ldap entry is alreay
	 * known. It is like find by Id in ORM
	 * </p>
	 */
	@Override
	public Optional<Person> finById(String dn) {
		// NOTE: we could have passed Name instance to lookup method as new
		// LdapName(dn), but it throws checked exception, so
		// better passing string and spring will throw runtime exception if passed dn is
		// invalid.
		if (dn != null & !dn.isEmpty()) {
			// If entry not found by dn, it throws exception, so we need to handle runtime
			// exception, if we wish to handle entry not found.
			// Otherwise, it will result into runtime exceptio, and will call entire method
			// to fail.
			try {
				Person person = ldapTemplate.lookup(dn, new PersonAttributeMapper());
				return Optional.ofNullable(person);
			} catch (NameNotFoundException e) {
				LOGGER.info("Ldap entry not found by dn :: {}", dn);
			}
		}
		return Optional.empty();
	}

	/**
	 * Attribute mapper for Person
	 * @author amit
	 *
	 */
	public class PersonAttributeMapper implements AttributesMapper<Person> {

		@Override
		public Person mapFromAttributes(Attributes attributes) throws NamingException {
			Person person = new Person();
			person.setLastName(attributes.get("sn") != null ? attributes.get("sn").toString() : null);
			return person;
		}
	}
}
