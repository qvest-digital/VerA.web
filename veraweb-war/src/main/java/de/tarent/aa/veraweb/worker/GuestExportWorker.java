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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.log4j.Logger;

import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.GuestSearch;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.utils.DatabaseHelper;
import de.tarent.aa.veraweb.utils.ExportHelper;
import de.tarent.aa.veraweb.utils.OctopusHelper;
import de.tarent.commons.spreadsheet.export.SpreadSheet;
import de.tarent.commons.spreadsheet.export.SpreadSheetFactory;
import de.tarent.dblayer.sql.Join;
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

/**
 * Diese Octopus-Worker-Klasse exportiert Dokumententypen einer G�steliste
 * in ein OpenDocument-SpreadSheet.
 * 
 * @author Christoph
 */
public class GuestExportWorker {
    /** Logger dieser Klasse */
	private final Logger logger = Logger.getLogger(getClass());

    //
    // Octopus-Aktionen
    //
	/** Octopus-Eingabeparameter f�r {@link #calc(OctopusContext, Integer)} */
	public static final String INPUT_calc[] = { "doctype" };
	/** Octopus-Eingabeparameter f�r {@link #calc(OctopusContext, Integer)} */
	public static final boolean MANDATORY_calc[] = { false };
	/** Octopus-Ausgabeparameter f�r {@link #calc(OctopusContext, Integer)} */
	public static final String OUTPUT_calc = "exportCalc";
	/**
	 * <p>
	 * Berechnet wieviele Dokumenttypen einer G�steliste exportiert
	 * werden sollen und k�nnen.
	 * </p>
	 * 
	 * @param cntx OctopusContext
	 * @param doctypeid Dokumenttyp der exportiert werden soll.
	 */
	public Map calc(OctopusContext cntx, Integer doctypeid) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		Event event = (Event)cntx.contentAsObject("event");
		GuestSearch search = (GuestSearch)cntx.contentAsObject("search");
		List selection = (List)cntx.sessionAsObject("selectionGuest");
		
