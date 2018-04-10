package de.tarent.aa.veraweb.utils;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2007 Jan Meyer <jan@evolvis.org>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
import de.tarent.aa.veraweb.beans.Import;
import de.tarent.aa.veraweb.beans.ImportPerson;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.helper.ResultMap;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.BeanStatement;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.ExecutionContext;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class contains methods to handle all needed duplication checks on a
 * {@link Person}.
 *
 * @author Viktor Hamm, tarent Solution GmbH, v.hamm@tarent.de
 */
public class PersonDuplicateCheckHelper {

    /**
     * the database
     */
    private Database database;

    /**
     * the execution context
     */
    private ExecutionContext context;

    /**
     * instance for an import type
     */
    private Import importInstance;

    /**
     * Default constructor
     */
    public PersonDuplicateCheckHelper() {
    }

    /**
     * Content: IDs of all persons in table "tperson"
     */
    private List<Integer> personIdArray = new ArrayList<Integer>();

    /**
     * Content: Person object with personal data of "timportperson"
     */
    private ImportPerson duplicateImportedPerson = new ImportPerson();

    /**
     * Constructor to use to check person imports.
     *
     * @param context        - the execution context
     * @param importInstance - the import instance
     */
    public PersonDuplicateCheckHelper(ExecutionContext context,
            Import importInstance) {
        this.context = context;
        this.database = context.getDatabase();
        this.importInstance = importInstance;
    }

    /**
     * Count the duplicates found from the database.
     *
     * @param cntx FIXME
     * @return duplicate count
     * @throws IOException   FIXME
     * @throws BeanException FIXME
     */
    public int getDuplicatesCount(OctopusContext cntx) throws IOException, BeanException {
        int duplicateCount = 0;
        // first get all imported persons data
        final List<ResultMap> importedPersonsList = getImportPersonData();

        // iteration over all imported persons data
        for (ResultMap result : importedPersonsList) {
            personIdArray = new ArrayList<Integer>();
            final ResultList list = searchForImportPersonInDB(cntx, result);
            if (list.size() > 0) {
                handleDuplicateEntry(result, list);
                duplicateCount++;
            }
            setDuplicates(duplicateImportedPerson, personIdArray); //listUniquePersonIds(personIdArray));
        }

        return duplicateCount;
    }

    private ResultList searchForImportPersonInDB(OctopusContext cntx, ResultMap result) throws IOException, BeanException {
        final Person person = createPersonForCurrentImportEntry(result);

        // check if a duplicate entry was found
        final Select select = SQL.Select(database);
        select.from(" veraweb.tperson");
        select.selectAs(database.getProperty(person, "id"), "person_id");
        select.where(getDuplicateExprPerson(cntx, person));

        return database.getList(select, context);
    }

    private Person createPersonForCurrentImportEntry(ResultMap result) {
        // create a person object to check if a duplicate entry is available
        // into the tpersons table
        final Person person = new Person();
        person.getMainLatin().setFirstname((String) result.get("firstname"));
        person.getMainLatin().setLastname((String) result.get("lastname"));

        return person;
    }

    private void handleDuplicateEntry(ResultMap result, ResultList list) throws BeanException, IOException {
        duplicateImportedPerson = new ImportPerson();
        duplicateImportedPerson.id = (Integer) result.get("import_id");

        for (Iterator<ResultList> itDuplicates = list.iterator(); itDuplicates.hasNext(); ) {
            final Map next = (Map) itDuplicates.next();
            final Integer personId = (Integer) next.get("person_id");
            if (personId != null) {
                personIdArray.add(personId);
            }
        }
    }

    /**
     * Retrieves all imported persons data by the currently used import.
     * Identified by fk_import from the importInstance.id
     *
     * @return a list of {@link ResultList}
     * @throws IOException IOException
     * @throws BeanException BeanException
     */
    @SuppressWarnings("unchecked")
    private List<ResultMap> getImportPersonData() throws IOException, BeanException {
        final ImportPerson sampleImportPerson = new ImportPerson();

        final Select select = SQL.Select(database);
        select.from(" veraweb.timportperson");
        select.selectAs(database.getProperty(sampleImportPerson, "id"), "import_id");
        select.selectAs(database.getProperty(sampleImportPerson, "firstname_a_e1"), "firstname");
        select.selectAs(database.getProperty(sampleImportPerson, "lastname_a_e1"), "lastname");

        select.where(Expr.equal(
                database.getProperty(sampleImportPerson, "fk_import"),
                importInstance.id));
        return database.getList(select, context);
    }

