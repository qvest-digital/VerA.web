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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.*;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.TransformerFactoryConfigurationError;

import de.tarent.aa.veraweb.beans.*;
import de.tarent.aa.veraweb.utils.EventURLHandler;

import org.apache.log4j.Logger;

import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.GuestSearch;
import de.tarent.aa.veraweb.beans.Location;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.utils.DatabaseHelper;
import de.tarent.aa.veraweb.utils.EventURLHandler;
import de.tarent.aa.veraweb.utils.ExportHelper;
import de.tarent.aa.veraweb.utils.OctopusHelper;
import de.tarent.aa.veraweb.utils.PersonURLHandler;
import de.tarent.aa.veraweb.utils.PropertiesReader;
import de.tarent.aa.veraweb.utils.URLGenerator;
import de.tarent.commons.spreadsheet.export.SpreadSheet;
import de.tarent.commons.spreadsheet.export.SpreadSheetFactory;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.helper.ResultMap;
import de.tarent.dblayer.sql.Join;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.response.TcBinaryResponseEngine;
import de.tarent.octopus.server.OctopusContext;
import org.apache.log4j.Logger;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Diese Octopus-Worker-Klasse exportiert Dokumenttypen einer Gästeliste
 * in ein OpenDocument-SpreadSheet.
 *
 * @author Christoph
 */
public class GuestExportWorker {
    //
    // Octopus-Aktionen
    //
    /** Octopus-Eingabeparameter für {@link #calc(OctopusContext, Integer)} */
    public static final String INPUT_calc[] = { "doctype" };
    /** Octopus-Eingabeparameter für {@link #calc(OctopusContext, Integer)} */
    public static final boolean MANDATORY_calc[] = { false };
    /** Octopus-Ausgabeparameter für {@link #calc(OctopusContext, Integer)} */
    public static final String OUTPUT_calc = "exportCalc";
    /** Octopus-Eingabeparameter für {@link #export(OctopusContext, Integer)} */
    public static final String INPUT_export[] = { "doctype" };
    /** Octopus-Ausgabeparameter für {@link #export(OctopusContext, Integer)} */
    public static final String OUTPUT_export = "stream";

	public static final String EMPTY_FIELD_VALUE = "-";

    /** Logger dieser Klasse */
	private final Logger logger = Logger.getLogger(getClass());

	private boolean isOnlineRegistrationActive = true;

	private PropertiesReader propertiesReader = new PropertiesReader();

	/**
	 * <p>
	 * Berechnet wieviele Dokumenttypen einer Gästeliste exportiert
	 * werden sollen und können.
	 * </p>
	 *
	 * @param cntx OctopusContext
	 * @param doctypeid Dokumenttyp der exportiert werden soll.
	 */
	public Map calc(OctopusContext cntx, Integer doctypeid) throws BeanException, IOException {
		final Database database = new DatabaseVeraWeb(cntx);
        final Event event = (Event)cntx.contentAsObject("event");
        final GuestSearch search = (GuestSearch)cntx.contentAsObject("search");
        final List selection = (List)cntx.sessionAsObject("selectionGuest");

		if (doctypeid == null) {
            doctypeid = setDoctypeId(cntx, doctypeid);
		}
		if (doctypeid == null)
			return null;
        final Doctype doctype = (Doctype)
				database.getBean("Doctype",
				database.getSelect("Doctype").
				where(Expr.equal("pk", doctypeid)));

		Integer total = null;
		Integer available = null;

		if (selection != null && selection.size() > 0) {
			total = new Integer(selection.size());

			available =
					database.getCount(
					database.getCount("GuestDoctype").
					where(Where.and(
							Expr.equal("fk_doctype", doctypeid),
							Expr.in("fk_guest", selection))));

		} else if (event != null && event.id != null && event.id.intValue() != 0) {
            final WhereList where = new WhereList();
			search.addGuestListFilter(where);

			total = database.getCount(
					database.getCount("Guest").
					where(where));

			available =
					database.getCount(
					database.getCount("GuestDoctype").
					join("veraweb.tguest", "fk_guest", "tguest.pk").
					where(Where.and(where, Expr.equal("fk_doctype", doctypeid))));
		}

		cntx.setContent("doctype", doctype);
		cntx.setContent("extension", ExportHelper.getExtension(SpreadSheetFactory.getSpreadSheet(doctype.format).getFileExtension()));
        final Map result = new HashMap();
		result.put("doctype", doctypeid);
		result.put("total", total);
		result.put("available", available);
		result.put("sessionId", cntx.requestAsString(TcRequest.PARAM_SESSION_ID));
		return result;
	}

