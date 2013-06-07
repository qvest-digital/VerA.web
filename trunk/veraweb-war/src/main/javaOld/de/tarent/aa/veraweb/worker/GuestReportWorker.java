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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.GuestSearch;
import de.tarent.aa.veraweb.utils.DatabaseHelper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Worker stellt entsprechende Funktionen zur Erstellung von
 * Berichten zu G�stelisten bereit.
 * 
 * @author Christoph Jerolimov
 */
public class GuestReportWorker {
    //
    // Octopus-Aktionen
    //
    /** Octopus-Eingabeparameter f�r {@link #createReport(OctopusContext)} */
	public static final String INPUT_createReport[] = {};
	/**
     * Diese Octopus-Aktion erzeugt Daten f�r einen Bericht. Hierbei wird auf
     * die Werte unter den Schl�sseln "type" (Kategorisierung des Berichts) und
     * "sort1", "sort2" und "sort3" (Sortierkriterien f�r den Bericht) aus
     * dem Octopus-Request, "event" (die Veranstaltung) und "search" (Kriterien
     * f�r die G�stesuche) aus dem Octopus-Content, "selectionGuest" (Liste
     * ausgew�hlter G�ste-Eintr�ge) aus der Session und "freitextfeld2" (ID des
     * Dokumenttyps f�r das Bericht-Freitextfeld) zur�ckgegriffen.<br>
     * F�r den Bericht stehen zur Kategorisierung folgende Werte zur Verf�gung:
     * <ul>
     * <li>"Kat01": Kategorien mit Partner
     * <li>"Kat02": Kategorien mit Telefon und Mobiltelefon
     * <li>"Kat03": Kategorien mit Partner und Fax
     * <li>"Alpha01": Alphabetisch mit Partner, Telefon und Mobiltelefon
     * </ul>
     * Weiterhin stehen folgende Sortierkriterien zur Verf�gung:
     * <ul>
     * <li>"category": Kategorie-Rang, Kategorie-Name
     * <li>"country": Land (der gesch�ftliche Adresse in Latin)
     * <li>"name": Nachname, Vorname (der Hauptperson in Latin)
     * <li>"orderno": laufende Nummer
     * <li>"rank": Gast-Rang
     * <li>"table": Tischnummer
     * <li>"zipcode": Postleitzahl (der gesch�ftliche Adresse in Latin)
     * </ul>
     * Im Octopus-Content werden unter den Schl�sseln "datum" (das zu verwendende
     * aktuelle Datum), "titel" (Titel mit lesbarem Berichtstyp), "type" (wie oben
     * Kategorisierung des Berichts), "data" (Map mit G�stesummen unter "platz",
     * "reserve",  "all", "offen",  "zusagen" und "absagen"), "kategorie" (Flag f�r
     * "Kat*"-Typen), "alphabetisch" (Flag f�r "Alpha*"-Typen) und "guestlist"
     * (Liste der konkreten Gastdaten) Daten f�r den Bericht bereitgestellt.
     * 
     * @param cntx
     * @throws BeanException
     * @throws IOException
	 */
	public void createReport(OctopusContext cntx) throws BeanException {
		Database database = new DatabaseVeraWeb(cntx);
		Event event = (Event)cntx.contentAsObject("event");
		GuestSearch search = (GuestSearch)cntx.contentAsObject("search");
		List selection = (List)cntx.sessionAsObject("selectionGuest");
		Integer freitextfeld = ConfigWorker.getInteger(cntx, "freitextfeld2");
		
		if (event == null)
			return;
		
		// type:
		//   kat01 = Kategorien mit Partner
		//   kat02 = Kategorien mit Telefon und Mobiltelefon
		//   kat03 = Kategorien mit Partner und Fax
		//   alpha01 = Alphabetisch mit Partner, Telefon und Mobiltelefon
		String type = cntx.requestAsString("type");
		if (!("Kat02".equals(type) || "Kat03".equals(type) || "Alpha01".equals(type)))
			type = "Kat01";
		
		boolean kategorie = type.startsWith("Kat");
		boolean alphabetisch = type.startsWith("Alpha");
		
		String titel;
		if (search.invitationstatus != null && search.invitationstatus.intValue() == 1) {
			titel = "Offenliste";
		} else if (search.invitationstatus != null && search.invitationstatus.intValue() == 2) {
			titel = "Zusagenliste";
		} else if (search.invitationstatus != null && search.invitationstatus.intValue() == 3) {
			titel = "Absagenliste";
		} else {
			titel = "G&auml;steliste";
		}
		if (alphabetisch) {
			titel += " (alphabetisch)";
		}
		
		Select select = SQL.Select( database ).
				from("veraweb.tguest").
				selectAs("tguest.pk", "id").
				selectAs("CASE WHEN orderno IS NOT NULL THEN orderno ELSE orderno_p END", "someorderno").
				select("reserve").
				select("lastname_a_e1").
				select("title_a_e1").
				select("firstname_a_e1").
				select("lastname_b_e1").
				select("title_b_e1").
				select("firstname_b_e1").
				select("invitationtype").
				select("invitationstatus").
				select("invitationstatus_p").
				select("orderno").
				select("orderno_p").
				select("notehost").
				select("notehost_p").
				select("ishost").
				select("fon_a_e1").
				select("fon_b_e1").
				select("fon_c_e1").
				select("fax_a_e1").
				select("fax_b_e1").
				select("fax_c_e1").
				select("mobil_a_e1").
				select("mobil_b_e1").
				select("mobil_c_e1").
				select("function_a_e1").
				select("company_a_e1").
				selectAs("tcategorie.catname", "category").
				joinLeftOuter("veraweb.tperson", "fk_person", "tperson.pk").
				joinLeftOuter("veraweb.tcategorie", "fk_category", "tcategorie.pk");

		/*
		 * modified to support ordering by workarea as per change request for version 1.2.0
		 * 
		 * cklein
		 * 2008-02-20
		 */
		Boolean orderByWorkArea = cntx.requestAsBoolean( "orderByWorkArea" );
		if ( orderByWorkArea.booleanValue() )
		{
			select.joinLeftOuter( "veraweb.tworkarea", "tperson.fk_workarea", "tworkarea.pk" );
			select.selectAs( "CASE WHEN tworkarea.name <> 'Kein' THEN tworkarea.name ELSE 'Kein Arbeitsbereich' END", "workarea_name" );
		}
		cntx.setContent( "orderByWorkArea", orderByWorkArea );

		if (freitextfeld != null) {
			select.joinLeftOuter("veraweb.tguest_doctype", "tguest.pk", "tguest_doctype.fk_guest AND fk_doctype = " + freitextfeld);
			select.selectAs("tguest_doctype.pk IS NOT NULL", "showdoctype");
			select.selectAs("textfield", "textfield");
			select.selectAs("textfield_p", "textfield_p");
			select.selectAs("textjoin", "textjoin");
			select.selectAs("lastname", "lastname_a_gd");
			select.selectAs("titel", "title_a_gd");
			select.selectAs("firstname", "firstname_a_gd");
			select.selectAs("lastname_p", "lastname_b_gd");
			select.selectAs("titel_p", "title_b_gd");
			select.selectAs("firstname_p", "firstname_b_gd");
			select.selectAs("fon", "fon_a_gd");
			select.selectAs("fax", "fax_a_gd");
			select.selectAs("mobil", "mobil_a_gd");
			select.selectAs("function", "function_a_gd");
			select.selectAs("company", "company_a_gd");
		} else {
			select.selectAs("FALSE", "showdoctype");
		}
		
		/** Nur die aktuelle Auswahl zur�ckgeben. */
		WhereList where = new WhereList();
		GuestListWorker.addGuestListFilter(search, where);
		if (selection != null && selection.size() > 0) {
			where.addAnd(Expr.in("tguest.pk", selection));
		}
		select.where(where);
		
		// Sortierung erstellen
		List order = new ArrayList();
		order.add("tguest.ishost");
		order.add("DESC");
		if (kategorie) {
			order.add("tcategorie.rank");
			order.add("tcategorie.catname");
		}
		/*
		 * modified to support workareas as per change request for version 1.2.0
		 * cklein
		 * 2008-02-21
		 */
		if ( orderByWorkArea.booleanValue() )
		{
			setSortOrder( order, "workareaname" );
		}
		setSortOrder(order, cntx.requestAsString("sort1"));
		setSortOrder(order, cntx.requestAsString("sort2"));
		setSortOrder(order, cntx.requestAsString("sort3"));
		select.orderBy(DatabaseHelper.getOrder(order));
		
		Map data = new HashMap();
		WorkerFactory.getGuestListWorker(cntx).getSums(database, data, search, selection);
		
		cntx.setContent("datum", new Date());
		cntx.setContent("titel", titel);
		cntx.setContent("type", type);
		cntx.setContent("data", data);
		cntx.setContent("kategorie", Boolean.valueOf(kategorie));
		cntx.setContent("alphabetisch", Boolean.valueOf(alphabetisch));
		cntx.setContent("guestlist", database.getList(select, database));
	}

