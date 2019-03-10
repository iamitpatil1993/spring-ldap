/**
 * 
 */
package com.example.spring.ldap.basic.odm.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Component;

import com.example.spring.ldap.basic.attributemappter.Person;
import com.example.spring.ldap.basic.attributemappter.PersonRepo;
import com.example.spring.ldap.basic.odm.mapping.OrganizationalPerson;

/**
 * @author amit
 *
 */
@Component
@Qualifier("odm")
public class LdapOdmPersonRepo implements PersonRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(LdapOdmPersonRepo.class);

	private LdapTemplate ldapTemplate;

	public LdapOdmPersonRepo(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	@Override
	public List<String> getAllPersonNames() {
		return ldapTemplate.findAll(OrganizationalPerson.class).stream().map(OrganizationalPerson::getLastName).collect(Collectors.toList());
	}

	@Override
	public List<Person> getAllPersons() {
		// This operation will find all entries in LDAP DIT with matching object class as mentioned at class level @Entry annotation of OrganizationalPerson odm class.
		List<OrganizationalPerson> organizationalPersons = ldapTemplate.findAll(OrganizationalPerson.class);
		return organizationalPersons.stream().map(this::buildLdapPersonMapping).collect(Collectors.toList());
	}

	@Override
	public Optional<Person> finById(String dn) {
		try {
			// this read operation will read entry dn into dn field of class and also read dn attribute from dn into username field of class which is marked as dn attriute,.
			OrganizationalPerson organizationalPerson = ldapTemplate.findByDn(LdapUtils.newLdapName(dn), OrganizationalPerson.class);
			Person person = buildLdapPersonMapping(organizationalPerson);
			return Optional.of(person);
		} catch (NameNotFoundException e) {
			LOGGER.info("Person not found by dn :: {}", dn);
		}
		return Optional.empty();
	}

	@Override
	public Optional<Person> findByLastName(String lastName) {
		// Building filters
		AndFilter searchFilter = new AndFilter();
		searchFilter.and(new EqualsFilter("sn", lastName));

		// building search controls
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		searchControls.setReturningAttributes( new String[] {"cn", "sn", "telephoneNumber", "postalCode"});
		searchControls.setCountLimit(1);

		// execute
		List<OrganizationalPerson> organizationalPersons = ldapTemplate.find(LdapUtils.newLdapName("ou=engineering, ou=praxify"), searchFilter, searchControls, OrganizationalPerson.class);
		if (organizationalPersons != null && !organizationalPersons.isEmpty()) {
			return Optional.of(buildLdapPersonMapping(organizationalPersons.get(0)));
		}
		return Optional.empty();
	}

	@Override
	public String create(Person person) {
		OrganizationalPerson organizationalPerson = buildLdapPersonMapping(person);
		ldapTemplate.create(organizationalPerson);
		return organizationalPerson.getDn().toString();
	}

	@Override
	public void removeById(String dn) {
		// actuallt ODM not suits here, using other api is much cleaner than this, for example,
		//ldapTemplate.unbind(dn); 
		OrganizationalPerson organizationalPerson = new OrganizationalPerson();
		organizationalPerson.setDn(LdapUtils.newLdapName(dn));
		ldapTemplate.delete(organizationalPerson);
	}

	/**
	 * [Important] NOTE: Update operation has same effect we have in case of ORM
	 * framework like hibernate, it updates specified values and set all other non
	 * specified values to null in persistence store (Relational database for ORM)
	 * 
	 * Here, ODM also does same, it updates value of specified attribute, but set value of all other unspecified values to null, which we never want to happen.
	 * So, in order to update single field, we need entire ODM object fully populated with existing values.
	 */
	@Override
	public void setLastName(Person person) {
		// this below commented code will set all other fields to null except one which we are updating i.e lastName (sn)
		/*
		OrganizationalPerson organizationalPerson = new OrganizationalPerson();
		organizationalPerson.setDn(buildDn(person));
		organizationalPerson.setLastName(person.getLastName());
		organizationalPerson.setUsername(person.getUsername()); // we need to specify or populate all dn attribute in addition to one which we actually need to update, otherwise update operation will fail.

		ldapTemplate.update(organizationalPerson);
		 */

		// So, we need fully populated ODM object, in order to avoid above effect, so we will need to first get object and then update required field and then call update API.
		// this will also populate dn attributes of ODM object, so we do not need to manually set them as a case in above commented code.
		Name dn = buildDn(person);
		OrganizationalPerson organizationalPerson = ldapTemplate.findByDn(dn, OrganizationalPerson.class);
		if (organizationalPerson != null) {
			organizationalPerson.setLastName(person.getLastName());
			ldapTemplate.update(organizationalPerson);
		}
	}

	@Override
	public void addPostalCode(Person person) {

		// also here we need to first get the fully populated object from ldap and then need to perform update operations, to avoid other fields get set to null value.
		Name dn = buildDn(person);
		OrganizationalPerson organizationalPerson = ldapTemplate.findByDn(dn, OrganizationalPerson.class);
		if (organizationalPerson != null && person.getPostalCodes() != null) {
			person.getPostalCodes().stream().forEach(organizationalPerson::addPostalCode);
			ldapTemplate.update(organizationalPerson);
		}
	}

	private OrganizationalPerson buildLdapPersonMapping(Person person) {
		Name dn = buildDn(person);
		OrganizationalPerson organizationalPerson = new OrganizationalPerson();
		organizationalPerson.setDn(dn);
		organizationalPerson.setLastName(person.getLastName());
		organizationalPerson.setPhone(person.getPhone());
		organizationalPerson.setPostalCodes(person.getPostalCodes());
		return organizationalPerson;
	}

	private Name buildDn(Person person) {
		return LdapNameBuilder.newInstance("ou=engineering,ou=praxify").add("cn", person.getUsername()).build();
	}

	private Person buildLdapPersonMapping(OrganizationalPerson organizationalPerson) {
		Name dn = organizationalPerson.getDn();
		Person person = new Person();
		person.setUsername(dn.toString());
		person.setLastName(organizationalPerson.getLastName());
		person.setPhone(organizationalPerson.getPhone());
		person.setPostalCodes(organizationalPerson.getPostalCodes());
		return person;
	}

}
