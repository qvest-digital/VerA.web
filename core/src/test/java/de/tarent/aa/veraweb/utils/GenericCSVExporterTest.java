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
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
     *
     * @throws TcConfigException
     */
    public void testMixedExportPositive() throws IOException, TcConfigException {
        // disabled currently
        if (true) {
            return;
        }
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
