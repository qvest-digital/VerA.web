package de.tarent.aa.veraweb.beans;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
import java.util.List;


/**
 * Diese Bean stellt einen Eintrag der Tabelle veraweb.timportperson dar,
 * eine zu importierende Person im Transit-Bereich.
 *
 * @author hendrik
 * @author mikel
 */
public class ImportPerson extends Person {

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
    /** Anlaßliste, zeilenweise; für den MAdLAN-Import, ansonsten werden {@link ImportPersonCategorie}-Instanzen genutzt */
    public String occasion;

    /**
     * Element-Trennzeichen, um eine Menge von Primärschlüsseln in einem
     * Datenfeld (namentlich {@link #duplicates}) zu speichern.
     */
    public final static char PK_SEPERATOR_CHAR = ';';

    /** DB-Integerwert für <code>true</code> für {@link #dupcheckaction} und {@link #dupcheckstatus} */
    public final static Integer TRUE = new Integer(1);

    /** DB-Integerwert für <code>false</code> für {@link #dupcheckaction} und {@link #dupcheckstatus} */
    public final static Integer FALSE = new Integer(0);

    /**
     * Duplikatliste; wird in {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} aus {@link #duplicates} zusammengestellt
     * @return FIXME
     */
    public List getDuplicateList() {
		return dups;
	}

    /**
     * Duplikatliste; wird in {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} aus {@link #duplicates} zusammengestellt
     * @param duplicates FIXME
     */
    public void setDuplicateList(List duplicates) {
		dups = duplicates;
	}

    /**
     * Flag: Es gibt mehr Duplikate als die in {@link #getDuplicateList()}; wird in {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} beim Zusammenstellen gesetzt
     * @return FIXME
     */
    public boolean hasMoreDuplicates() {
		return moreDuplicates;
	}

    /**
     * @param moreDuplicates FIXME
     * Flag: Es gibt mehr Duplikate als die in {@link #getDuplicateList()}; wird in {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} beim Zusammenstellen gesetzt
     */
    public void setMoreDuplicates(boolean moreDuplicates) {
		this.moreDuplicates = moreDuplicates;
	}

    /**
     * Duplikatliste; wird in {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} aus {@link #duplicates} zusammengestellt
     */
    private List dups;
    /**
     * Flag: Es gibt mehr Duplikate als die in {@link #getDuplicateList()}; wird in {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} beim Zusammenstellen gesetzt
     */
    private boolean moreDuplicates;
}
