package de.tarent.octopus.beans;

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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.Pool;
import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.statement.Select;

/**
 * Diese Klasse stellt einen Transaktionskontext für Massenoperationen dar.
 *
 * @author mikel
 */
public class TransactionContext implements ExecutionContext {
    //
    // Konstruktor und Finalizer
    //

    /**
     * Dieser Konstruktor legt die übergebene {@link Database}-Referenz ab.
     *
     * @param database die zu nutzende {@link Database}.
     */
    TransactionContext(Database database) {
        super();
        assert database != null;
        this.database = database;
    }

    /**
     * Returns the DBLayer Pool identifier for this DBContext
     * For this implementation this ist identical with the moduleName.
     *
     * @see de.tarent.dblayer.engine.DBContext#getPoolName()
     */
    public String getPoolName() {
        return database.getPoolName();
    }

    /**
     * Returns the DBLayer Pool Object for this DBContext
     *
     * @see de.tarent.dblayer.engine.DBContext#getPool()
     */
    public Pool getPool() {
        return database.getPool();
    }

    /**
     * Dieser Finalizer schließt die eventuell noch offene Verbindung mit
     * einem RollBack.
     */
    protected void finalize() throws Throwable {
        rollBack();
        super.finalize();
    }

    //
    // öffentliche Methoden
    //

    /**
     * Diese Methode führt das übergebene Statement aus.
     *
     * @param sql auszuführendes Statement
     * @throws BeanException
     */
    public void execute(Statement sql) throws BeanException {
        assert sql != null;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Executing " + sql);
        }
        try {
            ensureValidConnection();
            connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).execute(sql.statementToString());
        } catch (SQLException e) {
            throw new BeanException("Fehler beim Ausführen eines Transaktion-Statements", e);
        }
    }

    /**
     * Diese Methode führt das übergebene {@link Select}-{@link Statement}
     * aus und erwartet als Resultat ein {@link ResultSet}, das dann
     * zurückgegeben wird.
     *
     * @param sql auszuführendes {@link Select}-{@link Statement}
     * @return resultierendes {@link ResultSet}
     * @throws BeanException
     */
    public ResultSet result(Select sql) throws BeanException {
        assert sql != null;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Executing " + sql);
        }
        try {
            ensureValidConnection();
            return connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sql.statementToString());
        } catch (SQLException e) {
            throw new BeanException("Fehler beim Ausführen eines Transaktion-Statements", e);
        }
    }

    /**
     * This method closes a {@link ResultSet} returned by {@link #result(Select)}.
     * It may also close the {@link java.sql.Statement} and {@link java.sql.Connection}
     * used for creating the {@link ResultSet} if they were opened just for this
     * task.
     *
     * @param resultSet a {@link ResultSet} returned by {@link #result(Select)}.
     * @throws BeanException
     */
    public void close(ResultSet resultSet) throws BeanException {
        try {
            resultSet.close();
        } catch (SQLException e) {
            throw new BeanException("Error closing a ResultSet", e);
        }
    }

    /**
     * Diese Methode bereitet das übergebene {@link Statement} vor.
     *
     * @param statement vorzubereitendes {@link Statement}
     * @return resultierendes {@link PreparedStatement}
     * @throws BeanException
     */
    public PreparedStatement prepare(Statement statement) throws BeanException {
        try {
            return connection.prepareStatement(statement.statementToString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException e) {
            throw new BeanException("Fehler beim Erstellen eines PreparedStatements", e);
        }
    }

    /**
     * Diese Methode liefert die {@link Database}, in der dieser Kontext arbeitet.
     *
     * @return zugehörige {@link Database}
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Diese Methode führt ein Commit der angefallenen Änderungen aus und
     * schließt dann die Verbindung.
     *
     * @throws BeanException
     */
    public void commit() throws BeanException {
        try {
            close(true);
        } catch (SQLException e) {
            throw new BeanException("Fehler beim Commit einer Transaktion", e);
        }
    }

    /**
     * Diese Methode führt ein RollBack der angefallenen Änderungen aus und
     * schließt dann die Verbindung.
     *
     * @throws BeanException
     */
    public void rollBack() throws BeanException {
        try {
            close(false);
        } catch (SQLException e) {
            throw new BeanException("Fehler beim RollBack einer Transaktion", e);
        }
    }

    //
    // geschätzte Hilfsmethoden
    //

    /**
     * Diese Methode garantiert, dass in {@link #connection} eine
     * Datenbankverbindung bereit liegt, deren AutoCommit deaktiviert ist.
     */
    void ensureValidConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DB.getConnection(database.getPoolName());
            connection.setAutoCommit(false);
        }
    }

    /**
     * Returns the Connection of this transactional context.
     * If no connection exists yet it will be created by ensureValidConnection().
     */
    public Connection getDefaultConnection() throws SQLException {
        ensureValidConnection();
        return connection;
    }

    /**
     * Diese Methode schließt gegebenenfalls die zu Grunde liegende
     * Verbindung nach einem Commit oder RollBack.
     *
     * @param commit Flag: <code>true</code> führt zum Commit,
     *               <code>false</code> zum RollBack.
     * @throws SQLException
     */
    void close(boolean commit) throws SQLException {
        if (connection == null) {
            return;
        }
        try {
            if (commit) {
                connection.commit();
            } else {
                connection.rollback();
            }
        } catch (SQLException e) {
            try {
                connection.close();
            } catch (SQLException e1) {
                logger.log(Level.WARNING, "Fehler beim abschließenden Schließen einer Transaktionsverbindung", e);
            }
            connection = null;
            throw e;
        }
    }

    //
    // geschätzte Member
    //
    /**
     * Die Datenbank, auf der wir arbeiten.
     */
    final Database database;
    /**
     * Die Datenbankverbindung, auf der wir arbeiten; später eventuell nicht mehr eine eigene
     */
    Connection connection = null;
    /**
     * Logger dieser Klasse
     */
    final static Logger logger = Logger.getLogger("de.tarent.dblayer");
}
