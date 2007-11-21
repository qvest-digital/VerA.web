/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
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
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: AALoginTest.java,v 1.1 2007/06/20 11:56:53 christoph Exp $
 * 
 * Created on 19.04.2005
 */
package de.tarent.ldap;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import junit.framework.TestCase;

public class AALoginTest extends TestCase {
    LDAPManagerAA manager = null;
    protected void setUp() throws Exception {
        super.setUp();
        Map params = new HashMap();
        params.put(LDAPManager.KEY_BASE_DN, "ou=testav01,dc=aa");
        params.put(LDAPManager.KEY_RELATIVE, "ou=Users");
        params.put(LDAPManager.KEY_RELATIVE_USER, "ou=Users");
        manager = (LDAPManagerAA) LDAPManager.login(LDAPManagerAA.class, "ldap://192.168.250.128:3890/", params);
    }

    protected void tearDown() throws Exception {
        manager = null;
        super.tearDown();
    }

    public void testFindRolesForAddress() throws NamingException {
        SearchControls cons = new SearchControls();
        cons.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        String filterTemplate = "(&(|(person=uid={0}@auswaertiges-amt.de,ou=Personen,dc=aa)(person=uid={0}.auswaertiges-amt.de,ou=Personen,dc=aa)(person=uid={0},ou=Personen,dc=aa))(objectclass=AARole))";
        String filter = MessageFormat.format(filterTemplate, new Object[]{"dietmar.hilbrich"});
        NamingEnumeration ergebnis = manager.lctx.search("ou=Users,ou=testav01,dc=aa", filter, cons);
        while (ergebnis.hasMore()) {
            Attributes result = ((SearchResult) ergebnis.nextElement()).getAttributes();
            Attribute personAttribute = result.get("person");
            Attribute uidAttribute = result.get("uid");
            System.out.println(personAttribute);
            System.out.println(uidAttribute.get(0) + " - " + uidAttribute.get(1));
            System.out.println(uidAttribute + " (" + result + ")");
        }
    }
    
    public void testGetUserData() throws LDAPException {
        System.out.println(manager.getUserData("pol-2"));
    }
}
