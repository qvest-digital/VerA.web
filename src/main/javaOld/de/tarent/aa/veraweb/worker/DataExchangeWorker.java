package de.tarent.aa.veraweb.worker;

/*-
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

import de.tarent.aa.veraweb.beans.Import;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.utils.Exporter;
import de.tarent.aa.veraweb.utils.Importer;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    /**
     * Vorgabewert für den Parameter <code>formatEnumKey</code> von {@link #getFormats(OctopusContext, String)}
     */
    public final static String KEY_FORMAT_NAMES = "exchangeFormats";

    /**
     * Exportfilterwert: Filtrierung nach Kategorie
     */
    public final static String EXPORT_FILTER_CATEGORY = "category";

    /**
     * Exportfilterwert: Filtrierung nach Veranstaltung
     */
    public final static String EXPORT_FILTER_EVENT = "event";

    /**
     * Parameterwert: beliebige Personen
     */
    public final static String PARAM_DOMAIN_VALUE_ALL = "all";
    /**
     * Parameterwert: Personen des gleichen Mandanten
     */
    public final static String PARAM_DOMAIN_VALUE_OU = "ou";

    //
    // Octopus-Aktionen
    //
    /**
     * Octopus-Eingabe-Parameter für {@link #getFormats(OctopusContext, String)}
     */
    public static final String[] INPUT_getFormats = { "formatEnumKey" };
    /**
     * Octopus-Eingabepflicht-Parameter für {@link #getFormats(OctopusContext, String)}
     */
    public static final boolean[] MANDATORY_getFormats = { false };
    /**
     * Octopus-Ausgabe-Parameter für {@link #getFormats(OctopusContext, String)}
     */
    public static final String OUTPUT_getFormats = "formats";

    /**
     * Diese Octopus-Aktion liefert eine {@link Map} mit verfügbaren Austauschformaten.
     *
     * führt einen Export von Personendaten durch. Dies geschieht
     * je nach Parameter <code>fieldMapping</code> in eine XML-Darstellung von VerA.web
     * oder eine CSV-Datei. Der Exportdatenstrom wird in den Content geschrieben.
     * Zusätzlich kann er an eine Stelle im Dateisystem kopiert werden.
     *
     * @param cntx          Octopus-Kontext
     * @param formatEnumKey optionaler Schlüssel der verfügbaren Schlüssel, Default ist
     *                      {@link #KEY_FORMAT_NAMES}.
     * @return Abbildung von Schlüsselbezeichnern auf {@link ExchangeFormat}-Instanzen.
     */
    @SuppressWarnings("unchecked")
    public Map getFormats(OctopusContext cntx, String formatEnumKey) {
        Map result = Collections.EMPTY_MAP;
        TcModuleConfig moduleConfig = cntx.moduleConfig();
        if (moduleConfig != null) {
            result = new LinkedHashMap();
            Object formatNamesObject = moduleConfig.getParamAsObject(formatEnumKey != null ? formatEnumKey : KEY_FORMAT_NAMES);
            if (formatNamesObject instanceof List) {
                for (Object o : ((List) formatNamesObject)) {
                    String key = o.toString();
                    ExchangeFormat format = getExchangeFormat(moduleConfig.getParams(), key, null);
                    if (format != null) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Format " + key + ": " + format);
                        }
                        result.put(key, format);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Octopus-Eingabe-Parameter für {@link #export(OctopusContext, String, String, String, Integer, Integer, String)}
     */
    public static final String[] INPUT_export = { "format", "filenc", "exportFilter", "exportEvent", "exportCategory", "domain" };
    /**
     * Octopus-Eingabepflicht-Parameter für {@link #export(OctopusContext, String, String, String, Integer, Integer, String)}
     */
    public static final boolean[] MANDATORY_export = { true, true, false, false, false, false };
    /**
     * Octopus-Ausgabe-Parameter für {@link #export(OctopusContext, String, String, String, Integer, Integer, String)}
     */
    public static final String OUTPUT_export = "stream";

    /**
     * Diese Octopus-Aktion führt einen Export von Personendaten durch. Dies geschieht
     * in einem konfigurierten Format. Der Exportdatenstrom wird in den Content geschrieben.
     * Zusätzlich kann er an eine Stelle im Dateisystem kopiert werden.
     *
     * @param cntx      Octopus-Kontext
     * @param formatKey Schlüssel der Datenformatbeschreibung in der Modulkonfiguration
     * @param filenc    Encoding der zu schreibenden Datei, ggfs. mit „+BOM“ am Ende
     * @param filter    {@link #EXPORT_FILTER_CATEGORY} oder {@link #EXPORT_FILTER_EVENT},
     *                  je nach anzuwendenen Filter
     * @param event     Veranstaltungsfilter
     * @param category  Kategorienfilter
     * @param domain    Domäne, aus der die Personen stammen ("all" oder "ou")
     * @return exportierter Datenstrom
     * @throws TcContentProzessException bei ungültigen Parameterwerten.
     */
    public Map export(final OctopusContext cntx, final String formatKey, final String filenc, final String filter, final Integer event,
            final Integer category, final String domain) throws TcContentProzessException, IOException {
        TcModuleConfig moduleConfig = cntx.moduleConfig();
        assert moduleConfig != null;
        // Zunächst mal die benötigten Objekte erstellen
        final ExchangeFormat format = getExchangeFormat(moduleConfig.getParams(), formatKey, cntx.getRequestObject().getRequestParameters());
        if (format == null) {
            throw new TcContentProzessException("Unbekannter Exportformatschlüssel '" + formatKey + "'.");
        }
        final Database database = new DatabaseVeraWeb(cntx);

        final PipedInputStream pis = new PipedInputStream();
        final PipedOutputStream pos = new PipedOutputStream(pis);

        final Charset ocs;
        if ("UTF-8+BOM".equals(filenc)) {
            ocs = StandardCharsets.UTF_8;
            pos.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
        } else if ("UTF-16BE+BOM".equals(filenc)) {
            ocs = StandardCharsets.UTF_16BE;
            pos.write(new byte[] { (byte) 0xFE, (byte) 0xFF });
        } else if ("UTF-16LE+BOM".equals(filenc)) {
            ocs = StandardCharsets.UTF_16LE;
            pos.write(new byte[] { (byte) 0xFF, (byte) 0xFE });
        } else if ("UTF-32BE+BOM".equals(filenc)) {
            if (!Charset.isSupported("UTF-32BE")) {
                throw new TcContentProzessException("JVM unterstützt Encoding nicht: " + filenc);
            }
            ocs = Charset.forName("UTF-32BE");
            pos.write(new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0xFE, (byte) 0xFF });
        } else if ("UTF-32LE+BOM".equals(filenc)) {
            if (!Charset.isSupported("UTF-32LE")) {
                throw new TcContentProzessException("JVM unterstützt Encoding nicht: " + filenc);
            }
            ocs = Charset.forName("UTF-32LE");
            pos.write(new byte[] { (byte) 0xFF, (byte) 0xFE, (byte) 0x00, (byte) 0x00 });
        } else {
            try {
                if (!Charset.isSupported(filenc)) {
                    throw new TcContentProzessException("JVM unterstützt Encoding nicht: " + filenc);
                }
            } catch (IllegalCharsetNameException icne) {
                throw new TcContentProzessException("Ungültiger Encoding-Name: " + filenc);
            }
            ocs = Charset.forName(filenc);
        }

        new Thread(new Runnable() {
            public void run() {
                Context.addActive(cntx);

                Exporter exporter = null;
                try {
                    exporter = createExporter(format, database, pos, ocs);

                    // Mandantenbeschränkung
                    TcPersonalConfig pConfig = cntx.personalConfig();
                    Integer orgUnit = null;
                    if (pConfig instanceof PersonalConfigAA) {
                        PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
                        if (!(PARAM_DOMAIN_VALUE_ALL.equals(domain) && pConfig.isUserInGroup(PersonalConfigAA.GROUP_ADMIN))) {
                            orgUnit = aaConfig.getOrgUnitId();
                        }
                    } else {
                        throw new BeanException("Missing user information");
                    }

                    // Beschränkung auf Kategorie, wenn Benutzer eine ausgewählt hat
                    Integer categoryId = null;
                    if (EXPORT_FILTER_CATEGORY.equals(filter)) {
                        // category == 0 bedeutet: in irgendeiner Kategorie, = -1 bedeutet: in keiner Kategorie
                        categoryId = category;
                    }

                    //Den Exporter auf Mandant und Kategorie einschränken. Ist für den CSV-Exporter notwendig, damit keine überflüssigen
                    // überschriften erzeugt werden.
                    exporter.setOrgUnitId(orgUnit);
                    exporter.setCategoryId(categoryId);

                    // Dann exportieren
                    exporter.startExport();
                    if (EXPORT_FILTER_EVENT.equals(filter)) {
                        // event == 0 bedeutet: in irgendeiner Veranstaltung, = -1 bedeutet: in keiner Veranstaltung
                        exportEvent(database, event, exporter, orgUnit);
                    } else if (EXPORT_FILTER_CATEGORY.equals(filter)) {
                        // category == 0 bedeutet: in irgendeiner Kategorie, = -1 bedeutet: in keiner Kategorie
                        exportCategory(database, category, exporter, orgUnit);
                    } else {
                        // guter Default?
                        exportAll(database, exporter, orgUnit);
                    }
                    exporter.endExport();
                } catch (Throwable t) {
                    LOGGER.error("Fehler beim Erstellen des Exports aufgetreten.", t);
                    // This will force a log output.
                    t.printStackTrace(System.out);
                    t.printStackTrace(System.err);
                    try {
                        pos.close();
                    } catch (IOException e) {
                        LOGGER.error("Fehler beim Schließen", e);
                    }
                } finally {
                    try {
                        pos.close();
                    } catch (IOException t) {
                        LOGGER.error("Fehler beim Schließen", t);
                    }
                }
            }
        }).start();

        return createBinaryResponse(getFilename(cntx, format), format.getMimeType(), pis);
    }

    /**
     * Octopus-Eingabe-Parameter für {@link #importToTransit(OctopusContext, Map, String, String, String, Integer, Integer, Map)}
     */
    public static final String[] INPUT_importToTransit =
            { "importfile", "format", "filenc", "importSource", "orgUnit", "targetOrgUnit", "CONFIG:importProperties" };
    /**
     * Octopus-Eingabe-Parameter-Pflicht für {@link #importToTransit(OctopusContext, Map, String, String, String, Integer, Integer, Map)}
     */
    public static final boolean[] MANDATORY_importToTransit = { false, false, true, false, false, false, false };
    /**
     * Octopus-Ausgabe-Parameter für {@link #importToTransit(OctopusContext, Map, String, String, String, Integer, Integer, Map)}
     */
    public static final String OUTPUT_importToTransit = "importStatus";

    /**
     * Diese Octopus-Aktion importiert die Personen einer Datei in den Transit-Bereich,
     * also die Tabelle <code>timportperson</code>.
     *
     * @param octopusContext   Octopus-Kontext
     * @param stream           Datei-Upload-Map (enthält unter "ContentStream" einen <code>InputStream</code>)
     * @param formatKey        Schlüssel der Datenformatbeschreibung in der Modulkonfiguration
     * @param filenc           Encoding der Importdatei oder „_auto“
     * @param importSource     Importquellenbeschreibung
     * @param orgUnit          Ziel-Mandant; nur bei Super-Admins beachtetg
     * @param targetOrgUnit    Ziel-Mandant, wenn das Import über das CLI-Tool erfolgt
     * @param importProperties Einstellungen zum Import
     * @return Map mit Informationen zum Import, insbesondere der Anzahl gefundener
     * Datensätze unter "dsCount", der Anzahl Duplikate unter "dupCount", der Anzahl
     * importierter Datensätze unter "saveCount" und der Import-ID unter "id".
     * @throws IOException               FIXME
     * @throws TcContentProzessException FIXME
     */
    public Map importToTransit(OctopusContext octopusContext,
            Map stream,
            String formatKey,
            String filenc,
            String importSource,
            Integer orgUnit,
            Integer targetOrgUnit,
            Map importProperties)
            throws BeanException, IOException, TcContentProzessException {
        stream = getStream(octopusContext, stream);
        if (!octopusContext.getStatus().equals("streamClose")) {
            formatKey = getFormatKey(octopusContext, formatKey);
            importSource = getImportSource(octopusContext, importSource);
            Integer mandantId;
            if (targetOrgUnit != null) {
                mandantId = targetOrgUnit;
            } else {
                mandantId = getOrgUnit(octopusContext, orgUnit);
            }
            importProperties = getImportProperties(octopusContext, importProperties);

            TcModuleConfig moduleConfig = octopusContext.moduleConfig();
            assert moduleConfig != null;
            // Zunächst mal die benötigten Objekte erstellen
            ExchangeFormat format = getExchangeFormat(moduleConfig.getParams(), formatKey, octopusContext.getRequestObject().getRequestParameters());
            if (format == null) {
                throw new TcContentProzessException("Unbekannter Importformatschl\u00fcssel '" + formatKey + "'.");
            }
            if (importSource == null || importSource.length() == 0) {
                Map<String, String> status = new HashMap<>();
                status.put("invalidData", "importSource");
                octopusContext.setStatus("invalidData");
                return status;
            }

            String filename = (String) stream.get("ContentName");
            if (filename != null && filename.length() != 0) {
                String suffix = filename.lastIndexOf(".") == -1 ? null :
                        filename.substring(filename.lastIndexOf(".") + 1);
                if (suffix != null) {
                    suffix.toLowerCase();
                }

                if (suffix == null || suffix.length() == 0) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Endung der Import-Datei '" + filename + "' konnte nicht festgestellt werden.");
                    }
                } else if (
                        suffix.equals("ods") ||
                                suffix.equals("sxc") ||
                                suffix.equals("xls") ||
                                suffix.equals("pdf") ||
                                suffix.equals("zip") ||
                                suffix.equals("exe")) {
                    Map<String, String> status = new HashMap<>();
                    status.put("invalidData", "fileExtension");
                    status.put("fileextension", suffix);
                    octopusContext.setStatus("invalidData");
                    return status;
                }
            }

            InputStream istream = (InputStream) stream.get("ContentStream");

            if (istream == null || istream.available() <= 0) {
                Map<String, String> status = new HashMap<>();
                status.put("invalidData", "inputStream");
                octopusContext.setStatus("invalidData");
                return status;
            }

            Database database = new DatabaseVeraWeb(octopusContext);
            TransactionContext transactionContext = database.getTransactionContext();
            try {
                if (octopusContext.personalConfig() instanceof PersonalConfigAA) {
                    final PersonalConfigAA aaConfig = (PersonalConfigAA) octopusContext.personalConfig();
                    if (mandantId == null || mandantId == 0 || !aaConfig.isUserInGroup(PersonalConfigAA.GROUP_ADMIN)) {
                        mandantId = aaConfig.getOrgUnitId();
                    }
                } else {
                    throw new TcContentProzessException("Fehlende Benutzerinformation.");
                }

                Charset ics;
                if ("_auto".equals(filenc)) {
                    //XXX for later
                    if (!Charset.isSupported("cp1252")) {
                        LOGGER.error("JVM does not support \"cp1252\", falling back to latin1 standard encoding; some characters will be lost!");
                        ics = StandardCharsets.ISO_8859_1;
                    } else {
                        ics = Charset.forName("cp1252");
                    }
                    //XXX TODO: implement
                    ics = StandardCharsets.UTF_8;
                } else {
                    try {
                        if (!Charset.isSupported(filenc)) {
                            throw new TcContentProzessException("JVM unterstützt Encoding nicht: " + filenc);
                        }
                    } catch (IllegalCharsetNameException icne) {
                        throw new TcContentProzessException("Ungültiger Encoding-Name: " + filenc);
                    }
                    ics = Charset.forName(filenc);
                }

                Importer importer = createImporter(format, transactionContext, istream, ics);
                Import importInstance = createImport(transactionContext, formatKey, importSource, mandantId);
                VerawebDigester digester = new VerawebDigester(octopusContext, transactionContext, importProperties, importSource, importInstance);

                importer.importAll(digester, transactionContext);

                transactionContext.commit();

                // force gc after import
                System.gc();

                return digester.getImportStats();

            } catch (Exception e) {
                LOGGER.error("Fehler beim Import aufgetreten.", e);
                CharArrayWriter caw = new CharArrayWriter();
                PrintWriter pw = new PrintWriter(caw);
                e.printStackTrace(pw);
                pw.close();

                Map<String, String> status = new HashMap<>();
                status.put("invalidData", "errorOnImport");
                status.put("exception", caw.toString());
                status.put("message", e.getLocalizedMessage());
                octopusContext.setStatus("invalidData");
                return status;
            } finally {
                transactionContext.rollBack();
            }
        }
        return null;
    }

    private Map getImportProperties(OctopusContext cntx, Map importProperties) {
        if (importProperties != null) {
            cntx.setSession("importProperties", importProperties);
        } else {
            importProperties = (HashMap) cntx.sessionAsObject("importProperties");
        }
        return importProperties;
    }

    private Integer getOrgUnit(OctopusContext cntx, Integer orgUnit) {
        if (orgUnit != null) {
            cntx.setSession("orgUnit", orgUnit);
        } else {
            orgUnit = (Integer) cntx.sessionAsObject("orgUnit");
        }
        return orgUnit;
    }

    private String getImportSource(OctopusContext cntx, String importSource) {
        if (importSource != null) {
            cntx.setSession("importSource", importSource);
        } else {
            importSource = cntx.sessionAsString("importSource");
        }
        return importSource;
    }

    private String getFormatKey(OctopusContext cntx, String formatKey) {
        if (formatKey != null) {
            cntx.setSession("formatKey", formatKey);
        } else {
            formatKey = cntx.sessionAsString("formatKey");
        }
        return formatKey;
    }

    private Map getStream(OctopusContext cntx, Map stream) {
        if (stream != null) {
            cntx.setSession("stream", stream);
        } else {
            cntx.setStatus("streamClose");
        }
        return stream;
    }

    //
    // geschützte Hilfsmethoden
    //

    /**
     * Diese Methode erstellt eine {@link Map}, aus der die
     * {@link TcBinaryResponseEngine} die Daten für ihre
     * Octopus-Response-Erstellung entnimmt.
     *
     * @param filename    Dateiname, den die Response tragen soll
     * @param mimetype    MIME-Typ, den die Response haben soll
     * @param inputstream Datenstrom, der die Response bilden soll
     * @return eine {@link Map}, in der die Parameter eingetragen sind
     */
    static Map createBinaryResponse(String filename, String mimetype, InputStream inputstream) {
        Map<String, Object> binaryResponse = new HashMap<>();
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
     * @param config    Konfiguration
     * @param formatKey Schlüssel zum Format
     * @param params    {@link Map} mit Parametern, aus denen diejenigen ermittelt werden,
     *                  die dem {@link ExchangeFormat} zuzuordnen sind; dies sind genau die, die einen
     *                  Schlüssel der Form <code>"format-" + formatKey + '-' + choiceKey</code> haben,
     *                  wobei <code>choiceKey</code> über die Schlüssel der {@link Map} iteriert, die
     *                  in den {@link ExchangeFormat#getProperties() Properties} unter dem Schlüssel
     *                  <code>"choices"</code> liegt. Sie werden diesen Properties unter dem jeweiligen
     *                  Schlüssel <code>choiceKey</code> hinzugefügt.
     *                  XXX choices-Support wurde entfernt
     * @return zugehöriges {@link ExchangeFormat} oder <code>null</code>
     */
    static ExchangeFormat getExchangeFormat(Map config, String formatKey, Map params) {
        if (formatKey == null) {
            return null;
        }
        Object formatObject = config.get(formatKey);
        if (!(formatObject instanceof Map)) {
            return null;
        }
        ConfiguredExchangeFormat format = new ConfiguredExchangeFormat((Map) formatObject);
        return format;
    }

    /**
     * Diese Methode liefert zu einem Octopus-Kontext den zu einem bestimmten
     * Format passenden Dateinamen für Dateirückgaben.
     *
     * @param octx   Octopus-Kontext
     * @param format {@link ExchangeFormat}-Instanz
     * @return Dateiname mit Endung
     */
    static String getFilename(OctopusContext octx, ExchangeFormat format) {
        assert octx != null;
        assert format != null;
        String ext = format.getDefaultExtension();
        if (ext == null) {
            ext = "export";
        } else if (ext.length() > 0 && ext.charAt(0) == '.') {
            ext = ext.substring(1);
        }
        String def = "veraweb." + ext;
        return OctopusHelper.getFilename(octx, ext, def);
    }

    /**
     * Diese Methode erstellt und Initialisiert einen {@link Exporter}.
     *
     * @param format   basierendes {@link ExchangeFormat}
     * @param database zu benutzende {@link Database}
     * @param os       Ausgabestrom
     * @param cs       Ausgabe-Encoding (Unix, nicht VerA.web-„Zeichensatz“)
     * @return ein passender {@link Exporter}
     * @throws TcContentProzessException bei Fehlern beim Instanziieren des Exporters.
     */
    static Exporter createExporter(ExchangeFormat format, Database database, OutputStream os, Charset cs) throws TcContentProzessException {
        assert format != null;
        assert database != null;
        try {
            Exporter exporter = (Exporter) format.getExporterClass().newInstance();
            if (exporter instanceof Exchanger) {
                Exchanger exchanger = (Exchanger) exporter;
                exchanger.setExchangeFormat(format);
                exchanger.setOutputStream(os);
                exchanger.setFileEncoding(cs);
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
     * @param event    Veranstaltung, deren Gäste exportiert werden sollen; <code>null</code>
     *                 wird interpretiert als "Personen, die bei irgendeiner Veranstaltung Gast sind",
     *                 <code>0</code> als "Personen, die bei keiner Veranstaltung Gast sind".
     * @param exporter zu benutzender {@link Exporter}
     * @param orgUnit  Mandanten-ID, wenn danach gefiltert werden soll
     */
    void exportEvent(Database database, Integer event, Exporter exporter, Integer orgUnit) throws BeanException, IOException {
        assert database != null;
        assert exporter != null;
        Bean samplePerson = database.createBean("Person");
        Bean sampleGuest = database.createBean("Guest");

        Select outer = database.getSelect("Person");
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
        if (event == null || event == 0) {
            outerWhere.addAnd(Expr.in(
                    database.getProperty(samplePerson, "id"),
                    new RawClause('(' + inner.toString() + ')')));
        } else if (event == -1) {
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
     *                 wird interpretiert als "Personen, die in irgendeiner Kategorie sind",
     *                 <code>0</code> als "Personen, die in keiner Kategorie sind".
     * @param exporter zu benutzender {@link Exporter}
     * @param orgUnit  Mandanten-ID, wenn danach gefiltert werden soll
     */
    void exportCategory(Database database, Integer category, Exporter exporter, Integer orgUnit) throws BeanException, IOException {
        assert database != null;
        assert exporter != null;
        Bean samplePerson = database.createBean("Person");
        Bean samplePersonCategory = database.createBean("PersonCategorie");

        Select outer = database.getSelect(samplePerson);
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
        if (category == null || category == 0) {
            outerWhere.addAnd(Expr.in(
                    database.getProperty(samplePerson, "id"),
                    new RawClause('(' + inner.toString() + ')')));
        } else if (category == -1) {
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

        exportSelect(database, outer.where(outerWhere).orderBy(Order.asc("tperson.pk")), exporter);
    }

    /**
     * Diese Methode exportiert alle Personen.
     *
     * @param database zu benutzende Datenverbindung
     * @param exporter zu benutzender {@link Exporter}
     * @param orgUnit  Mandanten-ID, wenn danach gefiltert werden soll
     */
    void exportAll(Database database, Exporter exporter, Integer orgUnit) throws BeanException, IOException {
        assert database != null;
        assert exporter != null;

        Select outer = database.getSelect("Person");
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
     * @param select   DB-Select; dieses muß auf <code>database.getSelect("Person")</code>
     *                 beruhen, insbesondere zumindest mindestens die darin vorgegebenen Spalten haben.
     * @param exporter zu benutzender {@link Exporter}
     */
    void exportSelect(Database database, Select select, Exporter exporter) throws BeanException, IOException {
        assert database != null;
        assert select != null;
        assert exporter != null;

        try {
            ResultSet rs = ((Result) select.execute()).resultSet();
            ResultSetMetaData rsm = rs.getMetaData();
            Set<String> keys = new HashSet<>();
            for (int i = 1; i <= rsm.getColumnCount(); i++) {
                keys.add(rsm.getColumnName(i));
            }
            while (rs.next()) {
                Person person = new Person();
                for (String key : keys) {
                    person.setField(key, rs.getObject(key));
                }
                exporter.exportPerson(person);
            }
        } catch (SQLException e) {
            throw new BeanException(e.getMessage(), e);
        }
    }

    /**
     * Diese Methode erstellt einen Importvorgangeintrag.
     *
     * @param formatKey    Schlüssel des Formats des Importvorgangs
     * @param importSource Bezeichner der Importquelle
     * @param orgunit      Mandanten-ID, in der der Import erfolgt
     * @return neue {@link Import}-Instanz zu den angegebenen Daten
     * @throws TcContentProzessException FIXME
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
    static Importer createImporter(ExchangeFormat format, TransactionContext context, InputStream is, final Charset cs)
            throws TcContentProzessException {
        assert format != null;
        assert context != null;
        try {
            Importer importer = (Importer) format.getImporterClass().newInstance();
            if (importer instanceof Exchanger) {
                Exchanger exchanger = (Exchanger) importer;
                exchanger.setExchangeFormat(format);
                exchanger.setInputStream(is);
                exchanger.setFileEncoding(cs);
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
     * @param dsCount  Anzahl Datensätze insgesamt
     * @param dupCount Anzahl Duplikate
     * @param id       Import-ID
     * @return Map mit Informationen zum Import, insbesondere der Anzahl gefundener
     * Datensätze unter "dsCount", der Anzahl Duplikate unter "dupCount", der Anzahl
     * importierter Datensätze unter "saveCount" und der Import-ID unter "id".
     */
    public static Map<String, Integer> createImportStats(int dsCount, int dupCount, int saveCount, Number id) {
        Map<String, Integer> rMap = new HashMap<>();
        rMap.put("dsCount", dsCount);
        rMap.put("dupCount", dupCount);
        rMap.put("saveCount", saveCount);
        rMap.put("id", (Integer) id);
        return rMap;
    }

    /**
     * Diese Methode erzeugt eine <code>Map</code>, in der Statistiken zu einem Import
     * kodiert sind. In dieser Variante werden auch die Datensätze gezählt, die wegen
     * Unkorrektheit ignoriert wurden.
     *
     * @param igCount  Anzahl ignorierter (unkorrekter) Datensätze
     * @param dsCount  Anzahl nicht ignorierter Datensätze insgesamt
     * @param dupCount Anzahl Duplikate
     * @param id       Import-ID
     * @return Map mit Informationen zum Import, insbesondere der Anzahl gefundener
     * Datensätze unter "dsCount", der Anzahl Duplikate unter "dupCount", der Anzahl
     * importierter Datensätze unter "saveCount" und der Import-ID unter "id".
     */
    public static Map createImportStats(int igCount, int dsCount, int dupCount, int saveCount, Number id) {
        final Map<String, Integer> rMap = createImportStats(dsCount, dupCount, saveCount, id);
        rMap.put("igCount", igCount);
        return rMap;
    }

    //
    // geschützte Member
    //
    private final static Logger LOGGER = LogManager.getLogger(DataExchangeWorker.class);
}
