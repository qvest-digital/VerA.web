package de.tarent.octopus.util;
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
     * Void Constructor for instantiation as worker
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
            + "     <value>Hallo</value>\n"
            + "     <value>Ballo</value>\n"
            + "</param>\n";

        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = parser.parse(new ByteArrayInputStream(doc.getBytes()));
        List l = (List) Xml.getParamValue(document.getDocumentElement());

        assertEquals("Size", 2, l.size());
        assertEquals("List Element", "Hallo", l.get(0));
        assertEquals("List Element", "Ballo", l.get(1));
    }

    public void testNestedParams()
      throws Exception {

        String doc =
          "<param name=\"nix\" type=\"list\">\n"
            + "     <param value=\"Hallo\"/>\n"
            + "     <param value=\"Ballo\"/>\n"
            + "     <param type=\"map\">\n"
            + "        <param name=\"vorname\" value=\"Felix\"/>\n"
            + "        <param name=\"nachname\" value=\"Mancke\"/>\n"
            + "     </param>\n"
            + "</param>\n";

        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = parser.parse(new ByteArrayInputStream(doc.getBytes()));
        List l = (List) Xml.getParamValue(document.getDocumentElement());

        assertEquals("Size", 3, l.size());
        assertEquals("List Element", "Hallo", l.get(0));
        assertEquals("List Element", "Ballo", l.get(1));
        Map map = (Map) l.get(2);
        assertEquals("Map Size", 2, map.size());
        assertEquals("Map Elements", "Felix", map.get("vorname"));
        assertEquals("Map Elements", "Mancke", map.get("nachname"));
    }
}
