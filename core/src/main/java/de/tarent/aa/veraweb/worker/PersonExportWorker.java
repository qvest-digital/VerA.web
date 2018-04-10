package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
import de.tarent.aa.veraweb.beans.Grants;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.utils.ExportHelper;
import de.tarent.aa.veraweb.utils.OctopusHelper;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.commons.spreadsheet.export.SpreadSheet;
import de.tarent.commons.spreadsheet.export.SpreadSheetFactory;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.response.TcBinaryResponseEngine;
import de.tarent.octopus.server.OctopusContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Dieser Octopus-Worker dient dem Export von Personendaten.
 */
public class PersonExportWorker extends PersonListWorker {
    /**
     * Logger dieser Klasse
     */
    private final Logger logger = LogManager.getLogger(getClass());

    /**
     * Filtert nach der aktuellen Selektion oder den vom Benutzer eingegeben Filter.
     */
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
        super.extendWhere(cntx, select);

        List selection = (List) cntx.sessionAsObject("selectionPerson");
        if (selection != null && selection.size() != 0) {
            select.where(Where.and(
                    Expr.in("tperson.pk", selection),
                    getPersonListFilter(cntx, false)));
        }
    }

    //
    // Octopus-Aktionen
    //
    /**
     * Eingabe-Parameter der Octopus-Aktion {@link #exportFormat(OctopusContext)}
     */
    public static final String INPUT_exportFormat[] = {};
    /**
     * Ausgabe-Parameter der Octopus-Aktion {@link #exportFormat(OctopusContext)}
     */
    public static final String OUTPUT_exportFormat = "exportFormat";

    /**
     * Diese Octopus-Aktion liefert die Standard-Dateinamenerweiterung zu dem Format,
     * das beim Standard-Label-Dokumenttyp für einen Spreadsheet-Export eingestellt ist.
     *
     * @param cntx Octopus-Kontext
     * @return Standard-Dateinamenerweiterung
     */
    public String exportFormat(OctopusContext cntx) throws BeanException, IOException {
        return ExportHelper
                .getExtension(SpreadSheetFactory.getSpreadSheet(SpreadSheetFactory.TYPE_CSV_DOCUMENT).getFileExtension());
    }

    /**
     * Eingabe-Parameter der Octopus-Aktion {@link #export(OctopusContext)}
     */
    public static final String INPUT_export[] = {};
    /**
     * Ausgabe-Parameter der Octopus-Aktion {@link #export(OctopusContext)}
     */
    public static final String OUTPUT_export = "stream";

    /**
     * Diese Octopus-Aktion erzeugt ein Spreadsheet passend zum Standard-Label-Dokumenttyp
     * mit den aktuellen Personen.
     *
     * @param cntx Octopus-Kontext
     * @return eine Map mit Einträgen "type", "filename", "mimetype" und "stream" für die
     * {@link TcBinaryResponseEngine}
     */
    public Map export(final OctopusContext cntx)
            throws BeanException, IOException, FactoryConfigurationError, TransformerFactoryConfigurationError {
        final Database database = getDatabase(cntx);
        final SpreadSheet spreadSheet = SpreadSheetFactory.getSpreadSheet(SpreadSheetFactory.TYPE_CSV_DOCUMENT);
        final String filename = OctopusHelper.getFilename(cntx, spreadSheet.getFileExtension(),
                "export." + spreadSheet.getFileExtension());
        final PipedInputStream pis = new PipedInputStream();
        final PipedOutputStream pos = new PipedOutputStream(pis);

        new Thread(new Runnable() {
            public void run() {
                if (logger.isDebugEnabled()) {
                    logger.debug("Personen-Export: Starte das Speichern eines Spreadsheets.");
                }
                try {
                    spreadSheet.init();

                    String memberAEx = null;
                    String memberBEx = null;
                    String addressEx = null;

                    /*
                     * fixing typos here that resulted in that memberBEx and addressEx remained null
                     * cklein 2008-03-26
                     */
                    if (memberAEx == null) {
                        memberAEx = "_a_e1";
                    }
                    if (memberBEx == null) {
                        memberBEx = "_b_e1";
                    }
                    if (addressEx == null) {
                        addressEx = "_a_e1";
                    }

                    // Tabelle öffnen und erste Zeile schreiben
                    spreadSheet.openTable("Gäste", 65);
                    spreadSheet.openRow();
                    exportHeader(spreadSheet, cntx);
                    spreadSheet.closeRow();

                    // Export-Select zusammenbauen
                    Select select = database.getSelect(BEANNAME);
                    extendColumns(cntx, select);
                    extendWhere(cntx, select);

                    // Export-Select ausführen
                    /*
                     * fixing exportSelect tries to access the current octopus context which tries to get itself
                     * from the thread local map of this thread which is not an octopus controlled thread
                     * cklein 2008-03-26
                     */
                    exportSelect(spreadSheet, database, ((PersonalConfigAA) cntx.personalConfig()).getGrants(),
                            select, memberAEx, memberBEx, addressEx);

                    // Tabelle schließen
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
                if (logger.isDebugEnabled()) {
                    logger.debug("Personen-Export: Beende das Speichern eines Spreadsheets.");
                }
            }
        }).start();

        // Stream-Informationen zurück geben
        Map stream = new HashMap();
        stream.put(TcBinaryResponseEngine.PARAM_TYPE, TcBinaryResponseEngine.BINARY_RESPONSE_TYPE_STREAM);
        stream.put(TcBinaryResponseEngine.PARAM_FILENAME, ExportHelper.getFilename(filename));
        stream.put(TcBinaryResponseEngine.PARAM_MIMETYPE, ExportHelper.getContentType(spreadSheet.getContentType()));
        stream.put(TcBinaryResponseEngine.PARAM_STREAM, pis);
        stream.put(TcBinaryResponseEngine.PARAM_IS_ATTACHMENT, Boolean.TRUE);
        return stream;
    }

    /**
     * Schreibt die überschriften des Export-Dokumentes.
     *
     * @param spreadSheet {@link SpreadSheet}, in das geschrieben werden soll.
     */
    protected void exportHeader(SpreadSheet spreadSheet, OctopusContext octopusContext) {

        LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
        LanguageProvider languageProvider = languageProviderHelper.enableTranslation(octopusContext);
        //
        // Gast spezifische Daten
        //

        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_ADDRESS"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_CHARSET"));
        spreadSheet.addCell(languageProvider.getProperty("INTERNAL_ID"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_FUNCTION"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_TITLE"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_ACADEMIC_TITLE"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_FIRSTNAME"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_LASTNAME"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_PARTNER_TITLE"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_PARTNER_ACADEMIC_TITLE"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_PARTNER_FIRSTNAME"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_PARTNER_LASTNAME"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_POSTALCODE"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_CITY"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_STREET"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_COUNTRY"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_ADDITIONAL_ADDRESS_ONE"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_ADDITIONAL_ADDRESS_TWO"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_CITY_OF_BORN"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_TELEPHONE"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_FAX"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_EMAIL"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_WWW"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_MOBILE"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_COMPANY"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_POST_OFFICE_BOX_NUMBER"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_POST_OFFICE_BOX_PLZ"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_IN_COUNTRY"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_LANGUAGES"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_SEX"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_NATIONALITY"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_HINT_RESPONSIBLE"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_HINT_ORG_TEAM"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_PARTNER_IN_COUNTRY"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_PARTNER_LANGUAGES"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_PARTNER_SEX"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_PARTNER_NATIONALITY"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_PARTNER_HINT_RESPONSIBLE"));
        spreadSheet.addCell(languageProvider.getProperty("EXPORT_HEADER_PARTNER_HINT_ORG_TEAM"));
    }

    /**
     * Diese Methode exportiert die Personen aus dem übergebenen Select in das
     * übergebene Spreadsheet
     *
     * @param spreadSheet {@link SpreadSheet}, in das exportiert werden soll.
     * @param database    Datenbank, in der das Select ausgeführt werden soll.
     *                    gemeinsamen oder in zwei getrennten Zeilen exportiert werden sollen.
     * @param select      Select-Statement, das die zu exportierenden Personen selektiert.
     * @param memberAEx   Attributschlüsselsuffix der Hauptperson
     * @param memberBEx   Attributschlüsselsuffix der Partnerperson
     * @param addressEx   Attributschlüsselsuffix der Adressdaten
     */
    protected void exportSelect(SpreadSheet spreadSheet, Database database, Grants grants, Select select, String memberAEx,
            String memberBEx,
            String addressEx) throws BeanException {
        try {
            for (Iterator it = (new ResultList(select.executeSelect(database).resultSet())).iterator(); it.hasNext(); ) {
                Map person = (Map) it.next();

                boolean showA =
                        (person.get("lastname_a_e1") != null && ((String) person.get("lastname_a_e1")).length() != 0) ||
                                (person.get("lastname_a_e2") != null && ((String) person.get("lastname_a_e2")).length() != 0) ||
                                (person.get("lastname_a_e3") != null && ((String) person.get("lastname_a_e3")).length() != 0) ||
                                (person.get("firstname_a_e1") != null && ((String) person.get("firstname_a_e1")).length() != 0) ||
                                (person.get("firstname_a_e2") != null && ((String) person.get("firstname_a_e2")).length() != 0) ||
                                (person.get("firstname_a_e3") != null && ((String) person.get("firstname_a_e3")).length() != 0);

                boolean showB =
                        (person.get("lastname_b_e1") != null && ((String) person.get("lastname_b_e1")).length() != 0) ||
                                (person.get("lastname_b_e2") != null && ((String) person.get("lastname_b_e2")).length() != 0) ||
                                (person.get("lastname_b_e3") != null && ((String) person.get("lastname_b_e3")).length() != 0) ||
                                (person.get("firstname_b_e1") != null && ((String) person.get("firstname_b_e1")).length() != 0) ||
                                (person.get("firstname_b_e2") != null && ((String) person.get("firstname_b_e2")).length() != 0) ||
                                (person.get("firstname_b_e3") != null && ((String) person.get("firstname_b_e3")).length() != 0);
                boolean showRemarks = grants.mayReadRemarkFields();

                // Gleiches Dokument
                if (showA && showB) {
                    spreadSheet.openRow();
                    exportBothInOneLine(spreadSheet, showA, showB, showRemarks, person, memberAEx, memberBEx, addressEx);
                    spreadSheet.closeRow();
                } else if (showA) {
                    spreadSheet.openRow();
                    exportOnlyPerson(spreadSheet, showRemarks, person, memberAEx, addressEx);
                    spreadSheet.closeRow();
                } else if (showB) {
                    spreadSheet.openRow();
                    exportOnlyPartner(spreadSheet, showRemarks, person, memberBEx, addressEx);
                    spreadSheet.closeRow();
                }
            }
        } catch (SQLException e) {
            throw new BeanException("Der Export kann nicht ausgef\u00fchrt werden.", e);
        }
    }

    /**
     * Export die Gast- und Partner Daten in eine Zeile.
     *
     * @param spreadSheet In das geschrieben werden soll.
     * @param person      Map mit den Gastdaten.
     */
    protected void exportBothInOneLine(SpreadSheet spreadSheet, boolean showA, boolean showB, boolean showRemarks, Map person,
            String memberAEx,
            String memberBEx, String addressEx) {

        spreadSheet.addCell(ExportHelper.getAddresstype((Integer) person.get("addresstype")));
        spreadSheet.addCell(ExportHelper.getLocale((Integer) person.get("locale")));
        spreadSheet.addCell(person.get("internal_id"));

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

        //
        // Veranstaltungsspezifische Attribute für Person
        //
        if (showA) {
            spreadSheet.addCell(getDomestic((String) person.get("domestic_a_e1"))); // Inland Ja / Nein
            spreadSheet.addCell(person.get("languages_a_e1"));
            spreadSheet.addCell(ExportHelper.getGender((String) person.get("sex_a_e1"))); // M oder F
            spreadSheet.addCell(person.get("nationality_a_e1"));
            spreadSheet.addCell(person.get("notehost_a_e1"));
            spreadSheet.addCell(person.get("noteorga_a_e1"));
        } else {
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
            spreadSheet.addCell(getDomestic((String) person.get("domestic_b_e1"))); // Inland Ja / Nein
            spreadSheet.addCell(person.get("languages_b_e1"));
            spreadSheet.addCell(ExportHelper.getGender((String) person.get("sex_b_e1"))); // M oder F
            spreadSheet.addCell(person.get("nationality_b_e1"));
            spreadSheet.addCell(showRemarks ? person.get("notehost_b_e1") : null);
            spreadSheet.addCell(showRemarks ? person.get("noteorga_b_e1") : null);
        } else {
            spreadSheet.addCell(null);
            spreadSheet.addCell(null);
            spreadSheet.addCell(null);
            spreadSheet.addCell(null);
            spreadSheet.addCell(null);
            spreadSheet.addCell(null);
        }
    }

    /**
     * Export ausschließlich die Gast-Daten in eine Zeile.
     *
     * @param spreadSheet In das geschrieben werden soll.
     * @param person      Map mit den Gastdaten.
     */
    protected void exportOnlyPerson(SpreadSheet spreadSheet, boolean showRemarks, Map person, String memberAEx,
            String addressEx) {

        spreadSheet.addCell(ExportHelper.getAddresstype((Integer) person.get("addresstype")));
        spreadSheet.addCell(ExportHelper.getLocale((Integer) person.get("locale")));
        spreadSheet.addCell(person.get("internal_id"));

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

        spreadSheet.addCell(person.get("birthplace" + addressEx));
        spreadSheet.addCell(person.get("fon" + addressEx));
        spreadSheet.addCell(person.get("fax" + addressEx));
        spreadSheet.addCell(person.get("mail" + addressEx));
        spreadSheet.addCell(person.get("url" + addressEx));
        spreadSheet.addCell(person.get("mobil" + addressEx));
        spreadSheet.addCell(person.get("company" + addressEx));
        spreadSheet.addCell(person.get("pobox" + addressEx));
        spreadSheet.addCell(person.get("poboxzipcode" + addressEx));

        //
        // Veranstaltungsspezifische Attribute für Person
        //
        spreadSheet.addCell(getDomestic((String) person.get("domestic_a_e1"))); // Inland Ja / Nein
        spreadSheet.addCell(person.get("languages_a_e1"));
        spreadSheet.addCell(ExportHelper.getGender((String) person.get("sex_a_e1"))); // M oder F
        spreadSheet.addCell(person.get("nationality_a_e1"));
        spreadSheet.addCell(showRemarks ? person.get("notehost_a_e1") : null);
        spreadSheet.addCell(showRemarks ? person.get("noteorga_a_e1") : null);

        //
        // Veranstaltungsspezifische Attribute für Partner der Person
        //
        spreadSheet.addCell(null);
        spreadSheet.addCell(null);
        spreadSheet.addCell(null);
        spreadSheet.addCell(null);
        spreadSheet.addCell(null);
        spreadSheet.addCell(null);
    }

    /**
     * Export ausschließlich die Partner-Daten in eine Zeile.
     *
     * @param spreadSheet In das geschrieben werden soll.
     * @param person      Map mit den Personendaten.
     */
    protected void exportOnlyPartner(SpreadSheet spreadSheet, boolean showRemarks, Map person, String memberBEx,
            String addressEx) {
        spreadSheet.addCell(ExportHelper.getAddresstype((Integer) person.get("addresstype")));
        spreadSheet.addCell(ExportHelper.getLocale((Integer) person.get("locale")));

        spreadSheet.addCell(null);
        spreadSheet.addCell(null);
        spreadSheet.addCell(null);
        spreadSheet.addCell(null);
        spreadSheet.addCell(null);

        spreadSheet.addCell(person.get("salutation" + memberBEx));
        spreadSheet.addCell(person.get("title" + memberBEx));
        spreadSheet.addCell(person.get("firstname" + memberBEx));
        spreadSheet.addCell(person.get("lastname" + memberBEx));

        spreadSheet.addCell(person.get("zipcode" + addressEx));
        spreadSheet.addCell(person.get("city" + addressEx));
        spreadSheet.addCell(person.get("street" + addressEx));
        spreadSheet.addCell(person.get("country" + addressEx));
        spreadSheet.addCell(person.get("suffix1" + addressEx));
        spreadSheet.addCell(person.get("suffix2" + addressEx));

        spreadSheet.addCell(null);
        spreadSheet.addCell(person.get("fon" + addressEx));
        spreadSheet.addCell(person.get("fax" + addressEx));
        spreadSheet.addCell(person.get("mail" + addressEx));
        spreadSheet.addCell(person.get("url" + addressEx));
        spreadSheet.addCell(person.get("mobil" + addressEx));
        spreadSheet.addCell(person.get("company" + addressEx));
        spreadSheet.addCell(person.get("pobox" + addressEx));
        spreadSheet.addCell(person.get("poboxzipcode" + addressEx));

        //
        // Veranstaltungsspezifische Attribute für Person
        //
        spreadSheet.addCell(null);
        spreadSheet.addCell(null);
        spreadSheet.addCell(null);
        spreadSheet.addCell(null);
        spreadSheet.addCell(null);
        spreadSheet.addCell(null);

        //
        // Veranstaltungsspezifische Attribute für Partner der Person
        //
        spreadSheet.addCell(getDomestic((String) person.get("domestic_b_e1"))); // Inland Ja / Nein
        spreadSheet.addCell(person.get("languages_b_e1"));
        spreadSheet.addCell(ExportHelper.getGender((String) person.get("sex_b_e1"))); // M oder F
        spreadSheet.addCell(person.get("nationality_b_e1"));
        spreadSheet.addCell(showRemarks ? person.get("notehost_b_e1") : null);
        spreadSheet.addCell(showRemarks ? person.get("noteorga_b_e1") : null);
    }

    /**
     * Diese Methode liefert eine String-Darstellung zu einem Domestic-Attribut
     */
    private String getDomestic(String domestic) {
        return PersonConstants.DOMESTIC_AUSLAND.equals(domestic) ? "Nein" : "Ja";
    }
}
