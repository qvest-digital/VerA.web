/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id$
 * 
 * Created on 04.08.2005
 */
package de.tarent.aa.veraweb.utils;

import java.util.Map;

import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.octopus.exchange.ConfiguredExchangeFormat;
import de.tarent.octopus.server.OctopusContext;

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
     * @param cntx Octopus-Kontext
     * @param key Schlüssel des Parameters der Modulkonfiguration, in dem
     *  das gesuchte Format definiert ist.
     * @return ein {@link ExchangeFormat} zum übergebenen Schlüssel oder
     *  <code>null</code>, falls der entsprechende Konfigurationseintrag
     *  nicht gefunden wird
     */
    public static ExchangeFormat getFormat(OctopusContext cntx, String key) {
        if (key == null)
            return null;
        assert cntx != null;
        assert cntx.moduleConfig() != null;
        Object configurationMap = cntx.moduleConfig().getParamAsObject(key);
        if (configurationMap instanceof Map)
            return new ConfiguredExchangeFormat((Map) configurationMap);
        return null;
    }

    /**
     * Diese Methode liefert einen {@link Importer} für das Format zu dem
     * übergebenen Schlüssel gemäß den Daten der Konfiguration des aktiven
     * Octopus-Moduls.
     * 
     * @param cntx Octopus-Kontext
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
    public static Importer getImporter(OctopusContext cntx, String key) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ExchangeFormat format = getFormat(cntx, key);
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
     * @param cntx Octopus-Kontext
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
    public static Exporter getExporter(OctopusContext cntx, String key) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ExchangeFormat format = getFormat(cntx, key);
        if (format != null) {
            Class exporterClass = format.getExporterClass();
            if (exporterClass != null && Exporter.class.isAssignableFrom(exporterClass))
                return (Exporter) exporterClass.newInstance();
        }
        return null;
    }
}
