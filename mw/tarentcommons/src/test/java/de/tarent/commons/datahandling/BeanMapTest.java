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

package de.tarent.commons.datahandling;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/**
 * TestCase for class BeanMap
 * @author tim
 *
 */
public class BeanMapTest extends TestCase {

	ConcreteBeanMap aBeanMap;

	protected void setUp() throws Exception {
		super.setUp();
		aBeanMap = new ConcreteBeanMap();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'de.tarent.commons.datahandling.BeanMap.getValueType(String)'
	 */
	public void testGetValueType() throws SecurityException, NoSuchFieldException {
		assertEquals("Type mismatch!", String.class, aBeanMap.getValueType("anAttribute"));
		assertEquals("Type mismatch!", String.class, aBeanMap.getValueType("aReadonlyAttribute"));
		assertEquals("Type mismatch!", String.class, aBeanMap.getValueType("aWriteonlyAttribute"));
	}

	/*
	 * Test method for 'de.tarent.commons.datahandling.BeanMap.size()'
	 */
	public void testSize() {
		assertEquals("Wrong size!", 3, aBeanMap.size());
	}

	/*
	 * Test method for 'de.tarent.commons.datahandling.BeanMap.isEmpty()'
	 */
	public void testIsEmpty() {
		assertEquals("Should not be empty!", false, aBeanMap.isEmpty());
	}

	/*
	 * Test method for 'de.tarent.commons.datahandling.BeanMap.containsKey(Object)'
	 */
	public void testContainsKey() {
		assertEquals("Key should not be contained!", false, aBeanMap.containsKey("invalidKey"));
		assertEquals("Key should be contained!", true, aBeanMap.containsKey("anAttribute"));
		assertEquals("Key should be contained!", true, aBeanMap.containsKey("aReadonlyAttribute"));
		assertEquals("Key should be contained!", true, aBeanMap.containsKey("aWriteonlyAttribute"));
	}

	/*
	 * Test method for 'de.tarent.commons.datahandling.BeanMap.keySet()'
	 */
	public void testKeySet() {
		Set keySet = new HashSet();
		keySet.add("anAttribute");
		keySet.add("aReadonlyAttribute");
		keySet.add("aWriteonlyAttribute");
		assertEquals("Wrong key set returned", keySet, aBeanMap.keySet());
	}

	/*
	 * Test method for 'de.tarent.commons.datahandling.BeanMap.containsValue(Object)'
	 */
	public void testContainsValue() {
		assertEquals("Value should not be contained!", false, aBeanMap.containsValue("invalidValue"));
		assertEquals("Value should be contained!", true, aBeanMap.containsValue("anAttributeValue"));
		assertEquals("Value should be contained!", true, aBeanMap.containsValue("aReadonlyAttributeValue"));
		assertEquals("Value should be contained!", false, aBeanMap.containsValue("aWriteonlyAttributeValue"));
	}

	/*
	 * Test method for 'de.tarent.commons.datahandling.BeanMap.get(Object)'
	 */
	public void testGet() {
		assertEquals("Wrong attribute value", "anAttributeValue", aBeanMap.get("anAttribute"));
		assertEquals("Wrong attribute value", "aReadonlyAttributeValue", aBeanMap.get("aReadonlyAttribute"));
	}

	/*
	 * Test method for 'de.tarent.commons.datahandling.BeanMap.values()'
	 */
	public void testValues() {
		// not implemented!
	}

	/*
	 * Test method for 'de.tarent.commons.datahandling.BeanMap.entrySet()'
	 */
	public void testEntrySet() {
		// not implemented!
	}

	/*
	 * Test method for 'de.tarent.commons.datahandling.BeanMap.put(Object, Object)'
	 */
	public void testPut() {
		// not implemented!
	}

	/*
	 * Test method for 'de.tarent.commons.datahandling.BeanMap.remove(Object)'
	 */
	public void testRemove() {
		// not implemented!
	}

	/*
	 * Test method for 'de.tarent.commons.datahandling.BeanMap.putAll(Map)'
	 */
	public void testPutAll() {
		// not implemented!
	}

	/*
	 * Test method for 'de.tarent.commons.datahandling.BeanMap.clear()'
	 */
	public void testClear() {
		// not implemented!
	}

	/*
	 * Test method for 'de.tarent.commons.datahandling.BeanMap.getIgnoreCase(Object)'
	 */
	public void testGetIgnoreCase() {
		assertEquals("Wrong attribute value", "anAttributeValue", aBeanMap.getIgnoreCase("ANaTTRIBUTE"));
	}

}
