package de.tarent.dblayer.engine;

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

import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.tarent.commons.logging.LogFactory;
import de.tarent.dblayer.engine.proxy.ResultSetProxyInvocationHandler;
import de.tarent.dblayer.sql.Statement;

/**
 * Result objects are holder object for a ResultSet  and the corresponding Statement, intended for closing both in one step.
 * But you should better use the getResultSet(...), instead of this mehtod, and close the Objects by close() and closeAll().
 *
 * @author Wolfgang Klein
 */
public class Result {
    private java.sql.Connection connection;
    private java.sql.Statement statement;
    private java.sql.ResultSet result;

    private static final org.apache.commons.logging.Log logger = LogFactory.getLog(Result.class);

    boolean statementIsClosed;

    protected Result(DBContext dbx, String sql) throws SQLException {
        this.statement = DB.getStatement(dbx);
        initialise(sql);
    }

    protected Result(String poolname, String sql) throws SQLException {
        this.statement = DB.getStatement(poolname);
        initialise(sql);
    }

    private void initialise(String sql) throws SQLException {
        this.result = this.statement.executeQuery(sql);
        this.connection = statement.getConnection();
        statementIsClosed = false;
    }

    protected Result(DBContext dbx, Statement sql) throws SQLException {
        this(dbx, sql.statementToString());
    }

    protected Result(String poolname, Statement sql) throws SQLException {
        this(poolname, sql.statementToString());
    }

    public ResultSet resultSet() {
        return (ResultSet) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { ResultSet.class },
          new ResultSetProxyInvocationHandler(result));
    }

    /**
     * Iterates over the result set and calles the process method for each row.
     * Afterwards, the result set will be closed.
     *
     * @return the number of iterations
     */
    public int iterate(ResultProcessor processor) throws SQLException {
        int i = 0;
        try {
            while (result.next()) {
                processor.process(result);
                i++;
            }
        } finally {
            close();
        }
        return i;
    }

    public void close() {
        if (statementIsClosed) {
            return;
        }

        try {
            statement.close();
            statementIsClosed = true;
            statement = null;
            this.result = null;
        } catch (SQLException e) {
            logger.warn("Error on closing connection", e);
        }
    }

    /**
     * Close from the Statement up to the Connection
     */
    public void closeAll() {
        if (statementIsClosed) {
            DB.close(connection);
        } else {
            DB.closeAll(statement);
        }
        statementIsClosed = true;
        this.result = null;
        connection = null;
        statement = null;
    }
}