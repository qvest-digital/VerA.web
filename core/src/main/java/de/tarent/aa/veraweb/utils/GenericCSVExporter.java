package de.tarent.aa.veraweb.utils;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.data.exchange.FieldMapping.Entity;
import de.tarent.data.exchange.MappingException;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import org.evolvis.tartools.csvfile.CSVFileWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Diese Klasse implementiert einen generischen CSV-Export von VerA.web-Personen.
 *
 * @author mikel
 */
public class GenericCSVExporter extends GenericCSVBase implements Exporter {
    /**
     * Dieser Konstruktor ist leer; dieser wird von {@link ExchangeFormat#getExporterClass()} genutzt.
     */
    public GenericCSVExporter() {
    }

    /**
     * Diese Methode wird zu jeder zu exportierenden Person aufgerufen, übergeben wird die Person als Zusammenstellung von
     * {@link Person}. Sie fügt dem Export eine Beschreibung der übergebenen VerA.web-Person hinzu.
     *
     * @param person {@link Person}-Bean
     * @see de.tarent.aa.veraweb.utils.Exporter#exportPerson(de.tarent.aa.veraweb.beans.Person)
     */
    public void exportPerson(Person person) throws BeanException, IOException {
        assert csvFieldNames != null;
        assert fieldMapping != null;
        assert csvWriter != null;
        if (person == null) {
            return;
        }
        Entity entity = new PersonEntity(person);
        List line = new ArrayList(csvFieldNames.size());
        for (Object csvFieldName : csvFieldNames) {
            line.add(fieldMapping.resolve(csvFieldName.toString(), entity));
        }
        csvWriter.writeFields(line);
    }

    /**
     * Diese Methode wird zu Beginn eines Exports aufgerufen. In ihr kann etwa das Dokument mit einem Kopf zu schreiben
     * begonnen werden.
     *
     * @throws IOException FIXME
     * @see de.tarent.aa.veraweb.utils.Exporter#startExport()
     */
    public void startExport() throws IOException {
        if (exchangeFormat == null) {
            throw new IOException("Der Export benötigt ein ExchangeFormat.");
        }
        if (database == null) {
            throw new IOException("Der Export benötigt eine Database.");
        }
        if (outputStream == null) {
            throw new IOException("Der Export benötigt einen OutputStream.");
        }
        try {
            readProperties();
            parseFormat(true);
            initWriter();
            writeHeaderLine();
        } catch (MappingException e) {
            throw new IOException("Fehler im Feldmapping", e);
        } catch (BeanException e) {
            throw new IOException("Fehler beim Daten-Bean-Zugriff", e);
        }
    }

    /**
     * Diese Methode wird zum Ende eines Exports aufgerufen. In ihr kann etwa das bisher gesammelte Dokument
     * festgeschrieben werden.
     *
     * @throws IOException FIXME
     * @see de.tarent.aa.veraweb.utils.Exporter#endExport()
     */
    public void endExport() throws IOException {
        assert csvWriter != null;
        csvWriter.close();
    }

    //
    // geschützte Hilfsmethoden
    //

    /**
     * Diese Methode initialisiert den internen {@link CSVFileWriter}.
     */
    void initWriter() throws IOException {
        assert exchangeFormat != null;
        assert outputStream != null;
        Writer writer = new OutputStreamWriter(outputStream, fileEncoding);
        csvWriter = new CSVFileWriter(writer, fieldSeparator, textQualifier);
    }

    /**
     * Diese Methode schreibt die Kopfzeile mit den Spaltennamen.
     */
    void writeHeaderLine() {
        assert csvWriter != null;
        assert fieldMapping != null;
        csvWriter.writeFields(csvFieldNames);
    }

    //
    // geschützte innere Klassen
    //

    /**
     * Diese Klasse setzt die {@link Entity}-Facade für {@link Person}-Instanzen um.
     */
    class PersonEntity implements Entity {
        //
        // Schnittstelle Entity
        //

