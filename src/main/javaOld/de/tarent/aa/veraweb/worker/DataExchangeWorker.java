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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.tarent.aa.veraweb.beans.Import;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.utils.AlternativeDestination;
import de.tarent.aa.veraweb.utils.Exporter;
import de.tarent.aa.veraweb.utils.Importer;
import de.tarent.aa.veraweb.utils.MultiOutputStream;
import de.tarent.aa.veraweb.utils.OctopusHelper;
import de.tarent.aa.veraweb.utils.VerawebDigester;
import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.data.exchange.Exchanger;
import de.tarent.dblayer.engine.Result;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.DatabaseUtilizer;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.config.TcPersonalConfig;
import de.tarent.octopus.content.TcContentProzessException;
import de.tarent.octopus.exchange.ConfiguredExchangeFormat;
import de.tarent.octopus.response.TcBinaryResponseEngine;
import de.tarent.octopus.server.Context;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Klasse stellt einen Octopus-Worker für den Im- und Export von
 * Personendaten im VerA.web-eigenen Format dar.
 *
 * @author mikel
 */
public class DataExchangeWorker {
    //
    // Konstanten
    //
    /** Vorgabewert für den Parameter <code>formatEnumKey</code> von {@link #getFormats(OctopusContext, String)} */
    public final static String KEY_FORMAT_NAMES = "exchangeFormats";

    /** Exportfilterwert: Filtrierung nach Kategorie */
    public final static String EXPORT_FILTER_CATEGORY = "category";

    /** Exportfilterwert: Filtrierung nach Veranstaltung */
    public final static String EXPORT_FILTER_EVENT = "event";

    /** Parameterwert: beliebige Personen */
    public final static String PARAM_DOMAIN_VALUE_ALL = "all";
    /** Parameterwert: Personen des gleichen Mandanten */
    public final static String PARAM_DOMAIN_VALUE_OU = "ou";


    //
    // Octopus-Aktionen
    //
    /** Octopus-Eingabe-Parameter für {@link #getFormats(OctopusContext, String)} */
    public static final String[] INPUT_getFormats = { "formatEnumKey" };
    /** Octopus-Eingabepflicht-Parameter für {@link #getFormats(OctopusContext, String)} */
    public static final boolean[] MANDATORY_getFormats = { false };
    /** Octopus-Ausgabe-Parameter für {@link #getFormats(OctopusContext, String)} */
    public static final String OUTPUT_getFormats = "formats";
    /**
     * Diese Octopus-Aktion liefert eine {@link Map} mit verfügbaren Austauschformaten.
     *
     *  führt einen Export von Personendaten durch. Dies geschieht
     * je nach Parameter <code>fieldMapping</code> in eine XML-Darstellung von VerA.web
     * oder eine CSV-Datei. Der Exportdatenstrom wird in den Content geschrieben.
     * Zusätzlich kann er an eine Stelle im Dateisystem kopiert werden.
     *
     * @param cntx Octopus-Kontext
     * @param formatEnumKey optionaler Schlüssel der verfügbaren Schlüssel, Default ist
     *  {@link #KEY_FORMAT_NAMES}.
     * @return Abbildung von Schlüsselbezeichnern auf {@link ExchangeFormat}-Instanzen.
     */
    public Map getFormats(OctopusContext cntx, String formatEnumKey) {
        Map result = Collections.EMPTY_MAP;
        TcModuleConfig moduleConfig = cntx.moduleConfig();
        if (moduleConfig != null) {
            result = new LinkedHashMap();
            Object formatNamesObject = moduleConfig.getParamAsObject(formatEnumKey != null ? formatEnumKey : KEY_FORMAT_NAMES);
            if (formatNamesObject instanceof List) {
                Iterator itFormatNames = ((List) formatNamesObject).iterator();
                while (itFormatNames.hasNext()) {
                    String key = itFormatNames.next().toString();
                    ExchangeFormat format = getExchangeFormat(moduleConfig.getParams(), key, null);
                    if (format != null) {
                        if (logger.isDebugEnabled())
                            logger.debug("Format " + key + ": " + format);
                        result.put(key, format);
                    }
                }
            }
        }
        return result;
    }

