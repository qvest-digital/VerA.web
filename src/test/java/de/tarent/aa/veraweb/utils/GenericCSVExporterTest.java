/*
 * VerA.web,
 * Veranstaltungsmanagment VerA.web
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
 * interest in the program 'VerA.web'
 * Signature of Elmar Geese, 7 August 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: GenericCSVExporterTest.java,v 1.1 2007/06/20 11:56:52 christoph Exp $
 * 
 * Created on 24.08.2005
 */
package de.tarent.aa.veraweb.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.config.CommonConfig;
import de.tarent.octopus.config.OctopusConfig;
import de.tarent.octopus.config.OctopusConfigException;
import de.tarent.octopus.config.ModuleConfig;
import de.tarent.octopus.config.OctopusEnvironment;
import de.tarent.octopus.content.Content;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.exchange.ConfiguredExchangeFormat;
import de.tarent.octopus.request.Octopus;
import de.tarent.octopus.request.OctopusRequest;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.OctopusContextImpl;
import junit.framework.TestCase;

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
     * @throws OctopusConfigException 
     */
    public void testMixedExportPositive() throws IOException, OctopusConfigException {
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
    Database createDatabase() throws OctopusConfigException {
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
    
    CommonConfig createOctopusCommonConfig() throws OctopusConfigException {
        return new CommonConfig(createOctopusEnvironment(), createOctopus());
    }
    
    OctopusConfig createOctopusConfig(OctopusRequest request) throws OctopusConfigException {
        return new OctopusConfig(createOctopusCommonConfig(), createOctopusPersonalConfig(), "Test");
    }
    
    Content createOctopusContent() {
        return new Content();
    }
    
    OctopusContext createOctopusContext() throws OctopusConfigException {
        OctopusRequest request = createOctopusRequest();
        return new OctopusContextImpl(request, createOctopusContent(), createOctopusConfig(request));
    }
    
    OctopusEnvironment createOctopusEnvironment() throws OctopusConfigException {
        return new OctopusEnvironment();
    }
    
    ModuleConfig createModuleConfig() {
        return null; // Mhmpf!
    }
    
    PersonalConfigAA createOctopusPersonalConfig() {
        return new PersonalConfigAA();
    }
    
    OctopusRequest createOctopusRequest() {
        return new OctopusRequest("--Test--");
    }
}
