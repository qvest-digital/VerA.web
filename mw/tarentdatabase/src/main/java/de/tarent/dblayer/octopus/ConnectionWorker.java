package de.tarent.dblayer.octopus;

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

import de.tarent.dblayer.engine.DB;
import de.tarent.octopus.server.Closeable;
import de.tarent.octopus.server.OctopusContext;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This is a tarent-octopus worker which can open and close
 * database connections. It pushs or remove it from the context.
 *
 * @author Christoph Jerolimov, tarent GmbH
 */
@Log4j2
public class ConnectionWorker {
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
