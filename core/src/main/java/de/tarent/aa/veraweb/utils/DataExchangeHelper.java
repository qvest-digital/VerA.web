package de.tarent.aa.veraweb.utils;

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
import de.tarent.octopus.exchange.ConfiguredExchangeFormat;
import de.tarent.octopus.server.OctopusContext;

import java.util.Map;

/**
 * Diese Klasse enthält Hilfsmethoden für den Datenim- und -export.
 *
 * @author mikel
 */
public class DataExchangeHelper {
    /**
     * Diese Methode liefert zu einem Schlüssel ein {@link ExchangeFormat}
     * gemäß den Daten der Konfiguration des aktiven Octopus-Moduls.
     *
     * @param octopusContext Octopus-Kontext
     * @param key            Schlüssel des Parameters der Modulkonfiguration, in dem
     *                       das gesuchte Format definiert ist.
     * @return ein {@link ExchangeFormat} zum übergebenen Schlüssel oder
     * <code>null</code>, falls der entsprechende Konfigurationseintrag
     * nicht gefunden wird
     */
    public static ExchangeFormat getFormat(OctopusContext octopusContext, String key) {
        if (key == null) {
            return null;
        }
        assert octopusContext != null;
        assert octopusContext.moduleConfig() != null;
        Object configurationMap = octopusContext.moduleConfig().getParamAsObject(key);
        if (configurationMap instanceof Map) {
            return new ConfiguredExchangeFormat((Map) configurationMap);
        }
        return null;
    }

    /**
     * Diese Methode liefert einen {@link Importer} für das Format zu dem
     * übergebenen Schlüssel gemäß den Daten der Konfiguration des aktiven
     * Octopus-Moduls.
     *
     * @param octopusContext Octopus-Kontext
     * @param key            Schlüssel des Parameters der Modulkonfiguration, in dem
     *                       das Format zum gesuchten Importer definiert ist.
     * @return ein {@link Importer} zum übergebenen Schlüssel oder <code>null</code>,
     * falls die Konfigurationsdaten unvollständig sind
     * @throws ClassNotFoundException wenn die konfigurierte {@link Importer}-Klasse
     *                                nicht gefunden wird
     * @throws InstantiationException wenn es Probleme während des Erzeugens einer
     *                                Instanz des {@link Importer}s gab.
     * @throws IllegalAccessException wenn Berechtigungsprobleme das Erzeugen einer
     *                                {@link Importer}-Instanz verhindert haben.
     */
    public static Importer getImporter(OctopusContext octopusContext, String key)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ExchangeFormat format = getFormat(octopusContext, key);
        if (format != null) {
            Class importerClass = format.getImporterClass();
            if (importerClass != null && Importer.class.isAssignableFrom(importerClass)) {
                return (Importer) importerClass.newInstance();
            }
        }
        return null;
    }

    /**
     * Diese Methode liefert einen {@link Exporter} für das Format zu dem
     * übergebenen Schlüssel gemäß den Daten der Konfiguration des aktiven
     * Octopus-Moduls.
     *
     * @param octopusContext Octopus-Kontext
     * @param key            Schlüssel des Parameters der Modulkonfiguration, in dem
     *                       das Format zum gesuchten Exporter definiert ist.
     * @return ein {@link Exporter} zum übergebenen Schlüssel oder <code>null</code>,
     * falls die Konfigurationsdaten unvollständig sind
     * @throws ClassNotFoundException wenn die konfigurierte {@link Exporter}-Klasse
     *                                nicht gefunden wird
     * @throws InstantiationException wenn es Probleme während des Erzeugens einer
     *                                Instanz des {@link Exporter}s gab.
     * @throws IllegalAccessException wenn Berechtigungsprobleme das Erzeugen einer
     *                                {@link Exporter}-Instanz verhindert haben.
     */
    public static Exporter getExporter(OctopusContext octopusContext, String key)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ExchangeFormat format = getFormat(octopusContext, key);
        if (format != null) {
            Class exporterClass = format.getExporterClass();
            if (exporterClass != null && Exporter.class.isAssignableFrom(exporterClass)) {
                return (Exporter) exporterClass.newInstance();
            }
        }
        return null;
    }
}
