/*
 * VerA.web,
 * Veranstaltungsmanagment VerA.web
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
 * interest in the program 'VerA.web'
 * Signature of Elmar Geese, 7 August 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: LDAPManagerAA.java,v 1.1 2007/06/20 11:56:52 christoph Exp $
 * 
 * Created on 15.04.2005
 */
package de.tarent.ldap;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

/**
 * Diese Klasse 
 * 
 * @author mikel
 */
public class LDAPManagerAA extends LDAPManager {
    //
    // Konstanten
    //
    /** LDAP-Objektklasse für AA-Rollen */
    public final static String OBJECT_CLASS_AA_ROLE = "AARole";
    /** Parameter-Schlüssel für den AA-Rollen-Filter */
    public final static String KEY_ROLE_FILTER = "role-filter";

    //
    // Konstruktor
    //
    /**
     * Dieser Konstruktor reicht die übergebenen Parameter durch und setzt die
     * Vorgabe-Objektklassen.
     * 
     * @param lctx initialer LDAP-Kontext, auf dem dieser LDAP-Manager arbeitet
     * @param params LDAP-Manager-Parameter, vergleiche
     *  {@link LDAPManager#LDAPManager(InitialLdapContext, Map)}
     */
    public LDAPManagerAA(InitialLdapContext lctx, Map params) {
        super(lctx, params);
        setDefaultObjectClasses(new String[] {OBJECT_CLASS_AA_ROLE});
        String roleFilter = (String) params.get(KEY_ROLE_FILTER);
        filterTemplate = new MessageFormat(roleFilter != null ? roleFilter : "(objectclass=" + OBJECT_CLASS_AA_ROLE + ')');
    }

    //
    // öffentliche AA-spezifische Methoden
    //
    /**
     * Diese Methode erzeugt zu einem Benutzer eine {@link Map} seiner Attribute und
     * Attributswerte. Wenn ein Attribut mehrere Werte hat, so wird in der {@link Map}
     * der Attributname auf eine {@link java.util.List} der Werte abgebildet. 
     * 
     * @param userName Name des Benutzers
     * @return {@link Map} der Attribute des Benutzers
     * @throws LDAPException
     */
    public Map getUserData(String userName) throws LDAPException {
        try {
            String dn = getUserDN(userName);
            return LDAPUtil.toMap(lctx.getAttributes(dn + relativeUser + baseDN));
        } catch (NamingException e) {
            throw new LDAPException("Es ist ein Fehler beim Holen der Userdaten aufgetreten!", e);
        }
    }
    
    /**
     * Diese Methode ermittelt alle verfügbaren Rollen.
     * 
     * @return Sammlung verfügbarer Rollen
     * @throws NamingException
     */
    public Set getPossibleRoles() throws NamingException {
        Set roleUids = getPossibleRoles(SearchControls.ONELEVEL_SCOPE, "(objectclass=AARole)");
        logger.fine("Alle verfügbaren Rollen: " + roleUids);
        return roleUids;
    }
    
    /**
     * Diese Methode ermittelt die verfügbaren Rollen zu einem Vorname.Nachname-Login.
     * Dies geschieht über eine Suche in den Rollen, die über den Rollenfilter (Parameter
     * mit Schlüssel {@link #KEY_ROLE_FILTER}) gefiltert wird. Dieser Rollenfilter ist
     * ein LDAP-Suchfilter, in dem aber {0} als Variable erlaubt ist, in die der Login
     * eingetragen wird.<br>
     * Zum Beispiel sucht folgender Filter die AARole-Knoten, in denen das login in
     * sinnvoller Weise im person-Attribut steht:<br>
     * (&amp;(|(person=uid={0}&#64;auswaertiges-amt.de,ou=Personen,dc=aa)(person=uid={0}.auswaertiges-amt.de,ou=Personen,dc=aa)(person=uid={0},ou=Personen,dc=aa))(objectclass=AARole))
     * 
     * @param login Login, zu dem mögliche Rollen gesucht werden sollen
     * @return Sammlung möglicher Rollen zu dem Login
     * @throws NamingException
     */
    public Set getPossibleRoles(String login) throws NamingException {
        Set roleUids = getPossibleRoles(SearchControls.ONELEVEL_SCOPE, filterTemplate.format(new Object[]{login}));
        logger.fine("Rollen für " + login + ": " + roleUids);
        return roleUids;
    }
    
    /**
     * Diese Methode ermittelt die verfügbaren Rollen zu einem Filter.
     * 
     * @param searchScope Suchtiefe, siehe {@link SearchControls#setSearchScope(int)}
     * @param filter JNDI-Suchfilter
     * @return Sammlung möglicher Rollen zu dem Filter
     * @throws NamingException
     */
    public Set getPossibleRoles(int searchScope, String filter) throws NamingException {
        Set roleUids = new HashSet();
        SearchControls cons = new SearchControls();
        cons.setSearchScope(searchScope);
        NamingEnumeration ergebnis = lctx.search(relativeUser.substring(1) + baseDN, filter, cons);
        while (ergebnis.hasMore()) {
            Attributes result = ((SearchResult) ergebnis.nextElement()).getAttributes();
            // TODO: hier wird einer der uid-Einträge genommen, nicht alle. Nach Anpassung andere getPossibleRoles entsprechend anpassen
            roleUids.add(result.get("uid").get());
        }
        return roleUids;
    }
    
    //
    // Members
    //
    /** filter-Template für {@link #getPossibleRoles(String)} */
    protected MessageFormat filterTemplate = null;
    
    /** eigener statischer Logger */
    private static Logger logger = Logger.getLogger(LDAPManagerAA.class.getName());
}