    /** Octopus-Eingabe-Parameter für {@link #export(OctopusContext, String, String, Integer, Integer, String)} */
    public static final String[] INPUT_export = { "format", "exportFilter", "exportEvent", "exportCategory", "domain" };
    /** Octopus-Eingabepflicht-Parameter für {@link #export(OctopusContext, String, String, Integer, Integer, String)} */
    public static final boolean[] MANDATORY_export = { true, false, false, false, false };
    /** Octopus-Ausgabe-Parameter für {@link #export(OctopusContext, String, String, Integer, Integer, String)} */
    public static final String OUTPUT_export = "stream";
    /**
     * Diese Octopus-Aktion führt einen Export von Personendaten durch. Dies geschieht
     * in einem konfigurierten Format. Der Exportdatenstrom wird in den Content geschrieben.
     * Zusätzlich kann er an eine Stelle im Dateisystem kopiert werden.
     *
     * @param cntx Octopus-Kontext
     * @param formatKey Schlüssel der Datenformatbeschreibung in der Modulkonfiguration
     * @param filter {@link #EXPORT_FILTER_CATEGORY} oder {@link #EXPORT_FILTER_EVENT},
     *  je nach anzuwendenen Filter
     * @param event Veranstaltungsfilter
     * @param category Kategorienfilter
     * @param domain Domäne, aus der die Personen stammen ("all" oder "ou")
     * @return exportierter Datenstrom
     * @throws TcContentProzessException bei ungültigen Parameterwerten.
     * @throws BeanException
     */
    public Map export(final OctopusContext cntx, final String formatKey, final String filter, final Integer event, final Integer category, final String domain) throws TcContentProzessException, IOException {
        TcModuleConfig moduleConfig = cntx.moduleConfig();
        assert moduleConfig != null;
        // Zunächst mal die benötigten Objekte erstellen
        final ExchangeFormat format = getExchangeFormat(moduleConfig.getParams(), formatKey, cntx.getRequestObject().getRequestParameters());
        if (format == null)
            throw new TcContentProzessException("Unbekannter Exportformatschlüssel '" +  formatKey + "'.");
        final Database database = new DatabaseVeraWeb(cntx);

        final MultiOutputStream mos = new MultiOutputStream();
        final PipedInputStream pis = new PipedInputStream();
        final PipedOutputStream pos = new PipedOutputStream(pis);
        mos.add(pos);

        new Thread(new Runnable() {
        	public void run() {
        		Context.addActive(cntx);

        		Exporter exporter = null;
        		try {
	                exporter = createExporter(format, database, mos);
	                if (exporter instanceof AlternativeDestination) {
	                    AlternativeDestination altdest = (AlternativeDestination) exporter;
	                    mos.add(altdest.getAlternativeOutputStream());
	                }

	                // Mandantenbeschränkung
	                TcPersonalConfig pConfig = cntx.personalConfig();
	                Integer orgUnit = null;
	                if (pConfig instanceof PersonalConfigAA) {
	                    PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
	                    if (!(PARAM_DOMAIN_VALUE_ALL.equals(domain) && pConfig.isUserInGroup(PersonalConfigAA.GROUP_ADMIN)))
	                        orgUnit = aaConfig.getOrgUnitId();
	                } else
	                    throw new BeanException("Missing user information");

	                // Beschränkung auf Kategorie, wenn Benutzer eine ausgewählt hat
	                Integer categoryId = null;
	                if (EXPORT_FILTER_CATEGORY.equals(filter)) // category == null bedeutet: in irgendeiner Kategorie, = 0 bedeutet: in keiner Kategorie
	                	categoryId = category;

	                //Den Exporter auf Mandant und Kategorie einschränken. Ist für den CSV-Exporter notwendig, damit keine überflüssigen überschriften erzeugt werden.
	                exporter.setOrgUnitId(orgUnit);
	                exporter.setCategoryId(categoryId);

	                // Dann exportieren
	                exporter.startExport();
	                if (EXPORT_FILTER_EVENT.equals(filter)) // event == null bedeutet: in irgendeiner Veranstaltung, = 0 bedeutet: in keiner Veranstaltung
	                    exportEvent(database, event, exporter, orgUnit);
	                else if (EXPORT_FILTER_CATEGORY.equals(filter)) // category == null bedeutet: in irgendeiner Kategorie, = 0 bedeutet: in keiner Kategorie
	                    exportCategory(database, category, exporter, orgUnit);
	                else // guter Default?
	                    exportAll(database, exporter, orgUnit);
	                exporter.endExport();
        		} catch (Throwable t) {
        			logger.error("Fehler beim Erstellen des Exports aufgetreten.", t);
        			// This will force a log output.
        			t.printStackTrace(System.out);
        			t.printStackTrace(System.err);
        			mos.close();
        			if (exporter instanceof AlternativeDestination) {
        				((AlternativeDestination)exporter).rollback();
        			}
        		} finally {
       				mos.close();
        		}
        	}
        }).start();

        return createBinaryResponse(getFilename(cntx, format), format.getMimeType(), pis);
    }

