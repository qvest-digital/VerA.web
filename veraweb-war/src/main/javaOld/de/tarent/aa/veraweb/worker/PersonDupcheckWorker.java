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
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.List;

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.utils.AddressHelper;
import de.tarent.aa.veraweb.utils.CharacterPropertiesReader;
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker bearbeitet Personenlisten unter besonderer Beachtung, ob
 * ein Duplikat zu einem bestimmten Datensatz vorliegt.
 */
public class PersonDupcheckWorker extends ListWorkerVeraWeb {
    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public PersonDupcheckWorker() {
		super("Person");
	}

    //
    // Oberklasse BeanListWorker
    //
	@Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
		Person person = (Person)cntx.contentAsObject("person");

		//Specific handling to differ between company and person dupcheck.
        String isCompany = cntx.requestAsString("person-iscompany");

        if (isCompany != null && isCompany.equals("t")) {
            select.where(getDuplicateExprCompany(cntx, person));
        } else {
            select.where(getDuplicateExprPerson(cntx, person));
        }
	}

	//
    // Octopus-Aktionen
    //
    /** Eingabe-Parameter der Octopus-Aktion {@link #check(OctopusContext, Boolean)} */
	public static final String INPUT_check[] = { "person-nodupcheck" };
    /** Eingabe-Parameterzwang der Octopus-Aktion {@link #check(OctopusContext, Boolean)} */
	public static final boolean MANDATORY_check[] = { false };
	/**
     * Diese Octopus-Aktion holt eine Person aus dem Octopus-Request
     * (unter "person-*") oder der Octopus-Session (unter "dupcheck-person"),
     * legt sie und ihr Akkreditierungsdatum unter "person" bzw. "person-diplodatetime"
     * in den Octopus-Content und testet das �bergebene Flag. Ist es
     * <code>true</code>, so wird der Eintrag in der Octopus-Session unter
     * "dupcheck-person" geloescht. Ansonsten wird dieser auf die eingelesene
     * Person gesetzt und ein Duplikats-Check durchgefuehrt; falls dieser Duplikate
     * zur Person findet, wird der Status "dupcheck" gesetzt.
     *
     * @param cntx Octopus-Kontext
     * @param nodupcheck Flag zum �bergehen des Duplikat-Checks
	 */
	public void check(OctopusContext cntx, Boolean nodupcheck) throws BeanException, IOException {
		Request request = new RequestVeraWeb(cntx);
		Database database = new DatabaseVeraWeb(cntx);

		// Person Daten laden und in den Content stellen!
		Person person = (Person)cntx.sessionAsObject("dupcheck-person");
		if (cntx.requestContains("id")) {
			person = (Person)request.getBean("Person", "person");
			/*
			 * fixes issue 1865
			 * must add time information from person-diplotime_a_e1
			 */
			DateHelper.addTimeToDate(person.diplodate_a_e1, cntx.requestAsString("person-diplotime_a_e1"), person.getErrors());
		}
		AddressHelper.checkPersonSalutation(person, database, database.getTransactionContext());
		cntx.setContent("person", person);
		cntx.setContent("person-diplodatetime", Boolean.valueOf(DateHelper.isTimeInDate(person.diplodate_a_e1)));

		if (nodupcheck != null && nodupcheck.booleanValue()) {
			cntx.setSession("dupcheck-person", null);
			return;
		}
		cntx.setSession("dupcheck-person", person);

		String isCompany = cntx.requestAsString("person-iscompany");

		Select select = database.getCount("Person");

		if(isCompany != null && isCompany.equals("t")){
			select.where(getDuplicateExprCompany(cntx, person));
		} else {
			select.where(getDuplicateExprPerson(cntx, person));
		}

		if (database.getCount(select).intValue() != 0) {
			cntx.setStatus("dupcheck");
		}
	}



    /* (non-Javadoc)
     * @see de.tarent.octopus.custom.beans.BeanListWorker#showList(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public List showList(OctopusContext cntx) throws BeanException, IOException {
    	cntx.setContent("originalPersonId", cntx.requestAsInteger("originalPersonId"));
		return super.showList(cntx);
	}

    protected Clause getDuplicateExprPerson(OctopusContext cntx, Person person) {
		Clause clause = Where.and(
				Expr.equal("fk_orgunit", ((PersonalConfigAA)cntx.personalConfig()).getOrgUnitId()),
				Expr.equal("deleted", PersonConstants.DELETED_FALSE));
		String ln = person == null || person.lastname_a_e1 == null ? "" : person.lastname_a_e1;
		String fn = person == null || person.firstname_a_e1 == null ? "" : person.firstname_a_e1;

		Clause normalNamesClause = Where.and(Expr.equal("lastname_a_e1", fn), Expr.equal("firstname_a_e1", ln));
		Clause revertedNamesClause = Where.and(Expr.equal("lastname_a_e1", ln), Expr.equal("firstname_a_e1", fn));
		Clause checkMixChanges = Where.or(normalNamesClause,revertedNamesClause);
		
		// Checking changes between first and lastname
		Clause dupNormalCheck = Where.and(clause, checkMixChanges);
		
		CharacterPropertiesReader cpr = new CharacterPropertiesReader();
		
		// Temporarily storage of the old values
		String helpFirstName = fn;
		String helpLastName = ln;
		
		for (final String key: cpr.properties.stringPropertyNames()) {
			String value = cpr.properties.getProperty(key);

			if (ln.contains(value)) {
				ln = ln.replaceAll(value, key);
			}
			else if (ln.contains(key)) {
				ln = ln.replaceAll(key, value);
			}
			
			if (fn.contains(value)) {
				fn = fn.replaceAll(value, key);
			}
			else if (fn.contains(key)) {
				fn = fn.replaceAll(key, value);
			}
		}
		
		Clause finalCaseQuery = getQueryOfAllDuplicatesCases(ln, fn, helpFirstName, helpLastName);
		
		// Merging with the easiest check
		return Where.or(dupNormalCheck, finalCaseQuery);
	}

	/**
	 * All posible cases for duplicates
	 * TODO Write explanation
	 * 
	 * @param ln converted lastname
	 * @param fn converted firstname
	 * @param helpFirstName old firstname
	 * @param helpLastName old lastname
	 * @return Clause finalCaseQuery
	 */
	private Clause getQueryOfAllDuplicatesCases(final String ln, final String fn,
			final String helpFirstName, final String helpLastName) {
		
		/* 
		 * Clauses to allow having duplicates with the first- and lastname with umlauts 
		 * */
		/* With reverted values firstname to lastname, lastname to firstname */
		Clause revertedNamesEncoding= Where.and(Expr.equal("lastname_a_e1", fn), Expr.equal("firstname_a_e1", ln));
		/* Without reverted values firstname to lastname, lastname to firstname */
		Clause normalNamesEncoding = Where.and(Expr.equal("lastname_a_e1", ln), Expr.equal("firstname_a_e1", fn));
		/* Merged clause */
		Clause checkMixPairChangesEncoding = Where.or(revertedNamesEncoding, normalNamesEncoding);
		
		/* 
		 * Clauses asuming that there are changes only in one of the names (first- or lastname)
		 * 'helpFirstName' and 'helpLastName' store only old values
		 * */
		/* With reverted values firstname to lastname, lastname to firstname. Old value: firstname */
			Clause revertedOldFNnewLN = Where.and(Expr.equal("lastname_a_e1", helpFirstName), Expr.equal("firstname_a_e1", ln));
		/* With reverted values firstname to lastname, lastname to firstname. Old value: lastname */
			Clause revertedNewFNoldLN = Where.and(Expr.equal("lastname_a_e1", fn), Expr.equal("firstname_a_e1", helpLastName));
		/* Merged clause */
			Clause revertedMixChangesEncoding = Where.or(revertedOldFNnewLN, revertedNewFNoldLN);
		/* Without reverted values firstname to lastname, lastname to firstname. Old value: lastname */
			Clause oldLNnewFN = Where.and(Expr.equal("lastname_a_e1", helpLastName), Expr.equal("firstname_a_e1", fn));
		/* Without reverted values firstname to lastname, lastname to firstname. Old value: firstname */
			Clause newLNoldFN = Where.and(Expr.equal("lastname_a_e1", ln), Expr.equal("firstname_a_e1", helpFirstName));
		/* Merged clause */
			Clause checkMixChangesEncoding= Where.or(oldLNnewFN,newLNoldFN);
		/* Clause to check reverted values of the old values (first and lastname) at the same time */
			Clause oldFNoldLN = Where.and(Expr.equal("lastname_a_e1", helpFirstName), Expr.equal("firstname_a_e1", helpLastName));

		/* Merged clauses */
			Clause normalQueryWithReverseChecks = Where.or(checkMixPairChangesEncoding, revertedMixChangesEncoding);
			Clause mergedValuesQuery = Where.or(checkMixChangesEncoding, oldFNoldLN);
		/* FINAL CLAUSE */
			Clause finalCaseQuery = Where.or(normalQueryWithReverseChecks, mergedValuesQuery);
		/* ************** */
		
		return finalCaseQuery;
	}
    
	protected Clause getDuplicateExprCompany(OctopusContext cntx, Person person) {
		Clause clause = Where.and(
				Expr.equal("fk_orgunit", ((PersonalConfigAA)cntx.personalConfig()).getOrgUnitId()),
				Expr.equal("deleted", PersonConstants.DELETED_FALSE));
		String companyName = person == null || person.company_a_e1 == null ? "" : person.company_a_e1;
		return Where.and(clause, Expr.equal("company_a_e1", companyName));
	}
}