	/**
	 * <p>
	 * Exportiert die Dokumenttypen einer Gästeliste.
	 * </p>
	 * <p>
	 * Wenn Daten Gäste selektiert wurden werden diese exportiert,
	 * falls dies nicht der Fall ist alle Gäste der übergebenen Veranstaltung.
	 * </p>
	 *
	 * @param cntx OctopusContext
	 * @param doctypeid Dokumenttyp-ID der exportiert werden soll.
	 * @return Map mit Stream-Informationen
	 * @throws BeanException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws FactoryConfigurationError
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public Map export(OctopusContext cntx, Integer doctypeid) throws BeanException, IOException, FactoryConfigurationError, TransformerFactoryConfigurationError {
        final Database database = new DatabaseVeraWeb(cntx);
        final Event event = (Event)cntx.contentAsObject("event");
        final GuestSearch search = (GuestSearch)cntx.contentAsObject("search");
        final List selection = (List)cntx.sessionAsObject("selectionGuest");
        final Doctype doctype = (Doctype)database.getBean("Doctype", doctypeid);

		// Spreadsheet öffnen
		final SpreadSheet spreadSheet = SpreadSheetFactory.getSpreadSheet(doctype.format);
		spreadSheet.init();
        final String fileExtension = spreadSheet.getFileExtension();
        final String filename = OctopusHelper.getFilename(cntx, fileExtension, "export." + fileExtension);

		if (logger.isInfoEnabled()) {
            logger.info("Exportiere G\u00e4steliste. (Dateiname: '" + filename + "'; Dokumenttyp: #" + doctype.id
                    + "; Format: '" + spreadSheet.getClass().getName() + "')");
        }

		// Tabelle öffnen und erste Zeile schreiben
		spreadSheet.openTable("Gäste", 65);
		spreadSheet.openRow();
		exportHeader(spreadSheet, cntx, event);
		spreadSheet.closeRow();

		// Zusatzinformationen
        final Map data = new HashMap();
		data.put("doctype", doctype.name);
        final String withHost = (doctype.host != null && doctype.host.booleanValue()) ? "" :
				"(tguest.ishost IS NULL OR tguest.ishost = 0) AND ";

		// Export-Select zusammenbauen
        final Select select = database.getSelect("GuestDoctype");
		select.select("tguest.*");
		select.selectAs("CASE WHEN orderno IS NOT NULL THEN orderno ELSE orderno_p END", "someorderno");

		if (selection != null && selection.size() > 0) {
			// Joint tguest und schränkt das Ergebnis auf den entsprechenden
			// Dokumenten-Typ und bestimmte Gäste ein.
			if (logger.isInfoEnabled())
				logger.info("Exportiere G\u00e4steliste anhand der Sleektion.");

			select.join(new Join(Join.INNER, "veraweb.tguest", new RawClause(withHost +
					"tguest_doctype.fk_guest = tguest.pk AND tguest_doctype.fk_doctype = " + doctype.id)));

			select.where(Expr.in("tguest_doctype.fk_guest", selection));
		} else if (event != null && event.id != null && event.id.intValue() != 0) {
			// Joint tguest und schränkt das Ergebnis auf den entsprechenden
			// Dokumenten-Typ und eine Veranstaltung ein.
			if (logger.isInfoEnabled())
				logger.info("Exportiere G\u00e4steliste der Veranstaltung " + event.id + ".");

			select.join(new Join(Join.INNER, "veraweb.tguest", new RawClause(withHost +
					"tguest_doctype.fk_guest = tguest.pk AND tguest_doctype.fk_doctype = " + doctype.id)));

            final WhereList list = new WhereList();
			search.addGuestListFilter(list);
			select.where(list);
		} else {
			logger.error("Konnte G\u00e4steliste nicht exportieren.");
			throw new BeanException("Konnte G\u00e4steliste nicht exportieren.");
		}

		WorkerFactory.getGuestListWorker(cntx).getSums(database, data, search, selection);

		select.joinLeftOuter("veraweb.tperson", "tperson.pk", "tguest.fk_person");
		select.select("tperson.lastname_a_e1");
		select.select("tperson.firstname_a_e1");
		select.select("tperson.country_a_e1");
		select.select("tperson.zipcode_a_e1");
		select.select("tperson.birthplace_a_e1");
		select.select("tperson.company_a_e1");
		select.joinLeftOuter("veraweb.tcategorie", "tguest.fk_category", "tcategorie.pk");

		/*
		 * modified to support workareas as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		select.selectAs("tworkarea.name", "workareaname");
		select.joinLeftOuter("veraweb.tworkarea", "tworkarea.pk", "tperson.fk_workarea");

		select.selectAs("tcategorie.catname", "catname");
		select.selectAs("tcategorie.rank", "catrang");
		select.joinLeftOuter("veraweb.tperson_categorie", "tperson_categorie.fk_person", "tguest.fk_person AND tperson_categorie.fk_categorie = tguest.fk_category");
		select.selectAs("tperson_categorie.rank", "guestrang");
		select.joinLeftOuter("veraweb.tcolor c1", "tguest.fk_color", "c1.pk");
		select.joinLeftOuter("veraweb.tcolor c2", "tguest.fk_color_p", "c2.pk");
		select.selectAs("c1.rgb", "color1");
		select.selectAs("c2.rgb", "color2");

		// Sortierung erstellen
        final List order = new ArrayList();
		order.add("tguest.ishost");
		order.add("DESC");
		GuestReportWorker.setSortOrder(order, cntx.requestAsString("sort1"));
		GuestReportWorker.setSortOrder(order, cntx.requestAsString("sort2"));
		GuestReportWorker.setSortOrder(order, cntx.requestAsString("sort3"));
		select.orderBy(DatabaseHelper.getOrder(order));


		Location location = null;
		if (event.location != null) {
			location = (Location)database.getBean("Location", event.location);
		}

		// Export-Select ausführen
		exportSelect(spreadSheet, database, event, location, doctype, search, select, data, cntx);

		// Tabelle schließen
		spreadSheet.closeTable();

		// SpreadSheet speichern
		if (logger.isInfoEnabled())
			logger.info("Gebe G\u00e4steliste als Download zur\u00fcck.");
        final PipedInputStream pis = new PipedInputStream();
        final PipedOutputStream pos = new PipedOutputStream(pis);
        new Thread(new Runnable() {
        	public void run() {
        		if (logger.isDebugEnabled())
        			logger.debug("G\u00e4stelisten-Export: Starte das Speichern eines Spreadsheets.");
        		try {
					spreadSheet.save(pos);
				} catch (Throwable t) {
					logger.error("Fehler beim Erstellen des Exports aufgetreten.", t);
        			// This will force a log output.
        			t.printStackTrace(System.out);
        			t.printStackTrace(System.err);
				} finally {
					try {
						pos.close();
					} catch (IOException e) {
                        // TODO
					}
				}
        		if (logger.isDebugEnabled())
        			logger.debug("G\u00e4stelisten-Export: Beende das Speichern eines Spreadsheets.");
        	}
        }).start();

		// Stream-Informationen zurück geben
        final Map stream = new HashMap();
		stream.put(TcBinaryResponseEngine.PARAM_TYPE, TcBinaryResponseEngine.BINARY_RESPONSE_TYPE_STREAM);
		stream.put(TcBinaryResponseEngine.PARAM_FILENAME, ExportHelper.getFilename(filename));
		stream.put(TcBinaryResponseEngine.PARAM_MIMETYPE, ExportHelper.getContentType(spreadSheet.getContentType()));
		stream.put(TcBinaryResponseEngine.PARAM_STREAM, pis);
		stream.put(TcBinaryResponseEngine.PARAM_IS_ATTACHMENT, Boolean.TRUE);

		return stream;
	}

    //
    // Hilfsmethoden
    //
    /**
     * Diese Methode exportiert alle Gäste, die über das übergebene Select-Statement
     * erfasst werden, in das übergebene Spreadsheet.<br>
     * TODO: Parameter, von denen nur eine oder zwei Eigenschaften benutzt werden, durch Parameter ersetzen, die direkt diese Eigenschaften darstellen
     *
     * @param spreadSheet Exportziel
     * @param database Exportquelle, auf die das Statement select angewendet werden soll
     * @param event Veranstaltung, zu der die Gäste gehören
     * @param doctype Dokumenttyp, der über sein partner-Flag bestimmt, ob Hauptperson
     *  und Partner in der gleichen oder in separaten Zeilen exportiert werden.
     * @param search Gästesuche-Kriterien, die über ihre Eigenschaften invitationstatus
     *  und reserve bestimmen, ob Hauptperson, Partner oder beide exportiert werden sollen.
     * @param select Select-Statement, das auf der übergebenen database ausgeführt wird, um
     *  die zu exportierenden Datensätze zu liefern.
     * @param data Map mit Zusatzinformationen unter den Schlüsseln "doctype", "zusagen",
     *  "absagen", "offenen", "platz" und "reserve".
     * @throws IOException
     */
	protected void exportSelect(SpreadSheet spreadSheet, Database database, Event event, Location location, Doctype doctype, GuestSearch search, Select select, Map data, OctopusContext cntx) throws BeanException, IOException {
		for (final Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {
            final Map guest = (Map)it.next();

            Boolean isPressStaff = isPressGuest(cntx, event.mediarepresentatives, (Integer)guest.get("id"));

			Integer invitationtype = (Integer)guest.get("invitationtype");
			if (invitationtype == null || invitationtype.intValue() == 0)
				invitationtype = event.invitationtype;

			// Test ob die Hauptperson / der Partner zum aktuellen Filter passen.
            final Integer reserve = (Integer)guest.get("reserve");
            final Integer invitationstatusA = (Integer)guest.get("invitationstatus");
            final Integer invitationstatusB = (Integer)guest.get("invitationstatus_p");

			/*
			 * modified to support the additional fourth invitation_status of "teilgenommen"
			 * cklein
			 * 2008-02-26
			 */
			boolean showA = search.invitationstatus == null || (
					(search.invitationstatus.intValue() == 1 && (invitationstatusA == null || invitationstatusA.intValue() == 0)) ||
					(search.invitationstatus.intValue() == 2 && (invitationstatusA != null && invitationstatusA.intValue() == 1)) ||
					(search.invitationstatus.intValue() == 3 && (invitationstatusA != null && invitationstatusA.intValue() == 2)) ||
					(search.invitationstatus.intValue() == 4 && (invitationstatusA != null && invitationstatusA.intValue() == 3)));
			boolean showB = search.invitationstatus == null || (
					(search.invitationstatus.intValue() == 1 && (invitationstatusB == null || invitationstatusB.intValue() == 0)) ||
					(search.invitationstatus.intValue() == 2 && (invitationstatusB != null && invitationstatusB.intValue() == 1)) ||
					(search.invitationstatus.intValue() == 3 && (invitationstatusB != null && invitationstatusB.intValue() == 2)) ||
					(search.invitationstatus.intValue() == 4 && (invitationstatusB != null && invitationstatusB.intValue() == 3)));
			showA = showA && (search.reserve == null || (
					(search.reserve.intValue() == 1 && (reserve == null || reserve.intValue() == 0)) ||
					(search.reserve.intValue() == 2 && (reserve != null && reserve.intValue() == 1))));
			showB = showB && (search.reserve == null || (
					(search.reserve.intValue() == 1 && (reserve == null || reserve.intValue() == 0)) ||
					(search.reserve.intValue() == 2 && (reserve != null && reserve.intValue() == 1))));

			if (doctype.partner != null && doctype.partner.booleanValue()) {
				// Eigenes Dokument
				if (invitationtype == null
						|| invitationtype.intValue() == EventConstants.TYPE_MITPARTNER) {
					// Mit Partner
					if (showA) {
						spreadSheet.openRow();
						exportOnlyPerson(spreadSheet, event, location, guest, data, isPressStaff, cntx);
						spreadSheet.closeRow();
					}
					if (showB) {
						spreadSheet.openRow();
						exportOnlyPartner(spreadSheet, event, location, showA,
								showB, guest, data, cntx);
						spreadSheet.closeRow();
					}
				} else if (invitationtype.intValue() == EventConstants.TYPE_OHNEPARTNER) {
					// Ohne Partner
					if (showA) {
						spreadSheet.openRow();
						exportOnlyPerson(spreadSheet, event, location, guest, data, isPressStaff, cntx);
						spreadSheet.closeRow();
					}
				} else if (invitationtype.intValue() == EventConstants.TYPE_NURPARTNER) {
					// Nur Partner
					if (showB) {
						spreadSheet.openRow();
						exportOnlyPartner(spreadSheet, event, location, showA,
								showB, guest, data, cntx);
						spreadSheet.closeRow();
					}
				}
			} else {
				// Gleiches Dokument
				if (invitationtype == null
						|| invitationtype.intValue() == EventConstants.TYPE_MITPARTNER) {
					// Mit Partner
					if (showA || showB) {
						spreadSheet.openRow();
						exportBothInOneLine(spreadSheet, event, location,
								showA, showB, guest, data, cntx);
						spreadSheet.closeRow();
					}
				} else if (invitationtype.intValue() == EventConstants.TYPE_OHNEPARTNER) {
					// Ohne Partner
					if (showA) {
						spreadSheet.openRow();
						exportOnlyPerson(spreadSheet, event, location,  guest, data, isPressStaff, cntx);
						spreadSheet.closeRow();
					}
				} else if (invitationtype.intValue() == EventConstants.TYPE_NURPARTNER) {
					// Nur Partner
					if (showB) {
						spreadSheet.openRow();
						exportOnlyPartner(spreadSheet, event, location, showA,
								showB, guest, data, cntx);
						spreadSheet.closeRow();
					}
				}
			}
		}
	}

	/**
	 * Schreibt die überschriften des Export-Dokumentes.
	 *
	 * @param spreadSheet In das geschrieben werden soll.
	 */
	protected void exportHeader(SpreadSheet spreadSheet, OctopusContext cntx, Event event) {

		checkIfOnlineRegistrationIsAvailable(cntx);
		//
		// Gast spezifische Daten
		//
		spreadSheet.addCell("Dokument_Typ"); // Name des Dokument-Typs
		spreadSheet.addCell("Freitextfeld");
		spreadSheet.addCell("Partner_Freitextfeld");
		spreadSheet.addCell("Verbinder");

		spreadSheet.addCell("Anschrift"); // P, G oder S - Vorgabe aus Person, überschreibbar
		spreadSheet.addCell("Zeichensatz"); // L, F1 oder F2 - Vorgabe aus Person, überschreibbar

		spreadSheet.addCell("Funktion");
		spreadSheet.addCell("Anrede");
		spreadSheet.addCell("Akad_Titel");
		spreadSheet.addCell("Vorname");
		spreadSheet.addCell("Nachname");

		spreadSheet.addCell("Partner_Anrede");
		spreadSheet.addCell("Partner_Akad_Titel");
		spreadSheet.addCell("Partner_Vorname");
		spreadSheet.addCell("Partner_Nachname");

		spreadSheet.addCell("PLZ");
		spreadSheet.addCell("Ort");
		spreadSheet.addCell("Strasse");
		spreadSheet.addCell("Land");
		spreadSheet.addCell("Adresszusatz_1");
		spreadSheet.addCell("Adresszusatz_2");

		/*
		 * modified to support birthplace as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell("Geburtsort");
		spreadSheet.addCell("Telefon");
		spreadSheet.addCell("Fax");
		spreadSheet.addCell("Email");
		spreadSheet.addCell("WWW");
		spreadSheet.addCell("Mobil");
		spreadSheet.addCell("Firma");
		spreadSheet.addCell("Postfach_Nr");
		spreadSheet.addCell("Postfach_PLZ");

		//
		// Kategorie spezifische Daten, wenn nach Kategorie gefilter wurde.
		//
		spreadSheet.addCell("Kategorie"); // Verweis auf Kategorie, die zur Auswahl führte
		spreadSheet.addCell("Kategorie_Rang"); // Der Rang der Kategorie innerhalb der Kategorien
		spreadSheet.addCell("Rang"); // Der Rang der Person innerhalb der Kategorie

		/*
		 * modified to support workareas as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell("Arbeitsbereich");
		spreadSheet.addCell("Reserve"); // 0 = Tisch, 1 = Reservce

		//
		// Veranstaltungsspezifische Attribute für Person
		//
		spreadSheet.addCell("Status"); // 0 = Offen, 1 = Zusage, 2 = Absage
		spreadSheet.addCell("Tisch");
		spreadSheet.addCell("Platz");
		spreadSheet.addCell("Lfd_Nr");
		spreadSheet.addCell("Farbe"); // Verweiss auf Farbe die verwendet werden soll.
		spreadSheet.addCell("Inland"); // Ja / Nein
		spreadSheet.addCell("Sprachen");
		spreadSheet.addCell("Geschlecht"); // M oder F
		spreadSheet.addCell("Nationalität");
		spreadSheet.addCell("Hinweis_Gastgeber");
		spreadSheet.addCell("Hinweis_Orgateam");

		//
		// Veranstaltungsspezifische Attribute für Partner der Person
		//
		spreadSheet.addCell("Partner_Status"); // 0 = Offen, 1 = Zusage, 2 = Absage
		spreadSheet.addCell("Partner_Tisch");
		spreadSheet.addCell("Partner_Platz");
		spreadSheet.addCell("Partner_Lfd_Nr");
		spreadSheet.addCell("Partner_Farbe"); // Verweiss auf Farbe die verwendet werden soll.
		spreadSheet.addCell("Partner_Inland"); // Ja / Nein
		spreadSheet.addCell("Partner_Sprachen");
		spreadSheet.addCell("Partner_Geschlecht"); // M oder F
		spreadSheet.addCell("Partner_Nationalität");
		spreadSheet.addCell("Partner_Hinweis_Gastgeber");
		spreadSheet.addCell("Partner_Hinweis_Orgateam");

		//
		// Sonstiges
		//
		spreadSheet.addCell("Anzahl_Zusagen");
		spreadSheet.addCell("Anzahl_Absagen");
		spreadSheet.addCell("Anzahl_Offene");

		/*
		 * modified to support fourth invitation state as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell("Anzahl_Teilnahmen");
		spreadSheet.addCell("Anzahl_Gaeste_auf_Platz");
		spreadSheet.addCell("Anzahl_Gaeste_auf_Reserve");
		spreadSheet.addCell("Veranstaltungsname");
		spreadSheet.addCell("Veranstaltung_Beginn");
		spreadSheet.addCell("Veranstaltung_Ende");
		if (isOnlineRegistrationActive) {
			spreadSheet.addCell("Veranstaltung URL");
		}
		spreadSheet.addCell("Veranstaltungsort_Beschreibung");
		spreadSheet.addCell("Veranstaltungsort_Ansprechpartner");
		spreadSheet.addCell("Veranstaltungsort_Straße");
		spreadSheet.addCell("Veranstaltungsort_PLZ");
		spreadSheet.addCell("Veranstaltungsort_Ort");
		spreadSheet.addCell("Veranstaltungsort_Telefonnummer");
		spreadSheet.addCell("Veranstaltungsort_Faxnummer");
		spreadSheet.addCell("Veranstaltungsort_E-Mailadresse ");
		spreadSheet.addCell("Veranstaltungsort_Bemerkungsfeld");
		spreadSheet.addCell("Veranstaltungsort_URL");
		spreadSheet.addCell("Veranstaltungsort_GPS-Daten");
		spreadSheet.addCell("Veranstaltungsort_Raumnummer");

		if(isOnlineRegistrationActive) {
			//OSIAM Login
			spreadSheet.addCell("Anmeldename");
			spreadSheet.addCell("Passwort");
			spreadSheet.addCell("URL");
			if (!event.login_required) {
				spreadSheet.addCell("Anmeldung URL");
			}
		}
		spreadSheet.addCell("Bemerkung");
	}

	/**
	 * Export die Gast- und Partner Daten in eine Zeile.
	 *
	 * @param spreadSheet In das geschrieben werden soll.
	 * @param event Event das exportiert wird.
	 * @param guest Map mit den Gastdaten.
	 * @param data Zusatzinformationen.
	 * @throws IOException Wenn die Generierung von URL fur Delegation fehlschlug.
	 * @throws BeanException
	 */
	protected void exportBothInOneLine(SpreadSheet spreadSheet, Event event, Location location, boolean showA, boolean showB, Map guest, Map data, OctopusContext cntx) throws IOException, BeanException {
		//
		// Gast spezifische Daten
		//
		spreadSheet.addCell(data.get("doctype")); // Name des Dokument-Typs

        final String textA = (String)guest.get("textfield");
        final String textB = (String)guest.get("textfield_p");
		if (showA) {
			spreadSheet.addCell(textA);
		} else {
			spreadSheet.addCell(null);
		}
		if (showB) {
			spreadSheet.addCell(textB);
		} else {
			spreadSheet.addCell(null);
		}
		if (showA && showB && textA != null && textA.length() != 0 && textB != null && textB.length() != 0) {
			spreadSheet.addCell(guest.get("textjoin"));
		} else {
			spreadSheet.addCell(null);
		}

		spreadSheet.addCell(getAddresstype((Integer)guest.get("addresstype")));
		spreadSheet.addCell(getLocale((Integer)guest.get("locale")));

		if (showA) {
			spreadSheet.addCell(guest.get("function"));
			spreadSheet.addCell(guest.get("salutation"));
			spreadSheet.addCell(guest.get("titel"));
			spreadSheet.addCell(guest.get("firstname"));
			spreadSheet.addCell(guest.get("lastname"));
		} else {
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
		}

		if (showB) {
			spreadSheet.addCell(guest.get("salutation_p"));
			spreadSheet.addCell(guest.get("titel_p"));
			spreadSheet.addCell(guest.get("firstname_p"));
			spreadSheet.addCell(guest.get("lastname_p"));
		} else {
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
		}

		spreadSheet.addCell(guest.get("zipcode"));
		spreadSheet.addCell(guest.get("city"));
		spreadSheet.addCell(guest.get("street"));
		spreadSheet.addCell(guest.get("country"));
		spreadSheet.addCell(guest.get("suffix1"));
		spreadSheet.addCell(guest.get("suffix2"));

		/*
		 * modified to support birthplace as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(guest.get("birthplace_a_e1"));
		spreadSheet.addCell(guest.get("fon"));
		spreadSheet.addCell(guest.get("fax"));
		spreadSheet.addCell(guest.get("mail"));
		spreadSheet.addCell(guest.get("www"));
		spreadSheet.addCell(guest.get("mobil"));
		spreadSheet.addCell(guest.get("company"));
		spreadSheet.addCell(guest.get("pobox"));
		spreadSheet.addCell(guest.get("poboxzipcode"));

		//
		// Kategorie spezifische Daten, wenn nach Kategorie gefilter wurde.
		//
		spreadSheet.addCell(guest.get("catname")); // Verweis auf Kategorie, die zur Auswahl führte
		spreadSheet.addCell(guest.get("catrang")); // Der Rang der Kategorie innerhalb der Kategorien
		spreadSheet.addCell(guest.get("guestrang")); // Der Rang der Person innerhalb der Kategorie

		/*
		 * modified to support workareas as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(guest.get("workareaname"));
		spreadSheet.addCell(getReserve((Integer)guest.get("reserve"))); // 0 = Tisch, 1 = Reservce

		//
		// Veranstaltungsspezifische Attribute für Person
		//
		if (showA) {
			spreadSheet.addCell(getStatus((Integer)guest.get("invitationstatus"))); // 0 = Offen, 1 = Zusage, 2 = Absage
			spreadSheet.addCell(guest.get("tableno"));
			spreadSheet.addCell(guest.get("seatno"));
			spreadSheet.addCell(guest.get("orderno"));
			spreadSheet.addCell(getColor((Integer)guest.get("color1"))); // Verweiss auf Farbe die verwendet werden soll.
			spreadSheet.addCell(getDomestic((Integer)guest.get("domestic"))); // Inland Ja / Nein
			spreadSheet.addCell(guest.get("language"));
			spreadSheet.addCell(getGender((String)guest.get("gender"))); // M oder F
			spreadSheet.addCell(guest.get("nationality"));
			spreadSheet.addCell(guest.get("notehost"));
			spreadSheet.addCell(guest.get("noteorga"));
		} else {
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(getColor(null));
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
		}

		//
		// Veranstaltungsspezifische Attribute für Partner der Person
		//
		if (showB) {
			spreadSheet.addCell(getStatus((Integer)guest.get("invitationstatus_p"))); // 0 = Offen, 1 = Zusage, 2 = Absage
			spreadSheet.addCell(guest.get("tableno_p"));
			spreadSheet.addCell(guest.get("seatno_p"));
			spreadSheet.addCell(guest.get("orderno_p"));
			spreadSheet.addCell(getColor((Integer)guest.get("color2"))); // Verweiss auf Farbe die verwendet werden soll.
			spreadSheet.addCell(getDomestic((Integer)guest.get("domestic_p"))); // Inland Ja / Nein
			spreadSheet.addCell(guest.get("language_p"));
			spreadSheet.addCell(getGender((String)guest.get("gender_p"))); // M oder F
			spreadSheet.addCell(guest.get("nationality_p"));
			spreadSheet.addCell(guest.get("notehost_p"));
			spreadSheet.addCell(guest.get("noteorga_p"));
		} else {
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(getColor(null));
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
		}

		//
		// Sonstiges
		//
		spreadSheet.addCell(data.get("zusagen"));
		spreadSheet.addCell(data.get("absagen"));
		spreadSheet.addCell(data.get("offenen"));

		/*
		 * modified to support fourth invitation state as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(data.get("teilnahmen"));
		spreadSheet.addCell(data.get("platz"));
		spreadSheet.addCell(data.get("reserve"));
		spreadSheet.addCell(event.shortname);
		spreadSheet.addCell(event.begin);
		spreadSheet.addCell(event.end);

		addLocationCells(spreadSheet, location);
		if (isOnlineRegistrationActive) {
			addCredentialsDataColumns(spreadSheet, guest, event, cntx);
		}
		spreadSheet.addCell(event.note);
	}

	private void addLocationCells(SpreadSheet spreadSheet, Location location) {
		if (location != null) {
			spreadSheet.addCell(location.name);
			spreadSheet.addCell(location.contactperson);
			spreadSheet.addCell(location.address);
			spreadSheet.addCell(location.zip);
			spreadSheet.addCell(location.location);
			spreadSheet.addCell(location.callnumber);
			spreadSheet.addCell(location.faxnumber);
			spreadSheet.addCell(location.email);
			spreadSheet.addCell(location.comment);
			spreadSheet.addCell(location.url);
			spreadSheet.addCell(location.gpsdata);
			spreadSheet.addCell(location.roomnumber);
		} else {
			for(int i = 0; i != 12; i++) spreadSheet.addCell(null);
		}
	}

	private void checkIfOnlineRegistrationIsAvailable(OctopusContext cntx) {
		this.isOnlineRegistrationActive = Boolean.valueOf(cntx.getContextField("online-registration.activated").toString());
	}

	/**
	 * Columns username, password, url for delegations and persons.
	 *
	 * @param spreadSheet SpreadSheet
	 * @param guest Map with guests
	 * @param event Event
	 * @throws IOException
	 * @throws BeanException
	 */
	private void addCredentialsDataColumns(SpreadSheet spreadSheet, Map guest, Event event, OctopusContext cntx) throws IOException, BeanException {
		String password = EMPTY_FIELD_VALUE;
		Object username = EMPTY_FIELD_VALUE;
		String url = EMPTY_FIELD_VALUE;
		String directAccessURL = EMPTY_FIELD_VALUE;

		Database database = new DatabaseVeraWeb(cntx);

		String category = (String) guest.get("catname");
		if (category == null) { category = ""; }

		if(guestIsDelegationAndHasOsiamLogin(guest, category)) {

			url = generateLoginUrl(guest);

			password = generatePasswordForCompany(guest, event, password, database);

			username = guest.get("osiam_login");
		}
		else {
			username = guest.get("osiam_login");
			url = getResetPasswordURL(guest, cntx);
			if (!event.login_required) {
				directAccessURL = getLoginUUIDAndDirectAccessURL(guest, event, directAccessURL);
			}
		}

		spreadSheet.addCell(username);
		spreadSheet.addCell(password);
		spreadSheet.addCell(url);
		if (!event.login_required) {
			spreadSheet.addCell(directAccessURL);
		}
	}

//	private void updateGuestWithLoginUUID(Integer id, OctopusContext octopusContext, String loginUUID) throws BeanException {
//		Database database = new DatabaseVeraWeb(octopusContext);
//		TransactionContext transactionContext = database.getTransactionContext();
//
//		Update updateStatement = SQL.Update(database);
//		updateStatement.table("veraweb.tguest");
//		updateStatement.update("login_required_uuid", loginUUID);
//		updateStatement.whereAndEq("pk", id);
//
//		transactionContext.execute(updateStatement);
//	}
//
//	private String generateUUID() {
//		UUID uuid = UUID.randomUUID();
//		return uuid.toString();
//	}

	private Integer setDoctypeId(OctopusContext cntx, Integer doctypeid) {
        final List list = (List)cntx.contentAsObject("allEventDoctype");
        Iterator it = list.iterator();
        if (it.hasNext()) {
            doctypeid = (Integer)((Map)it.next()).get("doctype");
        }
        for (it = list.iterator(); it.hasNext(); ) {
            final Map data = (Map)it.next();
            if (((Integer)(data).get("isdefault")).intValue() == 1) {
                doctypeid = (Integer)data.get("doctype");
                break;
            }
        }
        return doctypeid;
    }

	private boolean guestIsDelegationAndHasOsiamLogin(Map guest, String category) {
        return guest.containsKey("delegation") &&
                guest.get("delegation") != null &&
                guest.get("delegation").toString().length() > 0 &&
                guest.containsKey("osiam_login") &&
                guest.get("osiam_login") != null &&
                guest.get("osiam_login").toString().length() > 0 &&
                !category.equals("Pressevertreter");
    }

    private String getLoginUUIDAndDirectAccessURL(Map guest, Event event, String directAccessURL) {
        String loginUUID = null;
        if (guest.get("login_required_uuid") != null) {
            loginUUID = guest.get("login_required_uuid").toString();
            directAccessURL = generateEventUrlWithoutLogin(event.hash, loginUUID);
        }
        return directAccessURL;
    }

    private String generatePasswordForCompany(Map guest, Event event, String password, Database database)
            throws BeanException, IOException {
        final Select select = database.getSelect("Person");
        select.joinLeftOuter("tguest", "tperson.pk", "tguest.fk_person");
        select.whereAndEq("tperson.pk", guest.get("fk_person"));

        final ResultList resultList = database.getList(select, database);

        for (int i = 0; i < resultList.size(); i++) {
            final ResultMap resultMap = (ResultMap) resultList.get(i);

            if(resultMap.get("iscompany").toString() != null && resultMap.get("iscompany").toString().equals("t")) {
                password = generatePassword(event, guest);
            }
        }
        return password;
    }

	private String getResetPasswordURL(Map guest, OctopusContext cntx) throws IOException, BeanException {
		String url;
		try {
            url = getURLLinkUUIDDataFromGuest(Integer.valueOf(guest.get("fk_person").toString()), cntx);
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException - getting URL/LinkUUID of the person:" + guest.get("fk_person").toString(), e);
            throw e;
        } catch (BeanException e) {
            logger.error("BeanException - getting URL/LinkUUID of the person:" + guest.get("fk_person").toString(), e);
            throw e;
        }
		return url;
	}

	/**
	 * Getting URL to reseting password
	 *
	 * @param personId
	 * @return String the url
	 * @throws IOException
	 * @throws BeanException
	 */
	private String getURLLinkUUIDDataFromGuest(Integer personId, OctopusContext cntx) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);

		String url = null;
		WhereList whereCriterias = new WhereList();

        whereCriterias.addAnd(new Where("personid", personId, "="));
        whereCriterias.addAnd(new Where("linktype", "passwordreset", "="));

		final Select select2 = SQL.Select(database);
	      select2.from("link_uuid");
	      select2.select("uuid");
	      select2.where(whereCriterias);
		List list = database.getBeanList("LinkUUID", select2);

		if (!list.isEmpty() && database.getField("uuid") != null) {
			String uuid = database.getField("uuid").toString();
			PersonURLHandler pHandler = new PersonURLHandler();
			url = pHandler.generateResetPasswordUrl(uuid);
		}
		return url;
	}

	private String generatePassword(Event event, Map guest) {
        final String shortName = event.get("shortname").toString();
        String companyName = "";
        if(!guest.get("company_a_e1").equals(null) && !guest.get("company_a_e1").equals("")) {
        	companyName = guest.get("company_a_e1").toString();
        }
        final StringBuilder passwordBuilder = new StringBuilder();
		passwordBuilder.append(extractFirstXChars(shortName, 3));
		passwordBuilder.append(extractFirstXChars(companyName, 3));
		passwordBuilder.append(extractFirstXChars(event.begin.toString(), 10));
		return passwordBuilder.toString();
	}

	private String generateLoginUrl(Map guest) throws IOException {
		final PropertiesReader propertiesReader = new PropertiesReader();
		final Properties properties = propertiesReader.getProperties();
		final URLGenerator url = new URLGenerator(properties);
		return url.getURLForDelegation() + guest.get("delegation");
	}

	private String generateEventUrlWithoutLogin(String eventHash, String guestLoginUUID) {
		final URLGenerator urlGenerator = getUrlGenerator();
		return urlGenerator.getURLForFreeVisitors() + eventHash + "/" + guestLoginUUID;
	}

	private String extractFirstXChars(String value, int x) {
		return value.substring(0, Math.min(value.length(), x));
	}

	/**
	 * Export ausschließlich die Gast-Daten in eine Zeile.
	 *
	 * @param spreadSheet In das geschrieben werden soll.
	 * @param event Event das exportiert wird.
	 * @param guest Map mit den Gastdaten.
	 * @param data Zusatzinformationen.
	 * @throws IOException Wenn die Generierung von URL fur Delegation fehlschlug.
	 * @throws BeanException
	 */
	protected void exportOnlyPerson(SpreadSheet spreadSheet, Event event, Location location, Map guest, Map data, Boolean isPressStaff, OctopusContext cntx) throws IOException, BeanException {
		checkIfOnlineRegistrationIsAvailable(cntx);
		//
		// Gast spezifische Daten
		//
		spreadSheet.addCell(data.get("doctype")); // Name des Dokument-Typs
		spreadSheet.addCell(guest.get("textfield"));
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);

		spreadSheet.addCell(getAddresstype((Integer)guest.get("addresstype")));
		spreadSheet.addCell(getLocale((Integer)guest.get("locale")));

		spreadSheet.addCell(guest.get("function"));
		spreadSheet.addCell(guest.get("salutation"));
		spreadSheet.addCell(guest.get("titel"));
		spreadSheet.addCell(guest.get("firstname"));
		spreadSheet.addCell(guest.get("lastname"));

		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);