		if (doctypeid == null) {
			List list = (List)cntx.contentAsObject("allEventDoctype");
			Iterator it = list.iterator();
			if (it.hasNext()) {
				doctypeid = (Integer)((Map)it.next()).get("doctype");
			}
			for (it = list.iterator(); it.hasNext(); ) {
				Map data = (Map)it.next();
				if (((Integer)(data).get("isdefault")).intValue() == 1) {
					doctypeid = (Integer)data.get("doctype");
					break;
				}
			}
		}
		if (doctypeid == null)
			return null;
		Doctype doctype = (Doctype)
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
			WhereList where = new WhereList();
			GuestListWorker.addGuestListFilter(search, where);
			
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
		Map result = new HashMap();
		result.put("doctype", doctypeid);
		result.put("total", total);
		result.put("available", available);
		result.put("sessionId", cntx.requestAsString(TcRequest.PARAM_SESSION_ID));
		return result;
	}

	/** Octopus-Eingabeparameter f�r {@link #export(OctopusContext, Integer)} */
	public static final String INPUT_export[] = { "doctype" };
	/** Octopus-Ausgabeparameter f�r {@link #export(OctopusContext, Integer)} */
	public static final String OUTPUT_export = "stream";
	/**
	 * <p>
	 * Exportiert die Dokumenttypen einer G�steliste.
	 * </p>
	 * <p>
	 * Wenn Daten G�ste selektiert wurden werden diese exportiert,
	 * falls dies nicht der Fall ist alle G�ste der �bergebenen Veranstaltung.
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
		Database database = new DatabaseVeraWeb(cntx);
		Event event = (Event)cntx.contentAsObject("event");
		GuestSearch search = (GuestSearch)cntx.contentAsObject("search");
		List selection = (List)cntx.sessionAsObject("selectionGuest");
		Doctype doctype = (Doctype)database.getBean("Doctype", doctypeid);
		
		// Spreadsheet �ffnen
		final SpreadSheet spreadSheet = SpreadSheetFactory.getSpreadSheet(doctype.format);
		spreadSheet.init();
		String filename = OctopusHelper.getFilename(cntx, spreadSheet.getFileExtension(), "export." + spreadSheet.getFileExtension());
		
		if (logger.isInfoEnabled())
			logger.info("Exportiere Gästeliste. (Dateiname: '" + filename + "'; Dokumenttyp: #" + doctype.id + "; Format: '" + spreadSheet.getClass().getName() + "')");
		
		// Tabelle �ffnen und erste Zeile schreiben
		spreadSheet.openTable("Gäste", 65);
		spreadSheet.openRow();
		exportHeader(spreadSheet);
		spreadSheet.closeRow();
		
		// Zusatzinformationen
		Map data = new HashMap();
		data.put("doctype", doctype.name);
		String withHost = (doctype.host != null && doctype.host.booleanValue()) ? "" :
				"(tguest.ishost IS NULL OR tguest.ishost = 0) AND ";
		
		// Export-Select zusammenbauen
		Select select = database.getSelect("GuestDoctype");
		select.select("tguest.*");
		select.selectAs("CASE WHEN orderno IS NOT NULL THEN orderno ELSE orderno_p END", "someorderno");
		
		if (selection != null && selection.size() > 0) {
			// Joint tguest und schr�nkt das Ergebnis auf den entsprechenden
			// Dokumenten-Typ und bestimmte G�ste ein.
			if (logger.isInfoEnabled())
				logger.info("Exportiere Gästeliste anhand der Sleektion.");
			
			select.join(new Join(Join.INNER, "veraweb.tguest", new RawClause(withHost +
					"tguest_doctype.fk_guest = tguest.pk AND tguest_doctype.fk_doctype = " + doctype.id)));
			
			select.where(Expr.in("tguest_doctype.fk_guest", selection));
		} else if (event != null && event.id != null && event.id.intValue() != 0) {
			// Joint tguest und schr�nkt das Ergebnis auf den entsprechenden
			// Dokumenten-Typ und eine Veranstaltung ein.
			if (logger.isInfoEnabled())
				logger.info("Exportiere Gästeliste der Veranstaltung " + event.id + ".");
			
			select.join(new Join(Join.INNER, "veraweb.tguest", new RawClause(withHost +
					"tguest_doctype.fk_guest = tguest.pk AND tguest_doctype.fk_doctype = " + doctype.id)));
			
			WhereList list = new WhereList();
			GuestListWorker.addGuestListFilter(search, list);
			select.where(list);
		} else {
			logger.error("Konnte Gästeliste nicht exportieren.");
			throw new BeanException("Konnte Gästeliste nicht exportieren.");
		}
		
		WorkerFactory.getGuestListWorker(cntx).getSums(database, data, search, selection);
		
		select.joinLeftOuter("veraweb.tperson", "tperson.pk", "tguest.fk_person");
		select.select("tperson.lastname_a_e1");
		select.select("tperson.firstname_a_e1");
		select.select("tperson.country_a_e1");
		select.select("tperson.zipcode_a_e1");
		select.select("tperson.birthplace_a_e1");
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
		List order = new ArrayList();
		order.add("tguest.ishost");
		order.add("DESC");
		GuestReportWorker.setSortOrder(order, cntx.requestAsString("sort1"));
		GuestReportWorker.setSortOrder(order, cntx.requestAsString("sort2"));
		GuestReportWorker.setSortOrder(order, cntx.requestAsString("sort3"));
		select.orderBy(DatabaseHelper.getOrder(order));
		
		// Export-Select ausf�hren
		exportSelect(spreadSheet, database, event, doctype, search, select, data);
		
		// Tabelle schlie�en
		spreadSheet.closeTable();
		
		// SpreadSheet speichern
		if (logger.isInfoEnabled())
			logger.info("Gebe Gästeliste als Download zurück.");
        final PipedInputStream pis = new PipedInputStream();
        final PipedOutputStream pos = new PipedOutputStream(pis);
        new Thread(new Runnable() {
        	public void run() {
        		if (logger.isDebugEnabled())
        			logger.debug("Gästelisten-Export: Starte das Speichern eines Spreadsheets.");
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
					}
				}
        		if (logger.isDebugEnabled())
        			logger.debug("Gästelisten-Export: Beende das Speichern eines Spreadsheets.");
        	}
        }).start();
		
		// Stream-Informationen zur�ck geben
		Map stream = new HashMap();
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
     * Diese Methode exportiert alle G�ste, die �ber das �bergebene Select-Statement
     * erfasst werden, in das �bergebene Spreadsheet.<br>
     * TODO: Parameter, von denen nur eine oder zwei Eigenschaften benutzt werden, durch Parameter ersetzen, die direkt diese Eigenschaften darstellen
     * 
     * @param spreadSheet Exportziel
     * @param database Exportquelle, auf die das Statement select angewendet werden soll
     * @param event Veranstaltung, zu der die G�ste geh�ren
     * @param doctype Dokumenttyp, der �ber sein partner-Flag bestimmt, ob Hauptperson
     *  und Partner in der gleichen oder in separaten Zeilen exportiert werden.
     * @param search G�stesuche-Kriterien, die �ber ihre Eigenschaften invitationstatus
     *  und reserve bestimmen, ob Hauptperson, Partner oder beide exportiert werden sollen.
     * @param select Select-Statement, das auf der �bergebenen database ausgef�hrt wird, um
     *  die zu exportierenden Datens�tze zu liefern.
     * @param data Map mit Zusatzinformationen unter den Schl�sseln "doctype", "zusagen",
     *  "absagen", "offenen", "platz" und "reserve".
     */
	protected void exportSelect(SpreadSheet spreadSheet, Database database, Event event, Doctype doctype, GuestSearch search, Select select, Map data) throws BeanException {
		for (Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {
			Map guest = (Map)it.next();
			
			Integer invitationtype = (Integer)guest.get("invitationtype");
			if (invitationtype == null || invitationtype.intValue() == 0)
				invitationtype = event.invitationtype;
			
			// Test ob die Hauptperson / der Partner zum aktuellen Filter passen.
			Integer reserve = (Integer)guest.get("reserve");
			Integer invitationstatus_a = (Integer)guest.get("invitationstatus");
			Integer invitationstatus_b = (Integer)guest.get("invitationstatus_p");

			/*
			 * modified to support the additional fourth invitation_status of "teilgenommen"
			 * cklein
			 * 2008-02-26
			 */
			boolean showA = search.invitationstatus == null || (
					(search.invitationstatus.intValue() == 1 && (invitationstatus_a == null || invitationstatus_a.intValue() == 0)) ||
					(search.invitationstatus.intValue() == 2 && (invitationstatus_a != null && invitationstatus_a.intValue() == 1)) ||
					(search.invitationstatus.intValue() == 3 && (invitationstatus_a != null && invitationstatus_a.intValue() == 2)) ||
					(search.invitationstatus.intValue() == 4 && (invitationstatus_a != null && invitationstatus_a.intValue() == 3)));
			boolean showB = search.invitationstatus == null || (
					(search.invitationstatus.intValue() == 1 && (invitationstatus_b == null || invitationstatus_b.intValue() == 0)) ||
					(search.invitationstatus.intValue() == 2 && (invitationstatus_b != null && invitationstatus_b.intValue() == 1)) ||
					(search.invitationstatus.intValue() == 3 && (invitationstatus_b != null && invitationstatus_b.intValue() == 2)) ||
					(search.invitationstatus.intValue() == 4 && (invitationstatus_b != null && invitationstatus_b.intValue() == 3)));
			showA = showA && (search.reserve == null || (
					(search.reserve.intValue() == 1 && (reserve == null || reserve.intValue() == 0)) ||
					(search.reserve.intValue() == 2 && (reserve != null && reserve.intValue() == 1))));
			showB = showB && (search.reserve == null || (
					(search.reserve.intValue() == 1 && (reserve == null || reserve.intValue() == 0)) ||
					(search.reserve.intValue() == 2 && (reserve != null && reserve.intValue() == 1))));
			
			if (doctype.partner != null && doctype.partner.booleanValue()) {
				// Eigenes Dokument
				if (invitationtype == null || invitationtype.intValue() == EventConstants.TYPE_MITPARTNER) {
					// Mit Partner
					if (showA) {
						spreadSheet.openRow();
						exportOnlyPerson(spreadSheet, event, showA, showB, guest, data);
						spreadSheet.closeRow();
					}
					if (showB) {
						spreadSheet.openRow();
						exportOnlyPartner(spreadSheet, event, showA, showB, guest, data);
						spreadSheet.closeRow();
					}
				} else if (invitationtype.intValue() == EventConstants.TYPE_OHNEPARTNER) {
					// Ohne Partner
					if (showA) {
						spreadSheet.openRow();
						exportOnlyPerson(spreadSheet, event, showA, showB, guest, data);
						spreadSheet.closeRow();
					}
				} else if (invitationtype.intValue() == EventConstants.TYPE_NURPARTNER) {
					// Nur Partner
					if (showB) {
						spreadSheet.openRow();
						exportOnlyPartner(spreadSheet, event, showA, showB, guest, data);
						spreadSheet.closeRow();
					}
				}
			} else {
				// Gleiches Dokument
				if (invitationtype == null || invitationtype.intValue() == EventConstants.TYPE_MITPARTNER) {
					// Mit Partner
					if (showA || showB) {
						spreadSheet.openRow();
						exportBothInOneLine(spreadSheet, event, showA, showB, guest, data);
						spreadSheet.closeRow();
					}
				} else if (invitationtype.intValue() == EventConstants.TYPE_OHNEPARTNER) {
					// Ohne Partner
					if (showA) {
						spreadSheet.openRow();
						exportOnlyPerson(spreadSheet, event, showA, showB, guest, data);
						spreadSheet.closeRow();
					}
				} else if (invitationtype.intValue() == EventConstants.TYPE_NURPARTNER) {
					// Nur Partner
					if (showB) {
						spreadSheet.openRow();
						exportOnlyPartner(spreadSheet, event, showA, showB, guest, data);
						spreadSheet.closeRow();
					}
				}
			}
		}
	}

	/**
	 * Schreibt die �berschriften des Export-Dokumentes.
	 * 
	 * @param spreadSheet In das geschrieben werden soll.
	 */
	protected void exportHeader(SpreadSheet spreadSheet) {
		//
		// Gast spezifische Daten
		//
		spreadSheet.addCell("Dokument_Typ"); // Name des Dokument-Typs
		spreadSheet.addCell("Freitextfeld");
		spreadSheet.addCell("Partner_Freitextfeld");
		spreadSheet.addCell("Verbinder");
		
		spreadSheet.addCell("Anschrift"); // P, G oder S - Vorgabe aus Person, �berschreibbar
		spreadSheet.addCell("Zeichensatz"); // L, F1 oder F2 - Vorgabe aus Person, �berschreibbar
		
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
		spreadSheet.addCell("Kategorie"); // Verweis auf Kategorie, die zur Auswahl f�hrte
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
		// Veranstaltungsspezifische Attribute f�r Person
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
		// Veranstaltungsspezifische Attribute f�r Partner der Person
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
		spreadSheet.addCell("Ort_der_Veranstaltung");
		spreadSheet.addCell("Bemerkung");
	}

	/**
	 * Export die Gast- und Partner Daten in eine Zeile.
	 * 
	 * @param spreadSheet In das geschrieben werden soll.
	 * @param event Event das exportiert wird.
	 * @param guest Map mit den Gastdaten.
	 * @param data Zusatzinformationen.
	 */
	protected void exportBothInOneLine(SpreadSheet spreadSheet, Event event, boolean showA, boolean showB, Map guest, Map data) {
		//
		// Gast spezifische Daten
		//
		spreadSheet.addCell(data.get("doctype")); // Name des Dokument-Typs
		
		String text_a = (String)guest.get("textfield");
		String text_b = (String)guest.get("textfield_p");
		if (showA) {
			spreadSheet.addCell(text_a);
		} else {
			spreadSheet.addCell(null);
		}
		if (showB) {
			spreadSheet.addCell(text_b);
		} else {
			spreadSheet.addCell(null);
		}
		if (showA && showB && text_a != null && text_a.length() != 0 && text_b != null && text_b.length() != 0) {
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
		spreadSheet.addCell(guest.get("catname")); // Verweis auf Kategorie, die zur Auswahl f�hrte
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
		// Veranstaltungsspezifische Attribute f�r Person
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
		// Veranstaltungsspezifische Attribute f�r Partner der Person
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
		spreadSheet.addCell(event.location);
		spreadSheet.addCell(event.note);
	}

	/**
	 * Export ausschlie�lich die Gast-Daten in eine Zeile.
	 * 
	 * @param spreadSheet In das geschrieben werden soll.
	 * @param event Event das exportiert wird.
	 * @param guest Map mit den Gastdaten.
	 * @param data Zusatzinformationen.
	 */
	protected void exportOnlyPerson(SpreadSheet spreadSheet, Event event, boolean showA, boolean showB, Map guest, Map data) {
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
		spreadSheet.addCell(guest.get("catname")); // Verweis auf Kategorie, die zur Auswahl f�hrte
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
		// Veranstaltungsspezifische Attribute f�r Person
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
		// Veranstaltungsspezifische Attribute f�r Partner der Person
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
		spreadSheet.addCell(event.location);
		spreadSheet.addCell(event.note);
	}

	/**
	 * Export ausschlie�lich die Partner-Daten in eine Zeile.
	 * 
	 * @param spreadSheet In das geschrieben werden soll.
	 * @param event Event das exportiert wird.
	 * @param guest Map mit den Gastdaten.
	 * @param data Zusatzinformationen.
	 */
	protected void exportOnlyPartner(SpreadSheet spreadSheet, Event event, boolean showA, boolean showB, Map guest, Map data) {
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
		spreadSheet.addCell(guest.get("catname")); // Verweis auf Kategorie, die zur Auswahl f�hrte
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
		// Veranstaltungsspezifische Attribute f�r Person
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
		// Veranstaltungsspezifische Attribute f�r Partner der Person
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
		spreadSheet.addCell(event.location);
		spreadSheet.addCell(event.note);
	}

    //
    // �ffentliche Hilfsmethoden
    //
    /** Diese Methode liefert eine String-Darstellung f�r einen Reserve-Wert */
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
     * Diese Methode liefert zu einem Integer den um 1 erh�hten Wert oder
     * 0, falls <code>null</code> �bergeben worden war. 
     */
	public static Integer getColor(Integer color) {
		return new Integer(color == null ? 0 : color.intValue() + 1);
	}

	/**
	 * Anschrifttyp als P,G,S zur�ckgeben.
	 * 
	 * Vorgabe aus PersonDoctype, �berschreibbar in GuestDoctype
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
	 * Zeichensatz als L,F1,F2 zur�ckgeben.
	 * 
	 * Vorgabe aus PersonDoctype, �berschreibbar in GuestDoctype
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
}
