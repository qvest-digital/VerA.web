package de.tarent.data.exchange;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
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
 *  © 2013, 2014, 2015, 2016, 2017 mirabilos <t.glaser@tarent.de>
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
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Diese Testklasse testet {@link FieldMapping}.
 *
 * @author mikel
 */
public class FieldMappingTest extends TestCase {
    //
    // Tests
    //
    /**
     * (Erzeugung / Parsen der Parameter) Dieser Test erzeugt ein vermischtes
     * Mapping.
     *
     * @throws MappingException
     */
    public void testCreatePositive() throws MappingException {
        Set sources = createSources();
        Map description = createDescriptions();
        Map expectedResolve = createResolves();
        FieldMapping mapping = new FieldMapping(sources, description);
        Assert.assertEquals(sources, mapping.availableSources);
        Assert.assertEquals(expectedResolve, mapping.resolvedMappings);
        Assert.assertEquals(expectedResolve.keySet(), mapping.availableTargets);
    }

    /**
     * (Erzeugung / Parsen der Parameter) Diese Testmethode testet, ob mehrfache
     * Joker in einem FormatString zum Abbruch führen.
     */
    public void testCreateDoubleJokerFail() {
        Set sources = createSources();
        Map description = createDescriptions();
        description.put("*", "{:*} in {:*}");
        try {
            FieldMapping mapping = new FieldMapping(sources, description);
            Assert.fail("FieldMapping " + mapping + " dürfte keine mehrfachen Joker akzeptieren");
        } catch (MappingException e) {
            // Geklappt
        }
    }

    /**
     * (Erzeugung / Parsen der Parameter) Diese Testmethode testet, ob mehrfache
     * Joker in einem FormatString zum Abbruch führen.
     */
    public void testCreateInnerJokerFail() {
        Set sources = createSources();
        Map description =createDescriptions();
        description.put("Kategorie *", "{CAT:*essen}");
        try {
            FieldMapping mapping = new FieldMapping(sources, description);
            Assert.fail("FieldMapping " + mapping + " dürfte keine inneren Joker akzeptieren");
        } catch (MappingException e) {
            // Geklappt
        }
    }

    /**
     * (Auswerten) Dieser Test testet ein vermischtes Auswerten.
     *
     * @throws MappingException
     */
    public void testEvaluatePositive() throws MappingException {
        Set sources = createSources();
        Map description = createDescriptions();
        FieldMapping mapping = new FieldMapping(sources, description);
        FieldMapping.Entity entity = createEntity();
        String test = mapping.resolve("vorname", entity);
        Assert.assertEquals("Hans in Backhausen", test);
        test = mapping.resolve("Dokumenttyp Etikett (Partner)", entity);
        Assert.assertEquals("Freifrau Anneliese von Hohenstaufen-Meier", test);
    }

    //
    // Hilfsmethoden
    //
    /** Erzeugt eine gemischte Auswahl Quellen */
    Set createSources() {
        Set sources = new HashSet();
        sources.add(":vorname");
        sources.add(":nachname");
        sources.add(":straße");
        sources.add(":ort");
        sources.add("CAT:Weihnachtsessen");
        sources.add("CAT:Mafia");
        sources.add("DTM:Etikett");
        sources.add("DTP:Etikett");
        return sources;
    }

    /** erzeugt eine gemischte Auswahl Mapping-Beschreibungen */
    Map createDescriptions() {
        Map descriptions = new HashMap();
        descriptions.put("*", "{:*} in {:ort}");
        descriptions.put("Straße", "{:straße}");
        descriptions.put("Kategorie *", "{CAT:*}");
        descriptions.put("Dokumenttyp * (Hauptperson)", "{DTM:*}");
        descriptions.put("Dokumenttyp * (Partner)", "{DTP:*}");
        return descriptions;
    }

    /** erzeugt Auflösung von {@link #createDescriptions()} für {@link #createSources()} */
    Map createResolves() {
        Map expectedResolves = new HashMap();
        expectedResolves.put("vorname", "{:vorname} in {:ort}");
        expectedResolves.put("nachname", "{:nachname} in {:ort}");
        expectedResolves.put("Straße", "{:straße}");
        expectedResolves.put("Kategorie Weihnachtsessen", "{CAT:Weihnachtsessen}");
        expectedResolves.put("Kategorie Mafia", "{CAT:Mafia}");
        expectedResolves.put("Dokumenttyp Etikett (Hauptperson)", "{DTM:Etikett}");
        expectedResolves.put("Dokumenttyp Etikett (Partner)", "{DTP:Etikett}");
        return expectedResolves;
    }

    FieldMapping.Entity createEntity() {
        return new FieldMapping.Entity() {
            public String get(String sourceKey) {
                if (":vorname".equals(sourceKey))
                    return "Hans";
                if (":nachname".equals(sourceKey))
                    return "Meier";
                if (":straße".equals(sourceKey))
                    return "Optionsweg";
                if (":ort".equals(sourceKey))
                    return "Backhausen";
                if ("CAT:Weihnachtsessen".equals(sourceKey))
                    return "14";
                if ("CAT:Mafia".equals(sourceKey))
                    return "0";
                if ("DTM:Etikett".equals(sourceKey))
                    return "Seine Exzellenz Hans Meier";
                if ("DTP:Etikett".equals(sourceKey))
                    return "Freifrau Anneliese von Hohenstaufen-Meier";
                return null;
            }
        };
    }
}
