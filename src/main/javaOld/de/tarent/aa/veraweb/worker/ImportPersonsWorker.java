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
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.ImportPerson;
import de.tarent.aa.veraweb.beans.ImportPersonCategorie;
import de.tarent.aa.veraweb.beans.ImportPersonDoctype;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonCategorie;
import de.tarent.aa.veraweb.beans.PersonDoctype;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.utils.AddressHelper;
import de.tarent.aa.veraweb.utils.CharacterPropertiesReader;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.ExecutionContext;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen zum Import von Personendaten zur
 * Verfügung.
 *
 * @author hendrik
 * @author mikel
 */
public class ImportPersonsWorker {

    /** Logger dieser Klasse */
    public static final Logger LOGGER = Logger.getLogger(ImportPersonsWorker.class.getName());

    //
    // Öffentliche Konstanten
    //
    /***/
    public final static String FIELD_IMPORTED_COUNT = "imported";
    //
    // Octopus-Aktionen
    //
    /** Octopus-Eingabe-Parameter für {@link #importStoredRecord(OctopusContext, Integer)} */
    public static final String[] INPUT_importStoredRecord = {"REQUEST:importId"};
    /** Octopus-Eingabe-Parameter-Pflicht für {@link #importStoredRecord(OctopusContext, Integer)} */
    public static final boolean[] MANDATORY_importStoredRecord = {false};
    /** Octopus-Ausgabe-Parameter für {@link #importStoredRecord(OctopusContext, Integer)} */
    public static final String OUTPUT_importStoredRecord = "importStatus";

