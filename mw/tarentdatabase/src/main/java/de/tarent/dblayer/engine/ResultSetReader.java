package de.tarent.dblayer.engine;

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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author kleinw
 *
 * Hier wird ein ResultSet ausgelesen
 * und in List oder Mapobjekte umgewandelt.
 *
 * HINWEIS : Date-Objekte werden hier nach Long
 * gewandelt, da sich dieser Datentyp f�r den
 * SOAPTransfer besser eignet.
 */
public class ResultSetReader {

    final static public List list(Collection listColumns, Result result) throws SQLException {
        List resultList = new ArrayList();
        try {
            if (listColumns.size() == 1) {
                while (result.resultSet().next()) {
                    getRow(result, listColumns, resultList);
                }
            } else {
                int size = listColumns.size();
                while (result.resultSet().next()) {
                    List row = new ArrayList(size);
                    getRow(result, listColumns, row);
                    resultList.add(row);
                }
            }
        } catch (SQLException e) {
            throw (e);
        } finally {
            if (result != null) {
                result.close();
            }
        }
        return resultList;
    }

    private static void getRow(Result result, Collection getColumns, List row) throws SQLException {
        for (Iterator it = getColumns.iterator(); it.hasNext(); ) {
            Object[] get = (Object[]) it.next();
            String column = (String) get[0];
            Class type = (Class) get[2];

            if (type.equals(String.class)) {
                row.add(result.resultSet().getString(column));
            } else if (type.equals(Integer.class)) {
                row.add(new Integer(result.resultSet().getInt(column)));
            } else if (type.equals(Timestamp.class)) {
                Timestamp tstmp = result.resultSet().getTimestamp(column);
                if (tstmp != null) {
                    row.add(new Long(tstmp.getTime()));
                } else {
                    row.add(null);
                }
            } else {
                row.add(result.resultSet().getObject(column));
            }
        }
    }
}
