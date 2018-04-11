package de.tarent.dblayer.sql.statement;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
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

import java.sql.ResultSet;
import java.sql.SQLException;

import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.Result;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.sql.Statement;

/**
 * This {@link Statement} models SQL sequence value queries.<br>
 * It differs quite a bit from most other {@link Statement} implementations
 * as it is not designed to be used on a single sequence only.
 *
 * @author Wolfgang Klein
 */
public class Sequence extends AbstractStatement {
    //
    // public methods
    //

    /**
     * This method sets this {@link Sequence} to return the current value
     * of the sequence having the given name.
     *
     * @param sequence name of the sequence
     * @return this {@link Sequence} {@link Statement} instance
     */
    public Sequence currVal(String sequence) {
        _sequence = new StringBuffer("SELECT CURRVAL ('")
          .append(sequence)
          .append("')")
          .toString();
        return this;
    }

    /**
     * This method sets this {@link Sequence} to proceed to the next value
     * of the sequence having the given name and return it.
     *
     * @param sequence name of the sequence
     * @return this {@link Sequence} {@link Statement} instance
     */
    public Sequence nextVal(String sequence) {
        _sequence = new StringBuffer("SELECT NEXTVAL ('")
          .append(sequence)
          .append("')")
          .toString();
        return this;
    }

    /**
     * This method returns the next value of the given sequence as an Integer.
     *
     * @param sequence name of the sequence
     * @return the next value of the sequence
     */
    public Integer nextVal(String pool, String sequence) throws SQLException {
        Sequence seq = new Sequence().nextVal(sequence);
        ResultSet rs = null;
        try {
            rs = DB.getResultSet(pool, seq);
            Integer nextVal = null;
            if (rs.next()) {
                nextVal = new Integer(rs.getInt("nextVal"));
            }
            return nextVal;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    /**
     * This method returns the next value of the given sequence as an Integer.
     *
     * @param sequence name of the sequence
     * @return the next value of the sequence
     */
    public Integer nextVal(DBContext dbx, String sequence) throws SQLException {
        Sequence seq = new Sequence().nextVal(sequence);
        ResultSet rs = null;
        try {
            rs = DB.getResultSet(dbx, seq);
            Integer nextVal = null;
            if (rs.next()) {
                nextVal = new Integer(rs.getInt("nextVal"));
            }
            return nextVal;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
    //
    // interface {@link Statement}
    //

    /**
     * This method executes the modelled sequence statement within the
     * {@link DBContext} of this {@link Sequence} instance and returns
     * a {@link Result}.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.
     *
     * @see Statement#execute()
     */
    public Object execute() throws SQLException {
        return DB.result(getDBContext(), this);
    }

    /**
     * This method creates the {@link DBContext} sensitive {@link String} representation
     * of the modelled SQL {@link Statement}.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.
     *
     * @see Statement#statementToString()
     */
    public String statementToString() {
        return _sequence;
    }

    //
    // class {@link Object}
    //

    /**
     * This method creates the {@link DBContext} sensitive {@link String} representation
     * of the modelled SQL {@link Statement} using the method {@link #statementToString()}.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return _sequence;
    }

    //
    // protected member variables
    //
    /**
     * The current statement of this {@link Sequence}; it is set using the
     * methods {@link #currVal(String)} or {@link #nextVal(String)}.
     */
    private String _sequence = null;
}
