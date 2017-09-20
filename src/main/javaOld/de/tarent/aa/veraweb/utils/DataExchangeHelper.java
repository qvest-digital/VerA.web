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
     * @param key Schlüssel des Parameters der Modulkonfiguration, in dem
     *  das gesuchte Format definiert ist.
     * @return ein {@link ExchangeFormat} zum übergebenen Schlüssel oder
     *  <code>null</code>, falls der entsprechende Konfigurationseintrag
     *  nicht gefunden wird
     */
    public static ExchangeFormat getFormat(OctopusContext octopusContext, String key) {
        if (key == null)
            return null;
        assert octopusContext != null;
        assert octopusContext.moduleConfig() != null;
        Object configurationMap = octopusContext.moduleConfig().getParamAsObject(key);
        if (configurationMap instanceof Map)
            return new ConfiguredExchangeFormat((Map) configurationMap);
        return null;
    }

    /**
     * Diese Methode liefert einen {@link Importer} für das Format zu dem
     * übergebenen Schlüssel gemäß den Daten der Konfiguration des aktiven
     * Octopus-Moduls.
     *
     * @param octopusContext Octopus-Kontext
     * @param key Schlüssel des Parameters der Modulkonfiguration, in dem
     *  das Format zum gesuchten Importer definiert ist.
     * @return ein {@link Importer} zum übergebenen Schlüssel oder <code>null</code>,
     *  falls die Konfigurationsdaten unvollständig sind
     * @throws ClassNotFoundException wenn die konfigurierte {@link Importer}-Klasse
     *  nicht gefunden wird
     * @throws InstantiationException wenn es Probleme während des Erzeugens einer
     *  Instanz des {@link Importer}s gab.
     * @throws IllegalAccessException wenn Berechtigungsprobleme das Erzeugen einer
     *  {@link Importer}-Instanz verhindert haben.
     */
    public static Importer getImporter(OctopusContext octopusContext, String key) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
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
     * @param key Schlüssel des Parameters der Modulkonfiguration, in dem
     *  das Format zum gesuchten Exporter definiert ist.
     * @return ein {@link Exporter} zum übergebenen Schlüssel oder <code>null</code>,
     *  falls die Konfigurationsdaten unvollständig sind
     * @throws ClassNotFoundException wenn die konfigurierte {@link Exporter}-Klasse
     *  nicht gefunden wird
     * @throws InstantiationException wenn es Probleme während des Erzeugens einer
     *  Instanz des {@link Exporter}s gab.
     * @throws IllegalAccessException wenn Berechtigungsprobleme das Erzeugen einer
     *  {@link Exporter}-Instanz verhindert haben.
     */
    public static Exporter getExporter(OctopusContext octopusContext, String key) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ExchangeFormat format = getFormat(octopusContext, key);
        if (format != null) {
            Class exporterClass = format.getExporterClass();
            if (exporterClass != null && Exporter.class.isAssignableFrom(exporterClass))
                return (Exporter) exporterClass.newInstance();
        }
        return null;
    }
}