    /**
     * Diese Octopus-Aktion liefert zum Import mit der übergebenen ID die
     * Import-Statistik-Daten
     *
     * @param octopusContext Octopus-Kontext
     * @param importId Import-ID
     * @return Map mit Informationen zum Import, insbesondere der Anzahl gefundener
     *  Datensätze unter "dsCount", der Anzahl Duplikate unter "dupCount", der Anzahl
     *  importierter Datensätze unter "saveCount" und der Import-ID unter "id".
     * @throws BeanException
     * @throws IOException
     */
    public Map importStoredRecord(OctopusContext octopusContext, Integer importId) throws BeanException, IOException {
        if (importId == null) {
            importId = getImportIdFromSession(octopusContext);
        }

        //Initialisiere Datenbank
        Database database = new DatabaseVeraWeb(octopusContext);
        ImportPerson sample = (ImportPerson) database.createBean("ImportPerson");
        //Erstelle SELECT-Anfrage, die die Anzahl der Datensätze liest.
        Select select = database.getCount(sample);
        WhereList where = new WhereList();
        //Bed: Datensatz wurde noch nicht festgeschrieben
        ////////

        Clause clause = Where.and(
                Expr.equal("fk_orgunit", ((PersonalConfigAA) octopusContext.personalConfig()).getOrgUnitId()),
                Expr.equal("deleted", PersonConstants.DELETED_FALSE));

        String firstname = sample.firstname_a_e1;
        String lastname = sample.lastname_a_e1;

        Clause normalNamesClause = Where.and(Expr.equal("lastname_a_e1", firstname), Expr.equal("firstname_a_e1", lastname));
        Clause revertedNamesClause = Where.and(Expr.equal("lastname_a_e1", lastname), Expr.equal("firstname_a_e1", firstname));
        Clause checkMixChanges = Where.or(normalNamesClause, revertedNamesClause);

        Clause dupNormalCheck = Where.and(clause, checkMixChanges);

        CharacterPropertiesReader cpr = new CharacterPropertiesReader();

        if (cpr.propertiesAreAvailable() && (lastname != null || firstname != null)) {
            for (final String key : cpr.properties.stringPropertyNames()) {
                String value = cpr.properties.getProperty(key);
                if (lastname != null) {
                    if (lastname.contains(value)) {
                        lastname = lastname.replaceAll(value, key);
                    } else if (lastname.contains(key)) {
                        lastname = lastname.replaceAll(key, value);
                    }
                }

                if (firstname.contains(value)) {
                    firstname = firstname.replaceAll(value, key);
                } else if (firstname.contains(key)) {
                    firstname = firstname.replaceAll(key, value);
                }
            }
        }
        Clause normalNamesEncoding = Where.and(Expr.equal("lastname_a_e1", firstname), Expr.equal("firstname_a_e1", lastname));
        Clause revertedNamesEncoding = Where.and(Expr.equal("lastname_a_e1", lastname), Expr.equal("firstname_a_e1", firstname));
        Clause checkMixChangesEncoding = Where.or(normalNamesEncoding, revertedNamesEncoding);
        // With encoding changes
        Where.or(dupNormalCheck, checkMixChangesEncoding);

        ////////
        where.addAnd(Expr.equal(database.getProperty(sample, "dupcheckaction"), ImportPerson.FALSE));
        //Bed: Nur Datensätze von dem aktuellen Importvorgang
        where.addAnd(Expr.equal(database.getProperty(sample, "fk_import"), importId));
        select.where(where);
        Integer dsCount = database.getCount(select);

        //Erstelle SELECT-Anfrage, die die Anzahl der Datensätze mit Duplikaten liest.
        select = database.getCount(sample);
        where = new WhereList();
        //Bed: Es existieren Duplikate zu dem Datensatz
        where.addAnd(Expr.isNotNull(database.getProperty(sample, "duplicates")));
        //Bed: Datensatz wurde noch nicht festgeschrieben
        where.addAnd(Expr.equal(database.getProperty(sample, "dupcheckaction"), ImportPerson.FALSE));
        //Bed: Nur Datensätze von dem aktuellen Importvorgang
        where.addAnd(Expr.equal(database.getProperty(sample, "fk_import"), importId));
        select.where(where);
        Integer dupCount = database.getCount(select);

        //Erstelle SELECT-Anfrage, die die Anzahl der zum Speichern freigegebenen Datensätze liest.
        select = database.getCount(sample);
        where = new WhereList();
        where.addAnd(
                Where.or(
                        //Bed: Es existieren keine Duplikate zu dem Datensatz
                        Expr.isNull(database.getProperty(sample, "duplicates")),
                        //Bed: Datensatz explizit zum speichern gekennzeichnet
                        Expr.equal(database.getProperty(sample, "dupcheckstatus"), ImportPerson.TRUE)
                ));
        //Bed: Datensatz wurde noch nicht festgeschrieben
        where.addAnd(Expr.equal(database.getProperty(sample, "dupcheckaction"), ImportPerson.FALSE));
        //Bed: Nur Datensätze von dem aktuellen Importvorgang
        where.addAnd(Expr.equal(database.getProperty(sample, "fk_import"), importId));
        select.where(where);
        Integer saveCount = database.getCount(select);

        octopusContext.setContent("selectNone", octopusContext.getRequestObject().getParam("selectNone"));

        return DataExchangeWorker.createImportStats(dsCount.intValue(), dupCount.intValue(), saveCount.intValue(), importId);
    }

    /** Octopus-Eingabe-Parameter für {@link #finalise(OctopusContext, Integer, List, Map)} */
    public static final String[] INPUT_finalise = {"REQUEST:importId", "CONFIG:ignorePersonFields", "CONFIG:importTextfieldMapping"};
    /** Octopus-Eingabe-Parameter-Pflicht für {@link #finalise(OctopusContext, Integer, List, Map)} */
    public static final boolean[] MANDATORY_finalise = {false, true, true};
    /** Octopus-Ausgabe-Parameter für {@link #finalise(OctopusContext, Integer, List, Map)} */
    public static final String OUTPUT_finalise = "importStatus";

