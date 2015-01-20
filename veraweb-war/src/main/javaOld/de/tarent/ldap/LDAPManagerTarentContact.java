/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.tarent.ldap.contact.ContactUser;
import de.tarent.octopus.security.TcSecurityException;
import de.tarent.octopus.server.UserManager;

/**
 * Diese Klasse enthält die Ergänzungen zum LDAPManager zum Zugriff auf
 * Spezialitäten von tarent-contact.
 *
 * @author mikel
 */
public class LDAPManagerTarentContact extends LDAPManager implements UserManager {
    //
    // Konstanten
    //
    /** LDAP-Objektklasse für tarent-contact-Kontakte */
    public final static String OBJECT_CLASS_TARENT_CONTACT = "TarentContact";
    /** Schlüssel für die Nichtverwendung der {@link #OBJECT_CLASS_TARENT_CONTACT} */
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
     * @param lctx initialer LDAP-Kontext, auf dem dieser LDAP-Manager arbeitet
     * @param params LDAP-Manager-Parameter, vergleiche
     *  {@link LDAPManager#LDAPManager(InitialLdapContext, Map)}
     */
    public LDAPManagerTarentContact(InitialLdapContext lctx, Map params) {
        super(lctx, params);
		use_tarent_objectclass = !isParameterTrue(params.get(KEY_OBJECTCLASS));
		gosa_support = isParameterTrue(params.get(KEY_GOSASUPPORT));
		really_delete = isParameterTrue(params.get(KEY_REALLYDELETE));
		if(use_tarent_objectclass){
			setDefaultObjectClasses(new String[] {OBJECT_CLASS_TARENT_CONTACT});
		}
    }

	/**
	 * @param param
	 */
	private boolean isParameterTrue(Object param) {
		if(("true".equals(param)||"1".equals(param)||"TRUE".equals(param)||"True".equals(param))){
			return true;
		}else{
			return false;
		}
	}

    /**
     * Fügt einen Contact zum LDAP hinzu
     *
     * @param ldc
     *            der Kontakt, der hinzugefügt werden soll
     * @throws LDAPException
     *             wenn etwas schief läuft
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
     * @param ldc
     *            der LDAPContact
     * @param gruppe
     *            Kategorie, zu der der Kontakt hinzugefügt werden soll
     * @throws LDAPException
     *             wenn etwas schiefläuft
     * @throws NamingException
     *             wenn etwas schiefläuft
     * @see de.tarent.octopus.sync.ldap.LDAPContact
     */
    public void addContact_restricted(LDAPContact ldc, String gruppe) throws LDAPException, NamingException {
        if (checkOu(gruppe) == false) {
            createOU(gruppe, ldc.getUsers());
        }
        try{
            BasicAttributes attrs = ldc.generate_Attributes_restricted(this);
            lctx.createSubcontext("uid=" + ldc.getUserid() + ",ou=" + gruppe + relative + baseDN, attrs); //NON-NLS-1$
        }catch(NoMemberException nme){
            logger.warning("Konnte Adresse " + ldc.getUserid() + " nicht exportieren, da kein User hierauf Berechtigungen hat.");
        }
    }

    /**
     * Löscht einen User aus dem LDAP
     * @param uid
     * @throws LDAPException
     */
    public void deleteContactUser(String uid) throws LDAPException{
        if(!checkuid(uid)){
            throw new LDAPException("Der User ist bereits gelöscht worden.");
        }
        try {
        	//FIXME: ueberarbeiten!!!
        	String dn = getUserDN(uid);
        	if(use_tarent_objectclass){
                Attribute objectClass;
                Vector mods = new Vector();
                try {
                    String[] objectClassA = {"objectClass"};
                    objectClass = lctx.getAttributes(dn+relative+baseDN, objectClassA).get("objectClass");
                } catch (NamingException e1) {
                    throw new LDAPException(e1);
                }
                if(this.checkAttribute(uid, "objectClass", "TarentContact")){
                	objectClass.remove("TarentContact");
                }
                mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, objectClass));
                logger.log(Level.FINE, mods.toString());
                lctx.modifyAttributes(dn+relative+baseDN, (ModificationItem[]) mods.toArray(new ModificationItem[1]));
        	}else{
        		if(really_delete){
        			lctx.destroySubcontext(dn+relative + baseDN);
        		}
        	}
        } catch (NamingException e) {
            throw new LDAPException("Fehler beim Löschen des Users!", e);
        }
    }


    /**
     * Methode, die einen Kontakt löscht
     *
     * @param uid
     *            UserID des Kontakts, relative und baseDN werden automatisch
     *            angehängt
     * @throws LDAPException
     *             wenn etwas schief läuft
     */
    public void delContact(String uid) throws LDAPException {
        //Object löschen
        try {
            lctx.destroySubcontext(uid + relative + baseDN);
        } catch (NamingException e) {
            new LDAPException(Messages.getString("LDAPManager.fehler_löschen_01"), e); //$NON-NLS-1$
        }
    }

    /**
     * Testet, ob Kontakt im LDAP vorhanden
     *
     * @param ldc
     *            Kontakt, die getestet werden soll
     * @param gruppe
     *            Gruppe, in der der Kontakt gesucht werden soll
     * @return true, wenn vorhanden, false sonst
     * @throws LDAPException,
     *             wenn Fehler auftritt
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
     * @param ldc
     *            der Kontakt
     * @param allvert
     *            Map mit allen Verteilergruppen
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
     * @param ldc
     *            der zu modifizierende Kontakt
     * @param vertgrp
     *            Verteilergruppe, in der der Kontakt sich befindet
     * @throws LDAPException
     *             wenn etwas schief läuft
     * @see de.tarent.octopus.sync.ldap.LDAPContact
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
            contact = lctx.getAttributes("uid=" + ldc.getUserid() + ",ou=" + vertgrp + relative + baseDN); //$NON-NLS-1$ //$NON-NLS-2$
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
                String value = getUserDN(ldc.getUsers().get(i).toString()) + relativeUser + baseDN;
                if (!checkAttribute(contact, "member", value)) {
                    member.add(value);
                    modified = true;
                }
            } catch (LDAPException le) {
                logger.log(Level.WARNING, Messages.getString("LDAPManager.84") + ldc.getUsers().get(i).toString()
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
        logger.log(Level.FINE, Messages.getString("LDAPManager.86") + modifications); //$NON-NLS-1$
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
     * @param testou
     *            OU, die gelöscht werden sollen
     */
    public void delOU(String testou) throws LDAPException {
        //checke, ob Objekt kinder hat
        try {
            List uids = this.getUIDs("ou=" + testou + relative + baseDN); //$NON-NLS-1$
            for (int i = 0; i < uids.size(); i++) {
                delContact("uid=" + uids.get(i) + ",ou=" + testou); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch (LDAPException e) {
            new LDAPException(Messages.getString("LDAPManager.fehler_löschen_01"), e); //$NON-NLS-1$
        }
        //Object löschen
        try {
            lctx.destroySubcontext("ou=" + testou + relative + baseDN); //$NON-NLS-1$
        } catch (NamingException e) {
            new LDAPException(Messages.getString("LDAPManager.fehler_löschen_01"), e); //$NON-NLS-1$
        }
    }

    public ContactUser getContactUser(String uid) throws LDAPException {
        return new ContactUser(getEntry(getUserDN(uid) + relativeUser + baseDN));
    }


    /**
     * @param userName
     * @throws LDAPException
     */
    public Map getUserData(String userName) throws LDAPException {
        Map userdata = new HashMap();
        String dn = getUserDN(userName);
        try {
            Attributes attr = lctx.getAttributes(dn+relative+baseDN);
            Attribute vorname = null, nachname = null, name = null, mail=null;
            Attribute adminflag = null;
            if(attr.get("givenname")!=null) {vorname = attr.get("givenname");}
            if(attr.get("gn")!=null) {vorname = attr.get("gn");}
            if(attr.get("surname")!=null) {nachname = attr.get("sn");}
            if(attr.get("sn")!=null) {nachname = attr.get("sn");}
            if(attr.get("commonname")!=null) {name = attr.get("commonname");}
            if(attr.get("cn")!=null) {name = attr.get("cn");}
            if(attr.get("mail")!=null) {mail = attr.get("mail");}
            if(attr.get("adminflag")!=null){adminflag = attr.get("adminflag");}

            if(vorname!=null)userdata.put("vorname", vorname.get());
            if(nachname!=null)userdata.put("nachname", nachname.get());
            if(name!=null)userdata.put("name", name.get());
            if(mail!=null)userdata.put("mail", mail.get());
            if(adminflag!=null)userdata.put("adminflag", adminflag.get());
        } catch (NamingException e) {
            throw new LDAPException("Es ist ein Fehler beim Holen der Userdaten aufgetreten! ", e);
        }
        logger.log(Level.FINE, userdata.toString());
        return userdata;
    }

    /**
     * Methode, um einen User im LDAP anzulegen
     *
     * @param userid    BenutzerID
     * @param vorname   Vorname
     * @param nachname  Nachname
     * @param passwort  Passwort
     * @param gruppe    Gruppe, wird im Moment nicht genutzt
     * @throws LDAPException
     */
    public void addContactUser(
        String userid,
        String vorname,
        String nachname,
        String passwort) throws LDAPException
    {
    	String password_enc2 = null;
        if(passwort!=null&&passwort.length()>0){
            try
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                password_enc2 = byteArrayToBase64(md.digest(passwort.getBytes()), false);
                } catch (NoSuchAlgorithmException e)
            {
                throw new LDAPException(e);
            }
            addContactUserRawPassword(userid, vorname, nachname, "{SHA}"+password_enc2);
        }else{
        	addContactUserRawPassword(userid, vorname, nachname, passwort);
        }
    }

    public void addContactUserRawPassword(        String userid,
            String vorname,
            String nachname,
            String passwort) throws LDAPException
        {
        logger.log(Level.FINE, "Füge User " + userid + " hinzu.");
        if(checkuid(userid)){modifyContactUserRawPassword(userid, vorname, nachname, passwort);}
        else{
            BasicAttributes attr = new BasicAttributes();
            BasicAttribute objectClass = new BasicAttribute("objectClass", "top");
            objectClass.add("simpleSecurityObject");
            objectClass.add("person");
            objectClass.add("organizationalPerson");
            objectClass.add("inetOrgPerson");
            if(use_tarent_objectclass)objectClass.add("TarentContact");
            if(gosa_support)objectClass.add("gosaAccount");
            attr.put(objectClass);
            attr.put("uid", userid);
            attr.put("sn", nachname);
            attr.put("cn", nachname + " " + vorname);
            attr.put("gn", vorname);
            attr.put("userPassword", passwort);
            try
            {
                if(gosa_support){
                	lctx.createSubcontext("cn=" + nachname + " " + vorname + relative + baseDN, attr);
                }else{
                	lctx.createSubcontext("uid=" + userid + relative + baseDN, attr);
                }
            } catch (NamingException e)
                {
                    throw new LDAPException(e);
                }
            }
    }

    /**
     * Modifiziere User so, dass er angegebenen Daten entspricht
     * @param userid User, der modifiziert werden soll
     * @param vorname   Vorname
     * @param nachname  Nachname
     * @param passwort  Passwort
     * @param gruppe    Gruppe(n) des Users
     * @throws LDAPException
     */
    public void modifyContactUserRawPassword(String userid, String vorname, String nachname, String passwort) throws LDAPException{
        logger.log(Level.FINE, "Modifiziere LDAP-User: " + userid);
        Vector mods = new Vector();

        //sichere ObjectClass
        Attribute objectClass;
        try {
            String[] objectClassA = {"objectClass"};
            objectClass = lctx.getAttributes(getUserDN(userid)+relative+baseDN, objectClassA).get("objectClass");
        } catch (NamingException e1) {
            throw new LDAPException(e1);
        }

        //Checke Objectclass
        boolean modified = false;
        if(!this.checkAttribute(userid, "objectClass", "top")){objectClass.add("top");modified=true;}
        if(!this.checkAttribute(userid, "objectClass", "simpleSecurityObject")){objectClass.add("simpleSecurityObject");modified=true;}
        if(!this.checkAttribute(userid, "objectClass", "person")){objectClass.add("person");modified=true;}
        if(use_tarent_objectclass&&!this.checkAttribute(userid, "objectClass", "TarentContact")){objectClass.add("TarentContact");modified=true;}
        if(!this.checkAttribute(userid, "objectClass", "organizationalPerson")){objectClass.add("organizationalPerson");modified=true;}
        if(!this.checkAttribute(userid, "objectClass", "inetOrgPerson")){objectClass.add("inetOrgPerson");modified=true;}
        if(gosa_support&&!this.checkAttribute(userid, "objectClass", "gosaAccount")){objectClass.add("gosaAccount");modified=true;}
        if(modified){
            mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, objectClass));
        }
        //Checke sn
        if(!this.checkAttribute(userid, "sn")){mods.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("sn", nachname)));        }
        else if(!this.checkAttribute(userid, "sn", nachname)){mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sn", nachname)));}
        //Checke gn
        if(!this.checkAttribute(userid, "gn")){mods.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("gn", vorname)));     }
        else if(!this.checkAttribute(userid, "gn", vorname)){mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("gn", vorname)));}
        //Checke cn
        if(!this.checkAttribute(userid, "cn")){mods.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("cn", nachname + " " + vorname)));        }
        else if(!this.checkAttribute(userid, "cn", nachname + " " + vorname)){mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("cn", nachname + " " + vorname)));}

        //Checke userPassword
        if(passwort!=null&&passwort.length()>0){
            if(!this.checkAttribute(userid, "userPassword")){mods.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("userPassword", passwort)));        }
            mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", passwort)));
        }
        try {
            logger.log(Level.FINE, mods.toString());
            lctx.modifyAttributes(getUserDN(userid)+relative+baseDN, (ModificationItem[]) mods.toArray(new ModificationItem[1]));
        } catch (NamingException e2) {
            throw new LDAPException(e2);
        }
    }

    /**
     * Modifiziere User so, dass er angegebenen Daten entspricht
     * @param userid User, der modifiziert werden soll
     * @param vorname   Vorname
     * @param nachname  Nachname
     * @param passwort  Passwort
     * @param gruppe    Gruppe(n) des Users
     * @throws LDAPException
     */
    public void modifyContactUser(String userid, String vorname, String nachname, String passwort) throws LDAPException{
    	String password_enc2 = null;
        if(passwort!=null&&passwort.length()>0){
            try
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                password_enc2 = byteArrayToBase64(md.digest(passwort.getBytes()), false);
                } catch (NoSuchAlgorithmException e)
            {
                throw new LDAPException(e);
            }
            modifyContactUserRawPassword(userid, vorname, nachname, "{SHA}"+password_enc2);
        }else{
        	modifyContactUserRawPassword(userid, vorname, nachname, passwort);
        }

    }

    /**
     * Modfiziert des Attribute attribut des Users userid
     * @param userid UserId des Users
     * @param attribute zu modifizierendes Attribut
     * @param value neue Value
     * @throws TcSecurityException Kapselt evtl auftretende NamingException
     */
    public void modifyContactUserAttribute(String userid, String attribute, String value) throws LDAPException{
        logger.log(Level.FINE, "Modifiziere Attribut " + attribute + " von: " + userid + " nach " + value);

        //Checke Attribute
        Vector mods = new Vector();
        if(!this.checkAttribute(userid, attribute)){mods.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute(attribute, value)));     }
        else if(!this.checkAttribute(userid, attribute, value)){mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(attribute, value)));}

        //try to modify user...
        if(!mods.isEmpty()){
            try {
                logger.log(Level.FINE, mods.toString());
                lctx.modifyAttributes(getUserDN(userid)+relative+baseDN, (ModificationItem[]) mods.toArray(new ModificationItem[1]));
            } catch (NamingException e2) {
                throw new LDAPException(e2);
            }
        }
    }


    /* (non-Javadoc)
     * @see de.tarent.octopus.server.UserManager#addUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void addUser(String userID, String firstName, String lastName, String password) throws TcSecurityException{
        try {
            addContactUser(userID, firstName, lastName, password);
        } catch (LDAPException e) {
            throw new TcSecurityException(TcSecurityException.ERROR_SERVER_USERMANAGEMENT_ERROR, e);
        }
    }

    /* (non-Javadoc)
     * @see de.tarent.octopus.server.UserManager#modifyUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
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

    private static Logger logger = Logger.getLogger(LDAPManagerTarentContact.class.getName());
}