    /**
     * Check if given {@link Person} has an duplicate into the database.
     *
     * @param cntx   - {@link OctopusContext} the octopus context
     * @param person - {@link Person} the person to check agains the database
     * @return {@link Clause} the sql statement for the check.
     */
    public Clause getDuplicateExprPerson(OctopusContext cntx, Person person) {
        // Not deleted person
        Clause clause = Where.and(Expr.equal("fk_orgunit",
                ((PersonalConfigAA) cntx.personalConfig()).getOrgUnitId()),
                Expr.equal("deleted", PersonConstants.DELETED_FALSE));

        String ln = person == null || person.lastname_a_e1 == null || person.lastname_a_e1.equals("") ? ""
                : person.lastname_a_e1;
        String fn = person == null || person.firstname_a_e1 == null || person.firstname_a_e1.equals("") ? ""
                : person.firstname_a_e1;

        Clause normalNamesClause = Where.and(Expr.equal("lastname_a_e1", fn),
                Expr.equal("firstname_a_e1", ln));
        Clause revertedNamesClause = Where.and(Expr.equal("lastname_a_e1", ln),
                Expr.equal("firstname_a_e1", fn));
        Clause checkMixChanges = Where.or(normalNamesClause,
                revertedNamesClause);

        // Checking changes between first and lastname

        CharacterPropertiesReader cpr = new CharacterPropertiesReader();

        // Temporarily storage of the old values
        String helpFirstName = fn;
        String helpLastName = ln;

        if (cpr.propertiesAreAvailable()) {
            for (final String key : cpr.properties.stringPropertyNames()) {
                String value = cpr.properties.getProperty(key);

                if (ln.contains(value)) {
                    ln = ln.replaceAll(value, key);
                } else if (ln.contains(key)) {
                    ln = ln.replaceAll(key, value);
                }

                if (fn.contains(value)) {
                    fn = fn.replaceAll(value, key);
                } else if (fn.contains(key)) {
                    fn = fn.replaceAll(key, value);
                }
            }
        }

        Clause finalCaseQuery = getQueryOfAllDuplicatesCases(ln, fn,
                helpFirstName, helpLastName);

        Clause dupCheckFinal = Where.or(checkMixChanges, finalCaseQuery);

        // Merging with the easiest check
        return Where.and(clause, dupCheckFinal);
    }

