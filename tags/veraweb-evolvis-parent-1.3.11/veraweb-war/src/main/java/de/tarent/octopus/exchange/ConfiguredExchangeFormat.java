/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.octopus.exchange;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.data.exchange.ExchangeFormat;

/**
 * Diese Klasse stellt die Eigenschaften eines Datenaustauschformats
 * f�r Export- und Importimplementierungen zur Verf�gung, die aus einer
 * {@link Map}, wie sie aus Octopus-Konfigurationen gelesen werden,
 * initialisiert werden.
 * 
 * @author mikel
 */
public class ConfiguredExchangeFormat extends ExchangeFormat {
    //
    // Konstruktor
    //
    /**
     * Dieser Konstruktor bekommt eine {@link Map} �bergeben, wie sie aus
     * einer Octopus-Konfiguration eingelesen wird, und initialisiert daraus
     * die {@link ExchangeFormat}-Attribute. Hierzu werden als Schl�ssel die
     * Konstanten <code>PARAM_*</code> dieser Klasse benutzt.
     * 
     * @param configuration {@link Map}, aus der die Attribute dieses Formats gelesen werden.
     */
    public ConfiguredExchangeFormat(Map configuration) {
        super();
        if (configuration != null)
            readConfiguration(configuration);
    }
    
    //
    // �ffentliche Hilfsmethoden
    //
    /**
     * Diese Methode f�gt den �ber die �rspr�ngliche Konfiguration vorgegebenen Parametern
     * weitere hinzu, die etwa �ber Benutzereingabe oder sonstige Umst�nde bestimmt werden.
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
    /** Parameterschl�ssel f�r den Name dieses Formats */
    public final static String PARAM_NAME = "name";
    /** Parameterschl�ssel f�r die Beschreibung dieses Formats */
    public final static String PARAM_DESCRIPTION = "description";
    /** Parameterschl�ssel f�r die URL zu einem Icon zu diesem Format */
    public final static String PARAM_ICON = "icon";
    /** Parameterschl�ssel f�r den Namen der {@link de.tarent.aa.veraweb.utils.Exporter}-Klasse zu diesem Format */
    public final static String PARAM_EXPORTER_CLASS = "exporterClass";
    /** Parameterschl�ssel f�r den Namen der {@link de.tarent.aa.veraweb.utils.Importer}-Klasse zu diesem Format */
    public final static String PARAM_IMPORTER_CLASS = "importerClass";
    /** Parameterschl�ssel f�r den MIME-Typ zu diesem Format */
    public final static String PARAM_MIME_TYPE = "mimeType";
    /** Parameterschl�ssel f�r das Standard-Suffix f�r Dateien dieses Formats */
    public final static String PARAM_DEFAULT_EXTENSION = "defaultExtension";
    /** Parameterschl�ssel f�r die speziellen Attribute dieses Formats */
    public final static String PARAM_PROPERTIES = "properties";

    //
    // gesch�tzte Hilfsmethoden
    //
    /**
     * Diese Methode liest aus der �bergebenen {@link Map} die Daten des Formats.
     * Als Schl�ssel werden die Konstanten <code>PARAM_*</code> benutzt.
     * 
     * @param configuration {@link Map} mit Konfigurationseintr�gen nach Octopus-Art
     */
    void readConfiguration(Map configuration) {
        assert configuration != null;
        setName(toString(configuration.get(PARAM_NAME)));
        setDescription(toString(configuration.get(PARAM_DESCRIPTION)));
        try {
            Object iconUrlString = configuration.get(PARAM_ICON);
            if (iconUrlString != null)
                setIconUrl(new URL(iconUrlString.toString()));
        } catch (MalformedURLException e) {
            logger.log(Level.WARNING, "Icon-URL konnte nicht ge-parse-t werden.", e);
        }
        setExporterClassName(toString(configuration.get(PARAM_EXPORTER_CLASS)));
        setImporterClassName(toString(configuration.get(PARAM_IMPORTER_CLASS)));
        setMimeType(toString(configuration.get(PARAM_MIME_TYPE)));
        setDefaultExtension(toString(configuration.get(PARAM_DEFAULT_EXTENSION)));
        Object propertiesMap = configuration.get(PARAM_PROPERTIES);
        if (propertiesMap instanceof Map)
            setProperties((Map)propertiesMap);
    }
    
    /**
     * Diese Methode dient der sicheren Umsetzung {@link Object} nach {@link String},
     * bei der <code>null</code> erhalten bleibt.
     * 
     * @param o in einen {@link String} zu �berf�hrendes Objekt.
     * @return Stringdarstellung des Parameters
     */
    final static String toString(Object o) {
        return o != null ? o.toString() : null;
    }
    
    //
    // gesch�tzte Member
    //
    /** Logger der Klasse */
    static Logger logger = Logger.getLogger(ConfiguredExchangeFormat.class.getName());
}
