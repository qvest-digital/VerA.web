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
import java.io.IOException;
import java.io.OutputStream;

/**
 * Repräsentiert eine Tabellenkalkulation-Dokument, stellt entsprechende
 * Methoden zur Erstellung von Tabellen, Zeilen und Zellen zur Verfügung.
 * API ist wie folgende Baumstruktur zu verwenden:
 *
 * <pre>
 * {@link SpreadSheetFactory}.getSpreadSheet(SpreadSheetHelper.TYPE_*)
 * init()
 *
 * openTable("Tabelle 1", 3)
 *   openRow()
 *     addCell("A1")
 *     addCell("B1")
 *     addCell("C1")
 *   closeRow()
 *   openRow()
 *     addCell("A2")
 *     addCell("")
 *     addCell(null)
 *   closeRow()
 * closeTable()
 *
 * save(OutputStream)
 * </pre>
 *
 * @author Christoph Jerolimov
 */
public interface SpreadSheet {
    /**
     * Setzt eine Einstellung für dieses Dokument. Sollte vor dem
     * initalisieren geschehen.
     *
     * @param key   Schlüßel
     * @param value Wert
     */
    public void setProperty(String key, String value) throws IOException;

    /**
     * Gibt eine Einstellung für dieses Dokument zurück.
     *
     * @param key Schlüßel
     * @return Wert
     */
    public String getProperty(String key) throws IOException;

    /**
     * Initalisiert das SpreadSheetDocument.
     *
     * @throws IOException
     */
    public void init() throws IOException;

    /**
     * Speichert das Dokument in dem übergebenem OutputStream.
     * <strong>Schließt diesen NICHT.</strong>
     *
     * @param outputStream
     */
    public void save(OutputStream outputStream) throws IOException;

    /**
     * Gibt den MimeType dieses Dokumentes zurück.
     *
     * @return MimeType
     */
    public String getContentType();

    /**
     * Gibt die Standard Dateierweiterung dieses Dokumentes zurück.
     *
     * @return Dateierweiterung
     */
    public String getFileExtension();

    /**
     * Öffnet eine neue Tabelle mit dem übergebenem Namen.
     *
     * @param name     Tabellen-Name
     * @param colCount Gibt an wieviele Spalten die Tabelle hat.
     */
    public void openTable(String name, int colCount);

    /**
     * Schließt die Tabelle wieder.
     */
    public void closeTable();

    /**
     * Öffnet eine neue Zeile.
     */
    public void openRow();

    /**
     * Schließt eine neue Zeile.
     */
    public void closeRow();

    /**
     * Fügt eine neue Zelle mit dem übergebenem Inhalt ein.
     *
     * @param content Zellen-Inhalt
     */
    public void addCell(Object content);
}
