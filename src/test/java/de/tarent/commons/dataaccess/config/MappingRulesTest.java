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

package de.tarent.commons.dataaccess.config;

import junit.framework.TestCase;

public class MappingRulesTest extends TestCase {
	public void testEmptyRules() {
		MappingRules mappingRules = new MappingRules();
		
		assertEquals("x-ldap-name", mappingRules.transformBackendToResult("x-ldap-name"));
		assertEquals("givenName", mappingRules.transformBackendToResult("givenName"));
		assertEquals("sn", mappingRules.transformBackendToResult("sn"));
		assertEquals("email", mappingRules.transformBackendToResult("email"));
		
		assertEquals("uid", mappingRules.transformResultToBackend("uid"));
		assertEquals("firstname", mappingRules.transformResultToBackend("firstname"));
		assertEquals("lastname", mappingRules.transformResultToBackend("lastname"));
		assertEquals("email", mappingRules.transformResultToBackend("email"));
		
		mappingRules.addRenameRule("uid", "x-ldap-name");
		mappingRules.addRenameRule("firstname", "givenName");
		mappingRules.addRenameRule("lastname", "sn");
		
		assertEquals("uid", mappingRules.transformBackendToResult("x-ldap-name"));
		assertEquals("firstname", mappingRules.transformBackendToResult("givenName"));
		assertEquals("lastname", mappingRules.transformBackendToResult("sn"));
		assertEquals("email", mappingRules.transformBackendToResult("email"));
		
		assertEquals("x-ldap-name", mappingRules.transformResultToBackend("uid"));
		assertEquals("givenName", mappingRules.transformResultToBackend("firstname"));
		assertEquals("sn", mappingRules.transformResultToBackend("lastname"));
		assertEquals("email", mappingRules.transformResultToBackend("email"));
	}

	public void testRuleTransformation() {
		MappingRules mappingRules = new MappingRules();
		
		mappingRules.addRenameRule("uid", "x-ldap-name");
		mappingRules.addRenameRule("firstname", "givenName");
		mappingRules.addRenameRule("lastname", "sn");
		mappingRules.addIgnoreRule("email", "email");
		
		assertEquals("uid", mappingRules.transformBackendToResult("x-ldap-name"));
		assertEquals("firstname", mappingRules.transformBackendToResult("givenName"));
		assertEquals("lastname", mappingRules.transformBackendToResult("sn"));
		assertNull(mappingRules.transformBackendToResult("email"));
		
		assertEquals("x-ldap-name", mappingRules.transformResultToBackend("uid"));
		assertEquals("givenName", mappingRules.transformResultToBackend("firstname"));
		assertEquals("sn", mappingRules.transformResultToBackend("lastname"));
		assertNull(mappingRules.transformResultToBackend("email"));
	}

//	public void testWildcardRules() {
//		MappingRules mappingRules = new MappingRules();
//		
//		mappingRules.addRenameRule("result-a-*", "backend-a-*");
//		mappingRules.addDropRule("result-b-*", "backend-b-*");
//		
//		assertEquals("result-a-test", mappingRules.transformLoading("result-a-test"));
//		assertEquals("result-b-test", mappingRules.transformLoading("result-b-test"));
//		assertEquals("result-a-test", mappingRules.transformLoading("backend-a-test"));
//		assertNull(mappingRules.transformLoading("backend-b-test"));
//
//		assertEquals("backend-a-test", mappingRules.transformSaving("result-a-test"));
//		assertNull(mappingRules.transformSaving("result-b-test"));
//		assertEquals("backend-a-test", mappingRules.transformSaving("backend-a-test"));
//		assertEquals("backend-b-test", mappingRules.transformSaving("backend-b-test"));
//	}
}
