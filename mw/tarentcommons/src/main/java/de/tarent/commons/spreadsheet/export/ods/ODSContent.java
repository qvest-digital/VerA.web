package de.tarent.commons.spreadsheet.export.ods;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.tarent.commons.spreadsheet.export.SpreadSheet;
import de.tarent.commons.spreadsheet.export.SpreadSheetFactory;
import de.tarent.commons.spreadsheet.export.XMLDocument;

/*
 * GENERATE:
 *
 * <table:table table:name="Tabelle 1" table:style-name="ta1" table:print="false">
 * <table:table-column table:style-name="co1" table:number-columns-repeated="3" table:default-cell-style-name="Default"/>
 *
 * <table:table-row table:style-name="ro1">
 * <table:table-cell office:value-type="string"><text:p>A1</text:p></table:table-cell>
 * <table:table-cell office:value-type="string"><text:p>B1</text:p></table:table-cell>
 * <table:table-cell office:value-type="string"><text:p>C1</text:p></table:table-cell>
 * </table:table-row>
 * <table:table-row table:style-name="ro1">
 * <table:table-cell office:value-type="string"><text:p>A2</text:p></table:table-cell>
 * <table:table-cell/>
 * <table:table-cell/>
 * </table:table-row>
 * </table:table>
 */

/**
 * Repräsentiert ein OpenDocument SpreadSheet XML-Baumstruktur.
 * <p>
 * <em>Als Vorlage diente eine unter Windows 2000 SP4 mit
 * OpenOffice.org 1.9.79 erzeugte ODS-Datei.</em>
 *
 * @author Christoph Jerolimov
 */
public class ODSContent extends XMLDocument implements SpreadSheet {
    /**
     * OpenDocument SpreadSheet MimeType
     */
    public static final String CONTENT_TYPE = "application/vnd.oasis.opendocument.spreadsheet";
    /**
     * <code>2005-03-23T12:00:00</code>
     */
    static final private DateFormat dateFormatValue = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    /**
     * <code>23.03.2005 12:00</code>
     */
    static final private DateFormat dateFormatText = new SimpleDateFormat(SpreadSheetFactory.bundle.getString("DATE_TIME"));

    protected Properties properties = new Properties();

    protected String tablename;
    protected int columnsize;

    protected Node spread;
    protected Node table;
    protected Node row;
    protected int column;

    public void setProperty(String key, String value) throws IOException {
        properties.setProperty(key, value);
    }

    public String getProperty(String key) throws IOException {
        String p = properties.getProperty(key);
        if (p == null) {
            throw new IOException("Einstellung '" + key + "' ist nicht gesetzt!");
        }
        return p;
    }

    protected InputStream getStream(String filename) throws IOException {
        return ODSContent.class.getResourceAsStream(filename);
    }

    public void init() throws IOException {
        try {
            loadDocument(getStream("content.xml"));

            Element element = (Element) getNode("office:document-content");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:chart",
              "urn:oasis:names:tc:opendocument:xmlns:chart:1.0");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:dc", "http://purl.org/dc/elements/1.1/");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:dom", "http://www.w3.org/2001/xml-events");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:dr3d",
              "urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:draw",
              "urn:oasis:names:tc:opendocument:xmlns:drawing:1.0");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:fo", "http://www.w3.org/1999/XSL/Format");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:form",
              "urn:oasis:names:tc:opendocument:xmlns:form:1.0");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:math", "http://www.w3.org/1998/Math/MathML");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:meta",
              "urn:oasis:names:tc:opendocument:xmlns:meta:1.0");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:number",
              "urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:office",
              "urn:oasis:names:tc:opendocument:xmlns:office:1.0");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ooo", "http://openoffice.org/2004/office");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:oooc", "http://openoffice.org/2004/calc");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ooow", "http://openoffice.org/2004/writer");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:script",
              "urn:oasis:names:tc:opendocument:xmlns:script:1.0");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:style",
              "urn:oasis:names:tc:opendocument:xmlns:style:1.0");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:svg", "http://www.w3.org/2000/svg");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:table",
              "urn:oasis:names:tc:opendocument:xmlns:table:1.0");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:text",
              "urn:oasis:names:tc:opendocument:xmlns:text:1.0");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xforms", "http://www.w3.org/2002/xforms");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xlink", "http://www.w3.org/1999/xlink");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        } catch (Exception e) {
            throwIOException(e);
        }
    }

    public void save(OutputStream outputStream) throws IOException {
        saveDocument(outputStream);
    }

    public String getContentType() {
        return CONTENT_TYPE;
    }

    public String getFileExtension() {
        return "ods";
    }

    public void openTable(String name, int colCount) {
        this.tablename = name;
        this.columnsize = colCount;
        this.column = 0;
        spread = getNode("office:spreadsheet");

        Element t = document.createElement("table:table");
        t.setAttribute("table:name", tablename);
        t.setAttribute("table:style-name", "ta1");
        t.setAttribute("table:print", "false");
        Element tc = document.createElement("table:table-column");
        tc.setAttribute("table:style-name", "co1");
        tc.setAttribute("table:number-columns-repeated", Integer.toString(columnsize));
        tc.setAttribute("table:default-cell-style-name", "Default");
        table = t;

        spread.appendChild(t);
        spread.appendChild(tc);
    }

    public void closeTable() {
    }

    public void openRow() {
        this.column = 0;

        Element tr = document.createElement("table:table-row");
        tr.setAttribute("table:style-name", "ro1");
        row = tr;

        table.appendChild(tr);
    }

    public void closeRow() {
        if (column < columnsize) {
            while (column < columnsize) {
                addCell(null);
            }
        }
    }

    public void addCell(Object content) {
        column++;

        Element cell = document.createElement("table:table-cell");
        if (content == null) {
            // nothing
        } else if (content instanceof Date) {
            cell.setAttribute("office:value-type", "date");
            cell.setAttribute("office:date-value", dateFormatValue.format((Date) content));
            cell.setAttribute("table:style-name", "ce1");
            Element text = document.createElement("text:p");
            text.appendChild(document.createTextNode(dateFormatText.format((Date) content)));
            cell.appendChild(text);
        } else if (content instanceof Number) {
            cell.setAttribute("office:value-type", "float");
            cell.setAttribute("office:value", content.toString());
            Element text = document.createElement("text:p");
            text.appendChild(document.createTextNode(content.toString()));
            cell.appendChild(text);
        } else {
            cell.setAttribute("office:value-type", "string");
            Element text = document.createElement("text:p");
            text.appendChild(document.createTextNode(content.toString()));
            cell.appendChild(text);
        }
        row.appendChild(cell);
    }
}
