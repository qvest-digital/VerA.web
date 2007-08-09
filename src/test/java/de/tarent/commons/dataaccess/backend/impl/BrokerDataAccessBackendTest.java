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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import de.tarent.commons.dataaccess.DataAccess;
import de.tarent.commons.dataaccess.DataAccessConfiguration;
import de.tarent.commons.dataaccess.Q;
import de.tarent.commons.dataaccess.test.Person;
import de.tarent.octopus.client.remote.OctopusRemoteTask;

public class BrokerDataAccessBackendTest extends TestCase {
	static {
		org.apache.log4j.Logger.getLogger("org.apache.axis").addAppender(
				new org.apache.log4j.varia.NullAppender());
		
		java.util.logging.Logger.getLogger(OctopusRemoteTask.class.getName()).
				setLevel(java.util.logging.Level.WARNING);
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		InputStream inputStream = BrokerDataAccessBackendTest.class.
				getResourceAsStream("broker-test-dataaccess.xml");
		
		new DataAccessConfiguration().parseXmlConfig(inputStream);
	}

	public void testLoadPersonsFromBroker() throws MalformedURLException {
		DataAccess dataAccess = new DataAccess("broker");
		
		try {
			String url = ((BrokerDataAccessBackend) dataAccess.getBackend()).getServiceURL();
			InputStream inputStream = new URL(url).openStream();
			while (inputStream.read(new byte[1024]) != -1) {
				// Nothing
			}
			inputStream.close();
		} catch (IOException e) {
			return;
		}
		
		List persons = dataAccess.getEntries(Person.class, Q.lucene("firstname:Christoph"));
		
		Collections.sort(persons);
		
		assertEquals(4, persons.size());
		
		Person person1 = (Person) persons.get(0);
		Person person2 = (Person) persons.get(1);
		Person person3 = (Person) persons.get(2);
		Person person4 = (Person) persons.get(3);
		
		assertEquals("Grieﬂhaber", person1.getLastname());
		assertEquals("Christoph", person1.getFirstname());
		assertEquals("K08griec", person1.getUid());
		
		assertEquals("Merkel", person2.getLastname());
		assertEquals("Christoph", person2.getFirstname());
		assertEquals("N51merkc", person2.getUid());
		
		assertEquals("Thalhammer", person3.getLastname());
		assertEquals("Christoph", person3.getFirstname());
		assertEquals("029thalc", person3.getUid());
		
		assertEquals("Wiﬂmeier", person4.getLastname());
		assertEquals("Christoph", person4.getFirstname());
		assertEquals("N73wissc", person4.getUid());
	}
}
