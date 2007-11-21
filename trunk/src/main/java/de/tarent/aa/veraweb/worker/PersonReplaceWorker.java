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
 * $Id: PersonReplaceWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Octopus-Worker �bernimmt das Suchen und Ersetzen von
 * Personendaten und entsprechendenen Dokumenttypen.
 * 
 * Er kennt sowohl Funktionen zum Ersetzen von allen Treffern,
 * als auch das vorherige Anzeigen der Treffer.
 * 
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class PersonReplaceWorker extends PersonListWorker {
	//
	// KONSTANTEN
	//
	
	/** Replace-Gruppen-Parameter */
	private static final String PARAM_GROUPS[] = {
			"snr-group01", "snr-group02", "snr-group03",
			"snr-group04", "snr-group05", "snr-group06",
			"snr-group07", "snr-group08", "snr-group09",
			"snr-group10", "snr-group20" };
	/** Vorname der Hauptperson und des Partners */
	static private final List GROUP_01 = new ArrayList();
	/** Nachname der Hauptperson und des Partners */
	static private final List GROUP_02 = new ArrayList();
	/** Amtsbezeichnung / Funktion */
	static private final List GROUP_03 = new ArrayList();
	/** Anrede und Titel */
	static private final List GROUP_04 = new ArrayList();
	/** Firma / Institution */
	static private final List GROUP_05 = new ArrayList();
	/** Stra�e, Adresszusatz und Land */
	static private final List GROUP_06 = new ArrayList();
	/** PLZ / Ort und PLZ / Postfach */
	static private final List GROUP_07 = new ArrayList();
	/** Telefon, Telefax und Mobiltelefon */
	static private final List GROUP_08 = new ArrayList();
	/** E-Mail-Adresse */
	static private final List GROUP_09 = new ArrayList();
	/** Internet-Adresse */
	static private final List GROUP_10 = new ArrayList();

	static {
		initLists();
	}

	/**
	 * Erstellt die Listen zum Suchen/Ersetzen.
	 */
	static private void initLists() {
		initList(GROUP_01, "firstname", true);
		initList(GROUP_02, "lastname", true);
		initList(GROUP_04, "title", true);
		initList(GROUP_04, "salutation", true);
		initList(GROUP_03, "function", false);
		initList(GROUP_05, "company", false);
		initList(GROUP_06, "street", false);
		initList(GROUP_06, "suffix1", false);
		initList(GROUP_06, "suffix2", false);
		initList(GROUP_06, "country", false);
		initList(GROUP_07, "zipcode", false);
		initList(GROUP_07, "city", false);
		initList(GROUP_07, "pobox", false);
		initList(GROUP_07, "poboxzipcode", false);
		initList(GROUP_08, "fon", false);
		initList(GROUP_08, "fax", false);
		initList(GROUP_08, "mobil", false);
		initList(GROUP_09, "mail", false);
		initList(GROUP_10, "url", false);
	}

	/**
	 * Erstellt die Listen zum Suchen/Ersetzen.
	 */
	static private void initList(List list, String field, boolean member) {
		for (char i = 'a'; i <= (member ? 'b' : 'c'); i++) {
			for (char j = '1'; j <= '3'; j++) {
				list.add(field + "_" + i + "_e" + j);
			}
		}
	}


	//
	// ERWEITERUNG DES BEANLISTWORKERS
	//

	/**
	 * Octopus-Aktion die eine <strong>bl�tterbare</strong> Liste
	 * mit Beans aus der Datenbank in den Content stellt. Kann durch
	 * {@link #extendColumns(OctopusContext, Select)} erweitert bzw.
	 * {@link #extendWhere(OctopusContext, Select)} eingeschr�nkt werden.
	 * 
	 * Lenkt hier die entsprechende getSelect - Anfrage an die
	 * Urspr�ngliche Version des BeanListWorkers.
	 * 
	 * @see #getSelection(OctopusContext, Integer)
	 * 
	 * @param cntx Octopus-Context
	 * @return Liste mit Beans, nie null.
	 * @throws BeanException
	 * @throws IOException
	 */
	public List showList(OctopusContext cntx) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		
		Integer start = getStart(cntx);
		Integer limit = getLimit(cntx);
		Integer count = getCount(cntx, database);
		Map param = getParamMap(start, limit, count);
		
		Select select = getSelect(database);
		extendColumns(cntx, select);
		extendWhere(cntx, select);
		select.Limit(new Limit((Integer)param.get("limit"), (Integer)param.get("start")));
		
		cntx.setContent(OUTPUT_showListParams, param);
		cntx.setContent(OUTPUT_getSelection, getSelection(cntx, count));
		return getResultList(database, select);
	}

	/**
	 * �ndert die Suchanfrage der Personenliste auf die zu ersetzenen Personen.
	 */
	protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
		WhereList where = new WhereList();
		where.addAnd(Expr.equal("fk_orgunit", ((PersonalConfigAA)cntx.personalConfig()).getOrgUnitId()));
		where.addAnd(Expr.equal("deleted", PersonConstants.DELETED_FALSE));
		where.addAnd(addPersonListFilter(cntx, new WhereList()));
		select.where(where);
		select.joinLeftOuter("veraweb.tperson_doctype", "tperson.pk", "tperson_doctype.fk_person");
	}

	protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
	}

	protected Integer getAlphaStart(OctopusContext cntx, String start) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		
		WhereList where = new WhereList();
		where.addAnd(Expr.equal("fk_orgunit", ((PersonalConfigAA)cntx.personalConfig()).getOrgUnitId()));
		where.addAnd(Expr.equal("deleted", PersonConstants.DELETED_FALSE));
		where.addAnd(addPersonListFilter(cntx, new WhereList()));
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		buffer.append(where.clauseToString());
		buffer.append(") AND lastname_a_e1 < '");
		Escaper.escape(buffer, start);
		buffer.append("'");
		
		Select select = database.getCount(BEANNAME);
		select.joinLeftOuter("veraweb.tperson_doctype", "tperson.pk", "tperson_doctype.fk_person");
		select.where(new RawClause(buffer));
		
		Integer i = database.getCount(select);
		return new Integer(i.intValue() - (i.intValue() % getLimit(cntx).intValue()));
	}

	protected WhereList addPersonListFilter(OctopusContext cntx, WhereList list) {
		Map replaceRequest = getReplaceRequest(cntx);
		List fields = getFieldList(replaceRequest);
		String search = (String)replaceRequest.get("search");
		
		boolean wildcardPre = search.charAt(0) == '*';
		boolean wildcardPost = search.charAt(search.length() - 1) == '*';
		search = search.substring(
					wildcardPre ? 1 : 0, search.length() - (
					wildcardPost ? 1 : 0));
		
		if (fields.size() > 0) {
			list.addOr(getReplaceWhere(fields, search, wildcardPre, wildcardPost));
		}
		if (((Boolean)replaceRequest.get("snr-group20")).booleanValue()) {
			list.addOr(Where.or(
					getReplaceWhere("textfield", search, wildcardPre, wildcardPost),
					getReplaceWhere("textfield_p", search, wildcardPre, wildcardPost)));
		}
		return list;
	}


	//
	// NEUE OCTOPUS AKTIONEN
	//

	/** Octopus-Eingabe-Parameter f�r {@link #getReplaceRequest(OctopusContext)} */
	public static final String INPUT_getReplaceRequest[] = {};
	/** Octopus-Eingabe-Parameter f�r {@link #getReplaceRequest(OctopusContext)} */
	public static final String OUTPUT_getReplaceRequest = "replace-request";
	/**
	 * Kopiert die Such & Ersetz anfragen in den Content und merkt
	 * sich entsprechende Anfrage ggf. in der Session.<br><br>
	 * 
	 * Ist die Suchanfrage zu kurz wird der Status auf
	 * <code>"invalidsearch"</code> gesetzt.
	 * 
	 * @param cntx
	 * @return Such und Ersetz Anfrage, nie null.
	 */
	public Map getReplaceRequest(OctopusContext cntx) {
		Map replaceRequest = (Map)cntx.contentAsObject(OUTPUT_getReplaceRequest);
		if (replaceRequest != null) {
			return replaceRequest;
		}
		
		String search = cntx.requestAsString("snr-search");
		String replace = cntx.requestAsString("snr-replace");
		
		if (search == null) {
			replaceRequest = (Map)cntx.sessionAsObject(OUTPUT_getReplaceRequest);
			if (replaceRequest == null) {
				cntx.setStatus("invalidsearch");
			}
			return replaceRequest;
		}
		
		replaceRequest = new HashMap();
		replaceRequest.put("search", search);
		replaceRequest.put("replace", replace);
		for (int i = 0; i < PARAM_GROUPS.length; i++) {
			replaceRequest.put(PARAM_GROUPS[i], cntx.requestAsBoolean(PARAM_GROUPS[i]));
		}
		
		if (search.replaceAll("\\*", "").length() < 2) {
			cntx.setStatus("invalidsearch");
		}
		if (!(getFieldList(replaceRequest).size() > 0 || ((Boolean)replaceRequest.get("snr-group20")).booleanValue())) {
			cntx.setStatus("invalidsearch");
		}
		
		cntx.setSession(OUTPUT_getReplaceRequest, replaceRequest);
		return replaceRequest;
	}

	/**
	 * Gibt eine Liste entsprechend der aktuellen Anfrage
	 * zum Suchen/Ersetzen zur�ck.
	 * 
	 * @param replaceRequest Map, siehe {@link #getReplaceRequest(OctopusContext)}
	 * @return Liste mit Feldern
	 */
	protected List getFieldList(Map replaceRequest) {
		List fields = new ArrayList();
		if (((Boolean)replaceRequest.get("snr-group01")).booleanValue())
			fields.addAll(GROUP_01);
		if (((Boolean)replaceRequest.get("snr-group02")).booleanValue())
			fields.addAll(GROUP_02);
		if (((Boolean)replaceRequest.get("snr-group03")).booleanValue())
			fields.addAll(GROUP_03);
		if (((Boolean)replaceRequest.get("snr-group04")).booleanValue())
			fields.addAll(GROUP_04);
		if (((Boolean)replaceRequest.get("snr-group05")).booleanValue())
			fields.addAll(GROUP_05);
		if (((Boolean)replaceRequest.get("snr-group06")).booleanValue())
			fields.addAll(GROUP_06);
		if (((Boolean)replaceRequest.get("snr-group07")).booleanValue())
			fields.addAll(GROUP_07);
		if (((Boolean)replaceRequest.get("snr-group08")).booleanValue())
			fields.addAll(GROUP_08);
		if (((Boolean)replaceRequest.get("snr-group09")).booleanValue())
			fields.addAll(GROUP_09);
		if (((Boolean)replaceRequest.get("snr-group10")).booleanValue())
			fields.addAll(GROUP_10);
		return fields;
	}


	/** Octopus-Eingabe-Parameter f�r {@link #countData(OctopusContext)} */
	public static final String INPUT_countData[] = {};
	/** Octopus-Eingabe-Parameter f�r {@link #countData(OctopusContext)} */
	public static final String OUTPUT_countData = "snr-count";
	/**
	 * Berechnet wieviele Datens�tze bei einem {@link #replaceAllData(OctopusContext)}
	 * oder einem {@link #replaceSelectedData(OctopusContext)} ersetzt werden w�rden
	 * und gibt diese Zahl zur Benutzerinformation zur�ck.
	 * 
	 * @param cntx
	 * @throws BeanException
	 * @throws IOException
	 */
	public Integer countData(OctopusContext cntx) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		
		Select select = database.getCount("Person");
		WhereList where = new WhereList();
		
		if ("replace".equals(cntx.contentAsString("snr-action"))) {
			List selection = getSelection(cntx, null);
			if (selection == null || selection.size() == 0)
				return new Integer(0);
			where.addAnd(Expr.in("tperson.pk", selection));
		}
		
		where.addAnd(Expr.equal("fk_orgunit", ((PersonalConfigAA)cntx.personalConfig()).getOrgUnitId()));
		where.addAnd(Expr.equal("deleted", PersonConstants.DELETED_FALSE));
		where.addAnd(addPersonListFilter(cntx, new WhereList()));
		select.where(where);
		select.joinLeftOuter("veraweb.tperson_doctype", "tperson.pk", "tperson_doctype.fk_person");
		
		return database.getCount(select);
	}


	/** Octopus-Eingabe-Parameter f�r {@link #replaceAllData(OctopusContext)} */
	public static final String INPUT_replaceAllData[] = {};
	/**
	 * Octopus-Aktion die alle Daten sucht und ersetzt.
	 * 
	 * @param cntx
	 * @throws BeanException
	 * @throws IOException
	 */
	public void replaceAllData(OctopusContext cntx) throws BeanException {
		Database database = getDatabase(cntx);
		Map replaceRequest = getReplaceRequest(cntx);
		List fields = getFieldList(replaceRequest);
		String search = (String)replaceRequest.get("search");
		String replace = (String)replaceRequest.get("replace");
		
		boolean wildcardPre = search.charAt(0) == '*';
		boolean wildcardPost = search.charAt(search.length() - 1) == '*';
		search = search.substring(
					wildcardPre ? 1 : 0, search.length() - (
					wildcardPost ? 1 : 0));
		
		if (fields.size() > 0) {
			WhereList where = new WhereList();
			where.addAnd(Expr.equal("fk_orgunit", ((PersonalConfigAA)cntx.personalConfig()).getOrgUnitId()));
			where.addAnd(Expr.equal("deleted", PersonConstants.DELETED_FALSE));
			where.addAnd(getReplaceWhere(fields, search, wildcardPre, wildcardPost));
			
			database.execute(
					getReplaceUpdate(fields, search, replace, wildcardPre, wildcardPost).
					where(where));
		}
		if (((Boolean)replaceRequest.get("snr-group20")).booleanValue()) {
			Clause where = new RawClause("fk_person IN (SELECT pk FROM veraweb.tperson" +
					" WHERE fk_orgunit = " + ((PersonalConfigAA)cntx.personalConfig()).getOrgUnitId() +
					" AND deleted = '" + PersonConstants.DELETED_FALSE + "')");
			where = Where.and(where, Where.or(
							getReplaceWhere("textfield", search, wildcardPre, wildcardPost),
							getReplaceWhere("textfield_p", search, wildcardPre, wildcardPost)));
			
			database.execute(SQL.Update().
					table("veraweb.tperson_doctype").
					update("textfield", getReplaceClause("textfield", search, replace, wildcardPre, wildcardPost)).
					update("textfield_p", getReplaceClause("textfield_p", search, replace, wildcardPre, wildcardPost)).
					where(where));
		}
	}


	/** Octopus-Eingabe-Parameter f�r {@link #replaceSelectedData(OctopusContext)} */
	public static final String INPUT_replaceSelectedData[] = {};
	/**
	 * Ersetzt in der G�steliste ausgew�hlte G�ste.
	 * 
	 * @param cntx
	 * @throws BeanException
	 * @throws IOException
	 */
	public void replaceSelectedData(OctopusContext cntx) throws BeanException, IOException {
		List selection = getSelection(cntx, null);
		if (selection == null || selection.size() == 0)
			return;
		
		Database database = getDatabase(cntx);
		Map replaceRequest = getReplaceRequest(cntx);
		List fields = getFieldList(replaceRequest);
		String search = (String)replaceRequest.get("search");
		String replace = (String)replaceRequest.get("replace");
		
		boolean wildcardPre = search.charAt(0) == '*';
		boolean wildcardPost = search.charAt(search.length() - 1) == '*';
		search = search.substring(
					wildcardPre ? 1 : 0, search.length() - (
					wildcardPost ? 1 : 0));
		
		if (fields.size() > 0) {
			WhereList where = new WhereList();
			where.addAnd(Expr.in("pk", selection));
			where.addAnd(Expr.equal("fk_orgunit", ((PersonalConfigAA)cntx.personalConfig()).getOrgUnitId()));
			where.addAnd(Expr.equal("deleted", PersonConstants.DELETED_FALSE));
			where.addAnd(getReplaceWhere(fields, search, wildcardPre, wildcardPost));
			
			database.execute(
					getReplaceUpdate(fields, search, replace, wildcardPre, wildcardPost).
					where(where));
		}
		if (((Boolean)replaceRequest.get("snr-group20")).booleanValue()) {
			Clause where = new RawClause("fk_person IN (SELECT pk FROM veraweb.tperson" +
					" WHERE fk_orgunit = " + ((PersonalConfigAA)cntx.personalConfig()).getOrgUnitId() +
					" AND deleted = '" + PersonConstants.DELETED_FALSE + "')");
			where = Where.and(Expr.in("fk_person", selection), Where.and(where, Where.or(
							getReplaceWhere("textfield", search, wildcardPre, wildcardPost),
							getReplaceWhere("textfield_p", search, wildcardPre, wildcardPost))));
			
			database.execute(SQL.Update().
					table("veraweb.tperson_doctype").
					update("textfield", getReplaceClause("textfield", search, replace, wildcardPre, wildcardPost)).
					update("textfield_p", getReplaceClause("textfield_p", search, replace, wildcardPre, wildcardPost)).
					where(where));
		}
	}


	/**
	 * Gibt eine Where-Bedingung f�r die �bergebenen Spalten zur�ck.
	 * 
	 * @param fields
	 * @param search
	 * @return Where
	 */
	protected Clause getReplaceWhere(List fields, String search, boolean wildcardPre, boolean wildcardPost) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		for (int i = 0; i < fields.size(); i++) {
			if (i != 0)
			buffer.append(" OR ");
			buffer.append(fields.get(i));
			if (wildcardPre && !wildcardPost) {
				buffer.append(" LIKE '%");
				Escaper.escape(buffer, search);
				buffer.append("'");
			} else if (wildcardPre) {
				buffer.append(" LIKE '%");
				Escaper.escape(buffer, search);
				buffer.append("%'");
			} else if (wildcardPost) {
				buffer.append(" LIKE '");
				Escaper.escape(buffer, search);
				buffer.append("%'");
			} else {
				buffer.append(" = '");
				Escaper.escape(buffer, search);
				buffer.append("'");
			}
		}
		buffer.append(")");
		return new RawClause(buffer);
	}

	protected Clause getReplaceWhere(String field, String search, boolean wildcardPre, boolean wildcardPost) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(field);
		if (wildcardPre && !wildcardPost) {
			buffer.append(" LIKE '%");
			Escaper.escape(buffer, search);
			buffer.append("'");
		} else if (wildcardPre) {
			buffer.append(" LIKE '%");
			Escaper.escape(buffer, search);
			buffer.append("%'");
		} else if (wildcardPost) {
			buffer.append(" LIKE '");
			Escaper.escape(buffer, search);
			buffer.append("%'");
		} else {
			buffer.append(" = '");
			Escaper.escape(buffer, search);
			buffer.append("'");
		}
		return new RawClause(buffer);
	}

	/**
	 * Gibt ein Update mit den entsprechenden Spalten zur�ck.
	 * 
	 * @param fields
	 * @param search
	 * @param replace
	 * @return Update
	 */
	protected Update getReplaceUpdate(List fields, String search, String replace, boolean wildcardPre, boolean wildcardPost) {
		Update update = SQL.Update().table("veraweb.tperson");
		for (int i = 0; i < fields.size(); i++) {
			String field = (String)fields.get(i);
			update.update(field, getReplaceClause(field, search, replace, wildcardPre, wildcardPost));
		}
		return update;
	}

	protected Clause getReplaceClause(String field, String search, String replace, boolean wildcardPre, boolean wildcardPost) {
		if (wildcardPre || wildcardPost) {
			return new RawClause("replace(" + field +
					", '" + Escaper.escape(search) + "'" +
					", '" + Escaper.escape(replace) + "')");
		} else {
			return new RawClause("CASE WHEN " +
					field + "='" + Escaper.escape(search) + "' THEN '" +
					Escaper.escape(replace) + "'" + "ELSE " +
					field + " END");
		}
	}
}