    /**
     * Diese Octopus-Aktion finalisiert einen Import. Hierbei wird als Nebeneffekt in
     * den Content unter dem Schlüssel {@link #FIELD_IMPORTED_COUNT "imported"} die Anzahl
     * der tatsächlich importierten Datensätze eingetragen.
     *
     * @param octopusContext Octopus-Kontext
     * @param importId ID eines früheren Imports
     * @param ignorePersonFields
     * @param importTextfieldMapping Map für das Mapping der Adressfreitextfelder
     * @return Map mit Informationen zum Import, insbesondere der Anzahl gefundener
     *  Datensätze unter "dsCount", der Anzahl Duplikate unter "dupCount", der Anzahl
     *  importierter Datensätze unter "saveCount" und der Import-ID unter "id".
     * @throws BeanException
     * @throws IOException
     */
    public Map finalise(OctopusContext octopusContext, Integer importId, List ignorePersonFields, Map importTextfieldMapping) throws BeanException, IOException {
        //Initialisiere Datenbank und lese die ID für den Importvorgang
        if (importId == null) {
            importId = getImportIdFromSession(octopusContext);
        }

        Database database = new DatabaseVeraWeb(octopusContext);
        TransactionContext transactionContext = database.getTransactionContext();

        List cleanupOrgunits = new ArrayList();

        try {
            int dsCount = 0;

            ImportPerson sampleImportPerson = new ImportPerson();
            // importTextfieldMapping analysieren, Doctypes holen...
            List personDoctypeCreators = parseTextfieldMapping(database, transactionContext, importTextfieldMapping);
            //Erstelle SELECT-Anfrage, die die einzufügenden Datensätze liest.
            Select select = database.getSelect(sampleImportPerson);
            WhereList where = new WhereList();
            //Bed: Nur Datensätze von dem aktuellen Importvorgang
            where.addAnd(Expr.equal(database.getProperty(sampleImportPerson, "fk_import"), importId));
            where.addAnd(
                    Where.or(
                            //Bed: Es existieren keine Duplikate zu dem Datensatz (NULL wird gefordert!!!)
                            Expr.isNull(database.getProperty(sampleImportPerson, "duplicates")),
                            //Bed: Datensatz explizit zum speichern gekennzeichnet
                            Expr.equal(database.getProperty(sampleImportPerson, "dupcheckstatus"), ImportPerson.TRUE)
                    ));
            //Bed: Datensatz wurde noch nicht festgeschrieben
            where.addAnd(Expr.equal(database.getProperty(sampleImportPerson, "dupcheckaction"), ImportPerson.FALSE));
            select.where(where);

            //Hole die festzuschreibenden Datensätze und schreiben diese iterativ in die Personen-Tabellen
            List result = database.getList(select, database);
            for (Object singleResult : result) {
                Map importPerson = (Map) singleResult;
                Integer ipID = (Integer) importPerson.get("id");

                // Import-Bean auf Personen-Bean mappen
                Person person = new Person();
                for (Iterator fieldIt = person.getFields().iterator(); fieldIt.hasNext(); ) {
                    String key = (String) fieldIt.next();
                    if (!ignorePersonFields.contains(key))
                        person.setField(key, importPerson.get(key));
                }
                AddressHelper.copyAddressData(octopusContext, person, null);

				/* assign default workarea = 0 in case that person.workarea is null
                 *
				 * modified as per change request for version 1.2.0
				 * cklein 2008-03-27
				 */
                if (person.workarea == null) {
                    person.workarea = new Integer(0);
                }

                // Neue Person speichern
                person.verify();
                if (person.isCorrect()) {
                    database.saveBean(person, transactionContext, true);
                    transactionContext.commit();

                    if (!cleanupOrgunits.contains(person.orgunit)) {
                        cleanupOrgunits.add(person.orgunit);
                    }

                    // Importierte Kategorien zu Personen erzeugen
                    createPersonCategories(database, transactionContext, (Integer) importPerson.get("id"), person);
                    // TODO: auslagern in MAdLANImporter
                    if (importPerson.get("category") != null && ((String) importPerson.get("category")).length() != 0) {
                        createPersonCategories(database, transactionContext, ((String) importPerson.get("category")).split("\n"), person, new Integer(Categorie.FLAG_DEFAULT));
                    }

                    if (importPerson.get("occasion") != null && ((String) importPerson.get("occasion")).length() != 0) {
                        createPersonCategories(database, transactionContext, ((String) importPerson.get("occasion")).split("\n"), person, new Integer(Categorie.FLAG_EVENT));
                    }

                    // Importierte Dokumenttypen zu Personen erzeugen
                    createPersonDoctypes(database, transactionContext, ipID, person.id);
                    // TODO: auslagern in MAdLANImporter
                    Iterator itPersonDoctypeCreators = personDoctypeCreators.iterator();
                    while (itPersonDoctypeCreators.hasNext()) {
                        ((PersonDoctypeImporter) itPersonDoctypeCreators.next()).createFor(importPerson, person.id, transactionContext);
                    }

                    // Restlichen Personen Dokumenttypen erzeugen
                    PersonDoctypeWorker.createPersonDoctype(octopusContext, database, transactionContext, person);

                    // Datensatz als festgeschrieben markieren
                    transactionContext.execute(database.getUpdate("ImportPerson").update("dupcheckaction", ImportPerson.TRUE).
                            where(Expr.equal(database.getProperty(sampleImportPerson, "id"), ipID)));
                    transactionContext.commit();
                    // Datensatz erfolgreich bearbeitet
                    dsCount++;
                }
            }
            transactionContext.commit();

            emptyImportingSession(octopusContext);

            octopusContext.setContent(FIELD_IMPORTED_COUNT, new Integer(dsCount));
            octopusContext.setContent("cleanupOrgunits", cleanupOrgunits);

            return importStoredRecord(octopusContext, importId);
        } catch (BeanException e) {
            transactionContext.rollBack();
            throw new BeanException("Die Personendaten konnten nicht importiert werden.", e);
        }
    }

