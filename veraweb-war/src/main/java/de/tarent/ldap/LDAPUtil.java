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
 * $Id$
 * 
 * Created on 21.04.2005
 */
package de.tarent.ldap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

/**
 * Diese Klasse stellt statisch Utility-Funktionen für den LDAP-Kontext bereit.
 * 
 * @author mikel
 */
public class LDAPUtil {
    /**
     * Diese Methode erzeugt aus einer {@link Attributes}-Sammlung eine {@link Map},
     * deren Schlüssel die jeweilige Attribut-ID und deren Werte der jeweilige
     * Attribut-Einzelwert oder eine Liste der jeweiligen Attributwerte sind. 
     * 
     * @param attribs LDAP-{@link Attributes}
     * @return eine {@link Map}, die die Attribute darstellt
     * @throws NamingException
     */
    public static Map toMap(Attributes attribs) throws NamingException {
        if (attribs == null)
            return null;
        Map result = new HashMap();
        NamingEnumeration enumAttribs = attribs.getAll();
        while (enumAttribs.hasMore()) {
            Attribute attrib = (Attribute) enumAttribs.next();
            if (attrib != null) {
                switch(attrib.size()) {
                case 0:
                    break;
                case 1:
                    result.put(attrib.getID(), attrib.get());
                    break;
                default:
                    result.put(attrib.getID(), Collections.list(attrib.getAll()));
                }
            }
        }
        return result;
    }
}
