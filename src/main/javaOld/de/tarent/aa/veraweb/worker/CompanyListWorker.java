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
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
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
	    cntx.setContent("tab", cntx.requestAsObject("tab"));
	    Database database2 = new DatabaseVeraWeb(cntx);
	    TransactionContext context = database2.getTransactionContext();

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
		companyNameLatin = personcompany.getMainLatin().getCompany();
		companyNameExtra1 = personcompany.getMainExtra1().getCompany();
		if (AddressHelper.empty(companyNameExtra1)) companyNameExtra1 = companyNameLatin;
		companyNameExtra2 = personcompany.getMainExtra2().getCompany();
		if (AddressHelper.empty(companyNameExtra2)) companyNameExtra2 = companyNameLatin;

		if (copyBusinessLatin || copyBusinessExtra1 || copyBusinessExtra2) {
			AddressHelper.copyAddressData(personcompany.getBusinessLatin(), person.getBusinessLatin(), true, true, true, true);
			person.getBusinessLatin().setCompany(companyNameLatin);
			AddressHelper.copyAddressData(personcompany.getBusinessExtra1(), person.getBusinessExtra1(), true, true, true, true);
			person.getBusinessExtra1().setCompany(companyNameExtra1);
			AddressHelper.copyAddressData(personcompany.getBusinessExtra2(), person.getBusinessExtra2(), true, true, true, true);
			person.getBusinessExtra2().setCompany(companyNameExtra2);
		}

		if (copyPrivateLatin || copyPrivateExtra1 || copyPrivateExtra2) {
			AddressHelper.copyAddressData(personcompany.getBusinessLatin(), person.getPrivateLatin(), true, true, true, true);
			person.getPrivateLatin().setCompany(companyNameLatin);
			AddressHelper.copyAddressData(personcompany.getBusinessExtra1(), person.getPrivateExtra1(), true, true, true, true);
			person.getPrivateExtra1().setCompany(companyNameExtra1);
			AddressHelper.copyAddressData(personcompany.getBusinessExtra2(), person.getPrivateExtra2(), true, true, true, true);
			person.getPrivateExtra2().setCompany(companyNameExtra2);
		}

		if (copyOtherLatin || copyOtherExtra1 || copyOtherExtra2) {
			AddressHelper.copyAddressData(personcompany.getBusinessLatin(), person.getOtherLatin(), true, true, true, true);
			person.getOtherLatin().setCompany(companyNameLatin);
			AddressHelper.copyAddressData(personcompany.getBusinessExtra1(), person.getOtherExtra1(), true, true, true, true);
			person.getOtherExtra1().setCompany(companyNameExtra1);
			AddressHelper.copyAddressData(personcompany.getBusinessExtra2(), person.getOtherExtra2(), true, true, true, true);
			person.getOtherExtra2().setCompany(companyNameExtra2);
		}

		AddressHelper.checkPersonSalutation(person, database, context);
		cntx.setContent("person", person);
		cntx.setContent("showAdressTab", "true");
		cntx.setContent("personAddresstypeTab", cntx.requestAsString("personAddresstypeTab"));
	}
}
