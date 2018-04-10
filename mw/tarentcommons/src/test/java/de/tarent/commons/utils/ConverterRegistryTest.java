package de.tarent.commons.utils;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
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

public class ConverterRegistryTest extends TestCase {

    Converter testConverter = new AbstractConverter() {
            public String getConverterName() {
                return "TestConverter";
            }
            public Object doConversion(Object sourceData) throws Exception {
                return "Just a test";
            }
        };

	protected void setUp() throws Exception {
        ConverterRegistry.getDefaultRegistry()
            .register(testConverter, false);
    }

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testConverterLookup() {
        assertEquals("Right converter", ConverterRegistry.getDefaultRegistry().getConverter(String.class, Boolean.class).getConverterName(), "StringToBoolean");
        assertEquals("Right converter", ConverterRegistry.getDefaultRegistry().getConverter(Boolean.class, String.class).getConverterName(), "BooleanToString");
        assertEquals("Right converter", ConverterRegistry.getDefaultRegistry().getConverter(Integer.class, String.class).getConverterName(), "ObjectToString");
        assertEquals("Right converter", testConverter, ConverterRegistry.getDefaultRegistry().getConverter("TestConverter"));
	}

	public void testSimpleConverions() {
        assertEquals("Right conversion", Boolean.TRUE, ConverterRegistry.convert(new Integer(1), Boolean.class));
        assertEquals("Right conversion", "Just a test", ConverterRegistry.convert("xxx",  "TestConverter"));
	}

	public void testPrimitiveConverions() {
        assertEquals("Right conversion", Boolean.TRUE, ConverterRegistry.convert(new Integer(1), Boolean.TYPE));
	}

	public void testNullConverions() {
        assertEquals("Primitive conversion with null", Boolean.FALSE, ConverterRegistry.convert(null, Boolean.TYPE));
        assertEquals("Complex conversion with null", null, ConverterRegistry.convert(null, Boolean.class));
	}

	public void testErrorHandling()
        throws Exception {
        try {
            ConverterRegistry.convert(this, Boolean.class);
        } catch (IllegalArgumentException e) {
            return;
        }
        throw new Exception("exception estimated");
    }

	public void testErrorHandling2()
        throws Exception {
        try {
            ConverterRegistry.convert("XXX", Integer.class);
        } catch (IllegalArgumentException e) {
            return;
        }
        throw new Exception("exception estimated");
    }
}
