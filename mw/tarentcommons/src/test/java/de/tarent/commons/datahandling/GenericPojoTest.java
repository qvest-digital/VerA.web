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

import junit.framework.TestCase;
import java.lang.reflect.Method;

/**
 * TestCase for class GenericPojo
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public class GenericPojoTest extends TestCase {

    PersonI personDefault;
    PersonI personCustomStorage;
    PersonI personWithManager;

	protected void setUp() throws Exception {
		super.setUp();
        personDefault = (PersonI)GenericPojo.implementPojo(PersonI.class);
        personCustomStorage = (PersonI)GenericPojo.implementPojo(PersonI.class, new HashMapPojoStorage() {
                /** serialVersionUID */
        		private static final long serialVersionUID = -6022561434143473039L;
        		
				public Object get(Object key) {
                    return key;
                }
            });
        personWithManager = (PersonI)GenericPojo.implementPojo(PersonI.class, new HashMapPojoStorage(), new GenericPojoManager() {
                public Object methodCalled(Object pojo, GenericPojo theGenericPojo, Method method, Object[] args) {
                    return args[0];
                }
            });
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
    
    
    public void testSimpleValues() {
        personDefault.setName("Sebastian");
        assertEquals("Propertie matches", "Sebastian", personDefault.getName());
        
        personDefault.setAge(10);
        assertEquals("Propertie matches", 10, personDefault.getAge());

        personDefault.setCondition(true);
        assertEquals("Propertie matches", true, personDefault.isCondition());
        
        personDefault.setCondition(false);
        assertEquals("Propertie matches", false, personDefault.isCondition());
        
        personDefault.setA(42);
        assertEquals("Propertie matches", 42, personDefault.getA());
    }    

    public void testNullForPrimitive() {
        try {
            personDefault.isCondition();
            fail("No Exception");
        } catch (NullPointerException npe) {}        
    }    

    public void testWrongType() {
        try {
            personCustomStorage.getA();
            fail("No Exception");
        } catch (ClassCastException npe) {}        
    }    

    
    public void testMethodCall() {
        assertEquals("method call response matches", "Test", personWithManager.echoMethod("Test"));        
    }
    

    
    public void testCustomStorage() {
        assertEquals("custom storage works", "name", personCustomStorage.getName());                
    }
    
}
