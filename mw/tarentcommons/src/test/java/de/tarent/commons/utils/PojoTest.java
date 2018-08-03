package de.tarent.commons.utils;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
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

import junit.framework.TestCase;

import java.lang.reflect.Method;

public class PojoTest extends TestCase {

    SimplePersonPojo pojo;
    SimplePersonPojoExt extPojo;

    protected void setUp() throws Exception {
        pojo = new SimplePersonPojo();
        pojo.setName("Mancke");
        pojo.setMale(true);
        pojo.setAge(26);

        extPojo = new SimplePersonPojoExt();
        extPojo.setName("Mancke");
        extPojo.setMale(true);
        extPojo.setAge(26);
    }

    protected void tearDown() throws Exception {
    }

    public void testGetter() {
        Method m = Pojo.getGetMethod(pojo, "name");
        assertEquals("Right value", "Mancke", Pojo.get(pojo, m));

        Method m2 = Pojo.getGetMethod(pojo, "nAmE");
        assertNull("No method for wrong case", m2);

        Method m3 = Pojo.getGetMethod(pojo, "nAmE", true);
        assertEquals("Right value", "Mancke", Pojo.get(pojo, m3));
    }

    public void testFastGetter() {
        assertEquals("Right value", "Mancke", Pojo.get(pojo, "name"));
        assertEquals("Right value", Boolean.TRUE, Pojo.get(pojo, "male"));
        assertEquals("Right value", new Integer(26), Pojo.get(pojo, "age"));
    }

    public void testFastGetterOnExtendedPojos() {
        assertEquals("Right value", "Mancke", Pojo.get(extPojo, "name"));
        assertEquals("Right value", Boolean.TRUE, Pojo.get(extPojo, "male"));
        assertEquals("Right value", new Integer(26), Pojo.get(extPojo, "age"));
    }

    public void testSetter() {
        Method m = Pojo.getSetMethod(pojo, "name");
        Pojo.set(pojo, m, "Meyer");
        assertEquals("Right value set", "Meyer", pojo.getName());

        Method m2 = Pojo.getSetMethod(pojo, "nAmE");
        assertNull("No method for wrong case", m2);

        Method m3 = Pojo.getSetMethod(pojo, "nAmE", true);
        assertEquals("Right setter method", m, m3);
    }

    public void testFastSetter() {
        Pojo.set(pojo, "name", "Meyer");
        Pojo.set(pojo, "male", Boolean.FALSE);
        Pojo.set(pojo, "age", new Integer(18));

        assertEquals("Right value set", "Meyer", pojo.getName());
        assertEquals("Right value set", false, pojo.isMale());
        assertEquals("Right value set", 18, pojo.getAge());
    }

    public void testWithConversion() {
        Pojo.set(pojo, "name", new AnyObject("Meyer"));
        Pojo.set(pojo, "male", "false");
        Pojo.set(pojo, "age", "18");
        assertEquals("Right value set", "Meyer", pojo.getName());
        assertEquals("Right value set", false, pojo.isMale());
        assertEquals("Right value set", 18, pojo.getAge());

        Pojo.set(pojo, "male", new Integer(0));
        Pojo.set(pojo, "age", new Float(18f));
        assertEquals("Right value set", false, pojo.isMale());
        assertEquals("Right value set", 18, pojo.getAge());

        Pojo.set(pojo, "male", null);
        Pojo.set(pojo, "age", null);
        assertEquals("Right value set", false, pojo.isMale());
        assertEquals("Right value set", 0, pojo.getAge());
    }

    public void testErrorHandlingWrongProperty()
      throws Exception {
        try {
            Pojo.set(pojo, "sdcsdcsd", null);
        } catch (IllegalArgumentException e) {
            return;
        }
        throw new Exception("exception estimated");
    }

    public void testErrorHandlingMissingConverter()
      throws Exception {
        try {
            Pojo.set(pojo, "age", new AnyObject("23"));
        } catch (IllegalArgumentException e) {
            return;
        }
        throw new Exception("exception estimated");
    }

    public void testErrorHandlingTargetException()
      throws Exception {
        try {
            Pojo.set(pojo, "ExceptionDummy", null);
        } catch (RuntimeException e) {
            return;
        }
        throw new Exception("exception estimated");
    }

    public class AnyObject {
        String value;

        public AnyObject(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

    public class SimplePersonPojo {
        String name;
        int age;
        boolean male;

        public void setExeptionDummy(Object o) {
            throw new RuntimeException("Have fun");
        }

        public boolean isMale() {
            return male;
        }

        public void setMale(boolean newMale) {
            this.male = newMale;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int newAge) {
            this.age = newAge;
        }

        public String getName() {
            return name;
        }

        public void setName(String newName) {
            this.name = newName;
        }
    }

    public class SimplePersonPojoExt extends SimplePersonPojo {

    }
}
