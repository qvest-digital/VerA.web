/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Import;
import de.tarent.aa.veraweb.beans.ImportPerson;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.dblayer.helper.ResultList;
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

/**
 * This class contains methods to handle all needed duplication checks on a
 * {@link Person}.
 *
 * @author Viktor Hamm, tarent Solution GmbH, v.hamm@tarent.de
 *
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

	int duplicateCount = 0;

	/**
	 * Default constructor
	 */
	public PersonDuplicateCheckHelper() {
	}

	/**
	 * Constructor to use to check person imports.
	 *
	 * @param context
	 *            - the execution context
	 * @param importInstance
	 *            - the import instance
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
	 * @return duplicate count
	 *
	 * @throws IOException
	 * @throws BeanException
	 * @throws SQLException
	 */
	public int getDuplicatesCount(OctopusContext cntx) throws IOException,
			BeanException {
		int duplicateCount = 0;

		// first get all imported persons data
		List<ResultList> importedPersonsList = getImportPersonData();

		// iteration over all imported persons data
		for (Iterator<ResultList> itImportPersons = importedPersonsList
				.iterator(); itImportPersons.hasNext();) {
			Map importPersonMap = (Map) itImportPersons.next();

			// create a person object to check if a duplicate entry is available
			// into the tpersons table
			Person person = new Person();
			person.getMainLatin().setFirstname(
					(String) importPersonMap.get("firstname"));
			person.getMainLatin().setLastname(
					(String) importPersonMap.get("lastname"));

			// check if a duplicate entry was found
			Select select = SQL.Select(database);
			select.from(" veraweb.tperson");
			select.selectAs(database.getProperty(person, "id"), "person_id");
			select.where(getDuplicateExprPerson(cntx, person));
			ResultList list = database.getList(select, context);

			// array list to store duplicate person ids
			List<Integer> duplicates = new ArrayList<Integer>();
			ImportPerson duplicateImportedPerson = new ImportPerson();
			duplicateImportedPerson.id = (Integer) importPersonMap
					.get("import_id");

			// duplicate entry was found. Handle it
			for (Iterator<ResultList> itDuplicates = list.iterator(); itDuplicates
					.hasNext();) {
				Map next = (Map) itDuplicates.next();
				Integer personId = (Integer) next.get("person_id");
				if (personId != null) {
					duplicates.add(personId);
				}
			}

			duplicateCount++;
			setDuplicates(duplicateImportedPerson, duplicates);
		}
		return duplicateCount;
	}

	/**
	 * Retrieves all imported persons data by the currently used import.
	 * Identified by fk_import from the importInstance.id
	 *
	 * @return a list of {@link ResultList}
	 * @throws IOException
	 * @throws BeanException
	 */
	@SuppressWarnings("unchecked")
	private List<ResultList> getImportPersonData() throws IOException,
			BeanException {
		ImportPerson sampleImportPerson = new ImportPerson();

		Select select = SQL.Select(database);
		select.from(" veraweb.timportperson");
		select.selectAs(database.getProperty(sampleImportPerson, "id"),
				"import_id");
		select.selectAs(
				database.getProperty(sampleImportPerson, "firstname_a_e1"),
				"firstname");
		select.selectAs(
				database.getProperty(sampleImportPerson, "lastname_a_e1"),
				"lastname");

		select.where(Expr.equal(
				database.getProperty(sampleImportPerson, "fk_import"),
				importInstance.id));
		return database.getList(select, context);
	}

	/***
	 * Check if given {@link Person} has an duplicate into the database.
	 *
	 * @param cntx
	 *            - {@link OctopusContext} the octopus context
	 * @param person
	 *            - {@link Person} the person to check agains the database
	 *
	 * @return {@link Clause} the sql statement for the check.
	 */
	public Clause getDuplicateExprPerson(OctopusContext cntx, Person person) {
		Clause clause = Where.and(Expr.equal("fk_orgunit",
				((PersonalConfigAA) cntx.personalConfig()).getOrgUnitId()),
				Expr.equal("deleted", PersonConstants.DELETED_FALSE));
		String ln = person == null || person.lastname_a_e1 == null ? ""
				: person.lastname_a_e1;
		String fn = person == null || person.firstname_a_e1 == null ? ""
				: person.firstname_a_e1;

		Clause normalNamesClause = Where.and(Expr.equal("lastname_a_e1", fn),
				Expr.equal("firstname_a_e1", ln));
		Clause revertedNamesClause = Where.and(Expr.equal("lastname_a_e1", ln),
				Expr.equal("firstname_a_e1", fn));
		Clause checkMixChanges = Where.or(normalNamesClause,
				revertedNamesClause);

		// Checking changes between first and lastname
		Clause dupNormalCheck = Where.and(clause, checkMixChanges);

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

		// Merging with the easiest check
		return Where.or(dupNormalCheck, finalCaseQuery);
	}

	/**
	 * All posible cases for duplicates TODO Write explanation
	 *
	 * @param ln
	 *            converted lastname
	 * @param fn
	 *            converted firstname
	 * @param helpFirstName
	 *            old firstname
	 * @param helpLastName
	 *            old lastname
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

	public Clause getDuplicateExprCompany(OctopusContext cntx, Person person) {
		Clause clause = Where.and(Expr.equal("fk_orgunit",
				((PersonalConfigAA) cntx.personalConfig()).getOrgUnitId()),
				Expr.equal("deleted", PersonConstants.DELETED_FALSE));
		String companyName = person == null || person.company_a_e1 == null ? ""
				: person.company_a_e1;
		return Where.and(clause, Expr.equal("company_a_e1", companyName));
	}

	/**
	 * Updates the found duplicate ids.
	 *
	 * @param importPerson
	 *            - the imported person to update
	 * @param duplicates
	 *            - the duplicate ids
	 * @throws BeanException
	 * @throws IOException
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
	 * {@link ImportPerson#PK_SEPERATOR_CHAR} character.
	 *
	 * @param duplicates
	 *            - the duplicate list
	 * @return the duplicates as string
	 */
	final static String serializeDuplicates(List<Integer> duplicates) {
		if (duplicates == null)
			return null;
		StringBuffer buffer = new StringBuffer();
		for (Iterator<Integer> itDuplicates = duplicates.iterator(); itDuplicates
				.hasNext();) {
			if (buffer.length() > 0)
				buffer.append(ImportPerson.PK_SEPERATOR_CHAR);
			buffer.append(itDuplicates.next());
		}
		return buffer.toString();
	}
}
