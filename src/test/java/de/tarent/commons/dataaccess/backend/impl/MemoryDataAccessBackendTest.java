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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.tarent.commons.dataaccess.DataAccess;
import de.tarent.commons.dataaccess.Q;
import de.tarent.commons.dataaccess.QueryProcessor;
import de.tarent.commons.dataaccess.query.QueryParser;
import de.tarent.commons.dataaccess.query.QueryVisitor;
import de.tarent.commons.dataaccess.query.impl.LuceneQueryParser;
import de.tarent.commons.dataaccess.query.impl.TraversingVisitor;
import de.tarent.commons.dataaccess.test.Person;

import junit.framework.TestCase;

public class MemoryDataAccessBackendTest extends TestCase {
	public void testCommit() {
		MemoryDataAccessBackend backend = new MemoryDataAccessBackend();
		assertEquals(0, backend.getRestrainedEntries());
		assertEquals(0, backend.getRestrainedStoreEntries());
		assertEquals(0, backend.getRestrainedDeleteEntries());
		assertEquals(0, backend.getObjectCacheSize());
		
		backend.store(null, "test01");
		backend.store(null, "test02");
		
		assertEquals(2, backend.getRestrainedEntries());
		assertEquals(2, backend.getRestrainedStoreEntries());
		assertEquals(0, backend.getRestrainedDeleteEntries());
		assertEquals(0, backend.getObjectCacheSize());
		
		backend.commit();
		
		assertEquals(0, backend.getRestrainedEntries());
		assertEquals(0, backend.getRestrainedStoreEntries());
		assertEquals(0, backend.getRestrainedDeleteEntries());
		assertEquals(2, backend.getObjectCacheSize());
		
		backend.delete(null, "test02");
		
		assertEquals(1, backend.getRestrainedEntries());
		assertEquals(0, backend.getRestrainedStoreEntries());
		assertEquals(1, backend.getRestrainedDeleteEntries());
		assertEquals(2, backend.getObjectCacheSize());
		
		backend.commit();
		
		assertEquals(0, backend.getRestrainedEntries());
		assertEquals(0, backend.getRestrainedStoreEntries());
		assertEquals(0, backend.getRestrainedDeleteEntries());
		assertEquals(1, backend.getObjectCacheSize());
	}

	public void testRollback() {
		MemoryDataAccessBackend backend = new MemoryDataAccessBackend();
		assertEquals(0, backend.getRestrainedEntries());
		assertEquals(0, backend.getRestrainedStoreEntries());
		assertEquals(0, backend.getRestrainedDeleteEntries());
		assertEquals(0, backend.getObjectCacheSize());
		
		backend.store(null, "test01");
		backend.store(null, "test02");
		
		assertEquals(2, backend.getRestrainedEntries());
		assertEquals(2, backend.getRestrainedStoreEntries());
		assertEquals(0, backend.getRestrainedDeleteEntries());
		assertEquals(0, backend.getObjectCacheSize());
		
		backend.rollback();
		
		assertEquals(0, backend.getRestrainedEntries());
		assertEquals(0, backend.getRestrainedStoreEntries());
		assertEquals(0, backend.getRestrainedDeleteEntries());
		assertEquals(0, backend.getObjectCacheSize());
		
		backend.delete(null, "test02");
		
		assertEquals(1, backend.getRestrainedEntries());
		assertEquals(0, backend.getRestrainedStoreEntries());
		assertEquals(1, backend.getRestrainedDeleteEntries());
		assertEquals(0, backend.getObjectCacheSize());
		
		backend.rollback();
		
		assertEquals(0, backend.getRestrainedEntries());
		assertEquals(0, backend.getRestrainedStoreEntries());
		assertEquals(0, backend.getRestrainedDeleteEntries());
		assertEquals(0, backend.getObjectCacheSize());
	}

