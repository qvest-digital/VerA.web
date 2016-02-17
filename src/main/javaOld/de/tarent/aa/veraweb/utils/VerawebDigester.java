/**
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
package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tarent.aa.veraweb.beans.Import;
import de.tarent.aa.veraweb.beans.ImportPerson;
import de.tarent.aa.veraweb.beans.ImportPersonExtra;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.worker.DataExchangeWorker;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.BeanStatement;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.ExecutionContext;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Klasse dient als Importziel für die Methode
 * {@link DataExchangeWorker#importToTransit(OctopusContext, Map, String, String, Integer, Integer, Map)}.
 */
public class VerawebDigester implements ImportDigester {

	final static Logger LOGGER = Logger.getLogger(VerawebDigester.class);

    int personCount = 0;
    int importableCount = 0;
    int incorrectCount = 0;
    int duplicateCount = 0;

    final OctopusContext cntx;
    final Database db;
    final ExecutionContext context;
    final Import importInstance;
    final Integer orgUnit;
    final String importSource;
    final String importPersonCompareField1;
    final String stockBeanCompareField1;
    final String importPersonCompareField2;
    final String stockBeanCompareField2;

    final PersonDuplicateCheckHelper duplicateCheckHelper;

    /**
     * Dieser Konstruktor initialisiert die finalen Member.
     */
    public VerawebDigester(OctopusContext cntx, ExecutionContext context, Map importProperties, String importSource, Import importInstance) {
        assert context != null;
        this.cntx = cntx;
        this.importSource = importSource;
        this.orgUnit = importInstance.orgunit;
        this.importInstance = importInstance;
        this.context = context;
        this.db = context.getDatabase();
        stockBeanCompareField1 = (String) importProperties.get("beanFieldEqual1");
        importPersonCompareField1 = (String) importProperties.get("fieldEqual1");
        stockBeanCompareField2 = (String) importProperties.get("beanFieldEqual2");
        importPersonCompareField2 = (String) importProperties.get("fieldEqual2");

        duplicateCheckHelper = new PersonDuplicateCheckHelper(context, importInstance);
    }

    //
    // Öffentliche Methoden
    //
    /**
     * Diese Methode liefert den aktuellen Stand dieses Imports in Form einer
     * {@link Map} mit speziellen Inhalten.
     *
     * @return Map mit Informationen zum Import, insbesondere der Anzahl gefundener
     *  Datensätze unter "dsCount", der Anzahl Duplikate unter "dupCount", der Anzahl
     *  importierter Datensätze unter "saveCount" und der Import-ID unter "id".
     */
    public Map getImportStats() {
        return DataExchangeWorker.createImportStats(incorrectCount, personCount, duplicateCount, importableCount, importInstance.id);
    }

    //
    // Schnittstelle ImportDigester
    //
    /** Diese Methode wird zu Beginn eines Imports aufgerufen. */
    public void startImport() throws BeanException {
    }

    /** Diese Methode wird zum Ende eines Imports aufgerufen. */
    public void endImport() throws BeanException {
        try {
            duplicateCount = duplicateCheckHelper.getDuplicatesCount(cntx);
            importableCount = personCount - duplicateCount;
        } catch (IOException e) {
            throw new BeanException("Fehler beim Zugriff auf Bean-Properties", e);
        }
    }

    /**
     * Diese Methode wird von einem {@link Importer} zu jeder zu importierenden
     * Person aufgerufen, übergeben wird die Person und eine Liste mit Beans,
     * die Zusätze zur Person darstellen.<br>
     * Falls Abhängigkeiten unter diesen Beans bestehen, stehen in der
     * Liste die Beans, von der eine bestimmte Bean abhängt, vor dieser.
     *
     * @param person eine {@link ImportPerson}-Instanz
     * @param extras eine Liste mit Beans, die Zusätze zur Person darstellen; es
     *  werden nur solche akzeptiert, die {@link ImportPersonExtra} implementieren.
     * @throws BeanException
     * @throws IOException
     * @see ImportDigester#importPerson(ImportPerson, List)
     */
    public void importPerson(ImportPerson person, List extras) throws BeanException, IOException {
        assert person != null;

        // Verwaltungsdaten: ID; muß null sein
        person.id = null;
        // Verwaltungsdaten: Mandanten ID
        person.orgunit = orgUnit;
        // Verwaltungsdaten: Import-ID
        person.fk_import = new Long(importInstance.id.longValue());
        // Verwaltungsdaten: Import-Quelle
        person.importsource = importSource;
        // Verwaltungsdaten: noch nicht nach tperson importiert
        person.dupcheckaction = ImportPerson.FALSE;
        // Verwaltungsdaten: wenn Duplikat, dann nicht nach tperson importieren
        person.dupcheckstatus = ImportPerson.FALSE;
        // Verwaltungsdaten: keine Duplikate
        person.duplicates = null;
        // Verwaltungsdaten: nicht gelöscht
        person.deleted = PersonConstants.DELETED_FALSE;
        // Verwaltungsdaten: istFirma-Flag
        if (!PersonConstants.ISCOMPANY_TRUE.equals(person.iscompany))
            person.iscompany = PersonConstants.ISCOMPANY_FALSE;
        AddressHelper.checkPersonSalutation(person, db, context);

        /*
         * fk_workarea must not be null, setting default workarea "Keine" with pk == 0
         * cklein 2008-03-27
         */
        person.workarea = new Integer(0);

        // Datensatz nicht berechtigte Felder entziehen.
        person.clearRestrictedFields(cntx);

        // Datensatz auf vollständigkeit testen.
        person.verify();
        if (person.isCorrect()) {
            // Zähler aktualisieren
            personCount++;

            // Datensatz speichern.
            if (extras == null) {
                db.saveBean(person, context, false); // neue ID wird nicht benötigt
            } else {
                db.saveBean(person, context, true);
                // Extras übernehmen
                for (Iterator itExtras = extras.iterator(); itExtras.hasNext(); ) {
                    Object extraObject = itExtras.next();
                    if (extraObject instanceof ImportPersonExtra) {
                        ((ImportPersonExtra) extraObject).associateWith(person);
                        if (extraObject instanceof Bean)
                            db.saveBean((Bean) extraObject, context, false);
                    }
                }
            }
        } else {
            incorrectCount++;
        }
    }
}
