/**
 * 
 */
package com.example.spring.ldap.basic.attributemappter;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.List;
import java.util.Optional;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
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
			person.setLastName(attributes.get("sn") != null ? attributes.get("sn").get().toString() : null);
			return person;
		}
	}

	/**
	 * Demonstrates advance search operation
	 */
	@Override
	public Optional<Person> findByLastName(String lastName) {
		if (lastName != null) {
			LdapQuery query = query()
					.base("ou=engineering,ou=praxify")
					.attributes("sn", "cn")
					.searchScope(SearchScope.ONELEVEL)
					.countLimit(1)
					.where("objectclass").is("organizationalPerson")
					.and("sn").is(lastName);

			// NOTE: In case of search, if no resoult found it do not throw excepion rather, only return empty list.
			List<Person> persons = ldapTemplate.search(query, new PersonAttributeMapper());
			if (persons != null && !persons.isEmpty()) {
				return Optional.of(persons.get(0));
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Builds dn for person.
	 * Currently hard coded, RDNs for demo.
	 * @param person
	 * @return dn of person
	 */
	private Name buildDn(Person person) {
		// creates dn relative to base configured in context-source. Uses newInstance() instead of newInstance(String base)
		// add RDNs in reverse order, i.e from RootDSE to entry.
		return LdapNameBuilder.newInstance()
				.add("ou", "praxify")
				.add("ou", "engineering")
				.add("cn", person.getUsername())
				.build();
	}
	
	/**
	 * Using ldapUtils methods we can parse and get RDNs from DN
	 * @param dn
	 * @return
	 */
	private String getUsernmeFromDn(final Name dn) {
		return LdapUtils.getStringValue(dn, "cn");
	}

	/**
	 * Creates person. ('Bind' in JNDI terms, which actually term used to refer Authentication in LDAP)
	 * Uses bind operation
	 */
	@Override
	public String create(Person person) {
		Name dn = buildDn(person);
		Attributes attributes = buildAttributes(person);

		try {
			ldapTemplate.bind(dn, null, attributes);
		} catch (NameAlreadyBoundException e) {
			LOGGER.info("Person already exists by dn :: {}", dn);
		}
		return dn.toString();
	}

	/**
	 * Builds attributes from Person
	 * @param person
	 * @return
	 */
	private Attributes buildAttributes(Person person) {
		Attributes attributes = new BasicAttributes();
	
		Attribute objectClass = new BasicAttribute("objectclass");
		objectClass.add("organizationalPerson");
		objectClass.add("top");
		
		attributes.put(objectClass);
		attributes.put("cn", person.getUsername());
		attributes.put("sn", person.getLastName());
		attributes.put("telephoneNumber", person.getPhone());
		
		return attributes;
	}

	/**
	 * Removes Person entry by dn
	 * Uses JNDI unbind operation to remove entry.
	 * 
	 * unbind operation does not throw any error if entry does not exists by provided dn, it silently ignores.
	 */
	@Override
	public void removeById(String dn) {
		// NOTE: In order to successfully perform this operaton on entry with provided
		// dn, entry MUST not have any childs.
		// If entry have child, then use unbind(dn, boolean recusrsive) instead. Second boolean
		// parameter used to decide whether to remove all child entries as well
		// recursively. If we set it's value to recursive = false, and if entry has child entries then operation will fail.
		ldapTemplate.unbind(dn);
	}
}
