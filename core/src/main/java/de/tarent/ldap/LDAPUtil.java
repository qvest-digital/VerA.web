package de.tarent.ldap;
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
        if (attribs == null) {
            return null;
        }
        Map result = new HashMap();
        NamingEnumeration enumAttribs = attribs.getAll();
        while (enumAttribs.hasMore()) {
            Attribute attrib = (Attribute) enumAttribs.next();
            if (attrib != null) {
                switch (attrib.size()) {
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
