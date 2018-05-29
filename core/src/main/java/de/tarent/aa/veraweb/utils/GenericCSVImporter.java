package de.tarent.aa.veraweb.utils;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
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

import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.aa.veraweb.beans.ImportPerson;
import de.tarent.aa.veraweb.beans.ImportPersonCategorie;
import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.data.exchange.FieldMapping.Entity;
import de.tarent.data.exchange.MappingException;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.utils.CSVFileReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Diese Klasse implementiert einen generischen CSV-Import von VerA.web-Personen.
 *
 * @author mikel
 */
public class GenericCSVImporter extends GenericCSVBase implements Importer {
    private static final int TAB_FIELD_SEPARATOR = 0x09;
    private static final int SEMICOLON_FIELD_SEPARATOR = 0x3B;
    private static final int COMMA_FIELD_SEPARATOR = 0x2C;

    /**
     * Dieser Konstruktor ist leer; dieser wird von {@link ExchangeFormat#getImporterClass()}
     * genutzt.
     */
    public GenericCSVImporter() {
    }

    //
    // Schnittstelle Importer
    //

    /**
     * Diese Methode führt einen Import aus. Hierbei werden alle erkannten zu
     * importierenden Personendatensätze und Zusätze nacheinander dem übergebenen
     * {@link ImportDigester} übergeben.
     *
     * @param digester der {@link ImportDigester}, der die Datensätze weiter
     *                 verarbeitet.
     * @throws IOException FIXME
     * @see de.tarent.aa.veraweb.utils.Importer#importAll(de.tarent.aa.veraweb.utils.ImportDigester, TransactionContext)
     */
    public void importAll(ImportDigester digester, TransactionContext transactionContext) throws IOException {
        if (exchangeFormat == null) {
            throw new IOException("Für einen Import muß ein Format angegeben sein.");
        }
        if (exchangeFormat.getProperties() == null) {
            throw new IOException("Für einen Import müssen in der Formatspezifikation die notwendigen Parameter angegeben sein.");
        }
        if (inputStream == null) {
            throw new IOException("Für einen Import muß ein Eingabedatenstrom angegeben sein.");
        }

        try {
            readProperties();
            parseFormat(false);
            initReader();
            fieldMapping.extendCategoryImport(headers);
            importRows(digester, transactionContext);
            csvReader.close();
        } catch (MappingException e) {
            throw new IOException("Fehler im Feldmapping", e);
        } catch (BeanException e) {
            throw new IOException("Fehler beim Daten-Bean-Zugriff", e);
        }
    }

    //
    // geschützte Hilfsmethoden
    //