		spreadSheet.addCell(guest.get("zipcode"));
		spreadSheet.addCell(guest.get("city"));
		spreadSheet.addCell(guest.get("street"));
		spreadSheet.addCell(guest.get("country"));
		spreadSheet.addCell(guest.get("suffix1"));
		spreadSheet.addCell(guest.get("suffix2"));

		/*
		 * modified to support birthplace as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(guest.get("birthplace_a_e1"));
		spreadSheet.addCell(guest.get("fon"));
		spreadSheet.addCell(guest.get("fax"));
		spreadSheet.addCell(guest.get("mail"));
		spreadSheet.addCell(guest.get("www"));
		spreadSheet.addCell(guest.get("mobil"));
		spreadSheet.addCell(guest.get("company"));
		spreadSheet.addCell(guest.get("pobox"));
		spreadSheet.addCell(guest.get("poboxzipcode"));

		//
		// Kategorie spezifische Daten, wenn nach Kategorie gefilter wurde.
		//
		spreadSheet.addCell(guest.get("catname")); // Verweis auf Kategorie, die zur Auswahl führte
		spreadSheet.addCell(guest.get("catrang")); // Der Rang der Kategorie innerhalb der Kategorien
		spreadSheet.addCell(guest.get("guestrang")); // Der Rang der Person innerhalb der Kategorie

		/*
		 * modified to support workareas as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(guest.get("workareaname"));
		spreadSheet.addCell(getReserve((Integer)guest.get("reserve"))); // 0 = Tisch, 1 = Reservce

		//
		// Veranstaltungsspezifische Attribute für Person
		//
		spreadSheet.addCell(getStatus((Integer)guest.get("invitationstatus"))); // 0 = Offen, 1 = Zusage, 2 = Absage
		spreadSheet.addCell(guest.get("tableno"));
		spreadSheet.addCell(guest.get("seatno"));
		spreadSheet.addCell(guest.get("orderno"));
		spreadSheet.addCell(getColor((Integer)guest.get("color1"))); // Verweiss auf Farbe die verwendet werden soll.
		spreadSheet.addCell(getDomestic((Integer)guest.get("domestic"))); // Inland Ja / Nein
		spreadSheet.addCell(guest.get("language"));
		spreadSheet.addCell(getGender((String)guest.get("gender"))); // M oder F
		spreadSheet.addCell(guest.get("nationality"));
		spreadSheet.addCell(guest.get("notehost"));
		spreadSheet.addCell(guest.get("noteorga"));

		//
		// Veranstaltungsspezifische Attribute für Partner der Person
		//
		spreadSheet.addCell(null); // 0 = Offen, 1 = Zusage, 2 = Absage
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null); // Verweiss auf Farbe die verwendet werden soll.
		spreadSheet.addCell(null); // Inland Ja / Nein
		spreadSheet.addCell(null);
		spreadSheet.addCell(null); // M oder F
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);

		//
		// Sonstiges
		//
		spreadSheet.addCell(data.get("zusagen"));
		spreadSheet.addCell(data.get("absagen"));
		spreadSheet.addCell(data.get("offenen"));

		/*
		 * modified to support fourth invitation state as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(data.get("teilnahmen"));
		spreadSheet.addCell(data.get("platz"));
		spreadSheet.addCell(data.get("reserve"));
		spreadSheet.addCell(event.shortname);
		spreadSheet.addCell(event.begin);
		spreadSheet.addCell(event.end);
		if (isOnlineRegistrationActive) {
			if ((guest.get("delegation")==null || guest.get("delegation").equals("")) && !isPressStaff) {
				final EventURLHandler eventURLHandler = new EventURLHandler();
				spreadSheet.addCell(eventURLHandler.generateEventUrl(event));
			} else {
				spreadSheet.addCell("");
			}
		}

		addLocationCells(spreadSheet, location);
		if(isOnlineRegistrationActive) {
			addCredentialsDataColumns(spreadSheet, guest, event, cntx);
			// Updating username in tperson
			updateDelegationUsername(cntx, guest.get("osiam_login"), (Integer)guest.get("fk_person"));
		}

		spreadSheet.addCell(event.note);
	}


	/**
	 * Checking if one guest is from the press staff
	 *
	 * @return Boolean
	 * @throws IOException
	 * @throws BeanException
	 */
	private void updateDelegationUsername(OctopusContext cntx, Object username, Integer personId) throws BeanException {
		final Database database = new DatabaseVeraWeb(cntx);
		if (username != null) {
			updatePerson(database, username, personId);
		} else {
			updatePerson(database, null, personId);
		}
	}

