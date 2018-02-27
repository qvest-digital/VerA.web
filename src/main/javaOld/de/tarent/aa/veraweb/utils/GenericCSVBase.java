package de.tarent.aa.veraweb.utils;

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

import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.data.exchange.Exchanger;
import de.tarent.data.exchange.FieldMapping;
import de.tarent.data.exchange.MappingException;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.DatabaseUtilizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Diese Klasse stellt Basisfunktionalitäten für den generischen CSV-Im- und -Export
 * zur Verfügung.
 *
 * @author mikel
 */
public class GenericCSVBase implements Exchanger, DatabaseUtilizer {
    //
    // Konstanten
    //
    /**
     * Property-Schlüssel für das Export-Mapping der Felder
     */
    public static final String PROPERTY_EXPORT_MAPPING = "exportMapping";

    /**
     * Property-Schlüssel für das Encoding der Ausgabedatei
     */
    public final static String PROPERTY_ENCODING = "encoding";

    /**
     * Property-Schlüssel für das Feldtrennzeichen
     */
    public final static String PROPERTY_FIELD_SEPARATOR = "fieldSeparator";

    /**
     * Property-Schlüssel für das Quote-Zeichen
     */
    public final static String PROPERTY_TEXT_QUALIFIER = "textQualifier";

    /**
     * Property-Schlüssel für das Datumsformatmuster
     */
    public final static String PROPERTY_DATE_FORMAT = "dateFormat";

    /**
     * Encoding: UTF-8
     */
    public final static String ENCODING_UTF_8 = "UTF-8";

    /**
     * Vorgabewert: Character-Encoding
     */
    public final static String DEFAULT_ENCODING = ENCODING_UTF_8;

    /**
     * Vorgabewert: Feldtrennzeichen
     */
    public final static char DEFAULT_FIELD_SEPARATOR = ';';

    /**
     * Vorgabewert: Feldtrennzeichen Alternative 1
     */
    public final static char ALT1_FIELD_SEPARATOR = '|';

    /**
     * Vorgabewert: Feldtrennzeichen Alternative 2
     */
    public final static char ALT2_FIELD_SEPARATOR = '~';

    /**
     * Vorgabewert: Quote-Zeichen
     */
    public final static char DEFAULT_TEXT_QUALIFIER = '"';

    /**
     * Default-Kategorie-Rang, wenn alle Rang-Einträge <code>null</code> sind.
     */
    protected static final String DEFAULT_RANK = "X";

    //
    // Schnittstelle DatabaseUtilizer
    //

    /**
     * Die zu nutzende Datenbank
     *
     * @see DatabaseUtilizer#setDatabase(Database)
     */
    public void setDatabase(Database database) {
        this.database = database;
    }

    /**
     * Die zu nutzende Datenbank
     *
     * @see DatabaseUtilizer#getDatabase()
     */
    public Database getDatabase() {
        return database;
    }

    //
    // Schnittstelle Exchanger
    //

    /**
     * Das zu verwendende Austauschformat.
     *
     * @see de.tarent.data.exchange.Exchanger#getExchangeFormat()
     */
    public ExchangeFormat getExchangeFormat() {
        return exchangeFormat;
    }

    /**
     * Das zu verwendende Austauschformat.
     *
     * @see de.tarent.data.exchange.Exchanger#setExchangeFormat(de.tarent.data.exchange.ExchangeFormat)
     */
    public void setExchangeFormat(ExchangeFormat format) {
        this.exchangeFormat = format;
    }

    /**
     * Der zu verwendende Eingabedatenstrom.
     *
     * @see de.tarent.data.exchange.Exchanger#getInputStream()
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Der zu verwendende Eingabedatenstrom.
     *
     * @see de.tarent.data.exchange.Exchanger#setInputStream(java.io.InputStream)
     */
    public void setInputStream(InputStream stream) {
        this.inputStream = stream;
    }

    /**
     * Der zu verwendende Ausgabedatenstrom.
     *
     * @see de.tarent.data.exchange.Exchanger#getOutputStream()
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Der zu verwendende Ausgabedatenstrom.
     *
     * @see de.tarent.data.exchange.Exchanger#setOutputStream(java.io.OutputStream)
     */
    public void setOutputStream(OutputStream stream) {
        this.outputStream = stream;
    }

