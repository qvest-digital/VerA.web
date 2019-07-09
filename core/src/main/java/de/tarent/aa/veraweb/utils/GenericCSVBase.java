package de.tarent.aa.veraweb.utils;
import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.data.exchange.Exchanger;
import de.tarent.data.exchange.FieldMapping;
import de.tarent.data.exchange.MappingException;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.DatabaseUtilizer;
import lombok.extern.log4j.Log4j2;

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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * Diese Klasse stellt Basisfunktionalitäten für den generischen CSV-Im- und -Export
 * zur Verfügung.
 *
 * @author mikel
 */
@Log4j2
public class GenericCSVBase implements Exchanger, DatabaseUtilizer {
    /**
     * Property-Schlüssel für das Export-Mapping der Felder
     */
    private static final String PROPERTY_EXPORT_MAPPING = "exportMapping";

    /**
     * Property-Schlüssel für das Feldtrennzeichen
     */
    private final static String PROPERTY_FIELD_SEPARATOR = "fieldSeparator";

    /**
     * Property-Schlüssel für das Quote-Zeichen
     */
    private final static String PROPERTY_TEXT_QUALIFIER = "textQualifier";

    /**
     * Property-Schlüssel für das Datumsformatmuster
     */
    private final static String PROPERTY_DATE_FORMAT = "dateFormat";

    /**
     * Vorgabewert: Feldtrennzeichen
     */
    private final static char DEFAULT_FIELD_SEPARATOR = ';';

    /**
     * Vorgabewert: Feldtrennzeichen Alternative 1
     */
    private final static char ALT1_FIELD_SEPARATOR = '|';

    /**
     * Vorgabewert: Feldtrennzeichen Alternative 2
     */
    private final static char ALT2_FIELD_SEPARATOR = '~';

    /**
     * Vorgabewert: Quote-Zeichen
     */
    private final static char DEFAULT_TEXT_QUALIFIER = '"';

    /**
     * Default-Kategorie-Rang, wenn alle Rang-Einträge <code>null</code> sind.
     */
    public static final String DEFAULT_RANK = "X";

    /**
     * Format zum Erstellen von Personenstammdaten-Feldbezeichnern
     */
    private final static MessageFormat PERSON_FIELD_FORMAT = new MessageFormat(":{0}");
    /**
     * Format zum Erstellen von Kategorie-Feldbezeichnern
     */
    private final static MessageFormat CATEGORY_FIELD_FORMAT = new MessageFormat("CAT:{0}");
    /**
     * Format zum Erstellen von Ereignis-Feldbezeichnern
     */
    private final static MessageFormat EVENT_FIELD_FORMAT = new MessageFormat("EVE:{0}");
    /**
     * Format zum Erstellen von Dipl.-Corps-Feldbezeichnern
     */
    private final static MessageFormat CORPS_FIELD_FORMAT = new MessageFormat("COR:{0}");
    /**
     * Simples 1:1-Mapping don Quell- und Zielspalten
     */
    private final static Map SIMPLE_FIELD_MAPPING = Collections.singletonMap("*", "*");

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
    public List csvFieldNames = null;
    /**
     * Der zu verwendende Feldtrenner
     */
    public char fieldSeparator = DEFAULT_FIELD_SEPARATOR;
    /**
     * Der zu verwendende Quote-Zeichen
     */
    public char textQualifier = DEFAULT_TEXT_QUALIFIER;
    /**
     * Das zu verwendende Datumsformat
     */
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH);

    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
    }

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
        final Map mappingDescription = getMappingDescription();
        fieldMapping = new FieldMapping(getAvailableFields(), mappingDescription);
        csvFieldNames = new ArrayList(fieldMapping.getTargets());
        Collections.sort(csvFieldNames);

        //sollte der Feldseparator in den Spaltennamen auftauchen, so wird er hier durch eine von zwei Alternativen ersetzt
        for (int i = 0; i < csvFieldNames.size(); i++) {
            csvFieldNames.set(i, csvFieldNames.get(i).toString().replace(fieldSeparator, getAlternativeFieldSeparator()));
        }

        if (!export) {
            fieldMapping = fieldMapping.invert();
        }
    }

    private Map getMappingDescription() {
        Map mappingDescription = null;
        if (exchangeFormat.getProperties() != null) {
            Object mappingObject = exchangeFormat.getProperties().get(PROPERTY_EXPORT_MAPPING);
            mappingDescription = setMappingDescription(mappingObject);
        }
        return mappingDescription;
    }

    private Map setMappingDescription(Object mappingObject) {
        Map mappingDescription;
        if (mappingObject instanceof Map) {
            mappingDescription = (Map) mappingObject;
        } else {
            mappingDescription = SIMPLE_FIELD_MAPPING;
        }
        return mappingDescription;
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
    private Set getPersonDataFields() throws BeanException {
        final Set result = new HashSet();
        final Bean person = database.createBean("Person");
        for (Object currentField : person.getFields()) {
            if (personDataFieldsFilter(currentField, new String[] { "workarea", "fk_salutation_a_e1", "fk_salutation_a_e2",
              "fk_salutation_a_e3", "fk_salutation_b_e1", "fk_salutation_b_e2", "fk_salutation_b_e3" })) {
                result.add(PERSON_FIELD_FORMAT.format(new Object[] { currentField }));
            }
        }
        return result;
    }

    private boolean personDataFieldsFilter(Object currentField, String[] filter) {
        for (String value : filter) {
            if (currentField.toString().equals(value)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Diese Methode liefert die Menge der verfügbaren Kategorienfelder.
     *
     * @throws IOException   IOException
     * @throws BeanException BeanException
     */
    private Set getCategoryFields() throws BeanException, IOException {
        final Set result = new HashSet();
        final List categories = getCategoriesFromDB();
        if (categories == null) {
            return result; //keine Kategorien
        }
        for (Object category : categories) {
            Object nameObject = ((Map) category).get("name");
            Integer flags = (Integer) ((Map) category).get("flags");
            if (nameObject == null) {
                continue;
            }

            if (flags == null || flags == Categorie.FLAG_DEFAULT) {
                result.add(CATEGORY_FIELD_FORMAT.format(new Object[] { nameObject }));
            } else if (flags == Categorie.FLAG_DIPLO_CORPS) {
                result.add(CORPS_FIELD_FORMAT.format(new Object[] { nameObject }));
            } else if (flags == Categorie.FLAG_EVENT) {
                result.add(EVENT_FIELD_FORMAT.format(new Object[] { nameObject }));
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
     * über die zu verwendenden Feldtrenner und Quote-Zeichen.
     *
     * @see #fieldSeparator
     * @see #textQualifier
     */
    protected void readProperties() {
        if (exchangeFormat.getProperties() != null) {
            Object property = exchangeFormat.getProperties().get(PROPERTY_FIELD_SEPARATOR);
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
                    logger.warn("Fehler beim Anwenden des Datumformats " + property, iae);
                }
            }
        }
    }
}
