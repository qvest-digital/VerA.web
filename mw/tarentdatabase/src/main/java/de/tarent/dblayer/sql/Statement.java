package de.tarent.dblayer.sql;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
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

import java.sql.SQLException;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.sql.statement.ExtPreparedStatement;

/**
 * All statement classes for use in the db layer context have to implement
 * this interface.
 *
 * @author Wolfgang Klein
 */
public interface Statement extends SetDbContext, ParamHolder {
    //
    // public constants
    //
    /**
     * the String "<code>SELECT </code>"
     */
    final static public String SELECT = "SELECT ";
    /**
     * the String "<code>SELECT DISTINCT </code>"
     */
    final static public String SELECTDISTINCT = "SELECT DISTINCT ";
    /**
     * the String "<code>INSERT INTO </code>"
     */
    final static public String INSERT = "INSERT INTO ";
    /**
     * the String "<code>UPDATE </code>"
     */
    final static public String UPDATE = "UPDATE ";
    /**
     * the String "<code>DELETE </code>"
     */
    final static public String DELETE = "DELETE ";
    /**
     * the String "<code>FROM </code>"
     */
    final static public String FROM = "FROM ";

    //
    // public methods
    //

    /**
     * This method creates the {@link DBContext} sensitive {@link String} representation
     * of the modelled SQL {@link Statement}.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.
     */
    public String statementToString() throws SyntaxErrorException;

    /**
     * This method executes the modelled statement within the {@link DBContext}
     * of this {@link Statement}.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise an exception is thrown or a default db layer context is assumed
     * which for now is a PostgresQL DBMS.
     *
     * @throws IllegalStateException if no DBContext is set.
     * @throws SQLException          if an SQL error occures.
     */
    public Object execute() throws SQLException;

    /**
     * Creates an ExtPreparedStatement of this statement which is already compiled.
     *
     * @param dbContext The database context in which the statenemt exists.
     */
    public ExtPreparedStatement prepare(DBContext dbContext) throws SQLException;

    /**
     * Creates an ExtPreparedStatement of this statement which is already compiled.
     * This method creates the PreparedStatement in the same DBContext as the Statement.
     * Therefore the DBContext of the Select must be set.
     *
     * <p><b>Attention:<b> Setting a DBContext and calling prepare() may cause errors,
     * if the same statement is used in multiple threads.</p>
     */
    public ExtPreparedStatement prepare() throws SQLException;
}
