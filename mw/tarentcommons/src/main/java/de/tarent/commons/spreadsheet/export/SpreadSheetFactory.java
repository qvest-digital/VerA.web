package de.tarent.commons.spreadsheet.export;

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

import java.util.ResourceBundle;

import de.tarent.commons.spreadsheet.export.csv.CSVDocument;
import de.tarent.commons.spreadsheet.export.ods.ODSContent;
import de.tarent.commons.spreadsheet.export.ods.ODSDocument;
import de.tarent.commons.spreadsheet.export.sxc.SXCContent;
import de.tarent.commons.spreadsheet.export.sxc.SXCDocument;
import de.tarent.commons.spreadsheet.export.xls.XLSSpreadSheet;

/**
 * Hilfsklasse für SpreadSheets.
 *
 * @author Christoph Jerolimov
 */
public class SpreadSheetFactory {
    /**
     * ResourceBundle
     */
    public static final ResourceBundle bundle = ResourceBundle.getBundle("de.tarent.commons.spreadsheet.export.messages");

    /**
     * Gibt eine SpreadSheet-Instanz zurück, welches eine CSV-Datei erzeugen kann.
     */
    public static final String TYPE_CSV_DOCUMENT = "csv";
    /**
     * Gibt eine SpreadSheet-Instanz zurück, welches ein ODS ZIP-Archiv erzeugen kann.
     */
    public static final String TYPE_SXC_DOCUMENT = "sxc-document";
    /**
     * Gibt eine SpreadSheet-Instanz zurück, welches ein ODS XML-Dokument erzeugen kann.
     */
    public static final String TYPE_SXC_CONTENT = "sxc-content";
    /**
     * Gibt eine SpreadSheet-Instanz zurück, welches ein ODS ZIP-Archiv erzeugen kann.
     */
    public static final String TYPE_ODS_DOCUMENT = "ods-document";
    /**
     * Gibt eine SpreadSheet-Instanz zurück, welches ein ODS XML-Dokument erzeugen kann.
     */
    public static final String TYPE_ODS_CONTENT = "ods-content";
    /**
     * Gibt eine SpreadSheet-Instanz zurück, welches ein Excel XLS-Dokument erzeugen kann.
     */
    public static final String TYPE_XLS_DOCUMENT = "xls";
    /**
     * Gibt die standard SpreadSheet-Instanz, aktuell: {@link #TYPE_ODS_DOCUMENT}
     */
    public static final String TYPE_DEFAULT = TYPE_ODS_DOCUMENT;

    /**
     * Gibt eine SpreadSheet-Instanz entsprechend des übergebenen Typens zurück.
     * Als Default gibt es ein {@link #TYPE_ODS_DOCUMENT} zurück.
     *
     * @param type siehe TYPE_*
     * @return SpreadSheetContent
     */
    public static SpreadSheet getSpreadSheet(String type) {
        if (TYPE_CSV_DOCUMENT.equals(type)) {
            return new CSVDocument();
        } else if (TYPE_SXC_CONTENT.equals(type)) {
            return new SXCContent();
        } else if (TYPE_SXC_DOCUMENT.equals(type)) {
            return new SXCDocument();
        } else if (TYPE_ODS_CONTENT.equals(type)) {
            return new ODSContent();
        } else if (TYPE_ODS_DOCUMENT.equals(type)) {
            return new ODSDocument();
        } else if (TYPE_XLS_DOCUMENT.equals(type)) {
            return new XLSSpreadSheet();
        } else {
            return getSpreadSheet(TYPE_DEFAULT);
        }
    }
}
