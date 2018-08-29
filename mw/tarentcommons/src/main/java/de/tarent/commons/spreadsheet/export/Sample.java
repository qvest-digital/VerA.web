package de.tarent.commons.spreadsheet.export;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2018 Dominik George (d.george@tarent.de)
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Beispiel-Anwendung
 *
 * @author Christoph Jerolimov
 */
public class Sample {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            testXML(SpreadSheetFactory.getSpreadSheet(SpreadSheetFactory.TYPE_SXC_CONTENT));
            testXML(SpreadSheetFactory.getSpreadSheet(SpreadSheetFactory.TYPE_ODS_CONTENT));
            testFile(SpreadSheetFactory.getSpreadSheet(SpreadSheetFactory.TYPE_SXC_DOCUMENT));
            testFile(SpreadSheetFactory.getSpreadSheet(SpreadSheetFactory.TYPE_ODS_DOCUMENT));
            testFile(SpreadSheetFactory.getSpreadSheet(SpreadSheetFactory.TYPE_XLS_DOCUMENT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testXML(SpreadSheet spreadSheet) throws IOException {
        spreadSheet.init();
        test(spreadSheet);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        spreadSheet.save(os);
        os.close();
        //		System.out.println(os.toString());
    }

    public static void testFile(SpreadSheet spreadSheet) throws IOException {
        spreadSheet.init();
        test(spreadSheet);
        FileOutputStream os = new FileOutputStream(getFilename(spreadSheet.getFileExtension()));
        spreadSheet.save(os);
        os.close();
    }

    public static void test(SpreadSheet content) throws IOException {
        content.openTable("Tabelle 1", 3);
        content.openRow();
        content.addCell("A1");
        content.addCell("B1");
        content.addCell("C1");
        content.addCell("D1");
        content.closeRow();
        content.openRow();
        content.addCell(null);
        content.addCell("A2");
        content.addCell("");
        content.closeRow();
        content.openRow();
        content.addCell(new Integer(new Random().nextInt()));
        content.addCell(new Long(new Random().nextLong()));
        content.addCell(new Double(new Random().nextDouble()));
        content.closeRow();
        content.openRow();
        content.addCell(new BigInteger(new Long(new Random().nextLong()).toString()));
        content.addCell(new BigDecimal(new Long(new Random().nextLong()).toString()));
        content.addCell(new Date());
        content.closeRow();
        content.openRow();
        content.addCell("A3");
        content.addCell("<test>");
        content.addCell("C3\n\ntest");
        content.closeRow();
        content.closeTable();
    }

    /**
     * @param extension Dateierweiterung
     * @return Datei auf dem Desktop bestehend aus Datum und Zeit.
     */
    protected static File getFilename(String extension) {
        // Sollte unter Windows + KDE + Gnome funktionieren.
        return new File(
          System.getProperty("user.home") + "/Desktop/" +
            new SimpleDateFormat("MM-dd_HH-mm").format(new Date()) +
            '.' + extension);
    }
}
