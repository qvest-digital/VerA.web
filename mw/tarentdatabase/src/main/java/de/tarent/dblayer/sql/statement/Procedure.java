package de.tarent.dblayer.sql.statement;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2018 Dominik George (d.george@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
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

import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.Result;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.SyntaxErrorException;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Statement for encapsultion of a Stored Procedure-Call
 *
 * @author kirchner
 */
public class Procedure extends AbstractStatement {
    protected String _name;

    protected List _params = new LinkedList();

    /**
     * Constructs a new Wrapper for an SQL-Procedure.
     *
     * @param dbx  DBContext to use
     * @param name Name of Procedure
     */
    public Procedure(DBContext dbx, String name) {
        setDBContext(dbx);
        _name = name;
    }

    /**
     * Executes the procedure on the given Context.
     *
     * @param dbx DBContext
     * @throws SQLException if a problem occurs
     */
    public void executeVoidProcedure(DBContext dbx) throws SQLException {
        if (SQL.isPostgres(dbx)) {
            DB.result(dbx, this);
        } else {
            DB.update(dbx, this);
        }
    }

    /**
     * Executes the procedure on the given Context.
     *
     * @param dbx DBContext
     * @return Result of SQL-Operation
     * @throws SQLException if a problem occurs
     */
    public Result executeProcedure(DBContext dbx) throws SQLException {
        return DB.result(dbx, this);
    }

    /* (non-Javadoc)
     * @see de.tarent.dblayer.sql.Statement#execute()
     */
    public Object execute() throws SQLException {
        return executeProcedure(getDBContext());
    }

    /* (non-Javadoc)
     * @see de.tarent.dblayer.sql.Statement#statementToString()
     */
    public String statementToString() throws SyntaxErrorException {
        StringBuffer sb = new StringBuffer();
        //sb.append("execute ");
        sb.append(_name);
        sb.append(" ");
        Iterator it = _params.iterator();
        boolean first = true;
        while (it.hasNext()) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }

            sb.append(SQL.format(getDBContext(), it.next()));
        }
        return sb.toString();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        try {
            return statementToString();
        } catch (SyntaxErrorException see) {
            return see.getMessage();
        }
    }

    /**
     * Sets the name of the Procedure to call
     *
     * @param name Name of Procedure
     */
    public Procedure call(String name) {
        _name = name;
        return this;
    }

    /**
     * Adds Parameter to call. Parameters are applied
     * in the order they are added via {@link Procedure#addParam(Object)}.
     *
     * @param value Value of the Param
     */
    public Procedure addParam(Object value) {
        _params.add(value);
        return this;
    }
}
