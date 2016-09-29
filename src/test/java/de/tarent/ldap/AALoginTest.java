/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.ldap;

import junit.framework.TestCase;

import javax.naming.CommunicationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class AALoginTest extends TestCase {
	private boolean TEST_ENABLED = false;

    private LDAPManagerAA manager = null;

    @Override
    protected void setUp() throws Exception {
    	super.setUp();

    	if (!TEST_ENABLED)
    		return;

        Map params = new HashMap();
        params.put(LDAPManager.KEY_BASE_DN, "ou=testav01,dc=aa");
        params.put(LDAPManager.KEY_RELATIVE, "ou=Users");
        params.put(LDAPManager.KEY_RELATIVE_USER, "ou=Users");
        try {
        	manager = (LDAPManagerAA) LDAPManager.login(LDAPManagerAA.class, "ldap://192.168.250.128:3890/", params);
        } catch (LDAPException e) {
        	// Catch CommunicationException for offline building.
        	if (!(e.getCause() instanceof CommunicationException))
        		throw e;
        }
    }

    @Override
    protected void tearDown() throws Exception {
        manager = null;
        super.tearDown();
    }

    public void testFindRolesForAddress() throws NamingException {
    	if (!TEST_ENABLED || manager == null)
    		return;

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
    	if (!TEST_ENABLED || manager == null)
    		return;

        System.out.println(manager.getUserData("pol-2"));
    }
}