    private Integer getImportIdFromSession(OctopusContext octopusContext) {
        final Long importId = (Long) octopusContext.sessionAsObject("importId");
        return importId.intValue();
    }

    /**
     * Cleaning the data stored in the session to allow the importing process
     *
     * @param octopusContext
     */
    private void emptyImportingSession(OctopusContext octopusContext) {
        octopusContext.setSession("importProperties", null);
        octopusContext.setSession("orgUnit", null);
        octopusContext.setSession("importSource", null);
        octopusContext.setSession("formatKey", null);
        octopusContext.setSession("stream", null);
    }

    //
    // Hilfsmethoden
    //

    /**
     * Diese Methode erzeugt zu einer Person und einer Menge Kategoriennamen
     * passende {@link PersonCategorie}-Instanzen. Gegebenenfalls werden hierbei
     * die Kategorien erst noch erzeugt.
     *
     * @param database die Datenbank, in der agiert werden soll
     * @param categoryNames ein Array aus Kategoriennamen
     * @param person Person
     * @param flags Art der gesuchten und gegebenenfalls erzeugten Kategorie
     */
    private static void createPersonCategories(Database database, ExecutionContext executionContext,
                                               String[] categoryNames, Person person, Integer flags)
            throws BeanException, IOException {
        final TransactionContext transactionContext = executionContext.getDatabase().getTransactionContext();
        for (int i = 0; i < categoryNames.length; i++) {
            String categoryName = categoryNames[i].trim();
            if (categoryName.length() != 0) {
                Categorie category = (Categorie) database.getBean("Categorie",
                        database.getSelect("Categorie").where(Where.and(
                                Expr.equal("catname", categoryName), Where.and(
                                        Expr.equal("flags", flags),
                                        Expr.equal("fk_orgunit", person.orgunit)))), executionContext);
                if (category == null) {
                    category = (Categorie) database.createBean("Categorie");
                    category.flags = flags;
                    category.name = categoryName;
                    category.orgunit = person.orgunit;
                    database.saveBean(category, executionContext, true);
                    transactionContext.commit();
                }
                PersonCategorie personCategory = new PersonCategorie();
                personCategory.categorie = category.id;
                personCategory.person = person.id;
                database.saveBean(personCategory, executionContext, false);
                transactionContext.commit();
            }
        }
    }

