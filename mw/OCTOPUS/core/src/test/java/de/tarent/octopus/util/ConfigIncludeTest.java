package de.tarent.octopus.util;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2018 tarent solutions GmbH and its contributors
 * Copyright © 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.resource.Resources;

/**
 * Tests the Parsing of List Params with nested Maps
 */
public class ConfigIncludeTest extends TestCase {
    public void testRecursiveIncludes() throws Exception {
        URL url = ConfigIncludeTest.class.getResource("config.xml");

        String resource = Resources.getInstance().get("REQUESTPROXY_URL_MODULE_CONFIG", url.getFile());

        Document document = Xml.getParsedDocument(resource);

        TcModuleConfig config = new TcModuleConfig("Testname", new File(url.getFile()).getParentFile(), document);

        assertEquals("Falsche Anzahl an eingelesenen Parametern: ", 2, config.getParams().size());
    }
}