    /**
     * Diese Methode initialisiert den internen {@link CSVFileReader}
     * und liest die erste Zeile als Kopfzeile mit den Spaltennamen ein.
     * Diese werden lokal in {@link #headers} abgelegt.
     */
    void initReader() throws IOException {
        assert exchangeFormat != null;
        assert inputStream != null;
        assert headers == null;

        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, fileEncoding));
        final String firstLine = reader.readLine();
        identifyFieldSeparator(firstLine);

        csvReader = new CSVFileReader(reader, fieldSeparator, textQualifier);
        headers = csvReader.readFields(firstLine);
    }

    private void identifyFieldSeparator(String firstLine) {
        // figure out field separator; we can do that in VerA.web
        if (firstLine.indexOf(TAB_FIELD_SEPARATOR) > -1) {
            fieldSeparator = TAB_FIELD_SEPARATOR;
        } else if (firstLine.indexOf(SEMICOLON_FIELD_SEPARATOR) > -1) {
            fieldSeparator = SEMICOLON_FIELD_SEPARATOR;
        } else if (firstLine.indexOf(COMMA_FIELD_SEPARATOR) > -1) {
            fieldSeparator = COMMA_FIELD_SEPARATOR;
        }
    }

    /**
     * Diese Methode importiert alle Datenzeilen der CSV-Datei in den übergebenen
     * {@link ImportDigester}. Hierbei wird vorausgesetzt, dass die führende Zeile
     * mit den Spaltennamen schon mit {@link #initReader()} eingelesen wurde.
     *
     * @param digester der {@link ImportDigester}, der die Datensätze weiter
     *                 verarbeitet.
     * @throws IOException   FIXME
     * @throws BeanException FIXME
     */
    void importRows(ImportDigester digester, TransactionContext transactionContext) throws IOException, BeanException {
        assert headers != null;
        assert digester != null;
        final RowEntity row = new RowEntity();
        List rawRow;
        digester.startImport();
        while ((rawRow = csvReader.readFields()) != null) {
            row.parseRow(rawRow);
            digestRow(digester, row);
            transactionContext.commit();
        }
        digester.endImport();
    }

    /**
     * Diese Methode verarbeitet eine einzelne aufbereitete Zeile; das bedeutet, dass eine
     * entsprechende {@link ImportPerson} und gegebenenfalls {@link ImportPersonCategorie}-Instanzen
     * erstellt werden und dem übergebenen
     * {@link ImportDigester} als neue Person weitergereicht werden.<br>
     * TODO: Timestamp-Format konfigurierbar machen
     *
     * @param digester der {@link ImportDigester}, der die Datensätze weiter
     *                 verarbeitet.
     * @param row      die aufbereitete CSV-Zeile
     * @throws BeanException FIXME
     * @throws IOException   FIXME
     */
    void digestRow(ImportDigester digester, RowEntity row) throws BeanException, IOException {
        ImportPerson person = new ImportPerson();
        Map extras = new HashMap();
        for (Object currentTarget : fieldMapping.getTargets()) {
            String targetField = currentTarget.toString();
            if (targetField != null && targetField.length() > 0) {
                String targetValue = fieldMapping.resolve(targetField, row);
                if (targetValue != null) {
                    targetValue = targetValue.trim();
                }
                if (targetField.charAt(0) == ':') {
                    Object targetObject = targetValue;
                    Class fieldClass = person.getFieldClass(targetField.substring(1));
                    if (Date.class.isAssignableFrom(fieldClass)) {
                        try {
                            if (targetValue != null && targetValue.length() > 0) {
                                Date dateValue = dateFormat.parse(targetValue);
                                Constructor constructor = fieldClass.getConstructor(ONE_LONG);
                                targetObject = constructor.newInstance(dateValue.getTime());
                            } else {
                                targetObject = null;
                            }
                        } catch (NoSuchMethodException e) {
                            logger.log(Level.WARNING, "Datums-Klasse ohne (long)-Konstruktor", e);
                        } catch (ParseException e) {
                            logger.log(Level.WARNING, "Datum nicht parse-bar", e);
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Datumsklasse nicht instanziierbar", e);
                        }
                    }
                    person.put(targetField.substring(1), targetObject);
                } else if (targetField.startsWith("CAT:")) {
                    addCategory(targetField.substring(4), targetValue, Categorie.FLAG_DEFAULT, extras);
                } else if (targetField.startsWith("EVE:")) {
                    addCategory(targetField.substring(4), targetValue, Categorie.FLAG_EVENT, extras);
                } else if (targetField.startsWith("COR:")) {
                    addCategory(targetField.substring(4), targetValue, Categorie.FLAG_DIPLO_CORPS, extras);
                } else {
                    logger.warning("Unbekanntes Zielfeld " + targetField);
                }
            }
        }
        person.created = new Timestamp(new Date().getTime());
        digester.importPerson(person, new ArrayList(extras.values()));
    }

    /**
     * Diese Methode fügt der übergebenen Sammlung von Import-Extras eine neue
     * {@link ImportPersonCategorie} hinzu oder --- falls es schon eine unter
     * dem Namen gibt --- aktualisiert diese.
     *
     * @param name   Name der Kategorie
     * @param rank   Rang in der Kategorie; <code>""</code> bedeutet keine Zuordnung
     *               zu der Kategorie
     * @param extras Map mit Import-Extras.
     */
    void addCategory(String name, String rank, int flags, Map extras) {
        assert name != null;
        assert extras != null;

        if (rank == null || rank.length() == 0) {
            return;
        }

        Integer rankNumber;
        try {
            rankNumber = new Integer(rank);
        } catch (NumberFormatException nfe) {
            if (rank.toUpperCase().equals(DEFAULT_RANK.toUpperCase())) {
                rankNumber = null;
            } else {
                return;
            }
        }

        String catKey;
        if (flags == Categorie.FLAG_EVENT) {
            catKey = "EVE:" + name;
        } else if (flags == Categorie.FLAG_DIPLO_CORPS) {
            catKey = "COR:" + name;
        } else {
            catKey = "CAT:" + name;
        }

        if (extras.containsKey(catKey)) {
            ImportPersonCategorie category = (ImportPersonCategorie) extras.get(catKey);
            if (rankNumber != null && category.rank != null && rankNumber > category.rank) {
                category.rank = rankNumber;
            }
        } else {
            final ImportPersonCategorie category = initCategory(name, flags, rankNumber);
            extras.put(catKey, category);
        }
    }

    private ImportPersonCategorie initCategory(String name, int flags, Integer rankNumber) {
        final ImportPersonCategorie category = new ImportPersonCategorie();
        category.name = name;
        category.rank = rankNumber;
        category.flags = flags;
        return category;
    }

    /**
     * Diese Klasse stellt eine CSV-Zeile als {@link de.tarent.data.exchange.FieldMapping.Entity}
     * dar.
     */
    class RowEntity implements Entity {
        //
        // Schnittstelle Entity
        //

        /**
         * Diese Methode erlaubt das Abfragen von Daten zu einem bestimmten Schlüssel.
         * Die Schlüssel sind die CSV-Spaltennamen.
         *
         * @param sourceKey Quellfeldschlüssel
         * @return Quellfeldwert als {@link String}; Werte unbekannter Felder werden
         * als Leerstring <code>""</code> geliefert.
         */
        public String get(String sourceKey) {
            Object result = rowMapping.get(sourceKey);
            return result != null ? result.toString() : "";
        }

        //
        // Öffentliche Methoden
        //

        /**
         * Diese Methode parse-t die übergebene Liste --- die aktuelle Zeile ---
         * und legt die Werte unter dem zugehörigen Spaltennamen lokal ab.<br>
         * TODO: Warnung bei zu langen oder zu kurzen Zeilen
         *
         * @param row Liste der Werte der aktuellen Zeile
         */
        public void parseRow(List row) {
            rowMapping.clear();
            if (row != null) {
                final Iterator itHeaders = headers.iterator();
                final Iterator itRowFields = row.iterator();
                while (itHeaders.hasNext() && itRowFields.hasNext()) {
                    rowMapping.put(itHeaders.next(), itRowFields.next());
                }
            }
        }

        //
        // geschützte Member
        //
        /**
         * Hier werden die Zuordnungen der aktuellen Zeile gehalten
         */
        final Map rowMapping = new HashMap();
    }

    //
    // geschützte Membervariablen
    //
    /**
     * CSV-Eingabe-Objekt
     */
    CSVFileReader csvReader = null;
    /**
     * Header-Felder
     */
    List headers = null;
    final static Class[] ONE_LONG = new Class[] { Long.TYPE };
    /**
     * Logger dieser Klasse
     */
    final static Logger logger = Logger.getLogger(GenericCSVImporter.class.getName());
}