        /**
         * Diese Methode erlaubt das Abfragen von Daten zu einem bestimmten Schlüssel. Die Schlüssel werden in
         * {@link GenericCSVExporter#getAvailableFields()} erstellt.
         *
         * @param sourceKey Quellfeldschlüssel
         * @return Quellfeldwert als {@link String}; <code>null</code>-Felder werden als Leerstring <code>""</code>
         * geliefert; <code>Date</code>-Felder werden mit {@link GenericCSVBase#dateFormat} formatiert.
         */
        public String get(String sourceKey) {
            Object result = null;
            // Personenstammdatenfelder
            try {
                if (sourceKey.equals(":workarea_name")) {
                    result = getWorkareaName(person.workarea);
                } else if (sourceKey.charAt(0) == ':') {
                    result = person.get(sourceKey.substring(1));
                } else if (sourceKey.startsWith("CAT:")) {
                    result = getRank(sourceKey.substring(4));
                } else if (sourceKey.startsWith("EVE:")) {
                    result = getRank(sourceKey.substring(4));
                } else if (sourceKey.startsWith("COR:")) {
                    result = getRank(sourceKey.substring(4));
                } else {
                    logger.warning("Unbekanntes Quellfeld");
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Fehler beim Beziehen von Personendaten", e);
            }
            if (result instanceof Date) {
                return dateFormat.format(result);
            }
            return result == null ? "" : result.toString();
        }

        //
        // Konstruktor
        //

        /**
         * Der Konstruktor vermerkt die Person
         */
        PersonEntity(Person person) {
            this.person = person;
        }

        //
        // Hilfsmethoden
        //

        /**
         * Diese Methode liefert den Rang einer Person in einer Kategorie.
         *
         * @param categoryName Name der Kategorie
         * @return Rang der Person in der Kategorie; <code>null</code>, wenn nicht in der Kategorie,
         * {@link GenericCSVExporter#DEFAULT_RANK}, wenn weder ein individueller noch ein allgemeiner Rang zu der
         * Kategorie vorliegt, jedoch sehr wohl Kategorienzugehörigkeit besteht.
         */
        Object getRank(String categoryName) throws BeanException, IOException {
            if (ranks == null) {
                ranks = new HashMap();
                Bean sampleCategory = database.createBean("Categorie");
                Bean samplePersonCategory = database.createBean("PersonCategorie");
                Select select = new Select(false).from(database.getProperty(samplePersonCategory, "table")).join(
                  database.getProperty(sampleCategory, "table"), database.getProperty(sampleCategory, "id"),
                  database.getProperty(samplePersonCategory, "categorie"))
                  .selectAs(database.getProperty(sampleCategory, "name"), "name")
                  .selectAs(database.getProperty(sampleCategory, "rank"), "rankDefault")
                  .selectAs(database.getProperty(samplePersonCategory, "rank"), "rank")
                  .where(Expr.equal(database.getProperty(samplePersonCategory, "person"), person.id));
                final List entries = database.getList(select, database);
                for (Object currentEntry : entries) {
                    final Map entry = (Map) currentEntry;
                    final Object name = entry.get("name");
                    if (name == null) {
                        continue;
                    }
                    Object rank = entry.get("rank");
                    if (rank == null) {
                        rank = entry.get("rankDefault");
                        if (rank == null) {
                            rank = DEFAULT_RANK;
                        }
                    }
                    ranks.put(name, rank);
                }
            }
            return ranks.get(categoryName);
        }

        private String getWorkareaName(int workareaId) throws BeanException {
            String workareaName = null;
            if (workareaId != 0) {
                Select select = new Select(false).from("tworkarea")
                  .selectAs("name", "name")
                  .where(Expr.equal("pk", workareaId));
                final List sqlResult = database.getList(select, database);
                final Map entry = (Map) sqlResult.get(0);
                if (entry != null) {
                    workareaName = entry.get("name").toString();
                }
            }
            return workareaName;
        }

        //
        // Membervariablen
        //
        /**
         * Die {@link Person}, für die dies eine Facade ist.
         */
        final Person person;

        /**
         * Die Ränge der Person in den Kategorien
         */
        Map ranks = null;
    }

    /**
     * Einschränkung auf Mandant
     */
    protected Integer orgUnitId = null;

    /**
     * Einschränkung auf Kategorie <code>Null</code> = alle Kategorien, 0 = keine Kategorie
     */
    protected Integer categoryId = null;

    //
    // geschützte Membervariablen
    //
    /**
     * CSV-Ausgabe-Objekt
     */
    CSVFileWriter csvWriter = null;

    /**
     * Logger dieser Klasse
     */
    final static Logger logger = Logger.getLogger(GenericCSVExporter.class.getName());

    /*
     * (non-Javadoc)
     *
     * @see de.tarent.aa.veraweb.utils.Exporter#setOrgUnitId(java.lang.Integer)
     */
    public void setOrgUnitId(Integer orgUnitId) {
        this.orgUnitId = orgUnitId;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.tarent.aa.veraweb.utils.Exporter#setCategoryId(java.lang.Integer)
     */
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Diese Methode holt alle notwendigen Kategorien aus der Datenbank.<br>
     * Nur die vom Nutzer ausgewählte Kategorien. Nur die Kategorien des Mandanten
     *
     * @throws IOException   FIXME
     * @throws BeanException FIXME
     */
    @Override
    protected List getCategoriesFromDB() throws BeanException, IOException {
        // alle Kategorien.
        if (this.categoryId == null && this.orgUnitId == null) {
            return super.getCategoriesFromDB();
        }
        // keine Kategorien
        if (this.categoryId != null && this.categoryId == -1) {
            return null;
        }

        Select sel = database.getSelect("Categorie");

        Clause expr1 = null, expr2 = null;
        if (this.categoryId != null) {
            expr1 = Expr.equal("pk", this.categoryId);
        }
        if (this.orgUnitId != null && this.orgUnitId != -1) {
            expr2 = Expr.equal("fk_orgunit", this.orgUnitId);
        }
        if (expr1 != null && expr2 != null) {
            sel.where(Where.and(expr1, expr2));
        } else if (expr1 != null) {
            sel.where(expr1);
        } else if (expr2 != null) {
            sel.where(expr2);
        }

        return database.getBeanList("Categorie", sel);
    }
}
