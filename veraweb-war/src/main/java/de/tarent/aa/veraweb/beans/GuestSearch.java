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
 * Bean zum Filtern einer G�steliste und navigieren
 * innerhalb der Detailansicht.
 * 
 * Entspricht keiner Datenbank-Tabelle und wird ausschlie�lich
 * in der Session gehalten oder aus dem Request geladen.
 */
public class GuestSearch extends AbstractBean {
	public Integer event;
	public Integer offset;
	public Integer count;
	/** Gibt an ob nach Platz / Reserve gefiltert werden soll. */
	public Integer reserve;
	/** Gibt an ob nach Offene / Zusagen / Absagen gefiltert werden soll. */
	public Integer invitationstatus;
	/** Gibt an in welcher Sortierreihenfolge die G�steliste angezeigt werden soll.*/
	public String listorder; // orderno, lastname, firstname, email
}