    /** Octopus-Eingabe-Parameter für {@link #importToTransit(OctopusContext, Map, String, String, Integer, Map)} */
    public static final String[] INPUT_importToTransit = { "importfile", "format", "importSource", "orgUnit", "CONFIG:importProperties" };
    /** Octopus-Eingabe-Parameter-Pflicht für {@link #importToTransit(OctopusContext, Map, String, String, Integer, Map)} */
    public static final boolean[] MANDATORY_importToTransit = { false, true, true, false, true };
    /** Octopus-Ausgabe-Parameter für {@link #importToTransit(OctopusContext, Map, String, String, Integer, Map)} */
    public static final String OUTPUT_importToTransit = "importStatus";
    /**
     * Diese Octopus-Aktion importiert die Personen einer Datei in den Transit-Bereich,
     * also die Tabelle <code>timportperson</code>.
     *
     * @param cntx Octopus-Kontext
     * @param stream Datei-Upload-Map (enthält unter "ContentStream" einen <code>InputStream</code>)
     * @param formatKey Schlüssel der Datenformatbeschreibung in der Modulkonfiguration
     * @param importSource Importquellenbeschreibung
     * @param orgUnit Ziel-Mandant; nur bei Super-Admins beachtetg
     * @param importProperties Einstellungen zum Import
     * @return Map mit Informationen zum Import, insbesondere der Anzahl gefundener
     *  Datensätze unter "dsCount", der Anzahl Duplikate unter "dupCount", der Anzahl
     *  importierter Datensätze unter "saveCount" und der Import-ID unter "id".
     * @throws IOException
     * @throws TcContentProzessException
     */
    public Map importToTransit(OctopusContext cntx, Map stream, String formatKey, String importSource, Integer orgUnit, Map importProperties) throws BeanException, IOException, TcContentProzessException {
        TcModuleConfig moduleConfig = cntx.moduleConfig();
        assert moduleConfig != null;
        // Zunächst mal die benötigten Objekte erstellen
        ExchangeFormat format = getExchangeFormat(moduleConfig.getParams(), formatKey, cntx.getRequestObject().getRequestParameters());
        if (format == null)
            throw new TcContentProzessException("Unbekannter Importformatschl\u00fcssel '" +  formatKey + "'.");
        if (importSource == null || importSource.length() == 0) {
        	Map status = new HashMap();
        	status.put("invalidData", "importSource");
        	cntx.setStatus("invalidData");
        	return status;
        }

        String filename = (String)stream.get("ContentName");
        if (filename != null && filename.length() != 0) {
        	String suffix = filename.lastIndexOf(".") == -1 ? null :
        		filename.substring(filename.lastIndexOf(".") + 1);
        	if (suffix != null) suffix.toLowerCase();

            if (suffix == null || suffix.length() == 0) {
        		if (logger.isEnabledFor(Level.DEBUG))
        			logger.log(Level.DEBUG, "Endung der Import-Datei '" + filename + "' konnte nicht festgestellt werden.");
        	} else if (
        			suffix.equals("ods") ||
        			suffix.equals("sxc") ||
        			suffix.equals("xls") ||
        			suffix.equals("pdf") ||
        			suffix.equals("zip") ||
        			suffix.equals("exe")) {
            	Map status = new HashMap();
            	status.put("invalidData", "fileExtension");
            	status.put("fileextension", suffix);
            	cntx.setStatus("invalidData");
            	return status;
        	}
        }

        InputStream istream = (InputStream) stream.get("ContentStream");
        if (istream == null || istream.available() <= 0) {
        	Map status = new HashMap();
        	status.put("invalidData", "inputStream");
        	cntx.setStatus("invalidData");
        	return status;
        }

        Database database = new DatabaseVeraWeb(cntx);
        TransactionContext context = database.getTransactionContext();
        try {
	        if (cntx.personalConfig() instanceof PersonalConfigAA) {
	            PersonalConfigAA aaConfig = (PersonalConfigAA)cntx.personalConfig();
	            if (orgUnit == null || orgUnit.intValue() == 0 || !aaConfig.isUserInGroup(PersonalConfigAA.GROUP_ADMIN))
	                orgUnit = aaConfig.getOrgUnitId();
	        } else
	            throw new TcContentProzessException("Fehlende Benutzerinformation.");

	        Importer importer = createImporter(format, context, istream);
	        Import importInstance = createImport(context, formatKey, importSource, orgUnit);
	        VerawebDigester digester = new VerawebDigester(cntx, context, importProperties, importSource, importInstance);

	        importer.importAll(digester);

        	context.commit();

        	// force gc after import
        	System.gc();
            return digester.getImportStats();
        } catch (Exception e) {
        	logger.error("Fehler beim Import aufgetreten.", e);
        	CharArrayWriter caw = new CharArrayWriter();
        	PrintWriter pw = new PrintWriter(caw);
        	e.printStackTrace(pw);
        	pw.close();

        	Map status = new HashMap();
        	status.put("invalidData", "errorOnImport");
        	status.put("exception", caw.toString());
        	status.put("message", e.getLocalizedMessage());
        	cntx.setStatus("invalidData");
        	return status;
        } finally {
        	context.rollBack();
        }
    }

