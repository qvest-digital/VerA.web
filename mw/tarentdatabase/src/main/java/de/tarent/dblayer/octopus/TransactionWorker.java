package de.tarent.dblayer.octopus;

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

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import de.tarent.commons.logging.LogFactory;
import de.tarent.octopus.server.Closeable;
import de.tarent.octopus.server.OctopusContext;

/**
 * This is a tarent-octopus worker which extend the {@link ConnectionWorker}
 * with some simple transaction methods, like
 * {@link #start(OctopusContext, Connection, String) start transaction},
 * {@link #commit(OctopusContext, Connection) commit transaction} and
 * {@link #rollback(OctopusContext, Connection) rollback a transaction}.
 *
 * @author Christoph Jerolimov, tarent GmbH
 */
public class TransactionWorker extends ConnectionWorker {
    /**
     * Private logger instance
     */
    private static final Log logger = LogFactory.getLog(TransactionWorker.class);

    /**
     * Input definition for {@link #start(OctopusContext, Connection, String)}.
     */
    public static final String INPUT_start[] = { CONNECTION, POOLNAME };
    /**
     * Input definition for {@link #start(OctopusContext, Connection, String)}.
     */
    public static final boolean MANDATORY_start[] = { false, false };
    /**
     * Output definition for {@link #start(OctopusContext, Connection, String)}.
     */
    public static final String OUTPUT_start = CONNECTION;

    /**
     * This octopus action start a transaction. It use the
     * {@link #openConnection(OctopusContext, Connection, String)} method
     * from the {@link ConnectionWorker} to verify an open connection.
     *
     * Set {@link Connection#setAutoCommit(boolean)} to false.
     *
     * @param cntx       The current octopus-context.
     * @param connection Optional database connection.
     * @param poolname   Optional poolname.
     * @return
     * @throws SQLException
     */
    public Connection start(OctopusContext cntx, Connection connection, String poolname) throws SQLException {
        connection = openConnection(cntx, connection, poolname);

        if (logger.isDebugEnabled()) {
            logger.debug("Start transaction, deactivate autocommit.");
        }
        connection.setAutoCommit(false);
        return connection;
    }

    /**
     * This octopus action add a transaction to the octopus cleanup
     * mechanism which rollback and close the transaction at the end
     * of the current task.
     *
     * @param cntx       The current octopus-context.
     * @param connection Database connection.
     * @throws SQLException
     * @see OctopusContext#addCleanupCode(Closeable)
     */
    public void addToCleanup(OctopusContext cntx, final Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Add transaction to octopus cleanup mechanism.");
            }
            cntx.addCleanupCode(new Closeable() {
                public void close() {
                    try {
                        if (!connection.isClosed()) {
                            if (logger.isWarnEnabled()) {
                                logger.warn("Transaction is not closed until octopus cleanup mechanism is called." +
                                        " Will know automatically ROLLBACK this transaction!");
                            }
                            connection.rollback();
                            connection.close();
                        }
                    } catch (SQLException e) {
                        logger.warn("Error while rollback/closing transaction at octopus cleanup.", e);
                    }
                }
            });
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Can not add transaction to octopus cleanup." +
                        connection == null ?
                        " No transaction available (null)." :
                        " Transaction already closed.");
            }
        }
    }

    /**
     * Input definition for {@link #commit(OctopusContext, Connection)}.
     */
    public static final String INPUT_commit[] = { CONNECTION, POOLNAME };
    /**
     * Input definition for {@link #commit(OctopusContext, Connection)}.
     */
    public static final boolean MANDATORY_commit[] = { false, false };

    /**
     * This octopus action commit a transaction.
     *
     * @param cntx       The current octopus-context.
     * @param connection Database connection.
     * @throws SQLException
     * @see Connection#commit()
     */
    public void commit(OctopusContext cntx, Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Commit transaction, activate autocommit.");
            }
            connection.commit();
            connection.setAutoCommit(true);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Can not commit transaction." +
                        connection == null ?
                        " No transaction available (null)." :
                        " Transaction already closed.");
            }
        }
    }

    /**
     * Input definition for {@link #rollback(OctopusContext, Connection)}.
     */
    public static final String INPUT_rollback[] = { CONNECTION, POOLNAME };
    /**
     * Input definition for {@link #rollback(OctopusContext, Connection)}.
     */
    public static final boolean MANDATORY_rollback[] = { false, false };

    /**
     * This octopus action rollback a transaction.
     *
     * @param cntx       The current octopus-context.
     * @param connection Database connection.
     * @throws SQLException
     * @see Connection#rollback()
     */
    public void rollback(OctopusContext cntx, Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Rollback transaction, activate autocommit.");
            }
            connection.rollback();
            connection.setAutoCommit(true);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Can not rollback transaction." +
                        connection == null ?
                        " No transaction available (null)." :
                        " Transaction already closed.");
            }
        }
    }
}
