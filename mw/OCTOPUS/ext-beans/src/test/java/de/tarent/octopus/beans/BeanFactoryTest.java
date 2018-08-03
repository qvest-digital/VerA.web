package de.tarent.octopus.beans;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

/**
 * This class test the functionality from the {@link BeanFactory}.
 *
 * @author Christoph Jerolimov
 */
public class BeanFactoryTest extends TestCase {
    /**
     * Simple test of the {@link BeanFactory#transform(Object, Class)} method,
     * verifiy same correct date inputs.
     *
     * @throws Exception
     */
    public void testGetDateSimpleCorrect() throws Exception {
        assertDate("01.01.2000", "1.1.00");
        assertDate("01.01.2000", "1.1.2000");
    }

    /**
     * Simple test of the {@link BeanFactory#transform(Object, Class)} method,
     * verifiy same incorrect date inputs.
     *
     * @throws Exception
     */
    public void testGetDateSimpleInvalid() throws Exception {
        try {
            assertDate("32.01.2000", "32.1.00");
            assertTrue(false);
        } catch (BeanException e) {
            assertTrue(true);
        }
        try {
            assertDate("32.01.2000", "32.1.2000");
            assertTrue(false);
        } catch (BeanException e) {
            assertTrue(true);
        }
    }

    /**
     * Test the {@link BeanFactory#transform(Object, Class)} method with
     * two digit year dates, in german.
     *
     * @throws Exception
     */
    public void testGetDateTwoDigitsGerman() throws Exception {
        for (int i = 0; i <= 99; i++) {
            assertDate(
              "01.01." + (i < 10 ? "0" + i : "" + i),
              "01.01." + (i < 10 ? "0" + i : "" + i));
        }
    }

    /**
     * Test the {@link BeanFactory#transform(Object, Class)} method with
     * two digit year dates, in english.
     *
     * @throws Exception
     */
    public void testGetDateTwoDigitsEnglish() throws Exception {
        for (int i = 0; i <= 99; i++) {
            assertDate(
              "01.01." + (i < 10 ? "0" + i : "" + i),
              "01-01-" + (i < 10 ? "0" + i : "" + i));
        }
    }

    /**
     * Test the {@link BeanFactory#transform(Object, Class)} method with
     * four digit year dates, in german.
     *
     * @throws Exception
     */
    public void testGetDateFourDigitsGerman() throws Exception {
        for (int i = 1500; i <= 2500; i++) {
            assertDate("01.01." + i, "01.01." + i);
        }
    }

    /**
     * Test the {@link BeanFactory#transform(Object, Class)} method with
     * four digit year dates, in english.
     *
     * @throws Exception
     */
    public void testGetDateFourDigitsEnglish() throws Exception {
        for (int i = 1500; i <= 2500; i++) {
            assertDate("01.01." + i, "01-01-" + i);
        }
    }

    /**
     * Test some date input.
     *
     * @param expectedInput Expected date in the german format <code>d.M.y</code>.
     * @param actualInput   The "userinput" which will be verified.
     * @throws BeanException
     * @throws ParseException
     */
    private void assertDate(String expectedInput, String actualInput) throws BeanException, ParseException {
        Date expected = new SimpleDateFormat("d.M.y").parse(expectedInput);
        Date actual = (Date) BeanFactory.transform(actualInput, Date.class);
        assertEquals(expected, actual);
    }
}
