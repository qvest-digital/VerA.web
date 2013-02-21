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
 * Created on 24.08.2005
 */
package de.tarent.aa.veraweb.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcConfigException;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcAll;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.exchange.ConfiguredExchangeFormat;
import de.tarent.octopus.request.Octopus;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Testklasse testet {@link GenericCSVExporter}. 
 * 
 * @author mikel
 */
public class GenericCSVExporterTest extends TestCase {
    //
    // Tests
    //
    /**
     * Diese Testmethode testet einen gemischten Export.
     * @throws TcConfigException 
     */
    public void testMixedExportPositive() throws IOException, TcConfigException {
    	// disabled currently
    	if (true)
    		return;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        GenericCSVExporter exporter = new GenericCSVExporter();
        exporter.setDatabase(createDatabase());
        exporter.setExchangeFormat(createExchangeFormat());
        exporter.setOutputStream(baOutputStream);
        
        exporter.startExport();
        exporter.endExport();
    }
    
    //
    // Hilfsmethoden
    //
    Database createDatabase() throws TcConfigException {
        return new DatabaseVeraWeb(createOctopusContext());
    }
    
    ExchangeFormat createExchangeFormat() {
        Map exportMapping = new HashMap();
        exportMapping.put("*", "{:*}");
        exportMapping.put("Kategorie *", "{CAT:*}");
        exportMapping.put("Ereignis *", "{EVE:*}");
        exportMapping.put("Dokumenttyp * (Hauptperson)", "{DTM:*}");
        exportMapping.put("Dokumenttyp * (Partner)", "{DTP:*}");
        Map properties = new HashMap();
        properties.put("encoding", "UTF-8");
        properties.put("exportMapping", Collections.unmodifiableMap(exportMapping));
        Map configuration = new HashMap();
        configuration.put("name", "CSV");
        configuration.put("description", "Comma-Separated-Values");
        configuration.put("defaultExtension", ".csv");
        configuration.put("mimeType", "text/csv");
        //configuration.put("icon", "");
        configuration.put("exporterClass", "de.tarent.aa.veraweb.utils.GenericCSVExporter");
        configuration.put("importerClass", "de.tarent.aa.veraweb.utils.GenericCSVImporter");
        configuration.put("properties", Collections.unmodifiableMap(properties));
        return new ConfiguredExchangeFormat(Collections.unmodifiableMap(configuration));
    }
    
    //
    // Octopus-Mockups
    //
    Octopus createOctopus() {
        return new Octopus();
    }
    
    TcCommonConfig createOctopusCommonConfig() throws TcConfigException {
        return new TcCommonConfig(createOctopusEnvironment(), createOctopus());
    }
    
    TcConfig createOctopusConfig(TcRequest request) throws TcConfigException {
        return new TcConfig(createOctopusCommonConfig(), createOctopusPersonalConfig(), "Test");
    }
    
    TcContent createOctopusContent() {
        return new TcContent();
    }
    
    OctopusContext createOctopusContext() throws TcConfigException {
        TcRequest request = createOctopusRequest();
        return new TcAll(request, createOctopusContent(), createOctopusConfig(request));
    }
    
    TcEnv createOctopusEnvironment() {
        return new TcEnv();
    }
    
    TcModuleConfig createModuleConfig() {
        return null; // Mhmpf!
    }
    
    PersonalConfigAA createOctopusPersonalConfig() {
        return new PersonalConfigAA();
    }
    
    TcRequest createOctopusRequest() {
        return new TcRequest("--Test--");
    }
}
