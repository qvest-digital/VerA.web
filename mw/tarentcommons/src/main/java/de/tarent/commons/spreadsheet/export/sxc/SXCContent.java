/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */
package de.tarent.commons.spreadsheet.export.sxc;

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
 * Repräsentiert ein OpenOffice SpreadSheet XML-Baumstruktur.
 * <p>
 * <em>Als Vorlage diente eine unter Windows 2000 SP4 mit
 * OpenOffice.org 1.1.1 erzeugte SXC-Datei.</em>
 * 
 * @author Christoph Jerolimov
 */
public class SXCContent extends XMLDocument implements SpreadSheet {
	/** OpenOffice SpreadSheet MimeType */
	public static final String CONTENT_TYPE = "application/vnd.sun.xml.calc";
	/** <code>2005-03-23T12:00:00</code> */
	static final private DateFormat dateFormatValue = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	/** <code>23.03.2005 12:00</code> */
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
		return SXCContent.class.getResourceAsStream(filename);
	}

	public void init() throws IOException {
		try {
			loadDocument(getStream("content.xml"));
			
			Element element = (Element)getNode("office:document-content");
			element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:chart", "http://openoffice.org/2000/chart");
			element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:dr3d", "http://openoffice.org/2000/dr3d");
			element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:draw", "http://openoffice.org/2000/drawing");
			element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:fo", "http://www.w3.org/1999/XSL/Format");
			element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:form", "http://openoffice.org/2000/form");
			element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:math", "http://www.w3.org/1998/Math/MathML");
			element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:number", "http://openoffice.org/2000/datastyle");
			element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:office", "http://openoffice.org/2000/office");
			element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:script", "http://openoffice.org/2000/script");
			element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:style", "http://openoffice.org/2000/style");
			element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:svg", "http://www.w3.org/2000/svg");
			element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:table", "http://openoffice.org/2000/table");
			element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:text", "http://openoffice.org/2000/text");
			element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xlink", "http://www.w3.org/1999/xlink");
			
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
		return "sxc";
	}

	public void openTable(String name, int colCount) {
		this.tablename = name;
		this.columnsize = colCount;
		this.column = 0;
		spread = getNode("office:body");
		
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
			cell.setAttribute("table:value-type", "date");
			cell.setAttribute("table:date-value", dateFormatValue.format((Date)content));
			cell.setAttribute("table:style-name", "ce1");
			Element text = document.createElement("text:p");
			text.appendChild(document.createTextNode(dateFormatText.format((Date)content)));
			cell.appendChild(text);
		} else if (content instanceof Number) {
			cell.setAttribute("table:value-type", "float");
			cell.setAttribute("table:value", content.toString());
			Element text = document.createElement("text:p");
			text.appendChild(document.createTextNode(content.toString()));
			cell.appendChild(text);
		} else {
			Element text = document.createElement("text:p");
			text.appendChild(document.createTextNode(content.toString()));
			cell.appendChild(text);
		}
		row.appendChild(cell);
	}
}
