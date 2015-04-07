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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonSearch;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.utils.CharacterPropertiesReader;
import de.tarent.dblayer.helper.ResultMap;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * This worker implements change request 2.3 for version 1.2.0.
 *
 * It enables the user to run a duplicate search over all persons
 * in the database. Duplicates are found by searching for equal
 * firstname and lastname names.
 *
 * @author cklein
 */
public class PersonDuplicateSearchWorker extends PersonListWorker
{
	/**
	 * Constructor
	 */
	public PersonDuplicateSearchWorker()
	{
		super();
	}

	@Override
	public List showList(OctopusContext cntx) throws BeanException, IOException
	{
		// code in part duplicated from PersonListWorker
		final Database database = getDatabase(cntx);

		final Integer start = getStart(cntx);
		final Integer limit = getLimit(cntx);
		final Integer count = getCount(cntx, database);
		final Map param = getParamMap(start, limit, count);
		cntx.setContent(OUTPUT_showListParams, param);
		cntx.setContent(OUTPUT_getSelection, getSelection(cntx, count));
		cntx.setContent( "action", "duplicateSearch" );

		final Select select = getSelect(database);
		this.extendColumns(cntx, select);
		this.extendWhere(cntx, select);
		this.extendLimit( cntx, select );
		select.orderBy( Order.asc( "lastname_a_e1" ).andAsc( "firstname_a_e1" ) );

		/* FIXME remove this temporary fix ASAP
		 * cklein 2009-09-17
		 * Temporary workaround for NPE Exception in Conjunction with temporary Connection Pooling Fix in tarent-database
		 * Somehow the resultlist returned by getResultList or its underlying ResultSet will be NULL when entering the view
		 * although, upon exiting this method the first time that it is called, will return the correct resultlist with at most
		 * 10 entries in the underlying resultset as is defined by the query.
		 */
		final ArrayList< Map > result = new ArrayList< Map >();
		final List resultList = getResultList(database, select);

		return getListWithOrdering(convertFromResultListToArrayList(resultList));
	}

	/**
	 * Conversion to manipulate dinamic lists
	 *
	 * @param resultList List result list
	 * @return ArrayList<Map>
	 */
	public ArrayList<Map> convertFromResultListToArrayList(List resultList) {
		final ArrayList< Map > result = new ArrayList< Map >();

		for (int i = 0; i < resultList.size(); i++) {
			final HashMap<String, Object> tmp = new HashMap<String, Object>();
			final Set<String> keys = ((ResultMap) resultList.get(i)).keySet();
			for (String key : keys) {
				tmp.put(key, ((ResultMap) resultList.get(i)).get(key));
			}
			result.add((Map) tmp);
		}

		return result;
	}

	/**
	 * Giving correct order to the duplicates list - THE DUPLICATES MUST GO TOGETHER
	 * @param initList ArrayList<Map>
	 * @return ArrayList<Map>
	 */
	public ArrayList<Map> getListWithOrdering(ArrayList<Map> initList) {

		final ArrayList< Map > result = new ArrayList< Map >();

		for (int i = 0; i < initList.size(); i++) {
			Map tmp = initList.get(i);

			for (int j = 0; j < initList.size(); j++) {
				Map tmp2 = initList.get(j);

				if (checkDuplicateNames(tmp,tmp2) && i != j) {
					result.add((Map) tmp2);
					initList.remove(j);
					j--;
				}
			}
			result.add((Map) tmp);
			if (!initList.isEmpty()) {
				initList.remove(i);
				i--;
			}
		}

		return result;
	}

	/**
	 * Checking duplicates result between duplicates
	 * @param tmp Map
	 * @param tmp2 Map
	 * @return Boolean
	 */
	public Boolean checkDuplicateNames(Map tmp, Map tmp2) {
		CharacterPropertiesReader cpr = new CharacterPropertiesReader();

		String firstname1 = cpr.convertUmlauts((String) tmp.get("firstname_a_e1"));
		String lastname1 = cpr.convertUmlauts((String) tmp.get("lastname_a_e1"));
		String firstname2 = cpr.convertUmlauts((String) tmp2.get("firstname_a_e1"));
		String lastname2 = cpr.convertUmlauts((String) tmp2.get("lastname_a_e1"));

		if ((firstname1.equalsIgnoreCase(firstname2) && lastname1.equals(lastname2))
				|| (firstname1.equalsIgnoreCase(lastname2) && lastname1.equals(firstname2))) {
			return true;
		}

		return false;
	}

	@Override
	protected Select getSelect(PersonSearch search, Database database) throws IOException, BeanException {
		final Person template = new Person();
		final Select result = database.getSelectIds(template);

		// replicated from PersonListWorker.
		return result.
				select("firstname_a_e1").
				select("lastname_a_e1").
				select("firstname_b_e1").
				select("lastname_b_e1").
				select("function_a_e1").
				select("company_a_e1").
				select("street_a_e1").
				select("zipcode_a_e1").
				select("city_a_e1");
	}

