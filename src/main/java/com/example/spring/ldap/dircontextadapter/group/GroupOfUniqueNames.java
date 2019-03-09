/**
 * 
 */
package com.example.spring.ldap.dircontextadapter.group;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.Name;
import javax.naming.ldap.LdapName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.BaseLdapNameAware;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Component;

import com.example.spring.ldap.DnNotExistsException;

/**
 * @author amit
 *
 */
@Component
public class GroupOfUniqueNames implements GroupRepo, BaseLdapNameAware {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GroupOfUniqueNames.class);
	
	private LdapTemplate ldapTemplate;
	
	private Name ldapBase;
	
	@Autowired
	public GroupOfUniqueNames(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	/**
	 * adds memeber to existing group.
	 * 
	 * spring-ldap or ldap itself does not checks whether provided memberDn is already exists in DIT somewhere, it just adds it as long is dn is syntactically correct.
	 * 
	 * If try to add same member multiple times, it will not give any error and will silently ignore operation.
	 */
	@Override
	public void addMemeberToGroup(String groupDn, String memeberDn) {
		DirContextOperations dirContextOperations = ldapTemplate.lookupContext(LdapUtils.newLdapName(groupDn));	
		if (dirContextOperations != null) {
			memeberDn = buildAbsouluteDb(memeberDn);
			dirContextOperations.addAttributeValue("uniqueMember", LdapUtils.newLdapName(memeberDn));
			ldapTemplate.modifyAttributes(dirContextOperations);
		} else {
			throw new DnNotExistsException("Group not found by dn :: " + groupDn);
		}
	}

	/**
	 * Remove member from grup.
	 * If provided dbn is not part of group, it does not gives any error, and simply silently ignores the operation.
	 */
	@Override
	public void removeMemberFromGroup(String groupDn, String memeberDn) {
		DirContextOperations dirContextOperations = ldapTemplate.lookupContext(LdapUtils.newLdapName(groupDn));	
		if (dirContextOperations != null) {
			memeberDn = buildAbsouluteDb(memeberDn);
			dirContextOperations.removeAttributeValue("uniqueMember", LdapUtils.newLdapName(memeberDn));
			ldapTemplate.modifyAttributes(dirContextOperations);
		} else {
			throw new DnNotExistsException("Group not found by dn :: " + groupDn);
		}
	}

	@Override
	public boolean isExistsInGroup(String groupDn, String memeberDn) {
		DirContextOperations contextOperations = ldapTemplate.lookupContext(LdapUtils.newLdapName(groupDn));
		Object[] members = contextOperations.getObjectAttributes("uniqueMember");
		
		Set<Name> memberDns = Arrays.asList(members).stream().map(Object::toString).map(LdapUtils::newLdapName).collect(Collectors.toSet());
		
		memeberDn = buildAbsouluteDb(memeberDn);
		
		return memberDns.contains(LdapUtils.newLdapName(memeberDn));
	}

	private String buildAbsouluteDb(String memeberDn) {
		if (!memeberDn.toLowerCase().endsWith(ldapBase.toString().toLowerCase())) {
			memeberDn = memeberDn.concat(",").concat(ldapBase.toString());
		}
		return memeberDn;
	}

	@Override
	public void setBaseLdapPath(LdapName baseLdapPath) {
		LOGGER.info("setting group base dn :: {}", baseLdapPath);
		this.ldapBase = baseLdapPath;
	}
}
