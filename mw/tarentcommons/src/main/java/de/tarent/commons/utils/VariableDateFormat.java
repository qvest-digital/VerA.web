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

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class VariableDateFormat extends SimpleDateFormat {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3591962069972843863L;

    public VariableDateFormat() {
        super();
    }

    private boolean isMonth(String input) {
        String[] monthNames = new DateFormatSymbols(Locale.getDefault()).getMonths();
        if (input != null && Arrays.asList(monthNames).contains(input)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isShortMonth(String input) {
        String[] monthNames = new DateFormatSymbols(Locale.getDefault()).getShortMonths();
        if (input != null && Arrays.asList(monthNames).contains(input)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isFormatA(String input) {
        // "Januar 2006"
        String[] tokens = input.split(" ");
        return isMonth(tokens[0]) && input.matches("[a-zA-Z]+ [0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatB(String input) {
        // "Januar 06"
        String[] tokens = input.split(" ");
        return isMonth(tokens[0]) && input.matches("[a-zA-Z]+ [0-9][0-9]");
    }

    private boolean isFormatC(String input) {
        // "Jan 2006"
        String[] tokens = input.split(" ");
        return isShortMonth(tokens[0]) && input.matches("[a-zA-Z]+ [0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatD(String input) {
        // "Jan 06"
        String[] tokens = input.split(" ");
        return isShortMonth(tokens[0]) && input.matches("[a-zA-Z]+ [0-9][0-9]");
    }

    private boolean isFormatE(String input) {
        // "1. Jan 2006"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isShortMonth(tokens[1]) && input.matches("[0-9]\\. [a-zA-Z]+ [0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatF(String input) {
        // "1. Jan 06"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isShortMonth(tokens[1]) && input.matches("[0-9]\\. [a-zA-Z]+ [0-9][0-9]");
    }

    private boolean isFormatG(String input) {
        // "01. Jan 2006"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isShortMonth(tokens[1]) && input.matches("[0-9][0-9]\\. [a-zA-Z]+ [0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatH(String input) {
        // "01. Jan 06"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isShortMonth(tokens[1]) && input.matches("[0-9][0-9]\\. [a-zA-Z]+ [0-9][0-9]");
    }

    private boolean isFormatI(String input) {
        // "1. Januar 2006"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isMonth(tokens[1]) && input.matches("[0-9]\\. [a-zA-Z]+ [0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatJ(String input) {
        // "01. Januar 2006"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isMonth(tokens[1]) && input.matches("[0-9][0-9]\\. [a-zA-Z]+ [0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatK(String input) {
        // "1. Januar 06"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isMonth(tokens[1]) && input.matches("[0-9]\\. [a-zA-Z]+ [0-9][0-9]");
    }

    private boolean isFormatL(String input) {
        // "01. Januar 06"
        String[] tokens = input.split(" ");
        return tokens.length > 1 && isMonth(tokens[1]) && input.matches("[0-9][0-9]\\. [a-zA-Z]+ [0-9][0-9]");
    }

    private boolean isFormatM(String input) {
        // "1.1.06"
        return input.matches("[0-9]\\.[0-9]\\.[0-9][0-9]");
    }

    private boolean isFormatN(String input) {
        // "1.1.2006"
        return input.matches("[0-9]\\.[0-9]\\.[0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatO(String input) {
        // "01.01.2006"
        return input.matches("[0-9][0-9]\\.[0-9][0-9]\\.[0-9][0-9][0-9][0-9]");
    }

    private boolean isFormatP(String input) {
        // "01.01.06"
        return input.matches("[0-9][0-9]\\.[0-9][0-9]\\.[0-9][0-9]");
    }

    private boolean isFormatQ(String input) {
        // "2006-01-20 12:30:50.9"
        return input.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d.\\d");
    }

    private boolean isFormatR(String input) {
        // "2006.20.01"
        return input.matches("\\d\\d\\d\\d.\\d\\d.\\d\\d");
    }

    public Date analyzeString(String input) throws ParseException {
        if (isFormatA(input)) {
            this.applyPattern("MMMMMMMMM yyyy");
            return parse(input);
        }
        if (isFormatB(input)) {
            this.applyPattern("MMMMMMMMM yy");
            return parse(input);
        }
        if (isFormatC(input)) {
            this.applyPattern("MMM yy");
            return parse(input);
        }
        if (isFormatD(input)) {
            this.applyPattern("MMM yyyy");
            return parse(input);
        }
        if (isFormatE(input)) {
            this.applyPattern("d. MMM yyyy");
            return parse(input);
        }
        if (isFormatF(input)) {
            this.applyPattern("d. MMM yy");
            return parse(input);
        }
        if (isFormatG(input)) {
            this.applyPattern("dd. MMM yyyy");
            return parse(input);
        }
        if (isFormatH(input)) {
            this.applyPattern("dd. MMM yy");
            return parse(input);
        }
        if (isFormatI(input)) {
            this.applyPattern("d. MMMMMMMMM yyyy");
            return parse(input);
        }
        if (isFormatJ(input)) {
            this.applyPattern("dd. MMMMMMMMM yyyy");
            return parse(input);
        }
        if (isFormatK(input)) {
            this.applyPattern("d. MMMMMMMMM yy");
            return parse(input);
        }
        if (isFormatL(input)) {
            this.applyPattern("dd. MMMMMMMMM yy");
            return parse(input);
        }
        if (isFormatM(input)) {
            this.applyPattern("d.M.yy");
            return parse(input);
        }
        if (isFormatN(input)) {
            this.applyPattern("d.M.yyyy");
            return parse(input);
        }
        if (isFormatO(input)) {
            this.applyPattern("dd.MM.yyyy");
            return parse(input);
        }
        if (isFormatP(input)) {
            this.applyPattern("dd.MM.yy");
            return parse(input);
        }
        if (isFormatQ(input)) {
            this.applyPattern("yyyy-MM-dd HH:mm:ss.S");
            return parse(input);
        }
        if (isFormatR(input)) {
            this.applyPattern("yyyy.dd.MM");
            return parse(input);
        } else {
            throw new ParseException("Unknow date string format. Can't parse.", 0);
        }
    }
}