    /**
     * Diese Methode erzeugt zu einer Person und einer ImportPerson
     * passende {@link PersonCategorie}-Instanzen. Gegebenenfalls werden hierbei
     * die Kategorien erst noch erzeugt.
     *
     * @param database die Datenbank, in der agiert werden soll
     * @param importPersonId ID einer ImportPerson
     * @param person Person, als die die ImportPerson importiert wird
     */
    private static void createPersonCategories(Database database, ExecutionContext executionContext,
                                               Integer importPersonId, Person person)
            throws BeanException, IOException {
        ImportPersonCategorie sample = new ImportPersonCategorie();
        Select select = database.getSelect(sample);
        select.where(Expr.equal(database.getProperty(sample, "importperson"), importPersonId));

        final TransactionContext transactionContext = executionContext.getDatabase().getTransactionContext();

        List importPersonCategories = database.getBeanList("ImportPersonCategorie", select, executionContext);
              for (Iterator itImportPersonCategories = importPersonCategories.iterator(); itImportPersonCategories.hasNext(); ) {
            ImportPersonCategorie importPersonCategorie = (ImportPersonCategorie) itImportPersonCategories.next();
            if (importPersonCategorie.name != null) {
                Categorie category = (Categorie) database.getBean("Categorie",
                        database.getSelect("Categorie").where(Where.and(
                                Expr.equal("catname", importPersonCategorie.name),
                                Expr.equal("fk_orgunit", person.orgunit))), executionContext);
                if (category == null) {
                    category = (Categorie) database.createBean("Categorie");
                    category.flags = importPersonCategorie.flags;
                    category.name = importPersonCategorie.name;
                    category.rank = importPersonCategorie.rank;
                    category.orgunit = person.orgunit;
                    database.saveBean(category, executionContext, true);
                    transactionContext.commit();
                }
                PersonCategorie personCategory = new PersonCategorie();
                personCategory.categorie = category.id;
                personCategory.person = person.id;
                personCategory.rank = importPersonCategorie.rank;
                database.saveBean(personCategory, executionContext, false);
                transactionContext.commit();
            }
        }
    }

    /**
     * Diese Methode erzeugt zu einer Person und einer ImportPerson
     * passende {@link PersonDoctype}-Instanzen. Gegebenenfalls werden hierbei
     * die Dokumenttypen erst noch erzeugt.
     *
     * @param database die Datenbank, in der agiert werden soll
     * @param importPersonId ID einer ImportPerson
     * @param personId ID der Person, als die die ImportPerson importiert wird
     */
    private static void createPersonDoctypes(Database database, ExecutionContext executionContext,
                                             Integer importPersonId, Integer personId)
            throws BeanException, IOException {
        final ImportPersonDoctype sample = (ImportPersonDoctype) database.createBean("ImportPersonDoctype");
        final Select select = database.getSelect(sample);
        select.where(Expr.equal(database.getProperty(sample, "importperson"), importPersonId));

        final List importPersonDoctypes = database.getList(select, executionContext);
        for (Iterator itImportPersonDoctypes = importPersonDoctypes.iterator(); itImportPersonDoctypes.hasNext(); ) {
            final Map importPersonDoctype = (Map) itImportPersonDoctypes.next();
            final String doctypeName = (String) importPersonDoctype.get("name");
            if (doctypeName != null) {
                handlePersonDoctypeCreation(database, executionContext, personId, importPersonDoctype, doctypeName);
            }
        }
    }

    private static void handlePersonDoctypeCreation(Database database, ExecutionContext executionContext,
                                                    Integer personId, Map importPersonDoctype, String name)
            throws BeanException, IOException {
        final Doctype doctype = getDoctypeByName(database, executionContext, name);
        if (doctype == null) {
            LOGGER.warn("Der Dokumenttyp '" + name + "' existiert nicht mehr und wird nicht importiert.");
            return;
        }
        final PersonDoctype personDoctype = initPersonDoctype(database, personId, importPersonDoctype, doctype);
        personDoctype.verify();
        database.saveBean(personDoctype, executionContext, false);
        final TransactionContext transactionContext = executionContext.getDatabase().getTransactionContext();
        transactionContext.commit();
    }

    private static Doctype getDoctypeByName(Database database, ExecutionContext executionContext, String name)
            throws BeanException, IOException {
        final Select getDoctypeByNameStatement = database.getSelect("Doctype").where(Expr.equal("docname", name));
        return (Doctype) database.getBean("Doctype", getDoctypeByNameStatement, executionContext);
    }

