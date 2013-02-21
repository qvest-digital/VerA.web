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
 * $Id$
 */
package de.tarent.aa.veraweb.beans;

import java.util.List;


/**
 * Diese Bean stellt einen Eintrag der Tabelle veraweb.timportperson dar,
 * eine zu importierende Person im Transit-Bereich.
 * 
 * @author hendrik
 * @author mikel
 */
public class ImportPerson extends Person {
    //
    // öffentliche Membervariablen: Tabellenfelder über die von Person hinaus
    //
    /** Import, bei dem diese Personendaten von extern importiert wurden */
	public Long fk_import;
	/** Duplikate, die beim Import erkannt wurden */
	public String duplicates;
	/** Flag: ImportPerson importieren, selbst wenn Duplikate vorliegen */
	public Integer dupcheckstatus;
    /** Flag: ImportPerson wurde schon importiert (nicht nur bei Duplikaten!!!) */
	public Integer dupcheckaction;

    //  Kategorien & Freitexte
    
    /** Kategorienliste, zeilenweise; für den MAdLAN-Import, ansonsten werden {@link ImportPersonCategorie}-Instanzen genutzt */
    public String category;
    /** Anlässsliste, zeilenweise; für den MAdLAN-Import, ansonsten werden {@link ImportPersonCategorie}-Instanzen genutzt */
    public String occasion;
    
    /** Freitextfeld 1; für den MAdLAN-Import, ansonsten werden {@link ImportPersonDoctype}-Instanzen genutzt */
    public String textfield_1;
    /** Freitextfeld 2; für den MAdLAN-Import, ansonsten werden {@link ImportPersonDoctype}-Instanzen genutzt */
    public String textfield_2;
    /** Freitextfeld 3; für den MAdLAN-Import, ansonsten werden {@link ImportPersonDoctype}-Instanzen genutzt */
    public String textfield_3;

    //
    // öffentliche Konstanten
    //
    /**
     * Element-Trennzeichen, um eine Menge von Primärschlüsseln in einem
     * Datenfeld (namentlich {@link #duplicates}) zu speichern.
     */
    public final static char PK_SEPERATOR_CHAR = ';';
    
    /** DB-Integerwert für <code>true</code> für {@link #dupcheckaction} und {@link #dupcheckstatus} */
    public final static Integer TRUE = new Integer(1);

    /** DB-Integerwert für <code>false</code> für {@link #dupcheckaction} und {@link #dupcheckstatus} */
    public final static Integer FALSE = new Integer(0);

    //
    // Getter und Setter
    //
    /** Duplikatliste; wird in {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} aus {@link #duplicates} zusammengestellt */
    public List getDuplicateList() {
		return dups;
	}
    /** Duplikatliste; wird in {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} aus {@link #duplicates} zusammengestellt */
	public void setDuplicateList(List duplicates) {
		dups = duplicates;
	}

    /** Flag: Es gibt mehr Duplikate als die in {@link #getDuplicateList()}; wird in {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} beim Zusammenstellen gesetzt */
    public boolean hasMoreDuplicates() {
		return moreDuplicates;
	}
    /** Flag: Es gibt mehr Duplikate als die in {@link #getDuplicateList()}; wird in {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} beim Zusammenstellen gesetzt */
	public void setMoreDuplicates(boolean moreDuplicates) {
		this.moreDuplicates = moreDuplicates;
	}

    //
    // geschützte Member
    //
    /** Duplikatliste; wird in {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} aus {@link #duplicates} zusammengestellt */
    private List dups;
    /** Flag: Es gibt mehr Duplikate als die in {@link #getDuplicateList()}; wird in {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} beim Zusammenstellen gesetzt */
    private boolean moreDuplicates;
}
