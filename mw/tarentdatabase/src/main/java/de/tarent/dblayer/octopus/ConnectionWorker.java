package de.tarent.dblayer.octopus;

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
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
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

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import de.tarent.commons.logging.LogFactory;
import de.tarent.dblayer.engine.DB;
import de.tarent.octopus.server.Closeable;
import de.tarent.octopus.server.OctopusContext;

/**
 * This is a tarent-octopus worker which can open and close
 * database connections. It pushs or remove it from the context.
 *
 * @author Christoph Jerolimov, tarent GmbH
 */
public class ConnectionWorker {
    /**
     * Private logger instance
     */
    private static final Log logger = LogFactory.getLog(ConnectionWorker.class);
    /**
     * This define the octopus input/output parameter for connections.
     */
    public static final String CONNECTION = "CONTENT:connection";
    /**
     * This define the octopus input parameter for an optional poolname.
     */
    public static final String POOLNAME = "CONTENT:connection";

    /**
     * Input definition for {@link #openConnection(OctopusContext, Connection, String)}.
     */
    public static final String INPUT_openConnection[] = { CONNECTION, POOLNAME };
    /**
     * Input definition for {@link #openConnection(OctopusContext, Connection, String)}.
     */
    public static final boolean MANDATORY_openConnection[] = { false, false };
    /**
     * Output definition for {@link #openConnection(OctopusContext, Connection, String)}.
     */
    public static final String OUTPUT_openConnection = CONNECTION;

    /**
     * This octopus action open a database connection and push it as
     * "{@link #CONNECTION connection}" into the octopus-context.
     *
     * @param cntx       The current octopus-context.
     * @param connection Optional connection.
     * @param poolname   Optional poolname.
     * @return A new database conection.
     * @throws SQLException
     * @see DB#getConnection(String)
     */
    public Connection openConnection(OctopusContext cntx, Connection connection, String poolname) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Reuse old connection.");
            }
            return connection;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Open new connection.");
            }
            return DB.getConnection(poolname != null ? poolname : cntx.getModuleName());
        }
    }

    /**
     * Input definition for {@link #openConnection(OctopusContext, Connection, String)}.
     */
    public static final String INPUT_addToCleanup[] = { CONNECTION };
    /**
     * Input definition for {@link #openConnection(OctopusContext, Connection, String)}.
     */
    public static final boolean MANDATORY_addToCleanup[] = { true };

    /**
     * This octopus action add a database connection to the octopus cleanup
     * mechanism which close the conncetion at the end of the current task.
     *
     * @param cntx       The current octopus-context.
     * @param connection Database connection.
     * @throws SQLException
     * @see OctopusContext#addCleanupCode(Closeable)
     */
    public void addToCleanup(OctopusContext cntx, final Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Add connection to octopus cleanup mechanism.");
            }
            cntx.addCleanupCode(new Closeable() {
                public void close() {
                    try {
                        if (!connection.isClosed()) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        logger.warn("Error while closing connection at octopus cleanup.", e);
                    }
                }
            });
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Can not add connection to octopus cleanup." +
                  connection == null ?
                  " No connection available (null)." :
                  " Connection already closed.");
            }
        }
    }

    /**
     * Input definition for {@link #closeConnection(OctopusContext, Connection)}.
     */
    public static final String INPUT_closeConnection[] = { CONNECTION };
    /**
     * Input definition for {@link #closeConnection(OctopusContext, Connection)}.
     */
    public static final boolean MANDATORY_closeConnection[] = { false };
    /**
     * Output definition for {@link #closeConnection(OctopusContext, Connection)}.
     */
    public static final String OUTPUT_closeConnection = CONNECTION;

    /**
     * This octopus action close a database connection and remove it
     * from the octopus-context.
     *
     * @param cntx       The current octopus-context.
     * @param connection Connection to close.
     * @return null
     * @throws SQLException
     * @see DB#close(Connection)
     */
    public Connection closeConnection(OctopusContext cntx, Connection connection) throws SQLException {
        if (logger.isDebugEnabled()) {
            logger.debug("Close connection.");
        }
        DB.close(connection);
        return null;
    }
}
