package de.tarent.octopus.security.ldap;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.apache.commons.logging.Log;

import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.security.TcSecurityException;

/**
 * Diese Klasse regelt den Zugriff auf eine LDAP-Resource
 *
 * @author philipp
 * @deprecated functionality moved into LDAPLib, will be removed as soon as 2005-06-01
 */
public class TcSecurityLDAPManager {

    //Hashtable mit Zugriffsdaten
    private Hashtable env;
    private InitialLdapContext lctx;
    private String baseDN;
    private String relative;
    public boolean login = false;
    private static Log logger = LogFactory.getLog(TcSecurityLDAPManager.class);

    /**
     * Erzeugt einen LDAPManager
     *
     * @param ldapurl URL des LDAP-Servers
     * @param baseDN  Base DN
     */
    public TcSecurityLDAPManager(String ldapurl, String baseDN, String relative) {
        this.baseDN = baseDN;
        this.relative = cleanup_relative(relative);
        env = new Hashtable();
        env.put(
                Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapurl);
    }

    /**
     * Methode, die einen gegebenen Benutzer einloggt.
     *
     * @param username     Benutzername entweder nur als Benutzername, wenn appendBaseDN
     *                     gesetzt ist, sonst als komplette DN des Users
     * @param appendBaseDN Soll der Base-DN angehängt werden?
     * @param passwort     Passwort
     * @param authType     Authentifizierungstyp gegenüber dem LDAP (im Moment nur "simple" ausprobiert & supported)
     * @throws TcSecurityException
     */
    public void login(
            String username,
            boolean appendBaseDN,
            String passwort,
            String authType)
            throws TcSecurityException {

        //At first, search for uid
        logger.debug("Doing login with userid " + username);
        String security_principal = username;
        if (appendBaseDN) {
            security_principal = getUserDN(username) + relative + baseDN;
        }
        logger.debug("User is " + security_principal);
        env.put(Context.SECURITY_AUTHENTICATION, authType);
        env.put(Context.SECURITY_PRINCIPAL, security_principal);
        env.put(Context.SECURITY_CREDENTIALS, passwort);

        try {
            //Versuche, LDAP-Verbindung herzustellen
            if (lctx != null) {
                logger.debug("We are already logged in, closing connection...");
                lctx.close();
                lctx = null;
            }
            logger.debug("Environment is: " + env.toString());
            lctx = new InitialLdapContext(env, null);
            login = true;
        } catch (AuthenticationException e) {
            //Falls die Authentification nicht stimmt
            if (logger.isInfoEnabled()) {
                logger.info("Fehlerhafte Authenifizierung über LDAP.");
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Fehlerhafte Authenifizierung über LDAP.", e);
            }
            throw new TcSecurityException(TcSecurityException.ERROR_AUTH_ERROR);

        } catch (CommunicationException e) {
            //Falls LDAP-Server nicht erreichbar
            logger.error("Der LDAP-Server ist nicht erreichbar, bitte versuchen Sie es später noch einmal.", e);
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR);
        } catch (NamingException e) {
            logger.error(
                    "Es ist ein Fehler bei der Kommunikation mit dem Authentifizierungs-Server aufgetreten, bitte versuchen Sie" +
                            " es später noch einmal.",
                    e);
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR);
        }
    }

    /**
     * Anonymer Login
     *
     * @throws TcSecurityException
     */
    public void login() throws TcSecurityException {
        logger.info("Doing anonymous login...");
        env.put(Context.SECURITY_AUTHENTICATION, "none");
        try {
            //Versuche, LDAP-Verbindung herzustellen
            lctx = new InitialLdapContext(env, null);
        } catch (CommunicationException e) {
            //Falls LDAP-Server nicht erreichbar
            logger.error("Der LDAP-Server ist nicht erreichbar, bitte versuchen Sie es später noch einmal.", e);
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR);
        } catch (NamingException e) {
            logger.error(
                    "Es ist ein Fehler bei der Kommunikation mit dem Authentifizierungs-Server aufgetreten, bitte versuchen Sie" +
                            " es später noch einmal.",
                    e);
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR);
        }
    }

    /**
     * Methode, um einen User im LDAP anzulegen
     *
     * @param userid   BenutzerID
     * @param vorname  Vorname
     * @param nachname Nachname
     * @param passwort Passwort
     * @param gruppe   Gruppe, wird im Moment nicht genutzt
     * @throws TcSecurityException wenn der Benutzer nicht angelegt werden konnte
     */
    public void addContactUser(
            String userid,
            String vorname,
            String nachname,
            String passwort,
            String gruppe)
            throws TcSecurityException {
        logger.debug("Füge User " + userid + " hinzu.");
        if (checkuid(userid)) {
            modifyContactUser(userid, vorname, nachname, passwort, gruppe);
        } else {
            gruppe = sortGroup(gruppe);
            BasicAttributes attr = new BasicAttributes();
            BasicAttribute objectClass = new BasicAttribute("objectClass", "top");
            objectClass.add("simpleSecurityObject");
            objectClass.add("person");
            objectClass.add("organizationalPerson");
            objectClass.add("inetOrgPerson");
            objectClass.add("TarentContact");
            objectClass.add("gosaAccount");
            attr.put(objectClass);
            attr.put("uid", userid);
            attr.put("sn", nachname);
            attr.put("cn", nachname + " " + vorname);
            attr.put("gn", vorname);
            attr.put("ContactClientGroups", gruppe);
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                String password_enc2 =
                        byteArrayToBase64(md.digest(passwort.getBytes()), false);
                attr.put("userPassword", "{SHA}" + password_enc2);
            } catch (NoSuchAlgorithmException e) {
                throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR, e);
            }
            try {
                lctx.createSubcontext("cn=" + nachname + " " + vorname + relative + baseDN, attr);
            } catch (NamingException e) {
                throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR, e);
            }
        }
    }

    /**
     * Modifiziere User so, dass er angegebenen Daten entspricht
     *
     * @param userid   User, der modifiziert werden soll
     * @param vorname  Vorname
     * @param nachname Nachname
     * @param passwort Passwort
     * @param gruppe   Gruppe(n) des Users
     * @throws TcSecurityException
     */
    public void modifyContactUser(String userid, String vorname, String nachname, String passwort, String gruppe)
            throws TcSecurityException {
        gruppe = sortGroup(gruppe);

        logger.debug("Modifiziere LDAP-User: " + userid);
        Vector mods = new Vector();
        String password = "";
        //Passwort generieren
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            String password_enc2 =
                    byteArrayToBase64(md.digest(passwort.getBytes()), false);
            password = "{SHA}" + password_enc2;
        } catch (NoSuchAlgorithmException e) {
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR, e);
        }
        //sichere ObjectClass
        Attribute objectClass;
        try {
            String[] objectClassA = { "objectClass" };
            objectClass = lctx.getAttributes(getUserDN(userid) + relative + baseDN, objectClassA).get("objectClass");
        } catch (NamingException e1) {
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR, e1);
        }

        //Checke Objectclass
        boolean modified = false;
        if (!this.checkAttribute(userid, "objectClass", "top")) {
            objectClass.add("top");
            modified = true;
        }
        if (!this.checkAttribute(userid, "objectClass", "simpleSecurityObject")) {
            objectClass.add("simpleSecurityObject");
            modified = true;
        }
        if (!this.checkAttribute(userid, "objectClass", "person")) {
            objectClass.add("person");
            modified = true;
        }
        if (!this.checkAttribute(userid, "objectClass", "TarentContact")) {
            objectClass.add("TarentContact");
            modified = true;
        }
        if (!this.checkAttribute(userid, "objectClass", "organizationalPerson")) {
            objectClass.add("organizationalPerson");
            modified = true;
        }
        if (!this.checkAttribute(userid, "objectClass", "inetOrgPerson")) {
            objectClass.add("inetOrgPerson");
            modified = true;
        }
        if (!this.checkAttribute(userid, "objectClass", "gosaAccount")) {
            objectClass.add("gosaAccount");
            modified = true;
        }
        if (modified) {
            mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, objectClass));
        }
        //Checke sn
        if (!this.checkAttribute(userid, "sn")) {
            mods.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("sn", nachname)));
        } else if (!this.checkAttribute(userid, "sn", nachname)) {
            mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sn", nachname)));
        }
        //Checke gn
        if (!this.checkAttribute(userid, "gn")) {
            mods.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("gn", vorname)));
        } else if (!this.checkAttribute(userid, "gn", vorname)) {
            mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("gn", vorname)));
        }
        //Checke cn
        if (!this.checkAttribute(userid, "cn")) {
            mods.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("cn", nachname + " " + vorname)));
        } else if (!this.checkAttribute(userid, "cn", nachname + " " + vorname)) {
            mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("cn", nachname + " " + vorname)));
        }
        //Checke ContactClientGroups
        if (!this.checkAttribute(userid, "ContactClientGroups")) {
            mods.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("ContactClientGroups", gruppe)));
        } else if (!this.checkAttribute(userid, "ContactClientGroups", gruppe)) {
            mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("ContactClientGroups", gruppe)));
        }
        //Checke userPassword
        if (!this.checkAttribute(userid, "userPassword")) {
            mods.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("userPassword", password)));
        }
        mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", password)));
        try {
            logger.debug(mods.toString());
            lctx.modifyAttributes(getUserDN(userid) + relative + baseDN,
                    (ModificationItem[]) mods.toArray(new ModificationItem[1]));
        } catch (NamingException e2) {
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR, e2);
        }
    }

    /**
     * Modfiziert des Attribute attribut des Users userid
     *
     * @param userid    UserId des Users
     * @param attribute zu modifizierendes Attribut
     * @param value     neue Value
     * @throws TcSecurityException Kapselt evtl auftretende NamingException
     */
    public void modifyContactUserAttribute(String userid, String attribute, String value) throws TcSecurityException {
        logger.debug("Modifiziere Attribut " + attribute + " von: " + userid + " nach " + value);

        //Checke Attribute
        Vector mods = new Vector();
        if (!this.checkAttribute(userid, attribute)) {
            mods.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute(attribute, value)));
        } else if (!this.checkAttribute(userid, attribute, value)) {
            mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(attribute, value)));
        }

        //try to modify user...
        if (!mods.isEmpty()) {
            try {
                logger.debug(mods.toString());
                lctx.modifyAttributes(getUserDN(userid) + relative + baseDN,
                        (ModificationItem[]) mods.toArray(new ModificationItem[1]));
            } catch (NamingException e2) {
                throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR, e2);
            }
        }
    }

    /**
     * Sortiert Gruppen-String
     *
     * @param gruppe Gruppen-String
     * @return sortierten String
     */
    private String sortGroup(String gruppe) {
        String[] gruppen = gruppe.split(" ");
        Arrays.sort(gruppen);
        gruppe = "";
        for (int i = 0; i < gruppen.length; i++) {
            gruppe += gruppen[i] + " ";
        }
        gruppe = gruppe.trim();
        return gruppe;
    }

    /**
     * Testet, ob Objekt im LDAP bestimmtes Attribut hat
     *
     * @param name     uid des Objekts, das getestet werden soll
     * @param attribut Attribut, das vorhanden sein soll
     * @return true, wenn attribut existiert, false sonst, auch wenn Fehler aufgetreten ist
     */
    private boolean checkAttribute(String name, String attribut) {
        boolean vorhanden = false;
        if (lctx == null) {
            try {
                this.login();
            } catch (TcSecurityException e1) {
                vorhanden = false;
            }
        }
        try {
            logger.debug(">>>>>>>>>>>>> Checke Attribut " + attribut + " von Object " + name);
            BasicAttributes zusuchendeAttribute = new BasicAttributes(true);
            zusuchendeAttribute.put(new BasicAttribute("uid", name));
            zusuchendeAttribute.put(new BasicAttribute(attribut));
            NamingEnumeration ergebnis = lctx.search(relative.substring(1) + baseDN, zusuchendeAttribute);
            if (ergebnis.hasMore()) {
                vorhanden = true;
            }
        } catch (NamingException e) {
            vorhanden = false;
        }
        return vorhanden;
    }

    /**
     * Methode, die testet, ob Objekt name im LDAP das bestimmte Attribut
     * mit dem bestimmten Wert hat.
     *
     * @param name      uid des Objekts
     * @param attribute zu suchendes Attribut
     * @param wert      Wert des Attributes
     * @return true, wenn Wert vorhanden, false sonst, auch bei anderen Fehlern
     */
    private boolean checkAttribute(String name, String attribute, String wert) {
        boolean vorhanden = false;
        if (lctx == null) {
            try {
                this.login();
            } catch (TcSecurityException e) {
                vorhanden = false;
            }
        }
        Attributes attributes;
        try {
            logger.debug(">>>>>>>>>>>>> Checke Attribut " + attribute + " von Object " + name + " auf Werrt " + wert);
            attributes = lctx.getAttributes(getUserDN(name) + relative + baseDN);
            try {
                Attribute test = attributes.get(attribute);
                if (test != null) {
                    NamingEnumeration liste = test.getAll();
                    while (liste.hasMore()) {
                        String testwert = (String) liste.next();
                        if (testwert.equals(wert)) {
                            vorhanden = true;
                        }
                    }
                }
            } catch (NamingException e) {
                vorhanden = false;
            }
        } catch (TcSecurityException e1) {
            vorhanden = false;
        } catch (NamingException e) {
            vorhanden = false;
        }
        return vorhanden;
    }

    /**
     * Testet, ob userid im LDAP vorhanden
     *
     * @param userid UserID, die getestet werden soll
     * @return true, wenn vorhanden, false sonst
     * @throws TcSecurityException, wenn Fehler auftritt
     */
    public boolean checkuid(String userid) throws TcSecurityException {
        boolean vorhanden = false;
        if (lctx == null) {
            this.login();
        }
        try {
            BasicAttributes zusuchendeAttribute = new BasicAttributes(true);
            zusuchendeAttribute.put("uid", userid);
            NamingEnumeration ergebnis = lctx.search(relative.substring(1) + baseDN, zusuchendeAttribute);
            if (ergebnis.hasMore()) {
                vorhanden = true;
            }
        } catch (NamingException e) {
            vorhanden = false;
        }
        return vorhanden;
    }

    /**
     * Testet, ob UserID Mitglied der Gruppe groupName ist
     *
     * @param userid    zu testende UserID
     * @param groupName zu testende Gruppe
     * @return true, wenn UserID Mitglied der Gruppe ist, false sonst
     * @throws TcSecurityException
     */
    public boolean isGroupMemberOf(String userid, String groupName) throws TcSecurityException {
        if (lctx == null) {
            this.login();
        }
        int index = -1;
        try {
            Attributes attribute = lctx.getAttributes(getUserDN(userid) + relative + baseDN);
            Attribute gruppen = attribute.get("ContactClientGroups");
            if (gruppen == null) {
                throw new TcSecurityException(userid + " ist kein registrierter User!");
            } else {
                String gruppe = (String) gruppen.get();
                String[] gruppenarray = gruppe.split(" ");
                Arrays.sort(gruppenarray);
                index = Arrays.binarySearch(gruppenarray, groupName);
            }
        } catch (NamingException e) {
            throw new TcSecurityException("Fehler beim Zugriff auf das LDAP-Verzeichnis.");
        }
        return (index >= 0);
    }

    private String byteArrayToBase64(byte[] a, boolean alternate) {
        int aLen = a.length;
        int numFullGroups = aLen / 3;
        int numBytesInPartialGroup = aLen - 3 * numFullGroups;
        int resultLen = 4 * ((aLen + 2) / 3);
        StringBuffer result = new StringBuffer(resultLen);
        char[] intToAlpha = (alternate ? intToAltBase64 : intToBase64);

        // Translate all full groups from byte array elements to Base64
        int inCursor = 0;
        for (int i = 0; i < numFullGroups; i++) {
            int byte0 = a[inCursor++] & 0xff;
            int byte1 = a[inCursor++] & 0xff;
            int byte2 = a[inCursor++] & 0xff;
            result.append(intToAlpha[byte0 >> 2]);
            result.append(intToAlpha[(byte0 << 4) & 0x3f | (byte1 >> 4)]);
            result.append(intToAlpha[(byte1 << 2) & 0x3f | (byte2 >> 6)]);
            result.append(intToAlpha[byte2 & 0x3f]);
        }

        // Translate partial group if present
        if (numBytesInPartialGroup != 0) {
            int byte0 = a[inCursor++] & 0xff;
            result.append(intToAlpha[byte0 >> 2]);
            if (numBytesInPartialGroup == 1) {
                result.append(intToAlpha[(byte0 << 4) & 0x3f]);
                result.append("==");
            } else {
                // assert numBytesInPartialGroup == 2;
                int byte1 = a[inCursor++] & 0xff;
                result.append(intToAlpha[(byte0 << 4) & 0x3f | (byte1 >> 4)]);
                result.append(intToAlpha[(byte1 << 2) & 0x3f]);
                result.append('=');
            }
        }
        // assert inCursor == a.length;
        // assert result.length() == resultLen;
        return result.toString();
    }

    /**
     * This array is a lookup table that translates 6-bit positive integer
     * index values into their "Base64 Alphabet" equivalents as specified
     * in Table 1 of RFC 2045.
     */
    private final char intToBase64[] =
            {
                    'A',
                    'B',
                    'C',
                    'D',
                    'E',
                    'F',
                    'G',
                    'H',
                    'I',
                    'J',
                    'K',
                    'L',
                    'M',
                    'N',
                    'O',
                    'P',
                    'Q',
                    'R',
                    'S',
                    'T',
                    'U',
                    'V',
                    'W',
                    'X',
                    'Y',
                    'Z',
                    'a',
                    'b',
                    'c',
                    'd',
                    'e',
                    'f',
                    'g',
                    'h',
                    'i',
                    'j',
                    'k',
                    'l',
                    'm',
                    'n',
                    'o',
                    'p',
                    'q',
                    'r',
                    's',
                    't',
                    'u',
                    'v',
                    'w',
                    'x',
                    'y',
                    'z',
                    '0',
                    '1',
                    '2',
                    '3',
                    '4',
                    '5',
                    '6',
                    '7',
                    '8',
                    '9',
                    '+',
                    '/' };

    /**
     * This array is a lookup table that translates 6-bit positive integer
     * index values into their "Alternate Base64 Alphabet" equivalents.
     * This is NOT the real Base64 Alphabet as per in Table 1 of RFC 2045.
     * This alternate alphabet does not use the capital letters.  It is
     * designed for use in environments where "case folding" occurs.
     */
    private final char intToAltBase64[] =
            {
                    '!',
                    '"',
                    '#',
                    '$',
                    '%',
                    '&',
                    '\'',
                    '(',
                    ')',
                    ',',
                    '-',
                    '.',
                    ':',
                    ';',
                    '<',
                    '>',
                    '@',
                    '[',
                    ']',
                    '^',
                    '`',
                    '_',
                    '{',
                    '|',
                    '}',
                    '~',
                    'a',
                    'b',
                    'c',
                    'd',
                    'e',
                    'f',
                    'g',
                    'h',
                    'i',
                    'j',
                    'k',
                    'l',
                    'm',
                    'n',
                    'o',
                    'p',
                    'q',
                    'r',
                    's',
                    't',
                    'u',
                    'v',
                    'w',
                    'x',
                    'y',
                    'z',
                    '0',
                    '1',
                    '2',
                    '3',
                    '4',
                    '5',
                    '6',
                    '7',
                    '8',
                    '9',
                    '+',
                    '?' };

    public void deleteContactUser(String uid) throws TcSecurityException {
        //Wenn keine Verbindung besteht, abbruch
        if (lctx == null) {
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR);
        }
        if (!checkuid(uid)) {
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR);
        }
        try {
            lctx.destroySubcontext(getUserDN(uid) + relative + baseDN);
        } catch (NamingException e) {
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR, e);
        }
    }

    /**
     * Kleiner Helper, der aufpasst, das bei relative am Anfang und am Ende ein Komma ist
     *
     * @param relative
     * @return relative
     */
    private String cleanup_relative(String relative) {
        //testen, ob am Anfang kein Komma, sonst hinzufügen
        if (relative.equals(" ")) {
            relative = "";
        } //$NON-NLS-1$ //$NON-NLS-2$

        if (!relative.startsWith(",")) {
            relative = "," + relative;
        } //$NON-NLS-1$ //$NON-NLS-2$
        //das ganze nochmal am Ende
        if (!relative.endsWith(",")) {
            relative = relative + ",";
        } //$NON-NLS-1$ //$NON-NLS-2$
        return relative;
    }

    public String getUserDN(String uid) throws TcSecurityException {
        if (lctx == null) {
            try {
                login();
            } catch (TcSecurityException e1) {
                throw e1;
            }
        }
        String dn = null;
        Attributes attr = new BasicAttributes();
        attr.put("uid", uid);
        attr.put("objectclass", "tarentContact");
        try {
            NamingEnumeration ne = lctx.search(relative.substring(1) + baseDN, attr);
            if (!ne.hasMore()) {
                throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR,
                        "Der User ist im LDAP nicht vorhanden!");
            }
            SearchResult search = (SearchResult) ne.next();
            dn = search.getName();
            if (ne.hasMore()) {
                throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR,
                        "Der User ist nicht eindeutig, bitte wählen sie einen anderen Usernamen");
            }
        } catch (NamingException e) {
            throw new TcSecurityException(
                    "Es ist ein Fehler beim Holen des Users aus dem LDAP aufgetreten. Bitte versuchen Sie es später noch einmal" +
                     ".");
        }
        return dn;
    }

    /**
     * @param userName
     */
    public Map getUserData(String userName) throws TcSecurityException {
        Map userdata = new HashMap();
        String dn = getUserDN(userName);
        try {
            Attributes attr = lctx.getAttributes(dn + relative + baseDN);
            Attribute vorname = null, nachname = null, name = null, mail = null;
            Attribute adminflag = null;
            if (attr.get("givenname") != null) {
                vorname = attr.get("givenname");
            }
            if (attr.get("gn") != null) {
                vorname = attr.get("gn");
            }
            if (attr.get("surname") != null) {
                nachname = attr.get("sn");
            }
            if (attr.get("sn") != null) {
                nachname = attr.get("sn");
            }
            if (attr.get("commonname") != null) {
                name = attr.get("commonname");
            }
            if (attr.get("cn") != null) {
                name = attr.get("cn");
            }
            if (attr.get("mail") != null) {
                mail = attr.get("mail");
            }
            if (attr.get("adminflag") != null) {
                adminflag = attr.get("adminflag");
            }

            if (vorname != null) {
                userdata.put("vorname", vorname.get());
            }
            if (nachname != null) {
                userdata.put("nachname", nachname.get());
            }
            if (name != null) {
                userdata.put("name", name.get());
            }
            if (mail != null) {
                userdata.put("mail", mail.get());
            }
            if (adminflag != null) {
                userdata.put("adminflag", adminflag.get());
            }
        } catch (NamingException e) {
            throw new TcSecurityException("Es ist ein Fehler beim Holen der Userdaten aufgetreten! ", e);
        }
        logger.debug(userdata.toString());
        return userdata;
    }
}
