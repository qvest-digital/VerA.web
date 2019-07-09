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

    /**
     * Import, bei dem diese Personendaten von extern importiert wurden
     */
    public Long fk_import;
    /**
     * Duplikate, die beim Import erkannt wurden
     */
    public String duplicates;
    /**
     * Flag: ImportPerson importieren, selbst wenn Duplikate vorliegen
     */
    public Integer dupcheckstatus;
    /**
     * Flag: ImportPerson wurde schon importiert (nicht nur bei Duplikaten!!!)
     */
    public Integer dupcheckaction;

    //  Kategorien & Freitexte

    /**
     * Kategorienliste, zeilenweise; für den MAdLAN-Import, ansonsten werden {@link ImportPersonCategorie}-Instanzen genutzt
     */
    public String category;
    /**
     * Anlaßliste, zeilenweise; für den MAdLAN-Import, ansonsten werden {@link ImportPersonCategorie}-Instanzen genutzt
     */
    public String occasion;

    /**
     * Element-Trennzeichen, um eine Menge von Primärschlüsseln in einem
     * Datenfeld (namentlich {@link #duplicates}) zu speichern.
     */
    public final static char PK_SEPARATOR_CHAR = ';';

    /**
     * DB-Integerwert für <code>true</code> für {@link #dupcheckaction} und {@link #dupcheckstatus}
     */
    public final static Integer TRUE = new Integer(1);

    /**
     * DB-Integerwert für <code>false</code> für {@link #dupcheckaction} und {@link #dupcheckstatus}
     */
    public final static Integer FALSE = new Integer(0);

    /**
     * Duplikatliste; wird in
     * {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)}
     * aus {@link #duplicates} zusammengestellt
     *
     * @return FIXME
     */
    public List getDuplicateList() {
        return dups;
    }

    /**
     * Duplikatliste; wird in
     * {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)}
     * aus {@link #duplicates} zusammengestellt
     *
     * @param duplicates FIXME
     */
    public void setDuplicateList(List duplicates) {
        dups = duplicates;
    }

    /**
     * Flag: Es gibt mehr Duplikate als die in {@link #getDuplicateList()}; wird in
     * {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} beim
     * Zusammenstellen gesetzt
     *
     * @return FIXME
     */
    public boolean hasMoreDuplicates() {
        return moreDuplicates;
    }

    /**
     * @param moreDuplicates FIXME
     *                       Flag: Es gibt mehr Duplikate als die in {@link #getDuplicateList()}; wird in
     *                       {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} beim
     *                       Zusammenstellen gesetzt
     */
    public void setMoreDuplicates(boolean moreDuplicates) {
        this.moreDuplicates = moreDuplicates;
    }

    /**
     * Duplikatliste; wird in
     * {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)}
     * aus {@link #duplicates} zusammengestellt
     */
    private List dups;
    /**
     * Flag: Es gibt mehr Duplikate als die in {@link #getDuplicateList()}; wird in
     * {@link de.tarent.aa.veraweb.worker.ImportPersonsDuplicateWorker#showList(de.tarent.octopus.server.OctopusContext)} beim
     * Zusammenstellen gesetzt
     */
    private boolean moreDuplicates;
}
