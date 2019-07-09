package de.tarent.octopus.exchange;
import de.tarent.data.exchange.ExchangeFormat;
import lombok.extern.log4j.Log4j2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Diese Klasse stellt die Eigenschaften eines Datenaustauschformats
 * für Export- und Importimplementierungen zur Verfügung, die aus einer
 * {@link Map}, wie sie aus Octopus-Konfigurationen gelesen werden,
 * initialisiert werden.
 *
 * @author mikel
 */
@Log4j2
public class ConfiguredExchangeFormat extends ExchangeFormat {
    //
    // Konstruktor
    //

    /**
     * Dieser Konstruktor bekommt eine {@link Map} übergeben, wie sie aus
     * einer Octopus-Konfiguration eingelesen wird, und initialisiert daraus
     * die {@link ExchangeFormat}-Attribute. Hierzu werden als Schlüssel die
     * Konstanten <code>PARAM_*</code> dieser Klasse benutzt.
     *
     * @param configuration {@link Map}, aus der die Attribute dieses Formats gelesen werden.
     */
    public ConfiguredExchangeFormat(Map configuration) {
        super();
        if (configuration != null) {
            readConfiguration(configuration);
        }
    }

    //
    // Öffentliche Hilfsmethoden
    //

    /**
     * Diese Methode fügt den über die ursprüngliche Konfiguration vorgegebenen Parametern
     * weitere hinzu, die etwa über Benutzereingabe oder sonstige Umstände bestimmt werden.
     *
     * @param data {@link Map} mit weiteren Format-Properties
     */
    @Override
    public void addProperties(Map data) {
        super.addProperties(data);
    }

    //
    // Konstanten
    //
    /**
     * Parameterschlüssel für den Name dieses Formats
     */
    public final static String PARAM_NAME = "name";
    /**
     * Parameterschlüssel für die Beschreibung dieses Formats
     */
    public final static String PARAM_DESCRIPTION = "description";
    /**
     * Parameterschlüssel für die URL zu einem Icon zu diesem Format
     */
    public final static String PARAM_ICON = "icon";
    /**
     * Parameterschlüssel für den Namen der {@link de.tarent.aa.veraweb.utils.Exporter}-Klasse zu diesem Format
     */
    public final static String PARAM_EXPORTER_CLASS = "exporterClass";
    /**
     * Parameterschlüssel für den Namen der {@link de.tarent.aa.veraweb.utils.Importer}-Klasse zu diesem Format
     */
    public final static String PARAM_IMPORTER_CLASS = "importerClass";
    /**
     * Parameterschlüssel für den MIME-Typ zu diesem Format
     */
    public final static String PARAM_MIME_TYPE = "mimeType";
    /**
     * Parameterschlüssel für das Standard-Suffix für Dateien dieses Formats
     */
    public final static String PARAM_DEFAULT_EXTENSION = "defaultExtension";
    /**
     * Parameterschlüssel für die speziellen Attribute dieses Formats
     */
    public final static String PARAM_PROPERTIES = "properties";

    //
    // geschützte Hilfsmethoden
    //

    /**
     * Diese Methode liest aus der übergebenen {@link Map} die Daten des Formats.
     * Als Schlüssel werden die Konstanten <code>PARAM_*</code> benutzt.
     *
     * @param configuration {@link Map} mit Konfigurationseinträgen nach Octopus-Art
     */
    void readConfiguration(Map configuration) {
        assert configuration != null;
        setName(toString(configuration.get(PARAM_NAME)));
        setDescription(toString(configuration.get(PARAM_DESCRIPTION)));
        try {
            Object iconUrlString = configuration.get(PARAM_ICON);
            if (iconUrlString != null) {
                setIconUrl(new URL(iconUrlString.toString()));
            }
        } catch (MalformedURLException e) {
            logger.warn("Icon-URL konnte nicht ge-parse-t werden.", e);
        }
        setExporterClassName(toString(configuration.get(PARAM_EXPORTER_CLASS)));
        setImporterClassName(toString(configuration.get(PARAM_IMPORTER_CLASS)));
        setMimeType(toString(configuration.get(PARAM_MIME_TYPE)));
        setDefaultExtension(toString(configuration.get(PARAM_DEFAULT_EXTENSION)));
        Object propertiesMap = configuration.get(PARAM_PROPERTIES);
        if (propertiesMap instanceof Map) {
            setProperties((Map) propertiesMap);
        }
    }

    /**
     * Diese Methode dient der sicheren Umsetzung {@link Object} nach {@link String},
     * bei der <code>null</code> erhalten bleibt.
     *
     * @param o in einen {@link String} zu überführendes Objekt.
     * @return Stringdarstellung des Parameters
     */
    final static String toString(Object o) {
        return o != null ? o.toString() : null;
    }
}
