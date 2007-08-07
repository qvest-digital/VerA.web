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

/* $Id: Grants.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 *
 * Created on 08.03.2005
 */
package de.tarent.aa.veraweb.beans;

/**
 * Diese Bohne stellt die Berechtigungen eines Nutzers dar
 * 
 * @author Christoph
 * @author mikel
 */
public interface Grants {
	/**
     * Dieses Attribut gibt an, ob der Benutzer authentisiert ist, wenn
     * er sich also gegenüber dem LDAP-Server anmelden konnte. Dies impliziert
     * noch keine Rechte im VerA.web-Kontext.
     *   
	 * @return <code>true</code> wenn der Benutzer angemeldet ist.
	 */
	boolean isAuthenticated();

	/**
     * Dieses Attribut gibt an, ob der Benutzer ein VerA.web-User ist, wenn
     * es zu seiner Anmeldungsrolle also einen VerA.web-Benutzereintrag gibt.
     * Dies impliziert noch keine Rechte auf Personen, Veranstaltungen,
     * Stamm- oder Konfigurationsdaten.
     *  
	 * @return <code>true</code> wenn der Benutzer VerA.web-User ist.
	 */
	boolean isUser();

    /**
     * Dieses Attribut gibt an, ob der Benutzer die nicht beschränkten
     * Felder lesen darf, also alles außer Bemerkungen.
     * 
     * @return <code>true</code> wenn der Benutzer nicht beschränkte Felder lesen darf.
     */
    boolean mayReadStandardFields();
    
    /**
     * Dieses Attribut gibt an, ob der Benutzer die beschränkten Felder lesen
     * darf, also die Bemerkungen.
     * 
     * @return <code>true</code> wenn der Benutzer beschränkte Felder lesen darf.
     */
    boolean mayReadRemarkFields();
    
    /**
     * Dieses Attribut gibt an, ob der Benutzer exportieren darf. Hierbei ist
     * zusätzlich mit {@link #mayReadStandardFields()} und {@link #mayReadRemarkFields()}
     * zu ermitteln, welche Feldinhalte exportiert werden dürfen.
     * 
     * @return <code>true</code> wenn der Benutzer exportieren darf.
     */
    boolean mayExport();
    
    /**
     * Dieses Attribut gibt an, ob der Benutzer Daten ändern darf. Hierbei ist mit
     * {@link #mayReadStandardFields()} und {@link #mayReadRemarkFields()} zu ermitteln,
     * auf welche Felder der Benutzer Zugriff hat.
     * 
     * @return <code>true</code> wenn der Benutzer schreiben darf.
     */
    boolean mayWrite();
    
    /**
     * Dieses Attribut gibt an, ob der Benutzer Teiladmin ist, ob er also innerhalb
     * seines Mandanten konfigurieren darf 
     * 
     * @return <code>true</code> wenn der Benutzer Teiladmin ist.
     */
    boolean isPartialAdmin();
    
	/**
     * Dieses Attribut gibt an, ob der Benutzer Volladmin ist, ob er also global
     * konfigurieren darf.
     * 
	 * @return <code>true</code>, wenn der Benutzer Volladmin ist.
	 */
	boolean isAdmin();
}
