/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
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
		select.where(getDuplicateExpr(cntx, person));
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
     * "dupcheck-person" gel�scht. Ansonsten wird dieser auf die eingelesene
     * Person gesetzt und ein Duplikats-Check durchgef�hrt; falls dieser Duplikate
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
		
		Select select = database.getCount("Person");
		select.where(getDuplicateExpr(cntx, person));
		if (database.getCount(select).intValue() != 0) {
			cntx.setStatus("dupcheck");
		}
	}

	
	
    /* (non-Javadoc)
     * @see de.tarent.octopus.custom.beans.BeanListWorker#showList(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public List showList(OctopusContext cntx) throws BeanException, IOException
	{
		//Bug 1592 
    	cntx.setContent("originalPersonId", cntx.requestAsInteger("originalPersonId"));
		return super.showList(cntx);
	}

	//
    // gesch�tzte Hilfsmethoden
    //
	protected Clause getDuplicateExpr(OctopusContext cntx, Person person) {
		Clause clause = Where.and(
				Expr.equal("fk_orgunit", ((PersonalConfigAA)cntx.personalConfig()).getOrgUnitId()),
				Expr.equal("deleted", PersonConstants.DELETED_FALSE));
		String ln = person == null || person.lastname_a_e1 == null ? "" : person.lastname_a_e1;
		String fn = person == null || person.firstname_a_e1 == null ? "" : person.firstname_a_e1;
		return Where.and(clause, Where.and(Expr.equal("lastname_a_e1", ln), Expr.equal("firstname_a_e1", fn)));
	}
}
