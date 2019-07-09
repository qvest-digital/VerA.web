package de.tarent.dblayer.engine.proxy;

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
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
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
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

public class ResultSetProxyInvocationHandler implements InvocationHandler {
    private ResultSet resultSet;

    public ResultSetProxyInvocationHandler(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;

        String methodName = method.getName();
        if ("getObject".equals(methodName) && args.length == 1) {
            int columnIndex;
            if (args[0] instanceof String) {
                columnIndex = this.resultSet.findColumn((String) args[0]);
            } else {
                columnIndex = (Integer) args[0];
            }
            if (this.resultSet.getMetaData().getColumnType(columnIndex) == java.sql.Types.TIMESTAMP) {
                result = ((ResultSet) proxy).getTimestamp(columnIndex);
            } else {
                result = method.invoke(this.resultSet, args);
            }
        } else if ("getTimestamp".equals(methodName)) {
            int columnIndex;
            if (args[0] instanceof String) {
                columnIndex = this.resultSet.findColumn((String) args[0]);
            } else {
                columnIndex = (Integer) args[0];
            }
            result = this.getTimestamp0(columnIndex, args.length == 2 ? (Calendar) args[1] : null);
        } else {
            result = method.invoke(this.resultSet, args);
        }

        return result;
    }

    private Timestamp getTimestamp0(int columnIndex, Calendar calendar) throws SQLException {
        // this fixes the issue with the incorrectly calculated time for
        // the postgresql driver, other drivers may behave similarly,
        // so we will strip all timezone related information from the
        // input string
        // note that this fix is made so that we do not have to fix all
        // views or behaviour that will manipulate existing timestamps
        // since JDBC does not define a timestamp with additional timezone
        // information, the timezone information will be lost and will be
        // replaced by the timezone of the current system locale on update.
        // note that since we live in a so-called modern era, we will simply
        // ignore the era information being part of the input string, so it
        // is always "AC". note that we will also ignore any fractional seconds
        // as they are not required for the veraweb application
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }

        String tmp = this.resultSet.getString(columnIndex);

        if (tmp != null && !("".equals(tmp))) {
            int pos = 0;

            // skip leading whitespace
            while (tmp.charAt(pos) == ' ') {
                pos++;
            }

            StringBuffer read = new StringBuffer();
            while (pos < tmp.length() && tmp.charAt(pos) != '-') {
                read.append(tmp.charAt(pos));
                pos++;
            }
            if (read.length() != 4) {
                return null;
            }
            calendar.set(Calendar.YEAR, Integer.valueOf(read.toString()));
            read.setLength(0);

            pos++;
            while (pos < tmp.length() && tmp.charAt(pos) != '-') {
                read.append(tmp.charAt(pos));
                pos++;
            }
            if (read.length() != 2) {
                return null;
            }
            calendar.set(Calendar.MONTH, Integer.valueOf(read.toString()) - 1);
            read.setLength(0);

            pos++;
            while (pos < tmp.length() && tmp.charAt(pos) != ' ') {
                read.append(tmp.charAt(pos));
                pos++;
            }
            if (read.length() != 2) {
                return null;
            }
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(read.toString()));
            read.setLength(0);

            // skip whitespace between date and time information
            while (pos < tmp.length() && tmp.charAt(pos) == ' ') {
                pos++;
            }

            while (pos < tmp.length() && tmp.charAt(pos) != ':') {
                read.append(tmp.charAt(pos));
                pos++;
            }
            if (read.length() != 2) {
                return null;
            }
            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(read.toString()));
            read.setLength(0);

            pos++;
            while (pos < tmp.length() && tmp.charAt(pos) != ':') {
                read.append(tmp.charAt(pos));
                pos++;
            }
            if (read.length() != 2) {
                return null;
            }
            calendar.set(Calendar.MINUTE, Integer.valueOf(read.toString()));
            read.setLength(0);

            pos++;
            while (pos < tmp.length() && tmp.charAt(pos) != '+' && tmp.charAt(pos) != '-') {
                read.append(tmp.charAt(pos));
                pos++;
            }
            if (read.length() != 2) {
                return null;
            }
            calendar.set(Calendar.SECOND, Integer.valueOf(read.toString()));
            read.setLength(0);

            return new Timestamp(calendar.getTimeInMillis());
        }

        return null;
    }
}
