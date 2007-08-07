/*
 * $Id: ImportPerson.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
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
    // �ffentliche Membervariablen: Tabellenfelder �ber die von Person hinaus
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
    
    /** Kategorienliste, zeilenweise; f�r den MAdLAN-Import, ansonsten werden {@link ImportPersonCategorie}-Instanzen genutzt */
    public String category;
    /** Anl�sssliste, zeilenweise; f�r den MAdLAN-Import, ansonsten werden {@link ImportPersonCategorie}-Instanzen genutzt */
    public String occasion;
    
    /** Freitextfeld 1; f�r den MAdLAN-Import, ansonsten werden {@link ImportPersonDoctype}-Instanzen genutzt */
    public String textfield_1;
    /** Freitextfeld 2; f�r den MAdLAN-Import, ansonsten werden {@link ImportPersonDoctype}-Instanzen genutzt */
    public String textfield_2;
    /** Freitextfeld 3; f�r den MAdLAN-Import, ansonsten werden {@link ImportPersonDoctype}-Instanzen genutzt */
    public String textfield_3;

    //
    // �ffentliche Konstanten
    //
    /**
     * Element-Trennzeichen, um eine Menge von Prim�rschl�sseln in einem
     * Datenfeld (namentlich {@link #duplicates}) zu speichern.
     */
    public final static char PK_SEPERATOR_CHAR = ';';
    
    /** DB-Integerwert f�r <code>true</code> f�r {@link #dupcheckaction} und {@link #dupcheckstatus} */
    public final static Integer TRUE = new Integer(1);

    /** DB-Integerwert f�r <code>false</code> f�r {@link #dupcheckaction} und {@link #dupcheckstatus} */
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
    // gesch�tzte Member
    //
    /** Duplikatliste; wird in {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} aus {@link #duplicates} zusammengestellt */
    private List dups;
    /** Flag: Es gibt mehr Duplikate als die in {@link #getDuplicateList()}; wird in {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} beim Zusammenstellen gesetzt */
    private boolean moreDuplicates;
}
