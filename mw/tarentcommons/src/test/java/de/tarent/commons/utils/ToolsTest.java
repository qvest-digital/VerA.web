package de.tarent.commons.utils;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author tim
 *
 * Unti test for {@link Tools}.
 */
public class ToolsTest extends TestCase {

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link Tools#mapIsEmpty(Map)}
     */
    public void testMapIsEmpty() {
        Map map = new HashMap();
        assertTrue("Problem with completely empty map", Tools.mapIsEmpty(map));
        map.put("test", null);
        assertTrue("Problem with null value", Tools.mapIsEmpty(map));
        map.put("test2", "");
        assertTrue("Problem with empty string", Tools.mapIsEmpty(map));
        map.put("test3", " ");
        assertTrue("Problem with whitespaces", Tools.mapIsEmpty(map));
        map.put("test4", "4");
        assertFalse("Problem with nonempty map", Tools.mapIsEmpty(map));
    }

    /**
     * Test method for {@link Tools#removeNullEntries(Map)}
     */
    public void testRemoveNullEntries() {
        Map map = new HashMap();
        Tools.removeNullEntries(map);
        assertEquals("Problem with completely empty map", 0, map.size());
        map.put("test", null);
        Tools.removeNullEntries(map);
        assertEquals("Problem with null value", 0, map.size());
        map.put("test2", "");
        Tools.removeNullEntries(map);
        assertEquals("Problem with empty string", 1, map.size());
        map.put("test3", " ");
        Tools.removeNullEntries(map);
        assertEquals("Problem with whitespaces", 2, map.size());
        map.put("test4", "4");
        Tools.removeNullEntries(map);
        assertEquals("Problem with nonempty ma", 3, map.size());
    }

    /**
     * Test method for {@link Tools#removeEmptyEntriesy(Map)}
     */
    public void testRemoveEmptyEntries() {
        Map map = new HashMap();
        Tools.removeEmptyEntries(map);
        assertEquals("Problem with completely empty map", 0, map.size());
        map.put("test", null);
        Tools.removeEmptyEntries(map);
        assertEquals("Problem with null value", 0, map.size());
        map.put("test2", "");
        Tools.removeEmptyEntries(map);
        assertEquals("Problem with empty string", 0, map.size());
        map.put("test3", " ");
        Tools.removeEmptyEntries(map);
        assertEquals("Problem with whitespaces", 0, map.size());
        map.put("test4", "4");
        Tools.removeEmptyEntries(map);
        assertEquals("Problem with nonempty ma", 1, map.size());
    }

    /**
     * Test method for {@link de.tarent.commons.utils.Tools#arrayContains(java.lang.Object[], java.lang.Object)}.
     */
    public void testArrayContains() {
        Object o1 = new Object();
        Object o2 = new Object();
        Object[] empty = new Object[] {};
        Object[] containsO1 = new Object[] { o1 };
        assertEquals("Problem with null-array", false, Tools.arrayContains(null, o1));
        assertEquals("Problem with empty array", false, Tools.arrayContains(empty, o1));
        assertEquals("Problem with non-empty array containing o1", true, Tools.arrayContains(containsO1, o1));
        assertEquals("Problem with non-empty array not containing o2", false, Tools.arrayContains(containsO1, o2));
    }

    /**
     * Test method for {@link de.tarent.commons.utils.Tools#putIfNotNull(java.util.Map, java.lang.Object, java.lang.Object)}.
     */
    public void testPutIfNotNull() {
        Object o1 = "o1";
        Object o2 = "o2";
        Map map = new HashMap();
        Tools.putIfNotNull(map, o1, null);
        assertEquals("Problem with null value", false, map.containsKey(o1));
        Tools.putIfNotNull(map, null, o2);
        assertEquals("Problem with null key", false, map.containsValue(o2));
        Tools.putIfNotNull(map, o1, o2);
        assertEquals("Problem with two existing values", true, map.containsKey(o1));
        assertEquals("Problem with two existing values", true, map.containsValue(o2));
    }

    /**
     * Test method for {@link Tools#iteratorToString(java.util.Iterator, String, boolean, String, boolean)}
     */
    public void testIteratortoString() {
        List linkList = new LinkedList();
        assertEquals("Problems with empty list",
          "",
          Tools.iteratorToString(linkList.iterator(), "-", true, "-", true));
        linkList.add("First element");
        linkList.add("Second element");
        assertEquals("Problems with prefix and suffix (also before first and after last)",
          "-First element--Second element-",
          Tools.iteratorToString(linkList.iterator(), "-", true, "-", true));
        assertEquals("Problems with prefix and suffix (not before first and after last)",
          "First element--Second element",
          Tools.iteratorToString(linkList.iterator(), "-", false, "-", false));
    }

    /**
     * Test method for {@link Tools#mapToString(Map, String, boolean, String, String, boolean)}
     */
    public void testMapToString() {
        Map map = new LinkedHashMap();
        assertEquals("Problems with empty list",
          "",
          Tools.mapToString(map, "-", true, "=", "-", true));
        map.put("Key1", "Value1");
        map.put("Key2", "Value2");
        assertEquals("Problems with prefix and suffix (also before first and after last)",
          "-Key1=Value1--Key2=Value2-",
          Tools.mapToString(map, "-", true, "=", "-", true));
        assertEquals("Problems with prefix and suffix (not before first and after last)",
          "Key1=Value1--Key2=Value2",
          Tools.mapToString(map, "-", false, "=", "-", false));
    }

    /**
     * Test method for {@link Tools#arrayToString(java.util.Iterator, String, boolean, String, boolean)}
     */
    public void testArraytoString() {
        Object[] array = new Object[0];
        assertEquals("Problems with empty Array",
          "",
          Tools.arrayToString(array, "-", true, "-", true));
        array = new Object[] { "First element", "Second element" };
        assertEquals("Problems with prefix and suffix (also before first and after last)",
          "-First element--Second element-",
          Tools.arrayToString(array, "-", true, "-", true));
        assertEquals("Problems with prefix and suffix (not before first and after last)",
          "First element--Second element",
          Tools.arrayToString(array, "-", false, "-", false));
    }
}
