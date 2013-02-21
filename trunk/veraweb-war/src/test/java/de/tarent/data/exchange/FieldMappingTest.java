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
 * Created on 23.08.2005
 */
package de.tarent.data.exchange;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

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