    /**
     * All posible cases for duplicates TODO Write explanation
     *
     * @param ln            converted lastname
     * @param fn            converted firstname
     * @param helpFirstName old firstname
     * @param helpLastName  old lastname
     * @return Clause finalCaseQuery
     */
    private Clause getQueryOfAllDuplicatesCases(final String ln,
            final String fn, final String helpFirstName,
            final String helpLastName) {

        /*
         * Clauses to allow having duplicates with the first- and lastname with
         * umlauts
         */
        /* With reverted values firstname to lastname, lastname to firstname */
        Clause revertedNamesEncoding = Where.and(
                Expr.equal("lastname_a_e1", fn),
                Expr.equal("firstname_a_e1", ln));
        /* Without reverted values firstname to lastname, lastname to firstname */
        Clause normalNamesEncoding = Where.and(Expr.equal("lastname_a_e1", ln),
                Expr.equal("firstname_a_e1", fn));
        /* Merged clause */
        Clause checkMixPairChangesEncoding = Where.or(revertedNamesEncoding,
                normalNamesEncoding);

        /*
         * Clauses asuming that there are changes only in one of the names
         * (first- or lastname) 'helpFirstName' and 'helpLastName' store only
         * old values
         */
        /*
         * With reverted values firstname to lastname, lastname to firstname.
         * Old value: firstname
         */
        Clause revertedOldFNnewLN = Where.and(
                Expr.equal("lastname_a_e1", helpFirstName),
                Expr.equal("firstname_a_e1", ln));
        /*
         * With reverted values firstname to lastname, lastname to firstname.
         * Old value: lastname
         */
        Clause revertedNewFNoldLN = Where.and(Expr.equal("lastname_a_e1", fn),
                Expr.equal("firstname_a_e1", helpLastName));
        /* Merged clause */
        Clause revertedMixChangesEncoding = Where.or(revertedOldFNnewLN,
                revertedNewFNoldLN);
        /*
         * Without reverted values firstname to lastname, lastname to firstname.
         * Old value: lastname
         */
        Clause oldLNnewFN = Where.and(
                Expr.equal("lastname_a_e1", helpLastName),
                Expr.equal("firstname_a_e1", fn));
        /*
         * Without reverted values firstname to lastname, lastname to firstname.
         * Old value: firstname
         */
        Clause newLNoldFN = Where.and(Expr.equal("lastname_a_e1", ln),
                Expr.equal("firstname_a_e1", helpFirstName));
        /* Merged clause */
        Clause checkMixChangesEncoding = Where.or(oldLNnewFN, newLNoldFN);
        /*
         * Clause to check reverted values of the old values (first and
         * lastname) at the same time
         */
        Clause oldFNoldLN = Where.and(
                Expr.equal("lastname_a_e1", helpFirstName),
                Expr.equal("firstname_a_e1", helpLastName));

        /* Merged clauses */
        Clause normalQueryWithReverseChecks = Where.or(
                checkMixPairChangesEncoding, revertedMixChangesEncoding);
        Clause mergedValuesQuery = Where
                .or(checkMixChangesEncoding, oldFNoldLN);
        /* FINAL CLAUSE */
        Clause finalCaseQuery = Where.or(normalQueryWithReverseChecks,
                mergedValuesQuery);
        /* ************** */

        return finalCaseQuery;
    }

    /**
     * Gets all companies with duplicated names
     *
     * @param octopusContext FIXME
     * @param person         FIXME
     * @return company name: company_a_e1
     */
    public Clause getDuplicateExprCompany(OctopusContext octopusContext, Person person) {
        Clause clause = Where.and(Expr.equal("fk_orgunit",
                ((PersonalConfigAA) octopusContext.personalConfig()).getOrgUnitId()),
                Expr.equal("deleted", PersonConstants.DELETED_FALSE));
        String companyName = person == null || person.company_a_e1 == null || person.company_a_e1.equals("") ? ""
                : person.company_a_e1;
        return Where.and(clause, Expr.equal("company_a_e1", companyName));
    }

    /**
     * Updates the found duplicate ids.
     *
     * @param importPerson - the imported person to update
     * @param duplicates   - the duplicate ids
     * @throws BeanException BeanException
     * @throws IOException IOException
     */
    void setDuplicates(ImportPerson importPerson, List<Integer> duplicates)
            throws BeanException, IOException {
        importPerson.duplicates = serializeDuplicates(duplicates);
        BeanStatement prepareUpdate = database.prepareUpdate(importPerson,
                Collections.singleton("id"),
                Collections.singleton("duplicates"), context);
        if (prepareUpdate != null) {
            prepareUpdate.execute(importPerson);
        }
    }

    /**
     * Creates a string from the given duplicate ids with the
     * {@link ImportPerson#PK_SEPARATOR_CHAR} character.
     *
     * @param duplicates - the duplicate list
     * @return the duplicates as string
     */
    final static String serializeDuplicates(List<Integer> duplicates) {
        if (duplicates == null) {
            return null;
        }
        final StringBuffer buffer = new StringBuffer();
        for (Iterator<Integer> itDuplicates = duplicates.iterator(); itDuplicates.hasNext(); ) {
            if (buffer.length() > 0) {
                buffer.append(ImportPerson.PK_SEPARATOR_CHAR);
            }
            buffer.append(itDuplicates.next());
        }
        return buffer.toString();
    }
}
