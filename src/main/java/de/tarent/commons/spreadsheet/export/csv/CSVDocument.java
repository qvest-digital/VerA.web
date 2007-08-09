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
package de.tarent.commons.spreadsheet.export.csv;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import de.tarent.commons.spreadsheet.export.SpreadSheet;

import java.io.*;

public class CSVDocument implements SpreadSheet {
	protected Properties properties = new Properties();

    List rows;
    List currentRow;

	public void setProperty(String key, String value) throws IOException {
		properties.setProperty(key, value);
	}

	public String getProperty(String key) throws IOException {
		return properties.getProperty(key);
	}

	public String getContentType() {
		return "text/comma-separated-values";
	}

	public String getFileExtension() {
		return "csv";
	}

	public void init() throws IOException {
    }

	public void save(OutputStream outputStream) throws IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(outputStream));
        for (Iterator rowIter = rows.iterator(); rowIter.hasNext();) {
            List row = (List)rowIter.next();
            for (Iterator cellIter = row.iterator(); cellIter.hasNext();) {
                String cell = (String)cellIter.next();
                cell = cell.replaceAll("\\\"", "\\\\\"");
                out.write('"');
                out.write(cell);
                out.write('"');
                if (cellIter.hasNext())
                    out.write(';');
            }
            out.write("\n");
        }
        out.flush();
    }

	public void openTable(String name, int colCount) {
        rows = new ArrayList();
	}

	public void closeTable() {
	}

	public void openRow() {
        currentRow = new ArrayList();
        rows.add(currentRow);
	}

	public void closeRow() {
	}

	public void addCell(Object content) {        
        currentRow.add(content == null ? "" : content.toString());
	}
}
