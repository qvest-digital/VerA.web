package de.tarent.commons.spreadsheet.export.csv;

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
        for (Iterator rowIter = rows.iterator(); rowIter.hasNext(); ) {
            List row = (List) rowIter.next();
            for (Iterator cellIter = row.iterator(); cellIter.hasNext(); ) {
                String cell = (String) cellIter.next();
                cell = cell.replaceAll("\\\"", "\\\\\"");
                out.write('"');
                out.write(cell);
                out.write('"');
                if (cellIter.hasNext()) {
                    out.write(';');
                }
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
