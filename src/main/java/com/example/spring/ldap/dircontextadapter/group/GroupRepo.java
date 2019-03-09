/**
 * 
 */
package com.example.spring.ldap.dircontextadapter.group;

/**
 * Provides CRUD operations in LDAP group.
 * 
 * @author amit
 *
 */
public interface GroupRepo {

	void addMemeberToGroup(final String groupDn, final String memeberDn);
	
	void removeMemberFromGroup(final String groupDn, final String memberDn);
	
	boolean isExistsInGroup(final String groupDn, final String dn);
}
