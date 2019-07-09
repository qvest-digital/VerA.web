package de.tarent.aa.veraweb.utils;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.octopus.beans.BeanException;

import java.io.IOException;

/**
 * Diese Schnittstelle ist für jede grundsätzliche Exportvariante umzusetzen.
 *
 * @author mikel
 */
public interface Exporter {
    /**
     * Diese Methode wird zu jeder zu exportierenden Person aufgerufen, übergeben wird die Person als Zusammenstellung von
     * {@link Person}. Sie fügt dem Export eine Beschreibung der übergebenen VerA.web-Person hinzu.
     *
     * @param person {@link Person}-Bean
     * @throws BeanException FIXME
     * @throws IOException   FIXME
     */
    void exportPerson(Person person) throws BeanException, IOException;

    /**
     * Diese Methode wird zu Beginn eines Exports aufgerufen. In ihr kann etwa das Dokument mit einem Kopf zu schreiben
     * begonnen werden.
     *
     * @throws IOException FIXME
     */
    void startExport() throws IOException;

    /**
     * Diese Methode wird zum Ende eines Exports aufgerufen. In ihr kann etwa das bisher gesammelte Dokument
     * festgeschrieben werden.
     *
     * @throws IOException FIXME
     */
    void endExport() throws IOException;

    /**
     * Obwohl ein Exporter die zu exportierenden Personen nicht selbst bestimmt (diese werden ihm durch
     * <code>exportPerson(Person)</code> übergeben), benötigt er evtl. schon Informationen zur Einschränkung auf einen
     * Mandanten. Z.B. muss beim CSV-Exporter schon vor <code>startExport()</code> auf einen Mandanten eingeschränkt
     * werden können, damit keine mandantenfremden Spaltenüberschriften erzeugt werden (Kategorien, die nicht zum
     * Mandanten gehören)
     *
     * @param orgUnitId die MandantenID, auf die der Exporter beschränkt wird
     */
    void setOrgUnitId(Integer orgUnitId);

    /**
     * Obwohl ein Exporter die zu exportierenden Personen nicht selbst bestimmt (diese werden ihm durch
     * <code>exportPerson(Person)</code>) übergeben), benötigt er evtl. Informationen zur Einschränkung auf bestimmte
     * Kategorien. Z.B. muss beim CSV-Exporter schon vor <code>startExport()</code> auf die vom Benutzer im GUI gewählte
     * Kategorie eingeschränkt werden können, damit andere Kategorien nicht als Spaltenüberschriften erzeugt werden.
     *
     * @param categoryId KategorieId, auf die der Exporter beschränkt wird. <code>Null</code> = alle Kategorien, 0 = keine
     *                   Kategorie
     */
    void setCategoryId(Integer categoryId);
}
