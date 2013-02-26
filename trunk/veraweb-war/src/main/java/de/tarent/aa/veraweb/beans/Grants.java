/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
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
     * er sich also gegen�ber dem LDAP-Server anmelden konnte. Dies impliziert
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
     * Dieses Attribut gibt an, ob der Benutzer die nicht beschr�nkten
     * Felder lesen darf, also alles au�er Bemerkungen.
     * 
     * @return <code>true</code> wenn der Benutzer nicht beschr�nkte Felder lesen darf.
     */
    boolean mayReadStandardFields();
    
    /**
     * Dieses Attribut gibt an, ob der Benutzer die beschr�nkten Felder lesen
     * darf, also die Bemerkungen.
     * 
     * @return <code>true</code> wenn der Benutzer beschr�nkte Felder lesen darf.
     */
    boolean mayReadRemarkFields();
    
    /**
     * Dieses Attribut gibt an, ob der Benutzer exportieren darf. Hierbei ist
     * zus�tzlich mit {@link #mayReadStandardFields()} und {@link #mayReadRemarkFields()}
     * zu ermitteln, welche Feldinhalte exportiert werden d�rfen.
     * 
     * @return <code>true</code> wenn der Benutzer exportieren darf.
     */
    boolean mayExport();
    
    /**
     * Dieses Attribut gibt an, ob der Benutzer Daten �ndern darf. Hierbei ist mit
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

    
	/**
     * Dieses Attribut gibt an, ob der Benutzer dem SystemUser entspricht.
     * Dieser Nutzer hat nur eingeschränkten Zugriff auf die Applikation und ist
     * in erster Linie dafür verantwortlich, während einer VerA.Web Installation
     * die Benutzer für die Applikation einzurichten.
     * 
	 * @return <code>true</code>, wenn der Benutzer der SystemUser ist.
	 */
	boolean isSystemUser();
}
