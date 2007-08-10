/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.dataaccess.backend.impl;

import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.tarent.commons.dataaccess.DataAccess;
import de.tarent.commons.dataaccess.DataAccessConfiguration;
import de.tarent.commons.dataaccess.Q;
import de.tarent.commons.dataaccess.config.ConfigParser;
import de.tarent.commons.dataaccess.data.AttributeSet;
import de.tarent.commons.dataaccess.data.AttributeSetList;
import de.tarent.commons.dataaccess.test.Person;
import junit.framework.TestCase;

public class LdapDataAccessBackendTest extends TestCase {
	/*protected void setUp() throws Exception {
		super.setUp();
		
		InputStream inputStream = LdapDataAccessBackendTest.class.
				getResourceAsStream("ldap-test-dataaccess.xml");
		
		new DataAccessConfiguration().parseXmlConfig(inputStream);
	}*/

	/*public void testLdapConfigOutput() {
		LdapDataAccessBackend ldapDataAccessBackend = new LdapDataAccessBackend();
		ldapDataAccessBackend.setBaseUrl("ldap://ldap.tarent.de/");
		ldapDataAccessBackend.setBaseDn("ou=tarent GmbH,dc=tarent,dc=de");
		ldapDataAccessBackend.internalStartup();
		
		new ConfigParser().formatBackend("ldap-tarent", ldapDataAccessBackend);
	}*/

	/*public void testLdapBackend() {
		DataAccess dataAccess = new DataAccess("ldap-tarent");
		LdapDataAccessBackend backend = (LdapDataAccessBackend) dataAccess.getBackend();
		
		assertEquals(0, backend.getRestrainedEntries());
		assertEquals(0, backend.getRestrainedStoreEntries());
		assertEquals(0, backend.getRestrainedDeleteEntries());
		
		AttributeSetList attributeSetList = (AttributeSetList) backend.executeQuery(null,
				"(|(uid=smanck)(uid=mklein)(uid=cjerol))");
		
		List list = attributeSetList.asList();
		Collections.sort(list);
		
		assertEquals("Missing people! What terrible things are happen here?", 3, attributeSetList.size());
		
		AttributeSet attributeSet1 = (AttributeSet) list.get(0);
		AttributeSet attributeSet2 = (AttributeSet) list.get(1);
		AttributeSet attributeSet3 = (AttributeSet) list.get(2);

		assertEquals("Sebastian Mancke", attributeSet1.getAttribute("cn"));
		assertEquals("s.mancke@tarent.de", attributeSet1.getAttribute("mail"));
		assertEquals("Sebastian", attributeSet1.getAttribute("givenName"));
		assertEquals("Mancke", attributeSet1.getAttribute("sn"));

		assertEquals("Michael Kleinhenz", attributeSet2.getAttribute("cn"));
		assertEquals("m.kleinhenz@tarent.de", attributeSet2.getAttribute("mail"));
		assertEquals("Michael", attributeSet2.getAttribute("givenName"));
		assertEquals("Kleinhenz", attributeSet2.getAttribute("sn"));
		
		assertEquals("Christoph Jerolimov", attributeSet3.getAttribute("cn"));
		assertEquals("c.jerolimov@tarent.de", attributeSet3.getAttribute("mail"));
		assertEquals("Christoph", attributeSet3.getAttribute("givenName"));
		assertEquals("Jerolimov", attributeSet3.getAttribute("sn"));
		
		AttributeSetList attributeSetList2 = (AttributeSetList) backend.executeQuery(null, "(l=Bonn)");
		
		assertTrue("Missing people! What terrible things are happen here?", attributeSetList2.size() > 50);
		
		attributeSetList = (AttributeSetList) backend.executeQuery(null, null);
		
		for (Iterator it = attributeSetList.iterator(); it.hasNext(); ) {
			AttributeSet attributeSet = (AttributeSet) it.next();
			assertNotNull(attributeSet);
		}
		
		assertTrue(attributeSetList.size() > 200);
	}*/

	/*public void testRealLdapQueries() {
		DataAccess dataAccess = new DataAccess("ldap-tarent");
		
		List list = dataAccess.getEntries(Person.class, Q.lucene("uid:amaier"));
		
		assertEquals("Missing people! What terrible things are happen here?", 1, list.size());
		
		Person person = (Person) list.get(0);
		
		assertEquals("Alex", person.getFirstname());
		assertEquals("Maier", person.getLastname());
	}*/
}
