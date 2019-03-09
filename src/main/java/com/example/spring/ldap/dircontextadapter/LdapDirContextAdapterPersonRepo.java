/**
 * 
 */
package com.example.spring.ldap.dircontextadapter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.naming.Name;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Component;

import com.example.spring.ldap.basic.attributemappter.Person;
import com.example.spring.ldap.basic.attributemappter.PersonRepo;

/**
 * This is another implementation of PersonRepo interface, it uses ldap to
 * maintain user details. It uses DirContextAdapter, ContextMapper and many
 * other newer APIs to do CRUD operations.
 *
 * @author amit
 *
 */

@Component
@Qualifier("dirContextAdapter")
public class LdapDirContextAdapterPersonRepo implements PersonRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(LdapDirContextAdapterPersonRepo.class);

	private LdapTemplate ldapTemplate;

	@Autowired
	public LdapDirContextAdapterPersonRepo(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	@Override
	public List<String> getAllPersonNames() {
		return ldapTemplate.search(LdapQueryBuilder.query().where("objectclass").is("organizationalPerson"),
				new PersonAttributeMapper()).stream().map(Person::getUsername).collect(Collectors.toList());
	}

	@Override
	public List<Person> getAllPersons() {
		return ldapTemplate.search(LdapQueryBuilder.query().where("objectclass").is("organizationalPerson"),
				new PersonAttributeMapper());
	}

	@Override
	public Optional<Person> finById(String dn) {
		try {
			return Optional.ofNullable(ldapTemplate.lookup(dn, new PersonAttributeMapper()));
		} catch (NameNotFoundException e) {
			LOGGER.info("Ldap entry not found by dn :: {}", dn);
		}
		return Optional.empty();
	}

	@Override
	public Optional<Person> findByLastName(String lastName) {
		LdapQuery query = LdapQueryBuilder.query()
				.base("ou=engineering, ou=praxify")
				.searchScope(SearchScope.ONELEVEL)
				.attributes("cn", "sn", "telephoneNumber", "postalCode")
				.countLimit(1).where("objectclass")
				.is("organizationalPerson")
				.and("sn").is(lastName);

		List<Person> persons = ldapTemplate.search(query, new PersonAttributeMapper());
		if (!persons.isEmpty()) {
			return Optional.of(persons.get(0));
		}
		return Optional.empty();
	}

	@Override
	public String create(Person person) {
		Name dn = buildDn(person);
		DirContextAdapter adapter = new DirContextAdapter(dn);

		// context adapter makes creating multi-value attribute easy.
		adapter.setAttributeValues("objectclass", new String[] { "organizationalPerson", "top" });
		adapter.setAttributeValue("cn", person.getUsername());
		adapter.setAttributeValue("sn", person.getLastName());
		adapter.setAttributeValue("telephoneNumber", person.getPhone());
		
		try {
			ldapTemplate.bind(adapter);
		} catch (NameAlreadyBoundException e) {
			LOGGER.info("Person already exists by dn :: {}", dn);
		}
		return dn.toString();
	}

	@Override
	public void removeById(String dn) {
		ldapTemplate.unbind(dn);
	}

	@Override
	public void setLastName(Person person) {
		Name dn = buildDn(person);

		// first we need to lookup existing entry by dn.
		DirContextOperations contextOperations = ldapTemplate.lookupContext(dn);
		contextOperations.setAttributeValue("sn", person.getLastName());

		ldapTemplate.modifyAttributes(contextOperations);
	}

	@Override
	public void addPostalCode(Person person) {
		Name dn = buildDn(person);

		DirContextOperations dirContextOperations = ldapTemplate.lookupContext(dn);
		person.getPostalCodes().stream()
				.forEach(postalCode -> dirContextOperations.addAttributeValue("postalCode", postalCode));

		ldapTemplate.modifyAttributes(dirContextOperations);
	}

	/**
	 * Builds dn for person. Currently hard coded, RDNs for demo.
	 * 
	 * @param person
	 * @return dn of person
	 */
	private Name buildDn(Person person) {
		// creates dn relative to base configured in context-source. Uses newInstance()
		// instead of newInstance(String base)
		// add RDNs in reverse order, i.e from RootDSE to entry.
		return LdapNameBuilder.newInstance().add("ou", "praxify").add("ou", "engineering")
				.add("cn", person.getUsername()).build();
	}

	/**
	 * Using ldapUtils methods we can parse and get RDNs from DN
	 * 
	 * @param dn
	 * @return
	 */
	private String getUsernmeFromDn(final Name dn) {
		return LdapUtils.getStringValue(dn, "cn");
	}

	/**
	 * Person Attribite mapper using ContextMapper
	 */
	public class PersonAttributeMapper implements ContextMapper<Person> {

		/**
		 * Spring creates DirContextAdapter object, for each entry in ldap. It maps dn
		 * and all attributes to DirContextAdapter. DirContextAdapter implements
		 * DirContextOperations, so we can refer it using interface.
		 */
		@Override
		public Person mapFromContext(Object ctx) throws NamingException {
			DirContextOperations contextOperations = (DirContextAdapter) ctx;

			Person person = new Person();
			person.setUsername(contextOperations.getStringAttribute("cn"));
			person.setLastName(contextOperations.getStringAttribute("sn"));
			person.setPhone(contextOperations.getStringAttribute("telephoneNumber"));

			// this is how we can map multi-value attributes using DirContextAdapter, single
			// line implementation. It internally does what we did in LdapBasicPersonRepo
			// person mapper.
			// It returns null if attribute not defined for entry.
			if (contextOperations.getStringAttributes("postalCode") != null) {
				person.setPostalCodes(Arrays.asList(contextOperations.getStringAttributes("postalCode")));
			}
			return person;
		}
	}
}
