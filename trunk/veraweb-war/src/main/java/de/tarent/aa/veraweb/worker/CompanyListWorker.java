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
package de.tarent.aa.veraweb.worker;

import java.io.IOException;

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.utils.AddressHelper;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * <p>
 * Diese Octopus-Worker-Klasse lädt eine Liste von Firmen,
 * diese werden in dem Popup zur Firmen-Auswahl angezeigt.
 * </p>
 * <p>
 * Details bitte dem {@link de.tarent.octopus.beans.BeanListWorker BeanListWorker} entnehmen.
 * </p>
 * 
 * @author Christoph Jerolimov
 */
public class CompanyListWorker extends ListWorkerVeraWeb {
    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public CompanyListWorker() {
		super("Person");
	}

    //
    // Oberklasse BeanListWorker
    //
	/**
	 * Schränkt das Suchergebnis auf nicht gelöschte Firmen ein.
	 */
	@Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
		select.where(Where.and(
				Expr.equal("fk_orgunit", ((PersonalConfigAA)cntx.personalConfig()).getOrgUnitId()), Where.and(
				Expr.equal("deleted", PersonConstants.DELETED_FALSE),
				Expr.equal("iscompany", PersonConstants.ISCOMPANY_TRUE))));
	}

	@Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
		select.orderBy(Order.asc("lastname_a_e1"));
	}

	@Override
    protected Integer getAlphaStart(OctopusContext cntx, String start) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		
		Where where = Where.and(
				Expr.equal("fk_orgunit", ((PersonalConfigAA)cntx.personalConfig()).getOrgUnitId()), Where.and(
				Expr.equal("deleted", PersonConstants.DELETED_FALSE),
				Expr.equal("iscompany", PersonConstants.ISCOMPANY_TRUE)));
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		buffer.append(where.clauseToString());
		buffer.append(") AND lastname_a_e1 < '");
		Escaper.escape(buffer, start);
		buffer.append("'");
		
		Select select = database.getCount(BEANNAME);
		select.where(new RawClause(buffer));
		
		Integer i = database.getCount(select);
		return new Integer(i.intValue() - (i.intValue() % getLimit(cntx).intValue()));
	}

	/** Gibt die maximale Anzahl von Datensätzen pro Seite. */
	protected Integer limit = new Integer(5);
	@Override
    protected Integer getLimit(OctopusContext cntx) {
		return limit;
	}

	//
    // Octopus-Aktionen
    //
	/** Octopus-Parameter für die Aktion {@link #copyCompanyData(OctopusContext, Integer, String)} */
	public static final String INPUT_copyCompanyData[] = { "company", "companyfield" };
	/**
	 * Diese Worker-Aktion lädt einen Person Eintrag und kopiert
	 * entsprechende Firmen-Daten in die aktuell geöffnete Person.
	 * 
	 * @param cntx Octopus-Context
	 * @param company Person-PK der Firma dessen Daten übernommen werden sollen.
	 * @param companyfield Name des HTML-Firmenfeldes zu dem Firmendaten geladen werden sollen.
	 * @throws BeanException
	 * @throws IOException
	 */
	public void copyCompanyData(OctopusContext cntx, Integer company, String companyfield) throws BeanException, IOException {
		cntx.setContent("personTab", cntx.requestAsInteger("personTab"));
		cntx.setContent("personMemberTab", cntx.requestAsInteger("personMemberTab"));
		cntx.setContent("personAddresstypeTab", cntx.requestAsInteger("personAddresstypeTab"));
		cntx.setContent("personLocaleTab", cntx.requestAsInteger("personLocaleTab"));
		
		final boolean copyAll = false;
		
		Request request = getRequest(cntx);
		Database database = getDatabase(cntx);
		
		Person person = (Person)request.getBean("Person", "person");
		Person personcompany = (Person)
				database.getBean("Person",
				database.getSelect(person).
				where(Expr.equal("pk", company)));
		if (personcompany == null) personcompany = new Person();
		
		boolean copyBusinessLatin = copyAll || "person-company_a_e1".equals(companyfield);
		boolean copyBusinessExtra1 = copyAll || "person-company_a_e2".equals(companyfield);
		boolean copyBusinessExtra2 = copyAll || "person-company_a_e3".equals(companyfield);
		boolean copyPrivateLatin = copyAll || "person-company_b_e1".equals(companyfield);
		boolean copyPrivateExtra1 = copyAll || "person-company_b_e2".equals(companyfield);
		boolean copyPrivateExtra2 = copyAll || "person-company_b_e3".equals(companyfield);
		boolean copyOtherLatin = copyAll || "person-company_c_e1".equals(companyfield);
		boolean copyOtherExtra1 = copyAll || "person-company_c_e2".equals(companyfield);
		boolean copyOtherExtra2 = copyAll || "person-company_c_e3".equals(companyfield);
		
		//Name der Firma steht in Lastname. Der wird bei der Person nach Company kopiert
		String companyNameLatin, companyNameExtra1, companyNameExtra2;
		companyNameLatin = personcompany.getMainLatin().getLastname();
		companyNameExtra1 = personcompany.getMainExtra1().getLastname();
		if (AddressHelper.empty(companyNameExtra1)) companyNameExtra1 = companyNameLatin;
		companyNameExtra2 = personcompany.getMainExtra2().getLastname();
		if (AddressHelper.empty(companyNameExtra2)) companyNameExtra2 = companyNameLatin;
		
		if (copyBusinessLatin) {
			AddressHelper.copyAddressData(personcompany.getBusinessLatin(), person.getBusinessLatin(), true, true, true, true);
			person.getBusinessLatin().setCompany(companyNameLatin);
		}
		if (copyBusinessExtra1) {
			AddressHelper.copyAddressData(personcompany.getBusinessExtra1(), person.getBusinessExtra1(), true, true, true, true);
			person.getBusinessExtra1().setCompany(companyNameExtra1);
		}
		if (copyBusinessExtra2) {
			AddressHelper.copyAddressData(personcompany.getBusinessExtra2(), person.getBusinessExtra2(), true, true, true, true); //ich hasse copy/paste Fehler!!
			person.getBusinessExtra2().setCompany(companyNameExtra2);
		}
		if (copyPrivateLatin) {
			AddressHelper.copyAddressData(personcompany.getPrivateLatin(), person.getPrivateLatin(), true, true, true, true);
			person.getPrivateLatin().setCompany(companyNameLatin);
		}
		if (copyPrivateExtra1) {
			AddressHelper.copyAddressData(personcompany.getPrivateExtra1(), person.getPrivateExtra1(), true, true, true, true);
			person.getPrivateExtra1().setCompany(companyNameExtra1);
		}
		if (copyPrivateExtra2) {
			AddressHelper.copyAddressData(personcompany.getPrivateExtra2(), person.getPrivateExtra2(), true, true, true, true);
			person.getPrivateExtra2().setCompany(companyNameExtra2);
		}
		if (copyOtherLatin) {
			AddressHelper.copyAddressData(personcompany.getOtherLatin(), person.getOtherLatin(), true, true, true, true);
			person.getOtherLatin().setCompany(companyNameLatin);
		}
		if (copyOtherExtra1) {
			AddressHelper.copyAddressData(personcompany.getOtherExtra1(), person.getOtherExtra1(), true, true, true, true);
			person.getOtherExtra1().setCompany(companyNameExtra1);
		}
		if (copyOtherExtra2) {
			AddressHelper.copyAddressData(personcompany.getOtherExtra2(), person.getOtherExtra2(), true, true, true, true);
			person.getOtherExtra2().setCompany(companyNameExtra2);
		}
		cntx.setContent("person", person);
	}
}