    //
    // geschützte Hilfsmethoden
    //
    /**
     * Diese Methode erstellt eine {@link Map}, aus der die
     * {@link TcBinaryResponseEngine} die Daten für ihre
     * Octopus-Response-Erstellung entnimmt.
     *
     * @param filename Dateiname, den die Response tragen soll
     * @param mimetype MIME-Typ, den die Response haben soll
     * @param inputstream Datenstrom, der die Response bilden soll
     * @return eine {@link Map}, in der die Parameter eingetragen sind
     */
    static Map createBinaryResponse(String filename, String mimetype, InputStream inputstream) {
        Map binaryResponse = new HashMap();
        binaryResponse.put(TcBinaryResponseEngine.PARAM_TYPE, TcBinaryResponseEngine.BINARY_RESPONSE_TYPE_STREAM);
        binaryResponse.put(TcBinaryResponseEngine.PARAM_FILENAME, filename);
        binaryResponse.put(TcBinaryResponseEngine.PARAM_MIMETYPE, mimetype);
        binaryResponse.put(TcBinaryResponseEngine.PARAM_STREAM, inputstream);
        binaryResponse.put(TcBinaryResponseEngine.PARAM_IS_ATTACHMENT, Boolean.TRUE);
        return binaryResponse;
    }

    /**
     * Diese Methode liest aus einer Konfigurations-{@link Map} ein {@link ExchangeFormat}.
     *
     * @param config Konfiguration
     * @param formatKey Schlüssel zum Format
     * @param params {@link Map} mit Parametern, aus denen diejenigen ermittelt werden,
     *  die dem {@link ExchangeFormat} zuzuordnen sind; dies sind genau die, die einen
     *  Schlüssel der Form <code>"format-" + formatKey + '-' + choiceKey</code> haben,
     *  wobei <code>choiceKey</code> über die Schlüssel der {@link Map} iteriert, die
     *  in den {@link ExchangeFormat#getProperties() Properties} unter dem Schlüssel
     *  <code>"choices"</code> liegt. Sie werden diesen Properties unter dem jeweiligen
     *  Schlüssel <code>choiceKey</code> hinzugefügt.
     * @return zugehöriges {@link ExchangeFormat} oder <code>null</code>
     */
    static ExchangeFormat getExchangeFormat(Map config, String formatKey, Map params) {
        if (formatKey == null)
            return null;
        Object formatObject = config.get(formatKey);
        if (!(formatObject instanceof Map))
            return null;
        ConfiguredExchangeFormat format = new ConfiguredExchangeFormat((Map) formatObject);
        Object choicesObject = format.getProperties().get("choices");
        if (choicesObject instanceof Map && params != null) {
            String prefix = "format-" + formatKey + '-';
            Map applicableParams = new HashMap();
            for (Iterator itChoiceKeys = ((Map)choicesObject).keySet().iterator(); itChoiceKeys.hasNext(); ) {
                Object choiceKey = itChoiceKeys.next();
                Object param = params.get(prefix + choiceKey);
                if (param != null)
                    applicableParams.put(choiceKey, param);
            }
            format.addProperties(applicableParams);
        }
        return format;
    }

