package de.tarent.utils;

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
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nunez Alvarez (j.nunez-alvarez@tarent.de)
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

/**
 * CSVFileWriter is a class derived from CSVFile used to format some fields into
 * a new CSV file.
 *
 * @author Fabrizio Fazzino
 */
public class CSVFileWriter extends CSVFile {
    //
    // Konstruktoren
    //

    /**
     * CSVFileWriter constructor just need the name of the CSV file that will be
     * written.
     *
     * @param outputFileName The name of the CSV file to be opened for writing
     * @throws IOException If an error occurs while creating the file
     */
    public CSVFileWriter(String outputFileName) throws IOException {
        this(outputFileName, DEFAULT_FIELD_SEPARATOR);
    }

    /**
     * CSVFileWriter constructor with a given field separator.
     *
     * @param outputFileName The name of the CSV file to be opened for writing
     * @param sep            The field separator to be used; overwrites the default one
     * @throws IOException If an error occurs while creating the file
     */
    public CSVFileWriter(String outputFileName, char sep) throws IOException {
        this(outputFileName, sep, DEFAULT_TEXT_QUALIFIER);
    }

    /**
     * CSVFileWriter constructor with given field separator and text qualifier.
     *
     * @param outputFileName The name of the CSV file to be opened for writing
     * @param sep            The field separator to be used; overwrites the default one
     * @param qual           The text qualifier to be used; overwrites the default one
     * @throws IOException If an error occurs while creating the file
     */
    public CSVFileWriter(String outputFileName, char sep, char qual) throws IOException {
        this(new FileWriter(outputFileName), sep, qual);
    }

    /**
     * CSVFileWriter constructor with given field separator and text qualifier.
     *
     * @param writer The Writer to be opened for writing
     * @param sep    The field separator to be used; overwrites the default one
     * @param qual   The text qualifier to be used; overwrites the default one
     */
    public CSVFileWriter(Writer writer, char sep, char qual) {
        super(sep, qual);
        out = new PrintWriter(new BufferedWriter(writer));
    }

    //
    // Öffentliche Methoden
    //

    /**
     * Close the output CSV file.
     */
    public void close() {
        out.flush();
        out.close();
    }

    /**
     * Join the fields and write them as a new line to the CSV file.
     *
     * @param fields The list of strings containing the fields
     */
    public void writeFields(List fields) {
        int n = fields.size();
        for (int i = 0; i < n; i++) {
            out.print(prepareField(fields.get(i)));
            if (i < (n - 1)) {
                out.print(fieldSeparator);
            }
        }
        out.println();
    }

    //
    // geschützte Hilfsmethoden
    //
    private String prepareField(Object field) {
        String fieldString = (field != null) ? field.toString() : "";
        if (fieldString.indexOf(fieldSeparator) >= 0 ||
          fieldString.indexOf('\n') >= 0 ||
          fieldString.indexOf('\r') >= 0 ||
          fieldString.indexOf(textQualifier) == 0) {
            return textQualifier + fieldString.replaceAll(String.valueOf(textQualifier),
              new String(new char[] { textQualifier, textQualifier })) + textQualifier;
        }
        return fieldString;
    }

    //
    // geschützte Membervariablen
    //
    /**
     * The print writer linked to the CSV file to be written.
     */
    protected final PrintWriter out;
}