	private void updatePerson(final Database database, final Object username, final Integer personId) throws BeanException {
		database.execute(SQL.Update( database ).
				table("veraweb.tperson").
				update("username", username).
				where(Expr.equal("pk", personId)));
	}

	/**
	 * Export ausschließlich die Partner-Daten in eine Zeile.
	 *
	 * @param spreadSheet In das geschrieben werden soll.
	 * @param event Event das exportiert wird.
	 * @param guest Map mit den Gastdaten.
	 * @param data Zusatzinformationen.
	 * @throws IOException
	 * @throws BeanException
	 */
	protected void exportOnlyPartner(SpreadSheet spreadSheet, Event event, Location location, boolean showA, boolean showB, Map guest, Map data, OctopusContext cntx) throws IOException, BeanException {
		//
		// Gast spezifische Daten
		//
		spreadSheet.addCell(data.get("doctype")); // Name des Dokument-Typs
		spreadSheet.addCell(guest.get("textfield_p"));
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);

		spreadSheet.addCell(getAddresstype((Integer)guest.get("addresstype")));
		spreadSheet.addCell(getLocale((Integer)guest.get("locale")));

		spreadSheet.addCell(null);
		spreadSheet.addCell(guest.get("salutation_p"));
		spreadSheet.addCell(guest.get("titel_p"));
		spreadSheet.addCell(guest.get("firstname_p"));
		spreadSheet.addCell(guest.get("lastname_p"));

		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);

		spreadSheet.addCell(guest.get("zipcode"));
		spreadSheet.addCell(guest.get("city"));
		spreadSheet.addCell(guest.get("street"));
		spreadSheet.addCell(guest.get("country"));
		spreadSheet.addCell(guest.get("suffix1"));
		spreadSheet.addCell(guest.get("suffix2"));

		/*
		 * modified to support birthplace as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(""); // birthplace does not exist for partner
		spreadSheet.addCell(guest.get("fon"));
		spreadSheet.addCell(guest.get("fax"));
		spreadSheet.addCell(guest.get("mail"));
		spreadSheet.addCell(guest.get("www"));
		spreadSheet.addCell(guest.get("mobil"));
		spreadSheet.addCell(guest.get("company"));
		spreadSheet.addCell(guest.get("pobox"));
		spreadSheet.addCell(guest.get("poboxzipcode"));

		//
		// Kategorie spezifische Daten, wenn nach Kategorie gefilter wurde.
		//
		spreadSheet.addCell(guest.get("catname")); // Verweis auf Kategorie, die zur Auswahl führte
		spreadSheet.addCell(guest.get("catrang")); // Der Rang der Kategorie innerhalb der Kategorien
		spreadSheet.addCell(guest.get("guestrang")); // Der Rang der Person innerhalb der Kategorie

		/*
		 * modified to support workareas as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(guest.get("workareaname"));
		spreadSheet.addCell(getReserve((Integer)guest.get("reserve"))); // 0 = Tisch, 1 = Reservce

		//
		// Veranstaltungsspezifische Attribute für Person
		//
		spreadSheet.addCell(getStatus((Integer)guest.get("invitationstatus_p"))); // 0 = Offen, 1 = Zusage, 2 = Absage
		spreadSheet.addCell(guest.get("tableno_p"));
		spreadSheet.addCell(guest.get("seatno_p"));
		spreadSheet.addCell(guest.get("orderno_p"));
		spreadSheet.addCell(getColor((Integer)guest.get("color2"))); // Verweiss auf Farbe die verwendet werden soll.
		spreadSheet.addCell(getDomestic((Integer)guest.get("domestic_p"))); // Inland Ja / Nein
		spreadSheet.addCell(guest.get("language_p"));
		spreadSheet.addCell(getGender((String)guest.get("gender_p"))); // M oder F
		spreadSheet.addCell(guest.get("nationality_p"));
		spreadSheet.addCell(guest.get("notehost_p"));
		spreadSheet.addCell(guest.get("noteorga_p"));

		//
		// Veranstaltungsspezifische Attribute für Partner der Person
		//
		spreadSheet.addCell(null); // 0 = Offen, 1 = Zusage, 2 = Absage
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null); // Verweiss auf Farbe die verwendet werden soll.
		spreadSheet.addCell(null); // Inland Ja / Nein
		spreadSheet.addCell(null);
		spreadSheet.addCell(null); // M oder F
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);

		//
		// Sonstiges
		//
		spreadSheet.addCell(data.get("zusagen"));
		spreadSheet.addCell(data.get("absagen"));
		spreadSheet.addCell(data.get("offenen"));

		/*
		 * modified to support fourth invitation state as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(data.get("teilnahmen"));
		spreadSheet.addCell(data.get("platz"));
		spreadSheet.addCell(data.get("reserve"));
		spreadSheet.addCell(event.shortname);
		spreadSheet.addCell(event.begin);
		spreadSheet.addCell(event.end);

		addLocationCells(spreadSheet, location);
		if (isOnlineRegistrationActive) {
			addCredentialsDataColumns(spreadSheet, guest, event, cntx);
		}

		spreadSheet.addCell(event.note);
	}

    //
    // Öffentliche Hilfsmethoden
    //
    /** Diese Methode liefert eine String-Darstellung für einen Reserve-Wert */
	public static String getReserve(Integer reserve) {
		return reserve == null ? null : (reserve.intValue() == 1 ? "Reserve" : "Tisch");
	}

    /** Diese Methode liefert eine String-Darstellung eines Gender-Werts */
	public static String getGender(String gender) {
		return gender;
	}

    /** Diese Methode liefert eine String-Darstellung eines Domestic-Werts */
	public static String getDomestic(Integer domestic) {
		return domestic == null ? null : (domestic.intValue() == 1 ? "Ja" : "Nein");
	}

    /**
     * Diese Methode liefert zu einem Integer den um 1 erhöhten Wert oder
     * 0, falls <code>null</code> übergeben worden war.
     */
	public static Integer getColor(Integer color) {
		return new Integer(color == null ? 0 : color.intValue() + 1);
	}

	/**
	 * Anschrifttyp als P,G,S zurückgeben.
	 *
	 * Vorgabe aus PersonDoctype, überschreibbar in GuestDoctype
	 * Muss auch in anderen Bereichen umgesetzt werden.
	 * Z.B. beim "Neu Laden" in Worker und Template.
	 */
	public static String getAddresstype(Integer addresstype) {
		if (addresstype == null) {
			return null;
		} else if (addresstype.intValue() == 2) {
			return "P";
		} else if (addresstype.intValue() == 3) {
			return "W";
		} else { // addresstype.intValue() == 1
			return "G";
		}
	}

	/**
	 * Zeichensatz als L,F1,F2 zurückgeben.
	 *
	 * Vorgabe aus PersonDoctype, überschreibbar in GuestDoctype
	 * Muss auch in anderen Bereichen umgesetzt werden.
	 * Z.B. beim "Neu Laden" in Worker und Template.
	 */
	public static String getLocale(Integer locale) {
		if (locale == null) {
			return null;
		} else if (locale.intValue() == 2) {
			return "ZS1";
		} else if (locale.intValue() == 3) {
			return "ZS2";
		} else { // locale.intValue() == 1
			return "L";
		}
	}

    /** Diese Methode liefert eine String-Darstellung eines Veranstaltungstyps */
	public static String getType(Integer type) {
		if (type == null || type.intValue() == EventConstants.TYPE_MITPARTNER) {
			return "Mit Partner";
		} else if (type.intValue() == EventConstants.TYPE_OHNEPARTNER) {
			return "Ohne Partner";
		} else { // type.intValue() == EventConstants.TYPE_NURPARTNER
			return "Nur Partner";
		}
	}

    /** Diese Methode liefert eine String-Darstellung eines Einladungsstatus */
	public static String getStatus(Integer status) {
		if (status == null || status.intValue() == 0) {
			return "Offen";
		} else if (status.intValue() == 1) {
			return "Zusage";
		} else if (status.intValue() == 2) {
			return "Absage";
		} else { // status == 3
			/*
			 * modified to support forth invitation state as per change request for version 1.2.0
			 * cklein
			 * 2008-02-26
			 */
			return "Teilnahme";
		}
	}

	private URLGenerator getUrlGenerator() {
		final Properties properties = propertiesReader.getProperties();
		return new URLGenerator(properties);
	}

	/**
	 * Checking if one guest is from the press staff
	 *
	 * @return Boolean
	 * @throws IOException
	 * @throws BeanException
	 */
	private Boolean isPressGuest(OctopusContext cntx, String pressUuid, Integer guestId) throws BeanException, IOException {
		if (pressUuid != null) {
	        final Database database = new DatabaseVeraWeb(cntx);
	        Select select = database.getCount("Guest");
	        select.join("veraweb.tcategorie", "tcategorie.pk","tguest.fk_category");
	        select.join("veraweb.tevent", "tevent.fk_orgunit","tcategorie.fk_orgunit");
	        select.where(Expr.equal("tcategorie.catname", "Pressevertreter"));
	        select.whereAnd(Expr.equal("tguest.pk", guestId));
	        select.whereAnd(Expr.equal("tevent.mediarepresentatives", pressUuid));

	        Integer i = database.getCount(select);
	        if (i.equals(1)) {
	        	return true;
	        }
		}
		return false;
	}

}