    private static PersonDoctype initPersonDoctype(Database database, Integer personId, Map importPersonDoctype,
                                                   Doctype doctype) throws BeanException {
        final PersonDoctype personDoctype = (PersonDoctype) database.createBean("PersonDoctype");
        personDoctype.doctype = doctype.id;
        personDoctype.person = personId;
        personDoctype.textfield = (String) importPersonDoctype.get("textfield");
        personDoctype.textfieldJoin = (String) importPersonDoctype.get("textfieldJoin");
        personDoctype.textfieldPartner = (String) importPersonDoctype.get("textfieldPartner");
        personDoctype.addresstype = doctype.addresstype;
        personDoctype.locale = doctype.locale;
        return personDoctype;
    }

    /**
     * Diese Methode parst das importTextfieldMapping und liefert eine Liste von
     * {@link ImportPersonsWorker.PersonDoctypeImporter}-Instanzen.
     *
     * @param database Datenbank
     * @param importTextfieldMapping konfiguriertes importTextfieldMapping
     * @return Liste erkannter PersonDoctype-Erstellungsvorschriften aus den
     *  Adressfreitextfeldern
     * @throws IOException
     * @throws BeanException
     */
    private static List parseTextfieldMapping(Database database, ExecutionContext executionContext,
                                              Map importTextfieldMapping) throws BeanException, IOException {
        assert database != null;
        if (importTextfieldMapping == null)
            return Collections.EMPTY_LIST;

        List result = new ArrayList();
        for (int i = 0; i < importTextfieldMapping.size(); i++) {
            String indexString = String.valueOf(i);
            String keyDoctype = indexString + ":Doctype";
            if (importTextfieldMapping.containsKey(keyDoctype)) {
                String keyPerson = indexString + ":Person";
                String keyPartner = indexString + ":Partner";
                String keyJoin = indexString + ":Join";
                result.add(new PersonDoctypeImporter(database, executionContext,
                        (String) importTextfieldMapping.get(keyDoctype),
                        (String) importTextfieldMapping.get(keyPerson),
                        (String) importTextfieldMapping.get(keyPartner),
                        (String) importTextfieldMapping.get(keyJoin)));
            }
        }
        return result;
    }

    //
    // innere Klassen
    //

    /**
     * Diese Klasse dient dem Erzeugen von {@link PersonDoctype}-Instanzen zu
     * {@link ImportPerson}s.
     */
    private static class PersonDoctypeImporter {
        //
        // Konstruktoren
        //
        private PersonDoctypeImporter(Database database, ExecutionContext executionContext,
                                      String doctypeName, String personField, String partnerField, String join)
                throws BeanException, IOException {
            this.database = database;
            this.context = executionContext;
            this.personField = personField;
            this.partnerField = partnerField;
            this.join = join;
            doctype = (Doctype) database.getBean("Doctype", database.getSelect("Doctype").where(Expr.equal("docname", doctypeName)), executionContext);
            if (doctype == null)
                LOGGER.warn("F\u00fcr den Import konfigurierten Dokumenttyp '" + doctypeName + "' nicht gefunden.");
        }

        //
        // Methoden
        //
        private void createFor(Map importPerson, Integer personId, TransactionContext transactionContext) throws BeanException, IOException {
            String personText = (String) importPerson.get(personField);
            String partnerText = (String) importPerson.get(partnerField);
            if (doctype != null && ((personText != null && personText.length() > 0) || (partnerText != null && partnerText.length() > 0))) {
                PersonDoctype personDoctype = new PersonDoctype();
                personDoctype.person = personId;
                personDoctype.doctype = doctype.id;
                personDoctype.locale = doctype.locale;
                personDoctype.addresstype = doctype.addresstype;
                personDoctype.doctypeLocale = doctype.locale;
                personDoctype.doctypeAddresstype = doctype.addresstype;
                personDoctype.name = doctype.name;
                personDoctype.textfield = personText;
                personDoctype.textfieldPartner = partnerText;
                personDoctype.textfieldJoin = join;
                database.saveBean(personDoctype, context, false);
                transactionContext.commit();
            }
        }

        //
        // Member
        //
        final Database database;
        final ExecutionContext context;
        final String personField;
        final String partnerField;
        final String join;
        final Doctype doctype;
    }

    //
    // interne Member
    //
    /** Logger dieser Klasse */
    static Logger logger = Logger.getLogger(ImportPersonsWorker.class.getName());
}
