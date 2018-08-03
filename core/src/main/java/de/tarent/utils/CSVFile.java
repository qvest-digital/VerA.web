package de.tarent.utils;

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

/* from http://sourceforge.net/projects/csvfile */

/**
 * CSVFile is a class used to handle <a
 * href="http://en.wikipedia.org/wiki/Comma-separated_values">Comma-Separated
 * Values</a> files.
 * <p>
 * It is abstract because it is the base class used for {@link CSVFileReader}
 * and {@link CSVFileWriter} so you should use one of these (or both) according
 * on what you need to do.
 * <p>
 * The simplest example for using the classes contained in this package is
 * CSVFileExample, that simply converts one CSV file into another one
 * that makes use of a different notation for field separator and text
 * qualifier.<br>
 * The example just comprises the following lines:
 * <p>
 *
 * <pre>
 * import java.util.*;
 * import java.io.*;
 *
 * public class CSVFileExample {
 *
 * public static void main(String[] args) throws IOException {
 *     CSVFileReader in = new CSVFileReader(&quot;csv_in.txt&quot;, ';', '&quot;');
 *     CSVFileWriter out = new CSVFileWriter(&quot;csv_out.txt&quot;, ',', '\'');
 *
 *     List fields = in.readFields();
 *     while (fields != null) {
 *         out.writeFields(fields);
 *         fields = in.readFields();
 *     }
 *
 *     in.close();
 *     out.close();
 * }}
 * </pre>
 *
 * @author Fabrizio Fazzino
 * @version %I%, %G%
 */
public abstract class CSVFile {
    //
    // Member-Variablen
    //
    /**
     * The default char used as field separator.
     */
    protected static final char DEFAULT_FIELD_SEPARATOR = ',';

    /**
     * The default char used as text qualifier
     */
    protected static final char DEFAULT_TEXT_QUALIFIER = '"';

    /**
     * The current char used as field separator.
     */
    protected char fieldSeparator;

    /**
     * The current char used as text qualifier.
     */
    protected char textQualifier;

    //
    // Konstruktoren
    //

    /**
     * CSVFile constructor with the default field separator and text qualifier.
     */
    public CSVFile() {
        this(DEFAULT_FIELD_SEPARATOR, DEFAULT_TEXT_QUALIFIER);
    }

    /**
     * CSVFile constructor with a given field separator and the default text
     * qualifier.
     *
     * @param sep The field separator to be used; overwrites the default one
     */
    public CSVFile(char sep) {
        this(sep, DEFAULT_TEXT_QUALIFIER);
    }

    /**
     * CSVFile constructor with given field separator and text qualifier.
     *
     * @param sep  The field separator to be used; overwrites the default one
     * @param qual The text qualifier to be used; overwrites the default one
     */
    public CSVFile(char sep, char qual) {
        setFieldSeparator(sep);
        setTextQualifier(qual);
    }

    //
    // Getter und Setter
    //

    /**
     * Set the current field separator.
     *
     * @param sep The new field separator to be used; overwrites the old one
     */
    public void setFieldSeparator(char sep) {
        fieldSeparator = sep;
    }

    /**
     * Set the current text qualifier.
     *
     * @param qual The new text qualifier to be used; overwrites the old one
     */
    public void setTextQualifier(char qual) {
        textQualifier = qual;
    }

    /**
     * Get the current field separator.
     *
     * @return The char containing the current field separator
     */
    public char getFieldSeparator() {
        return fieldSeparator;
    }

    /**
     * Get the current text qualifier.
     *
     * @return The char containing the current text qualifier
     */
    public char getTextQualifier() {
        return textQualifier;
    }
}