    /**
     * Diese Methode liefert zu einem Octopus-Kontext den zu einem bestimmten
     * Format passenden Dateinamen für Dateirückgaben.
     *
     * @param octx Octopus-Kontext
     * @param format {@link ExchangeFormat}-Instanz
     * @return Dateiname mit Endung
     */
    static String getFilename(OctopusContext octx, ExchangeFormat format) {
        assert octx != null;
        assert format != null;
        String ext = format.getDefaultExtension();
        if (ext == null)
            ext = "export";
        else if (ext.length() > 0 && ext.charAt(0) == '.')
            ext = ext.substring(1);
        String def = "veraweb." + ext;
        return OctopusHelper.getFilename(octx, ext, def);
    }

    /**
     * Diese Methode erstellt und Initialisiert einen {@link Exporter}.
     *
     * @param format basierendes {@link ExchangeFormat}
     * @param database zu benutzende {@link Database}
     * @return ein passender {@link Exporter}
     * @throws TcContentProzessException bei Fehlern beim Instanziieren des Exporters.
     */
    static Exporter createExporter(ExchangeFormat format, Database database, OutputStream os) throws TcContentProzessException {
        assert format != null;
        assert database != null;
        try {
            Exporter exporter = (Exporter) format.getExporterClass().newInstance();
            if (exporter instanceof Exchanger) {
                Exchanger exchanger = (Exchanger) exporter;
                exchanger.setExchangeFormat(format);
                exchanger.setOutputStream(os);
            }
            if (exporter instanceof DatabaseUtilizer) {
                DatabaseUtilizer dbUtilizer = (DatabaseUtilizer) exporter;
                dbUtilizer.setDatabase(database);
            }
            return exporter;
        } catch (Exception e) {
            throw new TcContentProzessException("Fehler beim Instanziieren des Exporters", e);
        }
    }

    /**
     * Diese Methode exportiert alle Personen, die Gast bei einer bestimmten /
     * irgendeiner / keiner Veranstaltung sind.
     *
     * @param database zu benutzende Datenverbindung
     * @param event Veranstaltung, deren Gäste exportiert werden sollen; <code>null</code>
     *  wird interpretiert als "Personen, die bei irgendeiner Veranstaltung Gast sind",
     *  <code>0</code> als "Personen, die bei keiner Veranstaltung Gast sind".
     * @param exporter zu benutzender {@link Exporter}
     * @param orgUnit Mandanten-ID, wenn danach gefiltert werden soll
     */
    void exportEvent(Database database, Integer event, Exporter exporter, Integer orgUnit) throws BeanException, IOException {
        assert database != null;
        assert exporter != null;
        Bean samplePerson = database.createBean("Person");
        Bean sampleGuest = database.createBean("Guest");

        Select outer = database.getSelect( "Person" );
        Select inner = new Select(false).
        		from(database.getProperty(sampleGuest, "table")).
        		selectAs(database.getProperty(sampleGuest, "person"), "person");

        WhereList outerWhere = new WhereList();
        outerWhere.addAnd(Expr.equal("deleted", PersonConstants.DELETED_FALSE));
        if (orgUnit != null) {
        	outerWhere.addAnd(Expr.equal("tperson.fk_orgunit", orgUnit));
        }

		/*
		 * cklein 2009-07-16: fixes issue 1815 - although option "Alle" yielded no return value
		 * jetty used for testing made it a valid integer object of value 0, which broke
		 * existing code relying on the fact that the request parameter would be null.
		 * Keine/None now equals -1.
		 */
        if (event == null || event.intValue() == 0) {
        	outerWhere.addAnd(Expr.in(
        			database.getProperty(samplePerson, "id"),
        			new RawClause('(' + inner.toString() + ')')));
        } else if (event.intValue() == -1) {
        	outerWhere.addAnd(new RawClause("NOT " + Expr.in(
        			database.getProperty(samplePerson, "id"),
                    new RawClause('(' + inner.toString() + ')')).clauseToString()));
        } else {
            inner.where(Expr.equal(
            		database.getProperty(sampleGuest, "event"),
            		event));
            outerWhere.addAnd(Expr.in(
            		database.getProperty(samplePerson, "id"),
            		new RawClause('(' + inner.toString() + ')')));
        }

        exportSelect(database, outer.where(outerWhere), exporter);
    }