    /**
     * Wir handhaben neuerdings das Encoding (Unix-Sinn, nicht VerA.web-„Zeichensatz“)
     * separat vom Ausgabeformat. Hier wird es gesetzt.
     *
     * @param cs file encoding to be used
     */
    public void setFileEncoding(Charset cs) {
        fileEncoding = cs;
    }

    //
    // geschützte Hilfsmethoden
    //

    /**
     * Diese Methode ermittelt Exportparameter aus dem
     * {@link #getExchangeFormat() ExchangeFormat}.
     *
     * @param export FIXME
     * @throws MappingException FIXME
     * @throws BeanException    FIXME
     * @throws IOException      FIXME
     */
    protected void parseFormat(boolean export) throws MappingException, BeanException, IOException {
        assert exchangeFormat != null;
        Map mappingDescription = simpleFieldMapping;
        if (exchangeFormat.getProperties() != null) {
            Object mappingObject = exchangeFormat.getProperties().get(PROPERTY_EXPORT_MAPPING);
            if (mappingObject instanceof Map) {
                mappingDescription = (Map) mappingObject;
            }
        }
        fieldMapping = new FieldMapping(getAvailableFields(), mappingDescription);
        csvFieldNames = new ArrayList(fieldMapping.getTargets());
        Collections.sort(csvFieldNames);

        //sollte der Feld Seperator in den Spaltennamen auftauchen, so wird er hier durch eine von zwei Alternativen ersetzt
        for (int i = 0; i < csvFieldNames.size(); i++) {
            csvFieldNames.set(i, csvFieldNames.get(i).toString().replace(fieldSeparator, getAlternativeFieldSeparator()));
        }

        if (!export) {
            fieldMapping = fieldMapping.invert();
        }
    }

    private char getAlternativeFieldSeparator() {
        return (Character.compare(fieldSeparator, ALT1_FIELD_SEPARATOR) != 0) ? ALT1_FIELD_SEPARATOR : ALT2_FIELD_SEPARATOR;
    }

    /**
     * Diese Methode liefert die Menge der verfügbaren Felder.
     *
     * @return FIXME
     * @throws BeanException FIXME
     * @throws IOException   FIXME
     * @see #getPersonDataFields()
     * @see #getCategoryFields()
     */
    protected Set getAvailableFields() throws BeanException, IOException {
        Set result = getPersonDataFields();
        result.addAll(getCategoryFields());
        return result;
    }

    /**
     * Diese Methode liefert die Menge der verfügbaren Personenstammdatenfelder.
     */
    Set getPersonDataFields() throws BeanException {
        Set result = new HashSet();
        Bean sample = database.createBean("Person");
        for (Iterator itFields = sample.getFields().iterator(); itFields.hasNext(); ) {
            result.add(personFieldFormat.format(new Object[] { itFields.next() }));
        }
        return result;
    }

    /**
     * Diese Methode liefert die Menge der verfügbaren Kategorienfelder.
     *
     * @throws IOException
     * @throws BeanException
     */
    Set getCategoryFields() throws BeanException, IOException {
        Set result = new HashSet();
        List categories = getCategoriesFromDB();
        if (categories == null) {
            return result; //keine Kategorien
        }

        for (Iterator itCategories = categories.iterator(); itCategories.hasNext(); ) {
            Map categoryData = (Map) itCategories.next();
            Object nameObject = categoryData.get("name");
            Integer flags = (Integer) categoryData.get("flags");

            if (nameObject == null) {
                continue;
            }

            if (flags == null || flags.intValue() == Categorie.FLAG_DEFAULT) {
                result.add(categoryFieldFormat.format(new Object[] { nameObject }));
            } else if (flags.intValue() == Categorie.FLAG_DIPLO_CORPS) {
                result.add(corpsFieldFormat.format(new Object[] { nameObject }));
            } else if (flags.intValue() == Categorie.FLAG_EVENT) {
                result.add(eventFieldFormat.format(new Object[] { nameObject }));
            } else {
                assert false;
            }
        }
        return result;
    }

