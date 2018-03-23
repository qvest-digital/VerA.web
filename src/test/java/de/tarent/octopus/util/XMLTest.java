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
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import java.io.*;

/**
 * Tests the Parsing of List Params with nested Maps
 */
public class XMLTest
    extends junit.framework.TestCase {

    /**
     *  Void Constructor for instantiation as worker
     */
    public XMLTest() {
    }

    public XMLTest(String init) {
        super(init);
    }

    public void setUp() {

    }

    public void testNestedValues()
        throws Exception {

        String doc =
            "<param name=\"nix\" type=\"list\">\n"
            +"     <value>Hallo</value>\n"
            +"     <value>Ballo</value>\n"
            +"</param>\n";

        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = parser.parse(new ByteArrayInputStream(doc.getBytes()));
        List l = (List)Xml.getParamValue(document.getDocumentElement());

        assertEquals("Size", 2, l.size());
        assertEquals("List Element", "Hallo", l.get(0));
        assertEquals("List Element", "Ballo", l.get(1));
    }

    public void testNestedParams()
        throws Exception {

        String doc =
            "<param name=\"nix\" type=\"list\">\n"
            +"     <param value=\"Hallo\"/>\n"
            +"     <param value=\"Ballo\"/>\n"
            +"     <param type=\"map\">\n"
            +"        <param name=\"vorname\" value=\"Felix\"/>\n"
            +"        <param name=\"nachname\" value=\"Mancke\"/>\n"
            +"     </param>\n"
            +"</param>\n";

        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = parser.parse(new ByteArrayInputStream(doc.getBytes()));
        List l = (List)Xml.getParamValue(document.getDocumentElement());

        assertEquals("Size", 3, l.size());
        assertEquals("List Element", "Hallo", l.get(0));
        assertEquals("List Element", "Ballo", l.get(1));
        Map map = (Map)l.get(2);
        assertEquals("Map Size", 2, map.size());
        assertEquals("Map Elements", "Felix", map.get("vorname") );
        assertEquals("Map Elements", "Mancke", map.get("nachname") );
    }
}