    /**
     * Diese Methode exportiert alle Personen, die in einer bestimmten /
     * irgendeiner / keiner Kategorie sind.<br>
     * Achtung: hier wird davon ausgegangen, dass Personen des eigenen Mandanten nicht
     * in Veranstaltungen anderer Mandanten auftauchen.
     *
     * @param database zu benutzende Datenverbindung
     * @param category Kategorie, deren Personen exportiert werden sollen; <code>null</code>
     *  wird interpretiert als "Personen, die in irgendeiner Kategorie sind",
     *  <code>0</code> als "Personen, die in keiner Kategorie sind".
     * @param exporter zu benutzender {@link Exporter}
     * @param orgUnit Mandanten-ID, wenn danach gefiltert werden soll
     */
    void exportCategory(Database database, Integer category, Exporter exporter, Integer orgUnit) throws BeanException, IOException {
        assert database != null;
        assert exporter != null;
        Bean samplePerson = database.createBean("Person");
        Bean samplePersonCategory = database.createBean("PersonCategorie");

        Select outer = database.getSelect( samplePerson );
        Select inner = new Select(false).
        		from(database.getProperty(samplePersonCategory, "table")).
        		selectAs(database.getProperty(samplePersonCategory, "person"), "person");

        WhereList outerWhere = new WhereList();
        outerWhere.addAnd(Expr.equal("deleted", PersonConstants.DELETED_FALSE));
        if (orgUnit != null) {
        	outerWhere.addAnd(Expr.equal("tperson.fk_orgunit", orgUnit));
        }

		/*
		 * cklein 2009-07-16: fixes issue 1815 - although option "Alle" yielded no return value
		 * the jetty used for testing made it a valid integer object of value 0, which broke
		 * existing code relying on the fact that the request parameter would be null.
		 * Keine/None now equals -1.
		 */
        if (category == null || category.intValue() == 0) {
            outerWhere.addAnd(Expr.in(
            		database.getProperty(samplePerson, "id"),
            		new RawClause('(' + inner.toString() + ')')));
        } else if (category.intValue() == -1) {
            outerWhere.addAnd(new RawClause("NOT " + Expr.in(
            		database.getProperty(samplePerson, "id"),
            		new RawClause('(' + inner.toString() + ')')).clauseToString()));
        } else {
            inner.where(Expr.equal(
            		database.getProperty(samplePersonCategory, "categorie"),
            		category));
            outerWhere.addAnd(Expr.in(
            		database.getProperty(samplePerson, "id"),
            		new RawClause('(' + inner.toString() + ')')));
        }

        exportSelect(database, outer.where(outerWhere).orderBy( Order.asc( "tperson.pk" ) ), exporter);
    }

    /**
     * Diese Methode exportiert alle Personen.
     *
     * @param database zu benutzende Datenverbindung
     * @param exporter zu benutzender {@link Exporter}
     * @param orgUnit Mandanten-ID, wenn danach gefiltert werden soll
     */
    void exportAll(Database database, Exporter exporter, Integer orgUnit) throws BeanException, IOException {
        assert database != null;
        assert exporter != null;
        Bean samplePerson = database.createBean("Person");

        Select outer = database.getSelect( "Person" );
        WhereList outerWhere = new WhereList();
        outerWhere.addAnd(Expr.equal("deleted", PersonConstants.DELETED_FALSE));
        if (orgUnit != null) {
        	outerWhere.addAnd(Expr.equal("tperson.fk_orgunit", orgUnit));
        }

        exportSelect(database, outer.where(outerWhere), exporter);
    }

    /**
     * Diese Methode exportiert alle Personen, die ein bestimmtes {@link Select} liefert.
     *
     * @param database zu benutzende Datenverbindung
     * @param select DB-Select; dieses muß auf <code>database.getSelect("Person")</code>
     *  beruhen, insbesondere zumindest mindestens die darin vorgegebenen Spalten haben.
     * @param exporter zu benutzender {@link Exporter}
     */
    void exportSelect(Database database, Select select, Exporter exporter) throws BeanException, IOException {
        assert database != null;
        assert select != null;
        assert exporter != null;

        try
        {
        	ResultSet rs =  ( ResultSet ) ( ( Result ) select.execute() ).resultSet();
        	ResultSetMetaData rsm = rs.getMetaData();
        	Set< String > keys = new HashSet< String >();
    		for ( int i = 1; i <= rsm.getColumnCount(); i++ )
    		{
    			keys.add( rsm.getColumnName( i ) );
    		}
        	while ( rs.next() )
        	{
        		Person person = new Person();
	       		for ( String key : keys )
	       		{
	       			person.setField( key, rs.getObject( key ) );
	       		}
	       		exporter.exportPerson(person);
	        }
        }
        catch ( SQLException e )
        {
        	throw new BeanException( e.getMessage(), e );
        }
    }

