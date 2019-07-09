package de.tarent.ldap;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import de.tarent.ldap.contact.ContactUser;
import de.tarent.octopus.security.TcSecurityException;
import de.tarent.octopus.server.UserManager;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.InitialLdapContext;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Diese Klasse enthält die Ergänzungen zum LDAPManager zum Zugriff auf
 * Spezialitäten von tarent-contact.
 *
 * @author mikel
 */
@Log4j2
public class LDAPManagerTarentContact extends LDAPManager implements UserManager {
    //
    // Konstanten
    //
    /**
     * LDAP-Objektklasse für tarent-contact-Kontakte
     */
    public final static String OBJECT_CLASS_TARENT_CONTACT = "TarentContact";
    /**
     * Schlüssel für die Nichtverwendung der {@link #OBJECT_CLASS_TARENT_CONTACT}
     */
    public final static String KEY_OBJECTCLASS = "disableForceTarentObjectClass";
    public final static String KEY_REALLYDELETE = "deleteOnNoObjectClass";
    public final static String KEY_GOSASUPPORT = "enableGoSASupport";

    //
    //private Variablen
    //
    private boolean use_tarent_objectclass = true;
    private boolean gosa_support = false;
    private boolean really_delete = false;
    //
    // Konstruktoren
    //

    /**
     * Dieser Konstruktor reicht die Daten durch oder legt sie ab. Nebenbei werden
     * die Default-Objektklassen gesetzt.
     *
     * @param lctx   initialer LDAP-Kontext, auf dem dieser LDAP-Manager arbeitet
     * @param params LDAP-Manager-Parameter, vergleiche
     *               {@link LDAPManager#LDAPManager(InitialLdapContext, Map)}
     */
    public LDAPManagerTarentContact(InitialLdapContext lctx, Map params) {
        super(lctx, params);
        use_tarent_objectclass = !isParameterTrue(params.get(KEY_OBJECTCLASS));
        gosa_support = isParameterTrue(params.get(KEY_GOSASUPPORT));
        really_delete = isParameterTrue(params.get(KEY_REALLYDELETE));
        if (use_tarent_objectclass) {
            setDefaultObjectClasses(new String[] { OBJECT_CLASS_TARENT_CONTACT });
        }
    }

    /**
     * @param param FIXME
     */
    private boolean isParameterTrue(Object param) {
        return ("true".equals(param) || "1".equals(param) || "TRUE".equals(param) || "True".equals(param));
    }

    /**
     * Fügt einen Contact zum LDAP hinzu
     *
     * @param ldc der Kontakt, der hinzugefügt werden soll
     * @throws LDAPException wenn etwas schief läuft
     */
    public void addContact(LDAPContact ldc) throws LDAPException {
        //Object anlegen
        try {
            Map verteiler = ldc.getVerteilergruppe();
            Iterator it = verteiler.keySet().iterator();
            String gruppe;
            Map allUsers = ldc.getAllUsers();
            while (it.hasNext()) {
                String key = (String) it.next();
                gruppe = (String) verteiler.get(key);
                ldc.setUsers((List) allUsers.get(key));
                addContact_restricted(ldc, gruppe);
            }
        } catch (NameAlreadyBoundException nabe) {
            throw new LDAPException(Messages.getString("LDAPManager.entry_already_there_01"), nabe); //$NON-NLS-1$
        } catch (NamingException e) {
            throw new LDAPException(Messages.getString("LDAPManager.fehler_anlegen_01") + e.getMessage(), e); //$NON-NLS-1$
        }
    }

