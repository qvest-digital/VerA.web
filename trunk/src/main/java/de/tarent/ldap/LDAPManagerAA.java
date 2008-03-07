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
    /** LDAP-Objektklasse fuer AA-Rollen */
    /** Parameter-Schluessel fuer den AA-Rollen-Filter */
    public final static String KEY_ROLE_FILTER = "role-filter";
    
    //
    // Konstruktor
    //
    /**
     * Dieser Konstruktor reicht die �bergebenen Parameter durch und setzt die
     * Vorgabe-Objektklassen.
     * 
     * @param lctx initialer LDAP-Kontext, auf dem dieser LDAP-Manager arbeitet
     * @param params LDAP-Manager-Parameter, vergleiche
     *  {@link LDAPManager#LDAPManager(InitialLdapContext, Map)}
     */
    public LDAPManagerAA(InitialLdapContext lctx, Map params) {
        super(lctx, params);
        String roleFilter = (String) params.get(KEY_ROLE_FILTER);
        filterTemplate = new MessageFormat(roleFilter != null ? roleFilter : "(objectclass=" + this.defaultUserObjectClass + ')');
    }

    //
    // �ffentliche AA-spezifische Methoden
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
     * Diese Methode ermittelt alle verf�gbaren Rollen.
     * 
     * @return Sammlung verf�gbarer Rollen
     * @throws NamingException
     */
    public Set getPossibleRoles() throws NamingException {
		SearchControls cons = new SearchControls();
		this.initializeSearchControls( cons );
		Set roleUids = getPossibleRoles("(objectclass=" + this.defaultUserObjectClass + ")", cons);
        logger.fine("Alle verf�gbaren Rollen: " + roleUids);
        return roleUids;
    }
    
    /**
     * Diese Methode ermittelt die verf�gbaren Rollen zu einem Vorname.Nachname-Login.
     * Dies geschieht �ber eine Suche in den Rollen, die �ber den Rollenfilter (Parameter
     * mit Schl�ssel {@link #KEY_ROLE_FILTER}) gefiltert wird. Dieser Rollenfilter ist
     * ein LDAP-Suchfilter, in dem aber {0} als Variable erlaubt ist, in die der Login
     * eingetragen wird.<br>
     * Zum Beispiel sucht folgender Filter die AARole-Knoten, in denen das login in
     * sinnvoller Weise im person-Attribut steht:<br>
     * (&amp;(|(person=uid={0}&#64;auswaertiges-amt.de,ou=Personen,dc=aa)(person=uid={0}.auswaertiges-amt.de,ou=Personen,dc=aa)(person=uid={0},ou=Personen,dc=aa))(objectclass=AARole))
     * 
     * @param login Login, zu dem m�gliche Rollen gesucht werden sollen
     * @return Sammlung m�glicher Rollen zu dem Login
     * @throws NamingException
     */
    public Set getPossibleRoles(String login) throws NamingException {
    	SearchControls cons = new SearchControls();
    	this.initializeSearchControls( cons );
        Set roleUids = getPossibleRoles(filterTemplate.format(new Object[]{login}), cons);
        logger.fine("Rollen f�r " + login + ": " + roleUids);
        return roleUids;
    }
    
    /**
     * Diese Methode ermittelt die verf�gbaren Rollen zu einem Filter.
     * 
     * @param searchScope Suchtiefe, siehe {@link SearchControls#setSearchScope(int)}
     * @param filter JNDI-Suchfilter
     * @return Sammlung m�glicher Rollen zu dem Filter
     * @throws NamingException
     */
    public Set getPossibleRoles(String filter, SearchControls cons) throws NamingException {
        Set roleUids = new HashSet();
        NamingEnumeration ergebnis = lctx.search(relativeUser.substring(1) + baseDN, filter, cons);
        while (ergebnis.hasMore()) {
            Attributes result = ((SearchResult) ergebnis.nextElement()).getAttributes();
            // TODO: hier wird einer der uid-Eintr�ge genommen, nicht alle. Nach Anpassung andere getPossibleRoles entsprechend anpassen
            roleUids.add(result.get("uid").get());
        }
        return roleUids;
    }
    
    //
    // Members
    //
    /** filter-Template f�r {@link #getPossibleRoles(String)} */
    protected MessageFormat filterTemplate = null;
    
    /** eigener statischer Logger */
    private static Logger logger = Logger.getLogger(LDAPManagerAA.class.getName());
}
