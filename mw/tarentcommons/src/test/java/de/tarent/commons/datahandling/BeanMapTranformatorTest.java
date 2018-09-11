package de.tarent.commons.datahandling;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2018 Dominik George (d.george@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
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
import java.util.Map;

import junit.framework.TestCase;

/**
 * Tests for {@link BeanMapTransformator}.
 *
 * @author Tim Steffens
 */
public class BeanMapTranformatorTest extends TestCase {

    BeanMapTransformator beanMapTransformator;

    /**
     * @param arg0
     */
    public BeanMapTranformatorTest(String arg0) {
        super(arg0);
        this.beanMapTransformator = new BeanMapTransformator();
    }

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
     * Test method for {@link de.tarent.commons.datahandling.BeanMapTransformator#transformBeanToMap(java.lang.Object)}.
     */
    public void testTransformBeanToMapObject() {
        Object bean = new ConcreteBeanMap();
        assertEquals("anAttributeValue", this.beanMapTransformator.transformBeanToMap(bean).get("anAttribute"));
    }

    /**
     * Test method for
     * {@link de.tarent.commons.datahandling.BeanMapTransformator#transformMapToBean(java.util.Map, java.lang.Class)}.
     */
    public void testTransformMapToBeanMapClass() {
        Map map = new HashMap();
        map.put("anAttribute", "aNewAttributeValue");
        ConcreteBeanMap bean = (ConcreteBeanMap) this.beanMapTransformator.transformMapToBean(map, ConcreteBeanMap.class);
        assertEquals("aNewAttributeValue", bean.getAnAttribute());
    }

    /**
     * Test method for
     * {@link de.tarent.commons.datahandling.BeanMapTransformator#transformBeanToMap(java.lang.Object, java.util.Map)}.
     */
    public void testTransformBeanToMapObjectMap() {
        Object bean = new ConcreteBeanMap();
        Map map = new HashMap();
        this.beanMapTransformator.transformBeanToMap(bean, map);
        assertEquals("anAttributeValue", map.get("anAttribute"));
    }

    /**
     * Test method for
     * {@link de.tarent.commons.datahandling.BeanMapTransformator#transformMapToBean(java.util.Map, java.lang.Object)}.
     */
    public void testTransformMapToBeanMapObject() {
        Map map = new HashMap();
        map.put("anAttribute", "aNewAttributeValue");
        ConcreteBeanMap bean = new ConcreteBeanMap();
        this.beanMapTransformator.transformMapToBean(map, bean);
        assertEquals("aNewAttributeValue", bean.getAnAttribute());
    }
}