	@Override
	protected Integer getAlphaStart(OctopusContext cntx, String start) throws BeanException, IOException {
		// code duplicated from PersonListWorker.getAlphaStart()
		final Database database = getDatabase(cntx);
		final Select select = database.getEmptySelect(new Person());
		select.select("COUNT(DISTINCT(tperson.pk))");
		select.from("veraweb.tperson person2");
		select.setDistinct(false);

		this.extendWhere(cntx, select);
		if (start != null && start.length() > 0) {
			select.whereAnd(Expr.less("tperson.lastname_a_e1", Escaper.escape(start)));
		}

		final Integer count = database.getCount(select);
		return new Integer(count.intValue() - (count.intValue() % getLimit(cntx).intValue()));
	}

	@Override
	protected Integer getCount(OctopusContext cntx, Database database) throws BeanException, IOException {
		final Select select = database.getEmptySelect(new Person());
		select.select("COUNT(DISTINCT(tperson.pk))");
		select.setDistinct(false);
		this.extendSubselect(cntx, select);

		return database.getCount(select);
	}

	/**
	 * Extend the subselect statement.
	 *
	 * @param cntx Context
	 * @param subselect Given statement
	 */
	protected void extendSubselect(OctopusContext cntx, Select subselect) {
		subselect.from("veraweb.tperson person2");

		subselect.whereAnd(
				getClauseForOrgunit(cntx)
		).whereAnd(
				getClausePersonNotDeleted()
		).whereAnd(
				getClausePkIsDifferentOrgunitIsSame()
		).whereAnd(
				Where.or(getClauseFirstnameAndLastnameEquals(), getClauseFirstAndLastnameSwapped())
		).whereAnd(
				getClauseFirstOrLastnameNotEmpty()
		);
	}

	/**
	 * Set the limit for the result list.
	 *
	 * @param cntx Context
	 * @param select Select
	 *
	 * @throws BeanException TODO
	 * @throws IOException TODO
	 */
	protected void extendLimit(OctopusContext cntx, Select select) throws BeanException, IOException {
		final Integer start = this.getStart(cntx);
		final Integer limit = this.getLimit(cntx);
		select.Limit(new Limit(limit, start));
	}

	@Override
	protected void extendWhere(OctopusContext cntx, Select select) {
		final Database database = new DatabaseVeraWeb(cntx);
		Select subselect = null;
		final Person person = new Person();
		try {
			subselect = database.getSelectIds(person);
		} catch (IOException io) {
			new IOException("Fehler beim select der IDs", io);
		} catch (BeanException be) {
			new IOException("Fehler beim select der IDs", be);
		}
		this.extendSubselect(cntx, subselect);
		subselect.setDistinct(false);
		subselect.orderBy(Order.asc("tperson.lastname_a_e1").andAsc("tperson.firstname_a_e1"));
		try {
			select.whereAnd(new RawClause("tperson.pk IN (" + subselect.statementToString() + ")"));
		} catch (SyntaxErrorException e) {
			new SyntaxErrorException("Konvertierung der Statement zu String fehlgeschlagen");
		}
	}

	private Where getClauseForOrgunit(OctopusContext cntx) {
		return Where.and(
				Expr.equal("tperson.fk_orgunit", ((PersonalConfigAA) cntx.personalConfig()).getOrgUnitId()),
				Expr.equal("tperson.deleted", PersonConstants.DELETED_FALSE)
		);
	}

	private Where getClausePkIsDifferentOrgunitIsSame() {
		return Where.and(
				new RawClause("tperson.pk!=person2.pk"),
				new RawClause("tperson.fk_orgunit=person2.fk_orgunit")
		);
	}

	private Where getClauseFirstOrLastnameNotEmpty() {
		return Where.and(
				new RawClause("tperson.lastname_a_e1<>''"),
				new RawClause("tperson.firstname_a_e1<>''")
		);
	}

	private Where getClauseFirstAndLastnameSwapped() {
		return Where.and( // Reverted names
				new RawClause("veraweb.umlaut_fix(tperson.firstname_a_e1)=veraweb.umlaut_fix(person2.lastname_a_e1)"),
				new RawClause("veraweb.umlaut_fix(tperson.lastname_a_e1)=veraweb.umlaut_fix(person2.firstname_a_e1)")
		);
	}

	private Where getClauseFirstnameAndLastnameEquals() {
		return Where.and(
				new RawClause("veraweb.umlaut_fix(tperson.firstname_a_e1)=veraweb.umlaut_fix(person2.firstname_a_e1)"),
				new RawClause("veraweb.umlaut_fix(tperson.lastname_a_e1)=veraweb.umlaut_fix(person2.lastname_a_e1)")
		);
	}

	private Where getClausePersonNotDeleted() {
		return Expr.equal("person2.deleted", PersonConstants.DELETED_FALSE);
	}
}
