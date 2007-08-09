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
import java.util.List;
import java.util.Map;

import de.tarent.commons.dataaccess.DataAccess;
import de.tarent.commons.dataaccess.DataAccessConfiguration;
import junit.framework.TestCase;

public class SqlDataAccessBackendTest extends TestCase {
	protected void setUp() throws Exception {
		super.setUp();
		
		InputStream inputStream = SqlDataAccessBackendTest.class.
				getResourceAsStream("hsqldb-test-dataaccess.xml");
		
		new DataAccessConfiguration().parseXmlConfig(inputStream);
	}

	public void testCorrectBackend() {
		DataAccess dataAccess = new DataAccess("hsqldb-memory01");
		
		SqlDataAccessBackend backend = (SqlDataAccessBackend) dataAccess.getBackend();
		
		backend.store(null, "CREATE TABLE users (" +
				"uid VARCHAR," +
				"username VARCHAR," +
				"password VARCHAR)");
		
		backend.store(null, "INSERT INTO users VALUES ('1', 'abc', 'abc')");
		backend.store(null, "INSERT INTO users VALUES ('2', 'def', 'def')");
		
		List result = (List) backend.executeQuery(null, "SELECT * FROM users");
		
		assertEquals(2, result.size());
		
		Map entry1 = (Map) result.get(0);
		Map entry2 = (Map) result.get(1);
		
		assertEquals("1", entry1.get("UID"));
		assertEquals("abc", entry1.get("USERNAME"));
		assertEquals("abc", entry1.get("PASSWORD"));
		
		assertEquals("2", entry2.get("UID"));
		assertEquals("def", entry2.get("USERNAME"));
		assertEquals("def", entry2.get("PASSWORD"));
	}
}