    /**
     * Diese Methode holt alle notwendigen Kategorien aus der Datenbank.
     * Kann zur Einschränkung überschrieben werden.
     * Null = keine Kategorien
     *
     * @return FIXME
     * @throws IOException   FIXME
     * @throws BeanException FIXME
     */
    protected List getCategoriesFromDB() throws BeanException, IOException {
        return database.getBeanList("Categorie", database.getSelect("Categorie"));
    }

    /**
     * Diese Methode liest aus den Properties des Austauschformats Informationen
     * über das zu verwendende Encoding und die zu verwendenden Feldtrenner und
     * Quote-Zeichen.
     *
     * @see #encoding
     * @see #fieldSeparator
     * @see #textQualifier
     */
    protected void readProperties() {
        if (exchangeFormat.getProperties() != null) {
            Object property = exchangeFormat.getProperties().get(PROPERTY_ENCODING);
            if (property instanceof String) {
                encoding = property.toString();
            }
            property = exchangeFormat.getProperties().get(PROPERTY_FIELD_SEPARATOR);
            if (property instanceof String && property.toString().length() > 0) {
                fieldSeparator = property.toString().charAt(0);
            }
            property = exchangeFormat.getProperties().get(PROPERTY_TEXT_QUALIFIER);
            if (property instanceof String && property.toString().length() > 0) {
                textQualifier = property.toString().charAt(0);
            }
            property = exchangeFormat.getProperties().get(PROPERTY_DATE_FORMAT);
            if (property instanceof String && property.toString().length() > 0) {
                try {
                    dateFormat.applyPattern(property.toString());
                } catch (IllegalArgumentException iae) {
                    logger.log(Level.WARNING, "Fehler beim Anwenden des Datumformats " + property, iae);
                }
            }
        }
    }

    //
    // geschützte Membervariablen
    //
    /**
     * Die zu nutzende Datenbank
     */
    Database database = null;
    /**
     * Das zu verwendende Austauschformat
     */
    ExchangeFormat exchangeFormat = null;
    /**
     * Das zu nutzende Feld-Mapping
     */
    FieldMapping fieldMapping = null;
    /**
     * Der zu verwendende Ausgabedatenstrom
     */
    OutputStream outputStream = null;
    /**
     * Der zu verwendende Eingabedatenstrom
     */
    InputStream inputStream = null;
    /**
     * Unix-Encoding der Ströme
     */
    Charset fileEncoding = StandardCharsets.UTF_8;
    /**
     * Bezeichner der CSV-Spalten
     */
    protected List csvFieldNames = null;
    /**
     * Das zu verwendende Encoding
     */
    protected String encoding = DEFAULT_ENCODING;
    /**
     * Der zu verwendende Feldtrenner
     */
    protected char fieldSeparator = DEFAULT_FIELD_SEPARATOR;
    /**
     * Der zu verwendende Quote-Zeichen
     */
    protected char textQualifier = DEFAULT_TEXT_QUALIFIER;
    /**
     * Das zu verwendende Datumsformat
     */
    final protected SimpleDateFormat dateFormat = new SimpleDateFormat();
    /**
     * Format zum Erstellen von Personenstammdaten-Feldbezeichnern
     */
    final static MessageFormat personFieldFormat = new MessageFormat(":{0}");
    /**
     * Format zum Erstellen von Kategorie-Feldbezeichnern
     */
    final static MessageFormat categoryFieldFormat = new MessageFormat("CAT:{0}");
    /**
     * Format zum Erstellen von Ereignis-Feldbezeichnern
     */
    final static MessageFormat eventFieldFormat = new MessageFormat("EVE:{0}");
    /**
     * Format zum Erstellen von Dipl.-Corps-Feldbezeichnern
     */
    final static MessageFormat corpsFieldFormat = new MessageFormat("COR:{0}");
    /**
     * Simples 1:1-Mapping don Quell- und Zielspalten
     */
    static final Map simpleFieldMapping = Collections.singletonMap("*", "*");
    /**
     * Logger dieser Klasse
     */
    final static Logger logger = Logger.getLogger(GenericCSVBase.class.getName());
}
