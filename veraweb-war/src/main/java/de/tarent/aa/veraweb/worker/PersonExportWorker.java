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

/* $Id$ */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.log4j.Logger;

import de.tarent.aa.veraweb.beans.Color;
import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.Grants;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.utils.ExportHelper;
import de.tarent.aa.veraweb.utils.OctopusHelper;
import de.tarent.commons.spreadsheet.export.SpreadSheet;
import de.tarent.commons.spreadsheet.export.SpreadSheetFactory;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.response.TcBinaryResponseEngine;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker dient dem Export von Personendaten.
 */
public class PersonExportWorker extends PersonListWorker {
    /** Logger dieser Klasse */
	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * Filtert nach der aktuellen Selektion oder den vom Benutzer eingegeben Filter.
	 */
	protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
		super.extendWhere(cntx, select);
		
		List selection = (List)cntx.sessionAsObject("selectionPerson");
		if (selection != null && selection.size() != 0) {
			select.where(Where.and(
					Expr.in("tperson.pk", selection),
					getPersonListFilter(cntx)));
		}
	}

    //
    // Octopus-Aktionen
    //
    /** Eingabe-Parameter der Octopus-Aktion {@link #exportFormat(OctopusContext)} */
	public static final String INPUT_exportFormat[] = {};
    /** Ausgabe-Parameter der Octopus-Aktion {@link #exportFormat(OctopusContext)} */
	public static final String OUTPUT_exportFormat = "exportFormat";
	/**
     * Diese Octopus-Aktion liefert die Standard-Dateinamenerweiterung zu dem Format,
     * das beim Standard-Label-Dokumenttyp f�r einen Spreadsheet-Export eingestellt ist.
     * 
     * @param cntx Octopus-Kontext
     * @return Standard-Dateinamenerweiterung
	 */
	public String exportFormat(OctopusContext cntx) throws BeanException, IOException {
		Doctype doctype = getExportDoctype(cntx);
		if (doctype == null) {
			logger.error("Export von Personendaten kann nicht angeboten werden, es wurde kein Dokumenttyp konfiguriert.");
			return null;
		}
		return ExportHelper.getExtension(SpreadSheetFactory.getSpreadSheet(doctype.format).getFileExtension());
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #export(OctopusContext)} */
	public static final String INPUT_export[] = {};
    /** Ausgabe-Parameter der Octopus-Aktion {@link #export(OctopusContext)} */
	public static final String OUTPUT_export = "stream";
	/**
     * Diese Octopus-Aktion erzeugt ein Spreadsheet passend zum Standard-Label-Dokumenttyp
     * mit den aktuellen Personen.
     * 
     * @param cntx Octopus-Kontext
     * @return eine Map mit Eintr�gen "type", "filename", "mimetype" und "stream" f�r die
     *  {@link TcBinaryResponseEngine}
	 */
	public Map export(final OctopusContext cntx) throws BeanException, IOException, FactoryConfigurationError, TransformerFactoryConfigurationError {
		final Database database = getDatabase(cntx);
		final Doctype doctype = getExportDoctype(cntx);
		if (doctype == null) {
			logger.error("Konnte G�steliste nicht exportieren, es wurde kein Dokumenttyp konfiguriert.");
			throw new BeanException("Konnte G�steliste nicht exportieren, es wurde kein Dokumenttyp konfiguriert.");
		}
		
		final SpreadSheet spreadSheet = SpreadSheetFactory.getSpreadSheet(doctype.format);
		final String filename = OctopusHelper.getFilename(cntx, spreadSheet.getFileExtension(), "export." + spreadSheet.getFileExtension());
        final PipedInputStream pis = new PipedInputStream();
        final PipedOutputStream pos = new PipedOutputStream(pis);
        
        new Thread(new Runnable() {
        	public void run() {
        		if (logger.isDebugEnabled())
        			logger.debug("Personen-Export: Starte das Speichern eines Spreadsheets.");
        		try {
        			spreadSheet.init();
        			
        			String memberAEx = null;
        			String memberBEx = null;
        			String addressEx = null;
        			if (doctype.locale != null && doctype.addresstype != null) {
        				if (doctype.locale.intValue() == PersonConstants.LOCALE_LATIN) {
        					memberAEx = "_a_e1";
        					memberBEx = "_b_e1";
        					if (doctype.addresstype.intValue() == PersonConstants.ADDRESSTYPE_BUSINESS) {
        						addressEx = "_a_e1";
        					} else if (doctype.addresstype.intValue() == PersonConstants.ADDRESSTYPE_PRIVATE) {
        						addressEx = "_b_e1";
        					} else if (doctype.addresstype.intValue() == PersonConstants.ADDRESSTYPE_OTHER) {
        						addressEx = "_c_e1";
        					}
        				} else if (doctype.locale.intValue() == PersonConstants.LOCALE_EXTRA1) {
        					memberAEx = "_a_e2";
        					memberBEx = "_b_e2";
        					if (doctype.addresstype.intValue() == PersonConstants.ADDRESSTYPE_BUSINESS) {
        						addressEx = "_a_e2";
        					} else if (doctype.addresstype.intValue() == PersonConstants.ADDRESSTYPE_PRIVATE) {
        						addressEx = "_b_e2";
        					} else if (doctype.addresstype.intValue() == PersonConstants.ADDRESSTYPE_OTHER) {
        						addressEx = "_c_e2";
        					}
        				} else if (doctype.locale.intValue() == PersonConstants.LOCALE_EXTRA2) {
        					memberAEx = "_a_e3";
        					memberBEx = "_b_e3";
        					if (doctype.addresstype.intValue() == PersonConstants.ADDRESSTYPE_BUSINESS) {
        						addressEx = "_a_e3";
        					} else if (doctype.addresstype.intValue() == PersonConstants.ADDRESSTYPE_PRIVATE) {
        						addressEx = "_b_e3";
        					} else if (doctype.addresstype.intValue() == PersonConstants.ADDRESSTYPE_OTHER) {
        						addressEx = "_c_e3";
        					}
        				}
        			}
        			/*
        			 * fixing typos here that resulted in that memberBEx and addressEx remained null
        			 * cklein 2008-03-26
        			 */
        			if (memberAEx == null) memberAEx = "_a_e1";
        			if (memberBEx == null) memberBEx = "_b_e1";
        			if (addressEx == null) addressEx= "_a_e1";
        			
        			// Tabelle �ffnen und erste Zeile schreiben
        			spreadSheet.openTable("Gäste", 65);
        			spreadSheet.openRow();
        			exportHeader(spreadSheet);
        			spreadSheet.closeRow();
        			
        			// Zusatzinformationen
        			Map data = new HashMap();
        			data.put("doctype", doctype.name);
        			data.put("color1", database.getBean("Color", new Integer(1)));
        			data.put("color2", database.getBean("Color", new Integer(2)));
        			data.put("color3", database.getBean("Color", new Integer(3)));
        			data.put("color4", database.getBean("Color", new Integer(4)));
        			
        			// Export-Select zusammenbauen
        			Select select = database.getSelect(BEANNAME);
        			extendColumns(cntx, select);
        			extendWhere(cntx, select);
        			
        			select.joinLeftOuter("veraweb.tperson_doctype", "tperson_doctype.fk_person", "tperson.pk");
        			select.whereAnd( new RawClause( "tperson_doctype.fk_doctype = " + doctype.id ) );
        			select.select("textfield");
        			select.select("textfield_p");
        			select.select("textjoin");
        			select.select("addresstype");
        			select.select("locale");
        			
        			// Export-Select ausf�hren
        			/*
        			 * fixing exportSelect tries to access the current octopus context which tries to get itself
        			 * from the thread local map of this thread which is not an octopus controlled thread
        			 * cklein 2008-03-26
        			 */
        			exportSelect(
        					spreadSheet, database, ((PersonalConfigAA)cntx.personalConfig()).getGrants(),
        					doctype, select, data, memberAEx, memberBEx, addressEx);
        			
        			// Tabelle schlie�en
        			spreadSheet.closeTable();
        			
        			// SpreadSheet speichern
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
        			logger.debug("Personen-Export: Beende das Speichern eines Spreadsheets.");
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

	/**
	 * Schreibt die �berschriften des Export-Dokumentes.
	 * 
	 * @param spreadSheet {@link SpreadSheet}, in das geschrieben werden soll.
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
		
		/*
		 * modified to support workareas as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell("Arbeitsbereich");
		//
		// Kategorie spezifische Daten, wenn nach Kategorie gefilter wurde.
		//
		spreadSheet.addCell("Kategorie"); // Verweis auf Kategorie, die zur Auswahl f�hrte
		spreadSheet.addCell("Kategorie_Rang"); // Der Rang der Kategorie innerhalb der Kategorien
		spreadSheet.addCell("Rang"); // Der Rang der Person innerhalb der Kategorie
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
		spreadSheet.addCell("Anzahl_Gaeste_auf_Platz");
		spreadSheet.addCell("Anzahl_Gaeste_auf_Reserve");
		spreadSheet.addCell("Veranstaltungsname");
		spreadSheet.addCell("Veranstaltung_Beginn");
		spreadSheet.addCell("Veranstaltung_Ende");
		spreadSheet.addCell("Ort_der_Veranstaltung");
		spreadSheet.addCell("Bemerkung");
	}

    /**
     * Diese Methode exportiert die Personen aus dem �bergebenen Select in das
     * �bergebene Spreadsheet  
     * 
     * @param spreadSheet {@link SpreadSheet}, in das exportiert werden soll.
     * @param database Datenbank, in der das Select ausgef�hrt werden soll.
     * @param doctype Dokumenttyp, der vorgibt, ob Hauptperson und Partner in einem
     *  gemeinsamen oder in zwei getrennten Zeilen exportiert werden sollen.
     * @param select Select-Statement, das die zu exportierenden Personen selektiert.
     * @param data Zusatzdaten unter den Schl�sseln "doctype", "color1", "color2",
     *  "color3" und "color4".
     * @param memberAEx Attributschl�sselsuffix der Hauptperson
     * @param memberBEx Attributschl�sselsuffix der Partnerperson
     * @param addressEx Attributschl�sselsuffix der Adressdaten
     */
	protected void exportSelect(SpreadSheet spreadSheet, Database database, Grants grants, Doctype doctype, Select select, Map data, String memberAEx, String memberBEx, String addressEx) throws BeanException {
		try
		{
			for (Iterator it = ( new ResultList( select.executeSelect( database ).resultSet() ) ).iterator(); it.hasNext(); ) {
				Map person = (Map)it.next();
				
				boolean showA =
					(person.get("lastname_a_e1") != null && ((String)person.get("lastname_a_e1")).length() != 0) ||
					(person.get("lastname_a_e2") != null && ((String)person.get("lastname_a_e2")).length() != 0) ||
					(person.get("lastname_a_e3") != null && ((String)person.get("lastname_a_e3")).length() != 0) ||
					(person.get("firstname_a_e1") != null && ((String)person.get("firstname_a_e1")).length() != 0) ||
					(person.get("firstname_a_e2") != null && ((String)person.get("firstname_a_e2")).length() != 0) ||
					(person.get("firstname_a_e3") != null && ((String)person.get("firstname_a_e3")).length() != 0);
	
				boolean showB =
					(person.get("lastname_b_e1") != null && ((String)person.get("lastname_b_e1")).length() != 0) ||
					(person.get("lastname_b_e2") != null && ((String)person.get("lastname_b_e2")).length() != 0) ||
					(person.get("lastname_b_e3") != null && ((String)person.get("lastname_b_e3")).length() != 0) ||
					(person.get("firstname_b_e1") != null && ((String)person.get("firstname_b_e1")).length() != 0) ||
					(person.get("firstname_b_e2") != null && ((String)person.get("firstname_b_e2")).length() != 0) ||
					(person.get("firstname_b_e3") != null && ((String)person.get("firstname_b_e3")).length() != 0);
				boolean showRemarks = grants.mayReadRemarkFields();
				
				if (doctype.partner != null && doctype.partner.booleanValue()) {
					// Eigenes Dokument
					if (showA) {
						spreadSheet.openRow();
						exportOnlyPerson(spreadSheet, showA, showB, showRemarks, person, data, memberAEx, memberBEx, addressEx);
						spreadSheet.closeRow();
					}
					if (showB) {
						spreadSheet.openRow();
						exportOnlyPartner(spreadSheet, showA, showB, showRemarks, person, data, memberAEx, memberBEx, addressEx);
						spreadSheet.closeRow();
					}
				} else {
					// Gleiches Dokument
					if (showA && showB) {
						spreadSheet.openRow();
						exportBothInOneLine(spreadSheet, showA, showB, showRemarks, person, data, memberAEx, memberBEx, addressEx);
						spreadSheet.closeRow();
					} else if (showA) {
						spreadSheet.openRow();
						exportOnlyPerson(spreadSheet, showA, showB, showRemarks, person, data, memberAEx, memberBEx, addressEx);
						spreadSheet.closeRow();
					} else if (showB) {
						spreadSheet.openRow();
						exportOnlyPartner(spreadSheet, showA, showB, showRemarks, person, data, memberAEx, memberBEx, addressEx);
						spreadSheet.closeRow();
					}
				}
			}
		}
		catch( SQLException e )
		{
			throw new BeanException( "Der Export kann nicht ausgeführt werden.", e );
		}
	}

	/**
	 * Export die Gast- und Partner Daten in eine Zeile.
	 * 
	 * @param spreadSheet In das geschrieben werden soll.
	 * @param person Map mit den Gastdaten.
	 * @param data Zusatzinformationen.
	 */
	protected void exportBothInOneLine(SpreadSheet spreadSheet, boolean showA, boolean showB, boolean showRemarks, Map person, Map data, String memberAEx, String memberBEx, String addressEx) {
		//
		// Gast spezifische Daten
		//
		spreadSheet.addCell(data.get("doctype")); // Name des Dokument-Typs
		
		String text_a = (String)person.get("textfield");
		String text_b = (String)person.get("textfield_p");
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
			spreadSheet.addCell(person.get("textjoin"));
		} else {
			spreadSheet.addCell(null);
		}
		
		spreadSheet.addCell(GuestExportWorker.getAddresstype((Integer)person.get("addresstype")));
		spreadSheet.addCell(GuestExportWorker.getLocale((Integer)person.get("locale")));
		
		if (showA) {
			spreadSheet.addCell(person.get("function" + addressEx));
			spreadSheet.addCell(person.get("salutation" + memberAEx));
			spreadSheet.addCell(person.get("title" + memberAEx));
			spreadSheet.addCell(person.get("firstname" + memberAEx));
			spreadSheet.addCell(person.get("lastname" + memberAEx));
		} else {
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
		}
		
		if (showB) {
			spreadSheet.addCell(person.get("salutation" + memberBEx));
			spreadSheet.addCell(person.get("title" + memberBEx));
			spreadSheet.addCell(person.get("firstname" + memberBEx));
			spreadSheet.addCell(person.get("lastname" + memberBEx));
		} else {
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
		}
		
		spreadSheet.addCell(person.get("zipcode" + addressEx));
		spreadSheet.addCell(person.get("city" + addressEx));
		spreadSheet.addCell(person.get("street" + addressEx));
		spreadSheet.addCell(person.get("country" + addressEx));
		spreadSheet.addCell(person.get("suffix1" + addressEx));
		spreadSheet.addCell(person.get("suffix2" + addressEx));
		
		/*
		 * modified to support birthplace as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(person.get("birthplace" + addressEx));
		spreadSheet.addCell(person.get("fon" + addressEx));
		spreadSheet.addCell(person.get("fax" + addressEx));
		spreadSheet.addCell(person.get("mail" + addressEx));
		spreadSheet.addCell(person.get("url" + addressEx));
		spreadSheet.addCell(person.get("mobil" + addressEx));
		spreadSheet.addCell(person.get("company" + addressEx));
		spreadSheet.addCell(person.get("pobox" + addressEx));
		spreadSheet.addCell(person.get("poboxzipcode" + addressEx));

		/*
		 * modified to support work areas as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(null); // Verweis auf Arbeitsbereich, der zur Auswahl führte
		//
		// Kategorie spezifische Daten, wenn nach Kategorie gefilter wurde.
		//
		spreadSheet.addCell(null); // Verweis auf Kategorie, die zur Auswahl f�hrte
		spreadSheet.addCell(null); // Der Rang der Kategorie innerhalb der Kategorien
		spreadSheet.addCell(null); // Der Rang der Person innerhalb der Kategorie
		spreadSheet.addCell(null); // 0 = Tisch, 1 = Reservce
		
		//
		// Veranstaltungsspezifische Attribute f�r Person
		//
		if (showA) {
			spreadSheet.addCell(null); // 0 = Offen, 1 = Zusage, 2 = Absage
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(GuestExportWorker.getColor(getColor(person, data, true))); // Verweiss auf Farbe die verwendet werden soll.
			spreadSheet.addCell(getDomestic((String)person.get("domestic_a_e1"))); // Inland Ja / Nein
			spreadSheet.addCell(person.get("languages_a_e1"));
			spreadSheet.addCell(GuestExportWorker.getGender((String)person.get("sex_a_e1"))); // M oder F
			spreadSheet.addCell(person.get("nationality_a_e1"));
			spreadSheet.addCell(person.get("notehost_a_e1"));
			spreadSheet.addCell(person.get("noteorga_a_e1"));
		} else {
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(GuestExportWorker.getColor(null));
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
			spreadSheet.addCell(null); // 0 = Offen, 1 = Zusage, 2 = Absage
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(GuestExportWorker.getColor(getColor(person, data, false))); // Verweiss auf Farbe die verwendet werden soll.
			spreadSheet.addCell(getDomestic((String)person.get("domestic_b_e1"))); // Inland Ja / Nein
			spreadSheet.addCell(person.get("languages_b_e1"));
			spreadSheet.addCell(GuestExportWorker.getGender((String)person.get("sex_b_e1"))); // M oder F
			spreadSheet.addCell(person.get("nationality_b_e1"));
			spreadSheet.addCell(showRemarks ? person.get("notehost_b_e1") : null);
			spreadSheet.addCell(showRemarks ? person.get("noteorga_b_e1") : null);
		} else {
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(GuestExportWorker.getColor(null));
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
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
	}

	/**
	 * Export ausschlie�lich die Gast-Daten in eine Zeile.
	 * 
	 * @param spreadSheet In das geschrieben werden soll.
	 * @param person Map mit den Gastdaten.
	 * @param data Zusatzinformationen.
	 */
	protected void exportOnlyPerson(SpreadSheet spreadSheet, boolean showA, boolean showB, boolean showRemarks, Map person, Map data, String memberAEx, String memberBEx, String addressEx) {
		//
		// Gast spezifische Daten
		//
		spreadSheet.addCell(data.get("doctype")); // Name des Dokument-Typs
		spreadSheet.addCell(person.get("textfield"));
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		
		spreadSheet.addCell(GuestExportWorker.getAddresstype((Integer)person.get("addresstype")));
		spreadSheet.addCell(GuestExportWorker.getLocale((Integer)person.get("locale")));
		
		spreadSheet.addCell(person.get("function" + addressEx));
		spreadSheet.addCell(person.get("salutation" + memberAEx));
		spreadSheet.addCell(person.get("title" + memberAEx));
		spreadSheet.addCell(person.get("firstname" + memberAEx));
		spreadSheet.addCell(person.get("lastname" + memberAEx));
		
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		
		spreadSheet.addCell(person.get("zipcode" + addressEx));
		spreadSheet.addCell(person.get("city" + addressEx));
		spreadSheet.addCell(person.get("street" + addressEx));
		spreadSheet.addCell(person.get("country" + addressEx));
		spreadSheet.addCell(person.get("suffix1" + addressEx));
		spreadSheet.addCell(person.get("suffix2" + addressEx));

		/*
		 * modified to support birthplace as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(person.get("birthplace" + addressEx));
		spreadSheet.addCell(person.get("fon" + addressEx));
		spreadSheet.addCell(person.get("fax" + addressEx));
		spreadSheet.addCell(person.get("mail" + addressEx));
		spreadSheet.addCell(person.get("url" + addressEx));
		spreadSheet.addCell(person.get("mobil" + addressEx));
		spreadSheet.addCell(person.get("company" + addressEx));
		spreadSheet.addCell(person.get("pobox" + addressEx));
		spreadSheet.addCell(person.get("poboxzipcode" + addressEx));
		
		/*
		 * modified to support work areas as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(null); // Verweis auf Arbeitsbereich, der zur Auswahl führte
		//
		// Kategorie spezifische Daten, wenn nach Kategorie gefilter wurde.
		//
		spreadSheet.addCell(null); // Verweis auf Kategorie, die zur Auswahl f�hrte
		spreadSheet.addCell(null); // Der Rang der Kategorie innerhalb der Kategorien
		spreadSheet.addCell(null); // Der Rang der Person innerhalb der Kategorie
		spreadSheet.addCell(null); // 0 = Tisch, 1 = Reservce
		
		//
		// Veranstaltungsspezifische Attribute f�r Person
		//
		spreadSheet.addCell(null); // 0 = Offen, 1 = Zusage, 2 = Absage
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(GuestExportWorker.getColor(getColor(person, data, true))); // Verweiss auf Farbe die verwendet werden soll.
		spreadSheet.addCell(getDomestic((String)person.get("domestic_a_e1"))); // Inland Ja / Nein
		spreadSheet.addCell(person.get("languages_a_e1"));
		spreadSheet.addCell(GuestExportWorker.getGender((String)person.get("sex_a_e1"))); // M oder F
		spreadSheet.addCell(person.get("nationality_a_e1"));
		spreadSheet.addCell(showRemarks ? person.get("notehost_a_e1") : null);
		spreadSheet.addCell(showRemarks ? person.get("noteorga_a_e1") : null);
		
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
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
	}

	/**
	 * Export ausschlie�lich die Partner-Daten in eine Zeile.
	 * 
	 * @param spreadSheet In das geschrieben werden soll.
	 * @param person Map mit den Personendaten.
	 * @param data Zusatzinformationen.
	 */
	protected void exportOnlyPartner(SpreadSheet spreadSheet, boolean showA, boolean showB, boolean showRemarks, Map person, Map data, String memberAEx, String memberBEx, String addressEx) {
		//
		// Gast spezifische Daten
		//
		spreadSheet.addCell(data.get("doctype")); // Name des Dokument-Typs
		spreadSheet.addCell(person.get("textfield_p"));
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		
		spreadSheet.addCell(GuestExportWorker.getAddresstype((Integer)person.get("addresstype")));
		spreadSheet.addCell(GuestExportWorker.getLocale((Integer)person.get("locale")));
		
		spreadSheet.addCell(null);
		spreadSheet.addCell(person.get("salutation" + memberBEx));
		spreadSheet.addCell(person.get("title" + memberBEx));
		spreadSheet.addCell(person.get("firstname" + memberBEx));
		spreadSheet.addCell(person.get("lastname" + memberBEx));
		
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		
		spreadSheet.addCell(person.get("zipcode" + addressEx));
		spreadSheet.addCell(person.get("city" + addressEx));
		spreadSheet.addCell(person.get("street" + addressEx));
		spreadSheet.addCell(person.get("country" + addressEx));
		spreadSheet.addCell(person.get("suffix1" + addressEx));
		spreadSheet.addCell(person.get("suffix2" + addressEx));

		/*
		 * modified to support birthplace as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(""); // partners do not have a birthplace + addressEx attribute
		spreadSheet.addCell(person.get("fon" + addressEx));
		spreadSheet.addCell(person.get("fax" + addressEx));
		spreadSheet.addCell(person.get("mail" + addressEx));
		spreadSheet.addCell(person.get("url" + addressEx));
		spreadSheet.addCell(person.get("mobil" + addressEx));
		spreadSheet.addCell(person.get("company" + addressEx));
		spreadSheet.addCell(person.get("pobox" + addressEx));
		spreadSheet.addCell(person.get("poboxzipcode" + addressEx));

		// fixing bug here, workarea cell was missing

		spreadSheet.addCell(null);
		//
		// Kategorie spezifische Daten, wenn nach Kategorie gefilter wurde.
		//
		spreadSheet.addCell(null); // Verweis auf Kategorie, die zur Auswahl f�hrte
		spreadSheet.addCell(null); // Der Rang der Kategorie innerhalb der Kategorien
		spreadSheet.addCell(null); // Der Rang der Person innerhalb der Kategorie
		spreadSheet.addCell(null); // 0 = Tisch, 1 = Reservce
		
		//
		// Veranstaltungsspezifische Attribute f�r Person
		//
		spreadSheet.addCell(null); // 0 = Offen, 1 = Zusage, 2 = Absage
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(GuestExportWorker.getColor(getColor(person, data, false))); // Verweiss auf Farbe die verwendet werden soll.
		spreadSheet.addCell(getDomestic((String)person.get("domestic_b_e1"))); // Inland Ja / Nein
		spreadSheet.addCell(person.get("languages_b_e1"));
		spreadSheet.addCell(GuestExportWorker.getGender((String)person.get("sex_b_e1"))); // M oder F
		spreadSheet.addCell(person.get("nationality_b_e1"));
		spreadSheet.addCell(showRemarks ? person.get("notehost_b_e1") : null);
		spreadSheet.addCell(showRemarks ? person.get("noteorga_b_e1") : null);
		
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
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
	}

    /**
     * Diese Methode liefert eine RGB-Farbe zu einer Person. 
     * 
     * @param person Person, zu der die Farbe gesucht wird.
     * @param data Zusatzdaten unter den Schl�sseln "color1", "color2",
     *  "color3" und "color4".
     * @param main Flag, ob es um die Hauptperson (<code>true</code>) oder
     *  den Partner (<code>false</code>) geht
     * @return RGB-Wert einer Farbe
     */
	private Integer getColor(Map person, Map data, boolean main) {
		Color color = null;
		if (main) {
			color = (Color)data.get("color" + ColorWorker.getColor((String)person.get("domestic_a_e1"), (String)person.get("sex_a_e1")));
		} else {
			color = (Color)data.get("color" + ColorWorker.getColor((String)person.get("domestic_b_e1"), (String)person.get("sex_b_e1")));
		}
		return color == null ? null : color.rgb;
	}

    /** Diese Methode liefert eine String-Darstellung zu einem Domestic-Attribut */
	private String getDomestic(String domestic) {
		return PersonConstants.DOMESTIC_AUSLAND.equals(domestic) ? "Nein" : "Ja";
	}

	/**
	 * L�dt den Dokumenttypen der zum Personen-Export verwendet werden soll,
	 * hierf�r wird der Standard-Dokumenttyp verwendet, ist dieser nicht
	 * gesetzt wird der Dokumenttyp des Freitextfeldes zur�ck gegeben.
	 * 
	 * @param cntx
	 * @return
	 * @throws BeanException
	 * @throws IOException
	 */
	private Doctype getExportDoctype(OctopusContext cntx) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		Doctype doctype = new Doctype();
		doctype.isdefault = Boolean.TRUE;
		doctype = (Doctype)
				database.getBean("Doctype",
				database.getSelect(doctype).where(
				database.getWhere(doctype)));
		
		if (doctype == null) {
			doctype = (Doctype)
					getDatabase(cntx).getBean("Doctype",
					ConfigWorker.getInteger(cntx, "freitextfeld"));
			if (doctype == null) {
				logger.fatal("Es konnte weder ein Standard noch ein " +
						"Freitextfeld Dokumenttyp gefunden!");
			} else {
				logger.warn("Es wurde kein Standard Dokumenttyp gefunden, " +
						"es wird automatisch der Dokumenttyp des " +
						"Freitextfeldes (Anzeige) verwendet: '" +
						doctype.name + "'");
			}
		}
		
		return doctype;
	}
}