    /**
     * Fügt einen einzelnen Kontakt zum LDAP hinzu
     *
     * @param ldapContact der LDAPContact
     * @param gruppe      Kategorie, zu der der Kontakt hinzugefügt werden soll
     * @throws LDAPException   wenn etwas schiefläuft
     * @throws NamingException wenn etwas schiefläuft
     * @see de.tarent.ldap.LDAPContact
     */
    public void addContact_restricted(LDAPContact ldapContact, String gruppe) throws LDAPException, NamingException {
        if (checkOu(gruppe) == false) {
            createOU(gruppe, ldapContact.getUsers());
        }
        try {
            BasicAttributes attrs = ldapContact.generateAttributesRestricted(this);
            lctx.createSubcontext("uid=" + ldapContact.getUserid() + ",ou=" + gruppe + relative + baseDN, attrs); //NON-NLS-1$
        } catch (NoMemberException nme) {
            logger.warn("Konnte Adresse " + ldapContact.getUserid() +
              " nicht exportieren, da kein User hierauf Berechtigungen hat.");
        }
    }

    /**
     * Löscht einen User aus dem LDAP
     *
     * @param uid FIXME
     * @throws LDAPException
     */
    public void deleteContactUser(String uid) throws LDAPException {
        if (!checkuid(uid)) {
            throw new LDAPException("Der User ist bereits gelöscht worden.");
        }
        try {
            //FIXME: ueberarbeiten!!!
            String dn = fullUserDN(uid);
            if (use_tarent_objectclass) {
                Attribute objectClass;
                Vector mods = new Vector();
                try {
                    String[] objectClassA = { "objectClass" };
                    objectClass = lctx.getAttributes(dn, objectClassA).get("objectClass");
                } catch (NamingException e1) {
                    throw new LDAPException(e1);
                }
                if (this.checkAttribute(uid, "objectClass", "TarentContact")) {
                    objectClass.remove("TarentContact");
                }
                mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, objectClass));
                logger.debug(mods.toString());
                lctx.modifyAttributes(dn, (ModificationItem[]) mods.toArray(new ModificationItem[1]));
            } else {
                if (really_delete) {
                    lctx.destroySubcontext(dn);
                }
            }
        } catch (NamingException e) {
            throw new LDAPException("Fehler beim Löschen des Users!", e);
        }
    }

    /**
     * Methode, die einen Kontakt löscht
     *
     * @param uid UserID des Kontakts, relative und baseDN werden automatisch
     *            angehängt
     * @throws LDAPException wenn etwas schief läuft
     */
    public void delContact(String uid) throws LDAPException {
        //Object löschen
        try {
            lctx.destroySubcontext(uid + relative + baseDN);
        } catch (NamingException e) {
            new LDAPException(Messages.getString("LDAPManager.fehler_loeschen_01"), e); //$NON-NLS-1$
        }
    }

    /**
     * Testet, ob Kontakt im LDAP vorhanden
     *
     * @param ldc    Kontakt, die getestet werden soll
     * @param gruppe Gruppe, in der der Kontakt gesucht werden soll
     * @return true, wenn vorhanden, false sonst
     * @throws LDAPException
     */
    public boolean checkContact(LDAPContact ldc, String gruppe) throws LDAPException {
        boolean vorhanden = false;
        try {
            BasicAttributes zusuchendeAttribute = new BasicAttributes(true);
            zusuchendeAttribute.put("uid", ldc.getUserid()); //$NON-NLS-1$
            NamingEnumeration ergebnis = lctx.search("ou=" + gruppe + relative + baseDN, zusuchendeAttribute); //$NON-NLS-1$
            if (ergebnis.hasMore()) {
                vorhanden = true;
            }
        } catch (NamingException e) {
            vorhanden = false;
        }
        return vorhanden;
    }

    /**
     * Modifiziert gegebenen Kontakt
     *
     * @param ldc     der Kontakt
     * @param allvert Map mit allen Verteilergruppen
     * @throws LDAPException
     */
    public void modifiyContact(LDAPContact ldc, Map allvert) throws LDAPException {
        Map verteiler = ldc.getVerteilergruppe();
        Map allUsers = ldc.getAllUsers();
        Iterator it = verteiler.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String gruppe = (String) verteiler.get(key);
            ldc.setUsers((List) allUsers.get(key));
            if (!checkContact(ldc, gruppe)) {
                try {
                    addContact_restricted(ldc, gruppe);
                } catch (LDAPException e) {
                    throw new LDAPException(e.getMessage(), e);
                } catch (NamingException e) {
                    throw new LDAPException(e.getMessage(), e);
                }
            } else {
                modifyContact_restricted(ldc, gruppe);
            }
        }
        //Checken, ob Kontakt in Verteilergruppen ist, in die er nicht
        // reingehört...
        it = verteiler.keySet().iterator();
        while (it.hasNext()) {
            allvert.remove(it.next());
        }
        it = allvert.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String gruppe = (String) allvert.get(key);
            if (checkContact(ldc, gruppe)) {
                delContact("uid=" + ldc.getUserid() + ",ou=" + gruppe + relative + baseDN); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }

    /**
     * Methode, die einen gegebenen Kontakt im LDAP modifiziert
     *
     * @param ldc     der zu modifizierende Kontakt
     * @param vertgrp Verteilergruppe, in der der Kontakt sich befindet
     * @throws LDAPException wenn etwas schief läuft
     * @see de.tarent.ldap.LDAPContact
     */
    public void modifyContact_restricted(LDAPContact ldc, String vertgrp) throws LDAPException {
        List modifications = new ArrayList();
        Element mapping = null;
        try {
            InputStream is = LDAPContact.class.getResourceAsStream("/de/tarent/octopus/sync/ldap/mapping.xml"); //$NON-NLS-1$
            mapping = XMLUtil.getParsedDocument(new InputSource(is)).getDocumentElement();
        } catch (Exception e) {
            throw new LDAPException(e.getMessage(), e);
        }
        Node objectclassnode = XMLUtil.getObjectClass(mapping);
        NodeList objectClassList = objectclassnode.getChildNodes();
        //relative filtern
        relative = surroundWithCommas(relative);
        //alten Contact aus dem LDAP holen
        Attributes contact = null;
        try {
            contact = lctx.getAttributes(
              "uid=" + ldc.getUserid() + ",ou=" + vertgrp + relative + baseDN); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (NamingException e1) {
            throw new LDAPException(Messages.getString("LDAPManager.Contact_01") + ldc.getUserid()
              + Messages.getString("LDAPManager._existiert_gar_nicht_01") + e1.getMessage(), e1); //$NON-NLS-1$
        }
        //hole ObjectClass
        Attribute objectClass = contact.get("objectClass"); //$NON-NLS-1$
        //checke objectclass
        boolean modified = false;
        for (int i = 0; i < objectClassList.getLength(); i++) {
            String value = objectClassList.item(i).getAttributes().item(0).getNodeValue();
            if (!checkAttribute(contact, "objectClass", value)) {
                objectClass.add(value);
                modified = true;
            }
        }
        //Wenn geändert, dann modifizieren
        if (modified) {
            modifications.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, objectClass));
        }
        //hole member
        Attribute member = contact.get("member"); //$NON-NLS-1$
        //checke objectclass
        modified = false;
        for (int i = 0; i < ldc.getUsers().size(); i++) {
            try {
                String value = fullUserDN(ldc.getUsers().get(i).toString());
                if (!checkAttribute(contact, "member", value)) {
                    member.add(value);
                    modified = true;
                }
            } catch (LDAPException le) {
                logger.warn(Messages.getString("LDAPManager.84") + ldc.getUsers().get(i).toString()
                  + Messages.getString("LDAPManager.85"), le); //$NON-NLS-1$
            }
        }
        //Wenn geändert, dann modifizieren
        if (modified) {
            modifications.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, member));
        }
        //Checke Rest vom Kontakt
        NodeList kinder = XMLUtil.getRelevantChildren(mapping);
        for (int i = 0; i < kinder.getLength(); i++) {
            modifications = modifyAttributes_restricted(modifications, contact, kinder.item(i).getAttributes().item(0)
              .getNodeValue(), ldc.getValue(kinder.item(i).getAttributes()));
        }
        logger.debug(Messages.getString("LDAPManager.86") + modifications); //$NON-NLS-1$
        //Führe Änderungen durch
        if (!modifications.isEmpty()) {
            try {
                lctx.modifyAttributes("uid=" + ldc.getUserid() + ",ou=" + vertgrp + relative + baseDN,
                  (ModificationItem[]) modifications.toArray(new ModificationItem[1]));
            } catch (NamingException e2) {
                throw new LDAPException(Messages.getString("LDAPManager.User_konnte_nicht_modifiziert_werden_01")
                  + e2.toString(), e2);
            }
        }
    }

    /**
     * Löscht eine OU im LDAP
     *
     * @param testou OU, die gelöscht werden sollen
     */
    public void delOU(String testou) throws LDAPException {
        //checke, ob Objekt kinder hat
        try {
            List uids = this.getUIDs("ou=" + testou + relative + baseDN); //$NON-NLS-1$
            for (int i = 0; i < uids.size(); i++) {
                delContact("uid=" + uids.get(i) + ",ou=" + testou); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch (LDAPException e) {
            new LDAPException(Messages.getString("LDAPManager.fehler_loeschen_01"), e); //$NON-NLS-1$
        }
        //Object löschen
        try {
            lctx.destroySubcontext("ou=" + testou + relative + baseDN); //$NON-NLS-1$
        } catch (NamingException e) {
            new LDAPException(Messages.getString("LDAPManager.fehler_loeschen_01"), e); //$NON-NLS-1$
        }
    }

    public ContactUser getContactUser(String uid) throws LDAPException {
        return new ContactUser(getEntry(fullUserDN(uid)));
    }

    /**
     * @param userName FIXME
     * @throws LDAPException
     */
    public Map getUserData(String userName) throws LDAPException {
        Map userdata = new HashMap();
        String dn = fullUserDN(userName);
        try {
            Attributes attr = lctx.getAttributes(dn);
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
            throw new LDAPException("Es ist ein Fehler beim Holen der Userdaten aufgetreten! ", e);
        }
        logger.debug(userdata.toString());
        return userdata;
    }

    /**
     * Methode, um einen User im LDAP anzulegen
     *
     * @param userid   BenutzerID
     * @param vorname  Vorname
     * @param nachname Nachname
     * @param passwort Passwort
     * @throws LDAPException
     */
    public void addContactUser(
      String userid,
      String vorname,
      String nachname,
      String passwort) throws LDAPException {
        String password_enc2 = null;
        if (passwort != null && passwort.length() > 0) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                password_enc2 = byteArrayToBase64(md.digest(passwort.getBytes()), false);
            } catch (NoSuchAlgorithmException e) {
                throw new LDAPException(e);
            }
            addContactUserRawPassword(userid, vorname, nachname, "{SHA}" + password_enc2);
        } else {
            addContactUserRawPassword(userid, vorname, nachname, passwort);
        }
    }

    public void addContactUserRawPassword(String userid,
      String vorname,
      String nachname,
      String passwort) throws LDAPException {
        logger.debug("Füge User " + userid + " hinzu.");
        if (checkuid(userid)) {
            modifyContactUserRawPassword(userid, vorname, nachname, passwort);
        } else {
            BasicAttributes attr = new BasicAttributes();
            BasicAttribute objectClass = new BasicAttribute("objectClass", "top");
            objectClass.add("simpleSecurityObject");
            objectClass.add("person");
            objectClass.add("organizationalPerson");
            objectClass.add("inetOrgPerson");
            if (use_tarent_objectclass) {
                objectClass.add("TarentContact");
            }
            if (gosa_support) {
                objectClass.add("gosaAccount");
            }
            attr.put(objectClass);
            attr.put("uid", userid);
            attr.put("sn", nachname);
            attr.put("cn", nachname + " " + vorname);
            attr.put("gn", vorname);
            attr.put("userPassword", passwort);
            try {
                if (gosa_support) {
                    lctx.createSubcontext("cn=" + nachname + " " + vorname + relative + baseDN, attr);
                } else {
                    lctx.createSubcontext("uid=" + userid + relative + baseDN, attr);
                }
            } catch (NamingException e) {
                throw new LDAPException(e);
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
     * @throws LDAPException
     */
    public void modifyContactUserRawPassword(String userid, String vorname, String nachname, String passwort)
      throws LDAPException {
        logger.debug("Modifiziere LDAP-User: " + userid);
        Vector mods = new Vector();

        //sichere ObjectClass
        Attribute objectClass;
        try {
            String[] objectClassA = { "objectClass" };
            objectClass = lctx.getAttributes(fullUserDN(userid), objectClassA).get("objectClass");
        } catch (NamingException e1) {
            throw new LDAPException(e1);
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
        if (use_tarent_objectclass && !this.checkAttribute(userid, "objectClass", "TarentContact")) {
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
        if (gosa_support && !this.checkAttribute(userid, "objectClass", "gosaAccount")) {
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

        //Checke userPassword
        if (passwort != null && passwort.length() > 0) {
            if (!this.checkAttribute(userid, "userPassword")) {
                mods.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("userPassword", passwort)));
            }
            mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", passwort)));
        }
        try {
            logger.debug(mods.toString());
            lctx.modifyAttributes(fullUserDN(userid), (ModificationItem[]) mods.toArray(new ModificationItem[1]));
        } catch (NamingException e2) {
            throw new LDAPException(e2);
        }
    }

    /**
     * Modifiziere User so, dass er angegebenen Daten entspricht
     *
     * @param userid   User, der modifiziert werden soll
     * @param vorname  Vorname
     * @param nachname Nachname
     * @param passwort Passwort
     * @throws LDAPException
     */
    public void modifyContactUser(String userid, String vorname, String nachname, String passwort) throws LDAPException {
        String password_enc2 = null;
        if (passwort != null && passwort.length() > 0) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                password_enc2 = byteArrayToBase64(md.digest(passwort.getBytes()), false);
            } catch (NoSuchAlgorithmException e) {
                throw new LDAPException(e);
            }
            modifyContactUserRawPassword(userid, vorname, nachname, "{SHA}" + password_enc2);
        } else {
            modifyContactUserRawPassword(userid, vorname, nachname, passwort);
        }
    }

    /**
     * Modfiziert des Attribute attribut des Users userid
     *
     * @param userid    UserId des Users
     * @param attribute zu modifizierendes Attribut
     * @param value     neue Value
     */
    public void modifyContactUserAttribute(String userid, String attribute, String value) throws LDAPException {
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
                lctx.modifyAttributes(fullUserDN(userid), (ModificationItem[]) mods.toArray(new ModificationItem[1]));
            } catch (NamingException e2) {
                throw new LDAPException(e2);
            }
        }
    }

    /* (non-Javadoc)
     * @see de.tarent.octopus.server.UserManager#addUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void addUser(String userID, String firstName, String lastName, String password) throws TcSecurityException {
        try {
            addContactUser(userID, firstName, lastName, password);
        } catch (LDAPException e) {
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_USERMANAGEMENT_ERROR, e);
        }
    }

    /* (non-Javadoc)
     * @see de.tarent.octopus.server.UserManager#modifyUser(java.lang.String, java.lang.String, java.lang.String, java.lang
     * .String)
     */
    public void modifyUser(String userID, String firstName, String lastName, String password) throws TcSecurityException {
        try {
            modifyContactUser(userID, firstName, lastName, password);
        } catch (LDAPException e) {
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_USERMANAGEMENT_ERROR, e);
        }
    }

    /* (non-Javadoc)
     * @see de.tarent.octopus.server.UserManager#deleteUser(java.lang.String)
     */
    public void deleteUser(String userID) throws TcSecurityException {
        try {
            deleteContactUser(userID);
        } catch (LDAPException e) {
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_USERMANAGEMENT_ERROR, e);
        }
    }

    /* (non-Javadoc)
     * @see de.tarent.octopus.server.UserManager#setUserParam(java.lang.String, java.lang.String, java.lang.Object)
     */
    public void setUserParam(String userID, String paramname, Object paramvalue) throws TcSecurityException {
        try {
            modifyContactUserAttribute(userID, paramname, paramvalue.toString());
        } catch (LDAPException e) {
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_USERMANAGEMENT_ERROR, e);
        }
    }
}
