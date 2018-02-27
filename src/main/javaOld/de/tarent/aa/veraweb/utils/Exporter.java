package de.tarent.aa.veraweb.utils;

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
