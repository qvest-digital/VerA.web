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
 * Diese Klasse dient als Importziel f�r die Methode
 * {@link DataExchangeWorker#importToTransit(OctopusContext, Map, String, String, Integer, Map)}.
 */
public class VerawebDigester implements ImportDigester {
    //
    // Konstruktoren
    //
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
    }
    
    //
    // �ffentliche Methoden
    //
    /**
     * Diese Methode liefert den aktuellen Stand dieses Imports in Form einer
     * {@link Map} mit speziellen Inhalten.
     * 
     * @return Map mit Informationen zum Import, insbesondere der Anzahl gefundener
     *  Datens�tze unter "dsCount", der Anzahl Duplikate unter "dupCount", der Anzahl
     *  importierter Datens�tze unter "saveCount" und der Import-ID unter "id".
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
            duplicateCount = markDuplicates();
            importableCount = personCount - duplicateCount;
        } catch (IOException e) {
            throw new BeanException("Fehler beim Zugriff auf Bean-Properties", e);
        }
    }

    /**
     * Diese Methode wird von einem {@link Importer} zu jeder zu importierenden
     * Person aufgerufen, �bergeben wird die Person und eine Liste mit Beans,
     * die Zus�tze zur Person darstellen.<br>
     * Falls Abh�ngigkeiten unter diesen Beans bestehen, stehen in der
     * Liste die Beans, von der eine bestimmte Bean abh�ngt, vor dieser. 
     * 
     * @param person eine {@link ImportPerson}-Instanz
     * @param extras eine Liste mit Beans, die Zus�tze zur Person darstellen; es
     *  werden nur solche akzeptiert, die {@link ImportPersonExtra} implementieren.
     * @throws BeanException 
     * @throws IOException 
     * @see ImportDigester#importPerson(ImportPerson, List)
     */
    public void importPerson(ImportPerson person, List extras) throws BeanException, IOException {
        assert person != null;
        
        // Verwaltungsdaten: ID; muss null sein
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
        // Verwaltungsdaten: nicht gel�scht
        person.deleted = PersonConstants.DELETED_FALSE;
        // Verwaltungsdaten: istFirma-Flag
        if (!PersonConstants.ISCOMPANY_TRUE.equals(person.iscompany))
            person.iscompany = PersonConstants.ISCOMPANY_FALSE;
        AddressHelper.checkPersonSalutation(person, db, context);
        
        /*
         * fk_workarea must not be null, setting default workarea "Keine" with pk == 0
         * cklein 2008-03-27
         */
        person.workarea = new Integer( 0 );
        
        // Datensatz nicht berechtigte Felder entziehen.
        person.clearRestrictedFields(cntx);
        
        // Datensatz auf vollst�ndigkeit testen.
        person.verify();
        if (person.isCorrect()) {
            // Z�hler aktualisieren
            personCount++;
            
        	// Datensatz speichern.
	        if (extras == null) {
                db.saveBean(person, context, false); // neue ID wird nicht ben�tigt
	        } else {
	        	db.saveBean(person, context, true);
	            // Extras �bernehmen
	            for(Iterator itExtras = extras.iterator(); itExtras.hasNext(); ) {
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

    /**
     * Diese Methode findet zu allen {@link ImportPerson}s dieses Imports Duplikate
     * in der Tabelle <code>veraweb.tperson</code> zu den {@link Person}s und tr�gt
     * diese jeweils in das Attribut {@link ImportPerson#duplicates} ein.
     */
    int markDuplicates() throws IOException, BeanException {
        Person samplePerson = new Person();
        int duplicateCount = 0;
        ImportPerson sampleImportPerson = new ImportPerson();
        Select select = SQL.Select( db );
        select.from(" veraweb.tperson JOIN veraweb.timportperson ON (" +
        		"(tperson.firstname_a_e1 = timportperson.firstname_a_e1 OR " +
        		"(timportperson.firstname_a_e1 IS NULL AND tperson.firstname_a_e1 IS NULL)) AND " +
        		"(tperson.lastname_a_e1 = timportperson.lastname_a_e1 OR " +
        		"(timportperson.lastname_a_e1 IS NULL AND tperson.lastname_a_e1 IS NULL)) AND " +
        		"tperson.deleted = timportperson.deleted) ");
        select.selectAs(db.getProperty(sampleImportPerson, "id"), "importperson");
        select.selectAs(db.getProperty(samplePerson, "id"), "person");
        select.where(Where.and(
                Expr.equal(db.getProperty(sampleImportPerson, "fk_import"), importInstance.id),
                Expr.equal(db.getProperty(samplePerson, "orgunit"), orgUnit)
                ));
        select.orderBy(Order.asc("importperson").andAsc("person"));
        
        List duplicates = db.getList(select, context);
        List localDuplicates = new ArrayList();
        ImportPerson importPerson = new ImportPerson();
        importPerson.id = null;
        for(Iterator itDuplicates = duplicates.iterator(); itDuplicates.hasNext(); ) {
            Map duplicate = (Map) itDuplicates.next();
            Integer importPersonId = (Integer) duplicate.get("importperson"); // NOT NULL
            Integer personId = (Integer) duplicate.get("person"); // NOT NULL
            if (!importPersonId.equals(importPerson.id)) {
                if (importPerson.id != null)
                    setDuplicates(importPerson, localDuplicates);
                localDuplicates.clear();
                importPerson.id = importPersonId;
                duplicateCount++;
            }
            localDuplicates.add(personId);
        }
        if (importPerson.id != null)
            setDuplicates(importPerson, localDuplicates);
        return duplicateCount;
    }
    
    /**
     * Diese Methode tr�gt in die �bergebene {@link ImportPerson} die �bergebenen
     * IDs von Duplikaten in der Tabelle <code>veraweb.tperson</code> ein. 
     * 
     * @param importPerson in dieser {@link ImportPerson} wird nur das Feld
     *  {@link ImportPerson#id} als stimmig angenommen und nur das Feld
     *  {@link ImportPerson#duplicates} ver�ndert.
     * @param duplicates diese IDs werden als Duplikate in die �bergebene
     *  {@link ImportPerson} eingetragen und abgespeichert.
     */
    void setDuplicates(ImportPerson importPerson, List duplicates) throws BeanException, IOException {
        importPerson.duplicates = serializeDuplicates(duplicates);
        if (updateDuplicatesStatement == null)
            updateDuplicatesStatement = db.prepareUpdate(importPerson, Collections.singleton("id"), Collections.singleton("duplicates"), context);
        updateDuplicatesStatement.execute(importPerson);
    }
    
    //
    // statische Hilfsmethoden
    //
    
    /**
     * Diese Methode erzeugt aus einer {@link List Liste} einen String, in dem die
     * Listenelemente durch {@link ImportPerson#PK_SEPERATOR_CHAR} getrennt aufgef�hrt
     * werden.  
     * 
     * @param duplicates Liste von Duplikat-IDs
     * @return {@link ImportPerson#PK_SEPERATOR_CHAR}-getrennte Stringdarstellung der
     *  Liste; <code>null</code> genau dann, wenn die eingehende Liste <code>null</code>
     *  war.
     */
    final static String serializeDuplicates(List duplicates) {
        if (duplicates == null)
            return null;
        StringBuffer buffer = new StringBuffer();
        for (Iterator itDuplicates = duplicates.iterator(); itDuplicates.hasNext(); ) {
            if (buffer.length() > 0)
                buffer.append(ImportPerson.PK_SEPERATOR_CHAR);
            buffer.append(itDuplicates.next());
        }
        return buffer.toString();
    }
    
    //
    // gesch�tzte Member
    //
    int personCount = 0;
    int importableCount = 0;
    int duplicateCount = 0;
    int incorrectCount = 0;
    BeanStatement updateDuplicatesStatement = null;
    
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
    
    final static Logger logger = Logger.getLogger(VerawebDigester.class);
}