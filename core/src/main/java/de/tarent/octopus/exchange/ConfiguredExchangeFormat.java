package de.tarent.octopus.exchange;

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
import de.tarent.data.exchange.ExchangeFormat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Diese Klasse stellt die Eigenschaften eines Datenaustauschformats
 * für Export- und Importimplementierungen zur Verfügung, die aus einer
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
     * Dieser Konstruktor bekommt eine {@link Map} übergeben, wie sie aus
     * einer Octopus-Konfiguration eingelesen wird, und initialisiert daraus
     * die {@link ExchangeFormat}-Attribute. Hierzu werden als Schlüssel die
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
    /** Parameterschlüssel für den Name dieses Formats */
    public final static String PARAM_NAME = "name";
    /** Parameterschlüssel für die Beschreibung dieses Formats */
    public final static String PARAM_DESCRIPTION = "description";
    /** Parameterschlüssel für die URL zu einem Icon zu diesem Format */
    public final static String PARAM_ICON = "icon";
    /** Parameterschlüssel für den Namen der {@link de.tarent.aa.veraweb.utils.Exporter}-Klasse zu diesem Format */
    public final static String PARAM_EXPORTER_CLASS = "exporterClass";
    /** Parameterschlüssel für den Namen der {@link de.tarent.aa.veraweb.utils.Importer}-Klasse zu diesem Format */
    public final static String PARAM_IMPORTER_CLASS = "importerClass";
    /** Parameterschlüssel für den MIME-Typ zu diesem Format */
    public final static String PARAM_MIME_TYPE = "mimeType";
    /** Parameterschlüssel für das Standard-Suffix für Dateien dieses Formats */
    public final static String PARAM_DEFAULT_EXTENSION = "defaultExtension";
    /** Parameterschlüssel für die speziellen Attribute dieses Formats */
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
     * @param o in einen {@link String} zu überführendes Objekt.
     * @return Stringdarstellung des Parameters
     */
    final static String toString(Object o) {
        return o != null ? o.toString() : null;
    }

    //
    // geschützte Member
    //
    /** Logger der Klasse */
    static Logger logger = Logger.getLogger(ConfiguredExchangeFormat.class.getName());
}
