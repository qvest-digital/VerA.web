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

/**
 * 
 */
package de.tarent.commons.datahandling;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Tests for {@link BeanMapTransformator}.
 * 
 * @author Tim Steffens
 *
 */
public class BeanMapTranformatorTest extends TestCase {
	
	BeanMapTransformator beanMapTransformator;

	/**
	 * @param arg0
	 */
	public BeanMapTranformatorTest(String arg0) {
		super(arg0);
		this.beanMapTransformator = new BeanMapTransformator();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link de.tarent.commons.datahandling.BeanMapTransformator#transformBeanToMap(java.lang.Object)}.
	 */
	public void testTransformBeanToMapObject() {
		Object bean = new ConcreteBeanMap();
		assertEquals("anAttributeValue", this.beanMapTransformator.transformBeanToMap(bean).get("anAttribute"));
	}

	/**
	 * Test method for {@link de.tarent.commons.datahandling.BeanMapTransformator#transformMapToBean(java.util.Map, java.lang.Class)}.
	 */
	public void testTransformMapToBeanMapClass() {
		Map map = new HashMap();
		map.put("anAttribute", "aNewAttributeValue");
		ConcreteBeanMap bean = (ConcreteBeanMap) this.beanMapTransformator.transformMapToBean(map, ConcreteBeanMap.class);		
		assertEquals("aNewAttributeValue", bean.getAnAttribute());
	}

	/**
	 * Test method for {@link de.tarent.commons.datahandling.BeanMapTransformator#transformBeanToMap(java.lang.Object, java.util.Map)}.
	 */
	public void testTransformBeanToMapObjectMap() {
		Object bean = new ConcreteBeanMap();
		Map map = new HashMap();
		this.beanMapTransformator.transformBeanToMap(bean, map);
		assertEquals("anAttributeValue", map.get("anAttribute"));
	}

	/**
	 * Test method for {@link de.tarent.commons.datahandling.BeanMapTransformator#transformMapToBean(java.util.Map, java.lang.Object)}.
	 */
	public void testTransformMapToBeanMapObject() {
		Map map = new HashMap();
		map.put("anAttribute", "aNewAttributeValue");
		ConcreteBeanMap bean = new ConcreteBeanMap(); 
		this.beanMapTransformator.transformMapToBean(map, bean);
		assertEquals("aNewAttributeValue", bean.getAnAttribute());
	}

}
