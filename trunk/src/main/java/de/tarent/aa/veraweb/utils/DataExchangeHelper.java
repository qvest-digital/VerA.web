/*
 * $Id: DataExchangeHelper.java,v 1.1 2007/06/20 11:56:52 christoph Exp $
 * 
 * Created on 04.08.2005
 */
package de.tarent.aa.veraweb.utils;

import java.util.Map;

import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.octopus.exchange.ConfiguredExchangeFormat;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Klasse enth�lt Hilfsmethoden f�r den Datenim- und -export.
 * 
 * @author mikel
 */
public class DataExchangeHelper {
    /**
     * Diese Methode liefert zu einem Schl�ssel ein {@link ExchangeFormat}
     * gem�� den Daten der Konfiguration des aktiven Octopus-Moduls. 
     * 
     * @param cntx Octopus-Kontext
     * @param key Schl�ssel des Parameters der Modulkonfiguration, in dem
     *  das gesuchte Format definiert ist.
     * @return ein {@link ExchangeFormat} zum �bergebenen Schl�ssel oder
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
     * Diese Methode liefert einen {@link Importer} f�r das Format zu dem
     * �bergebenen Schl�ssel gem�� den Daten der Konfiguration des aktiven
     * Octopus-Moduls.
     * 
     * @param cntx Octopus-Kontext
     * @param key Schl�ssel des Parameters der Modulkonfiguration, in dem
     *  das Format zum gesuchten Importer definiert ist.
     * @return ein {@link Importer} zum �bergebenen Schl�ssel oder <code>null</code>,
     *  falls die Konfigurationsdaten unvollst�ndig sind
     * @throws ClassNotFoundException wenn die konfigurierte {@link Importer}-Klasse
     *  nicht gefunden wird
     * @throws InstantiationException wenn es Probleme w�hrend des Erzeugens einer
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
     * Diese Methode liefert einen {@link Exporter} f�r das Format zu dem
     * �bergebenen Schl�ssel gem�� den Daten der Konfiguration des aktiven
     * Octopus-Moduls.
     * 
     * @param cntx Octopus-Kontext
     * @param key Schl�ssel des Parameters der Modulkonfiguration, in dem
     *  das Format zum gesuchten Exporter definiert ist.
     * @return ein {@link Exporter} zum �bergebenen Schl�ssel oder <code>null</code>,
     *  falls die Konfigurationsdaten unvollst�ndig sind
     * @throws ClassNotFoundException wenn die konfigurierte {@link Exporter}-Klasse
     *  nicht gefunden wird
     * @throws InstantiationException wenn es Probleme w�hrend des Erzeugens einer
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