	public void testStoreAndQuery() {
		QueryParser queryParser = new LuceneQueryParser();
		QueryVisitor queryVisitor = new TraversingVisitor();
		
		MemoryDataAccessBackend backend = new MemoryDataAccessBackend();
		backend.store(null, "test01");
		backend.store(null, Collections.singletonMap("key01", "value01"));
		backend.commit();
		
		assertEquals(2, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, Object.class, null, null, null, null), queryParser, queryVisitor).size());
		assertEquals(1, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, String.class, null, null, null, null), queryParser, queryVisitor).size());
		assertEquals(0, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, List.class, null, null, null, null), queryParser, queryVisitor).size());
		assertEquals(1, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, Map.class, null, null, null, null), queryParser, queryVisitor).size());
	}

	public void testStoreAndQueryWithExpression() {
		QueryParser queryParser = new LuceneQueryParser();
		QueryVisitor queryVisitor = new TraversingVisitor();
		
		MemoryDataAccessBackend backend = new MemoryDataAccessBackend();
		backend.store(null, "test01");
		backend.store(null, Collections.singletonMap("key01", "value01"));
		backend.commit();
		
		assertEquals(2, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, Object.class, null, null, null, null), queryParser, queryVisitor).size());
		assertEquals(1, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, String.class, null, null, null, null), queryParser, queryVisitor).size());
		assertEquals(0, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, List.class, null, null, null, null), queryParser, queryVisitor).size());
		assertEquals(1, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, Map.class, null, null, null, null), queryParser, queryVisitor).size());
		
		Object expr = Q.lucene("key01:value01");
		assertEquals(1, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, Object.class, expr, null, null, null), queryParser, queryVisitor).size());
		assertEquals(0, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, String.class, expr, null, null, null), queryParser, queryVisitor).size());
		assertEquals(0, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, List.class, expr, null, null, null), queryParser, queryVisitor).size());
		assertEquals(1, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, Map.class, expr, null, null, null), queryParser, queryVisitor).size());
		
		Object expr2 = Q.lucene("key01:value01 OR key02:value02");
		assertEquals(1, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, Object.class, expr2, null, null, null), queryParser, queryVisitor).size());
		assertEquals(0, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, String.class, expr2, null, null, null), queryParser, queryVisitor).size());
		assertEquals(0, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, List.class, expr2, null, null, null), queryParser, queryVisitor).size());
		assertEquals(1, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, Map.class, expr2, null, null, null), queryParser, queryVisitor).size());
		
		Object expr3 = Q.lucene("key01:value01 AND key02:value02");
		assertEquals(0, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, Object.class, expr3, null, null, null), queryParser, queryVisitor).size());
		assertEquals(0, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, String.class, expr3, null, null, null), queryParser, queryVisitor).size());
		assertEquals(0, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, List.class, expr3, null, null, null), queryParser, queryVisitor).size());
		assertEquals(0, backend.query(new QueryProcessor(null, backend, QueryProcessor.RESPONSE_MODE_BEAN_LIST, Map.class, expr3, null, null, null), queryParser, queryVisitor).size());
	}

	public void testRealMemoryDataAccess() {
		MemoryDataAccessBackend backend = new MemoryDataAccessBackend();
		
		DataAccess dataAccess = new DataAccess(null, backend);
		
		Person jim = new Person();
		jim.setFirstname("Jim");
		jim.setLastname("Panse");
		
		Person bernhard = new Person();
		bernhard.setFirstname("Bernhard");
		bernhard.setLastname("Diener");
		
		Person peter = new Person();
		peter.setFirstname("Peter");
		peter.setLastname("Silie");
		
		dataAccess.store(jim);
		dataAccess.store(bernhard);
		dataAccess.store(peter);
		
		assertEquals(3, backend.getRestrainedEntries());
		assertEquals(3, backend.getRestrainedStoreEntries());
		assertEquals(0, backend.getRestrainedDeleteEntries());
		assertEquals(0, backend.getObjectCacheSize());
		
		backend.commit();
		
		assertEquals(0, backend.getRestrainedEntries());
		assertEquals(0, backend.getRestrainedStoreEntries());
		assertEquals(0, backend.getRestrainedDeleteEntries());
		assertEquals(3, backend.getObjectCacheSize());
		
		Person person1 = (Person) dataAccess.getEntry(Person.class, Q.lucene("firstname:Jim"));
		Person person2 = (Person) dataAccess.getEntry(Person.class, Q.lucene("firstname:Bernhard"));
		Person person3 = (Person) dataAccess.getEntry(Person.class, Q.lucene("firstname:Peter"));
		Person person4 = (Person) dataAccess.getEntry(Person.class, Q.lucene("firstname:Dieter"));
		
		assertNotNull(person1);
		assertNotNull(person2);
		assertNotNull(person3);
		assertNull(person4);
		
		dataAccess.delete(person1);
		dataAccess.delete(person2);
		
		assertEquals(2, backend.getRestrainedEntries());
		assertEquals(0, backend.getRestrainedStoreEntries());
		assertEquals(2, backend.getRestrainedDeleteEntries());
		assertEquals(3, backend.getObjectCacheSize());
		
		person1 = (Person) dataAccess.getEntry(Person.class, Q.lucene("firstname:Jim"));
		person2 = (Person) dataAccess.getEntry(Person.class, Q.lucene("firstname:Bernhard"));
		person3 = (Person) dataAccess.getEntry(Person.class, Q.lucene("firstname:Peter"));
		person4 = (Person) dataAccess.getEntry(Person.class, Q.lucene("firstname:Dieter"));
		
		assertNotNull(person1);
		assertNotNull(person2);
		assertNotNull(person3);
		assertNull(person4);
		
		dataAccess.commit();

		assertEquals(0, backend.getRestrainedEntries());
		assertEquals(0, backend.getRestrainedStoreEntries());
		assertEquals(0, backend.getRestrainedDeleteEntries());
		assertEquals(1, backend.getObjectCacheSize());

		person1 = (Person) dataAccess.getEntry(Person.class, Q.lucene("firstname:Jim"));
		person2 = (Person) dataAccess.getEntry(Person.class, Q.lucene("firstname:Bernhard"));
		person3 = (Person) dataAccess.getEntry(Person.class, Q.lucene("firstname:Peter"));
		person4 = (Person) dataAccess.getEntry(Person.class, Q.lucene("firstname:Dieter"));
		
		assertNull(person1);
		assertNull(person2);
		assertNotNull(person3);
		assertNull(person4);
		
	}
}
