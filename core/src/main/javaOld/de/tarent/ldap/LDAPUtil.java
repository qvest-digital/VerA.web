package de.tarent.ldap;

/*-
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
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
