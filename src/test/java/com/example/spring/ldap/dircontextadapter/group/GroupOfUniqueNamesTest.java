package com.example.spring.ldap.dircontextadapter.group;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.spring.ldap.BaseTest;

@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroupOfUniqueNamesTest extends BaseTest {
	
	@Autowired
	GroupRepo groupRepo;
	
	private static String groupDn = "cn=user,ou=groups";
	
	@Test
	public void testAddMemeberToGroup() {
		// given
		String memberDn = "cn=amipatil,ou=engineering,ou=praxify";
		
		// when
		groupRepo.addMemeberToGroup(groupDn, memberDn);
		
		// then
		assertTrue(groupRepo.isExistsInGroup(groupDn, memberDn));
	}
	
	@Test
	public void testremoveMemberFromGroup() {
		// given
		String memberDn = "cn=amipatil,ou=engineering,ou=praxify";
		
		// when
		groupRepo.removeMemberFromGroup(groupDn, memberDn);
		
		// then
		assertFalse(groupRepo.isExistsInGroup(groupDn, memberDn));
	}

}
