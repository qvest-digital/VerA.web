package de.tarent.commons.web;

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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.tarent.commons.utils.StringTools;

import junit.framework.TestCase;

/**
 * @author tim
 *
 * Unit test for {@link Parameters}.
 *
 */
public class ParametersTest extends TestCase {
	/**
	 * Test method for {@link de.tarent.commons.web.Parameters#encodeUrl(java.lang.String)}.
	 * @throws UnsupportedEncodingException
	 */
	public void testEncodeUrl() throws UnsupportedEncodingException {
		Parameters parameters = new Parameters();
		parameters.put("key1", "value1");
		parameters.put("key2", "value2");

		String url="http://www.tarent.de";
		String paramString1 = "key1=value1";
		String paramString2 = "key2=value2";
		assertEquals("General problems", url + "?" + paramString1 + "&amp;" + paramString2, parameters.encodeUrl(url));
		assertEquals("Problems when already parameters exists in URL", url + "?a=b&amp;" + paramString1 + "&amp;" + paramString2, parameters.encodeUrl(url + "?a=b"));
		assertEquals("Problems with withespaces", url + "?" + paramString1 + "&amp;" + paramString2, parameters.encodeUrl(" " + url + " "));
		parameters.put("key2", "v alue2");
		assertEquals("Problems with string encoding", url + "?" + paramString1 + "&amp;key2=v+alue2", parameters.encodeUrl(url));
		parameters = new Parameters();
		assertEquals("Problems with empty parameter set", url, parameters.encodeUrl(url));
	}

	/**
	 * Test method for {@link de.tarent.commons.web.Parameters#encodeUrl(java.lang.String)}.
	 * @throws UnsupportedEncodingException
	 */
	public void testEncodeUrlDecode() throws UnsupportedEncodingException {
		Parameters parameters = new Parameters();
		parameters.setNeedsEncoding(false);
		parameters.put("a", URLEncoder.encode("\u00e4\u00fc\u00f6\u00df?$", parameters.getDefaultEncoding()));
		String url="http://www.tarent.de";
		String paramString1 = "a=\u00e4\u00fc\u00f6\u00df?$";
		parameters.decodeAll();
		assertEquals("General problems", url + "?" + paramString1, parameters.encodeUrl(url));
	}

	/**
	 * Test method for {@link de.tarent.commons.web.Parameters#getHiddenHTMLFormFields()}.
	 * @throws UnsupportedEncodingException
	 */
	public void testGetHiddenHTMLFormFields() throws UnsupportedEncodingException {
		Parameters parameters = new Parameters();
		parameters.put("key1", "value1");
		parameters.put("key2", "value2");

		assertEquals("General problems",
				"<input type=\"hidden\" name=\"key1\" value=\"value1\">" + StringTools.LINE_SEPARATOR +
				"<input type=\"hidden\" name=\"key2\" value=\"value2\">" + StringTools.LINE_SEPARATOR,
				parameters.getHiddenHTMLFormFields()) ;
		parameters.put("key2", "v alue2");
		parameters.put("key2", "v alue2");
		assertEquals("Problems with string encoding",
				"<input type=\"hidden\" name=\"key1\" value=\"value1\">" + StringTools.LINE_SEPARATOR +
				"<input type=\"hidden\" name=\"key2\" value=\"v+alue2\">" + StringTools.LINE_SEPARATOR,
				parameters.getHiddenHTMLFormFields()) ;
		parameters = new Parameters();
		assertEquals("Problems with empty parameter set", "", parameters.getHiddenHTMLFormFields());
	}

	public void testNullValues() throws UnsupportedEncodingException {
		Parameters parameters = new Parameters();
		parameters.put(null, "value1");
		parameters.put("key2", null);

		parameters.encodeUrl("http://www.tarent.de");
		parameters.encodeUrl("http://www.tarent.de?");

		assertTrue(true);
	}
}
