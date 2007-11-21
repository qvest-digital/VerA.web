/*
 * Created on 07.04.2005
 */
package de.tarent.aa.veraweb.beans;

/**
 * Bean zum Filtern einer Gästeliste und navigieren
 * innerhalb der Detailansicht.
 * 
 * Entspricht keiner Datenbank-Tabelle und wird ausschließlich
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
	/** Gibt an in welcher Sortierreihenfolge die Gästeliste angezeigt werden soll.*/
	public String listorder; // orderno, lastname, firstname, email
}
