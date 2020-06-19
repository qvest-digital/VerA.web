package de.tarent.commons.utils;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import java.text.DecimalFormat;

/**
 * <p>
 * A helper-class for converting bytes to kilobytes, megabytes, gigabytes and so on
 * </p>
 * <p>
 * This is the SI-compliant implementation of the byte-units-system. 1024 byte (2^10) is not a "kilo" because according to the
 * SI a kilo is always 1000 (10^3).<br />
 * See also the appropriate <a href="http://en.wikipedia.org/wiki/Byte">Wikipedia-Article</a> on this.
 * </p>
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 * @see de.tarent.commons.utils.ByteHandler
 */
public class ByteHandlerSI {
    /* SI prefixes */

    public final static double BYTES_PER_KILO_BYTE = Math.pow(10, 3);
    public final static double BYTES_PER_MEGA_BYTE = Math.pow(10, 6);
    public final static double BYTES_PER_GIGA_BYTE = Math.pow(10, 9);
    public final static double BYTES_PER_TERA_BYTE = Math.pow(10, 12);
    public final static double BYTES_PER_PETA_BYTE = Math.pow(10, 15);
    public final static double BYTES_PER_EXA_BYTE = Math.pow(10, 18);

	/* Out of double-range (64-bit, 1-Math.pow(10,19)
	public final static double BYTES_PER_ZETTA_BYTE = Math.pow(10,21);
	public final static double BYTES_PER_YOTTA_BYTE = Math.pow(10,24);
	*/

    /* Binary prefixes */

    public final static double BYTES_PER_KIBI_BYTE = Math.pow(2, 10);
    public final static double BYTES_PER_MEBI_BYTE = Math.pow(2, 20);
    public final static double BYTES_PER_GIBI_BYTE = Math.pow(2, 30);
    public final static double BYTES_PER_TEBI_BYTE = Math.pow(2, 40);
    public final static double BYTES_PER_PEBI_BYTE = Math.pow(2, 50);
    public final static double BYTES_PER_EXBI_BYTE = Math.pow(2, 60);

	/* Out of double-range (64-bit, 1-Math.pow(10,19)
	public final static double BYTES_PER_ZEBI_BYTE = Math.pow(70);
	public final static double BYTES_PER_YOBI_BYTE = Math.pow(80);
	*/

    /* SI prefixes */

    public static double convertByteToKiloByte(long pBytes) {
        return pBytes / BYTES_PER_KILO_BYTE;
    }

    public static double convertByteToMegaByte(long pBytes) {
        return pBytes / BYTES_PER_MEGA_BYTE;
    }

    public static double convertByteToGigaByte(long pBytes) {
        return pBytes / BYTES_PER_GIGA_BYTE;
    }

    public static double convertByteToTeraByte(long pBytes) {
        return pBytes / BYTES_PER_TERA_BYTE;
    }

    public static double convertByteToPetaByte(long pBytes) {
        return pBytes / BYTES_PER_PETA_BYTE;
    }

    public static double convertByteToExaByte(long pBytes) {
        return pBytes / BYTES_PER_EXA_BYTE;
    }

    /* binary prefixes */

    public static double convertByteToKibiByte(long pBytes) {
        return pBytes / BYTES_PER_KIBI_BYTE;
    }

    public static double convertByteToMebiByte(long pBytes) {
        return pBytes / BYTES_PER_MEBI_BYTE;
    }

    public static double convertByteToGibiByte(long pBytes) {
        return pBytes / BYTES_PER_GIBI_BYTE;
    }

    public static double convertByteToTebiByte(long pBytes) {
        return pBytes / BYTES_PER_TEBI_BYTE;
    }

    public static double convertByteToPebiByte(long pBytes) {
        return pBytes / BYTES_PER_PEBI_BYTE;
    }

    public static double convertByteToExbiByte(long pBytes) {
        return pBytes / BYTES_PER_EXBI_BYTE;
    }

    /* convenience method */

    /**
     * Delivers a String-representation of a byte-number in an appropriate unit.<br />
     * Example: A byte-size of 45029 will lead to the String "44 KiB"
     *
     * @param pBytes a byte size
     * @return A string-representation of the same size (only rounded) in the form <i>count unit</i>
     */

    public static String getOptimalRepresentationForBytes(long pBytes) {
        DecimalFormat df = new DecimalFormat("0.00"); //$NON-NLS-1$

        if (pBytes > BYTES_PER_EXBI_BYTE) {
            return df.format(convertByteToExbiByte(pBytes)) + "EiB"; //$NON-NLS-1$
        } else if (pBytes > BYTES_PER_PEBI_BYTE) {
            return df.format(convertByteToPebiByte(pBytes)) + " PiB"; //$NON-NLS-1$
        } else if (pBytes > BYTES_PER_TEBI_BYTE) {
            return df.format(convertByteToTebiByte(pBytes)) + " TiB"; //$NON-NLS-1$
        } else if (pBytes > BYTES_PER_GIBI_BYTE) {
            return df.format(convertByteToGibiByte(pBytes)) + " GiB"; //$NON-NLS-1$
        } else if (pBytes > BYTES_PER_MEBI_BYTE) {
            return df.format(convertByteToMebiByte(pBytes)) + " MiB"; //$NON-NLS-1$
        } else if (pBytes > BYTES_PER_KIBI_BYTE) {
            return df.format(convertByteToKibiByte(pBytes)) + " KiB"; //$NON-NLS-1$
        }

        return pBytes + " Bytes"; //$NON-NLS-1$
    }
}