    /**
     * Diese Methode erstellt einen Importvorgangeintrag.
     *
     * @param formatKey Schlüssel des Formats des Importvorgangs
     * @param importSource Bezeichner der Importquelle
     * @param orgunit Mandanten-ID, in der der Import erfolgt
     * @return neue {@link Import}-Instanz zu den angegebenen Daten
     * @throws TcContentProzessException
     */
    static Import createImport(TransactionContext context, String formatKey, String importSource, Integer orgunit) throws TcContentProzessException {
        try {
            Database database = context.getDatabase();
            Import importInstance = (Import) database.createBean("Import");
            importInstance.orgunit = orgunit;
            importInstance.importformat = formatKey;
            importInstance.importsource = importSource;
            database.saveBean(importInstance, context, true);
            return importInstance;
        } catch (BeanException e) {
            throw new TcContentProzessException("Fehler beim Erstellen eines Importvorgangs", e);
        } catch (IOException e) {
            throw new TcContentProzessException("Fehler beim Speichern eines Importvorgangs", e);
        }
    }

    /**
     * Diese Methode erstellt und Initialisiert einen {@link Importer}.
     *
     * @param format basierendes {@link ExchangeFormat}
     * @return ein passender {@link Importer}
     * @throws TcContentProzessException bei Fehlern beim Instanziieren des Exporters.
     */
    static Importer createImporter(ExchangeFormat format, TransactionContext context, InputStream is) throws TcContentProzessException {
        assert format != null;
        assert context != null;
        try {
            Importer importer = (Importer) format.getImporterClass().newInstance();
            if (importer instanceof Exchanger) {
                Exchanger exchanger = (Exchanger) importer;
                exchanger.setExchangeFormat(format);
                exchanger.setInputStream(is);
            }
            if (importer instanceof DatabaseUtilizer) {
                DatabaseUtilizer dbUtilizer = (DatabaseUtilizer) importer;
                dbUtilizer.setDatabase(context.getDatabase());
            }
            return importer;
        } catch (Exception e) {
            throw new TcContentProzessException("Fehler beim Instanziieren des Importers", e);
        }
    }

    /**
     * Diese Methode erzeugt eine <code>Map</code>, in der Statistiken zu einem Import
     * kodiert sind.
     *
     * @param dsCount Anzahl Datensätze insgesamt
     * @param dupCount Anzahl Duplikate
     * @param id Import-ID
     * @return Map mit Informationen zum Import, insbesondere der Anzahl gefundener
     *  Datensätze unter "dsCount", der Anzahl Duplikate unter "dupCount", der Anzahl
     *  importierter Datensätze unter "saveCount" und der Import-ID unter "id".
     */
    public static Map createImportStats(int dsCount, int dupCount, int saveCount, Number id) {
        Map rMap = new HashMap();
        rMap.put("dsCount", new Integer(dsCount));
        rMap.put("dupCount", new Integer(dupCount));
        rMap.put("saveCount", new Integer(saveCount));
        rMap.put("id", id);
        return rMap;
    }

    /**
     * Diese Methode erzeugt eine <code>Map</code>, in der Statistiken zu einem Import
     * kodiert sind. In dieser Variante werden auch die Datensätze gezählt, die wegen
     * Unkorrektheit ignoriert wurden.
     *
     * @param igCount Anzahl ignorierter (unkorrekter) Datensätze
     * @param dsCount Anzahl nicht ignorierter Datensätze insgesamt
     * @param dupCount Anzahl Duplikate
     * @param id Import-ID
     * @return Map mit Informationen zum Import, insbesondere der Anzahl gefundener
     *  Datensätze unter "dsCount", der Anzahl Duplikate unter "dupCount", der Anzahl
     *  importierter Datensätze unter "saveCount" und der Import-ID unter "id".
     */
    public static Map createImportStats(int igCount, int dsCount, int dupCount, int saveCount, Number id) {
        Map rMap = createImportStats(dsCount, dupCount, saveCount, id);
        rMap.put("igCount", new Integer(igCount));
        return rMap;
    }

    //
    // geschützte Member
    //
    private final static Logger logger = Logger.getLogger(DataExchangeWorker.class);
}