    //
    // Hilfsmethoden
    //
    /**
     * Diese Methode f�gt gem�� einem �bergebenen Sortierkriterium ("orderno",
     * "name", "country", "zipcode", "category", "rank" oder "table") passende
     * ORDER-BY-Spaltennamen in die �bergebene Liste ein.
     * 
     *  @param order Liste von ORDER-BY-Spaltennamen, die hier erg�nzt werden soll.
     *  @param sort abstraktes Sortierkriterium
     */
	static void setSortOrder(List order, String sort) {
		if (sort == null) {
			// nothing
		} else if (sort.equals("orderno")) {
			order.add("someorderno");
		} else if (sort.equals("name")) {
			order.add("tperson.lastname_a_e1");
			order.add("tperson.firstname_a_e1");
		} else if (sort.equals("country")) {
			order.add("country_a_e1");
		} else if (sort.equals("zipcode")) {
			order.add("zipcode_a_e1");
		} else if (sort.equals("category")) {
			order.add("tcategorie.rank");
			order.add("tcategorie.catname");
		} else if (sort.equals("rank")) {
			order.add("tguest.rank");
		} else if (sort.equals("table")) {
			order.add("tguest.tableno");
		} else if (sort.equals("workareaname")) {
			/*
			 * modified to support workareas as per change request for version 1.2.0
			 * cklein
			 * 2008-02-21
			 */
			order.add("tworkarea.name");
		}
	}
}
