/*
 * tarent-octopus bean extension,
 * an opensource webservice and webapplication framework (bean extension)
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus bean extension'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: TransactionContext.java,v 1.7 2007/06/11 13:24:36 christoph Exp $
 *
 * Created on 24.11.2005
 */
package de.tarent.octopus.beans;

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
        if (logger.isLoggable(Level.FINE))
            logger.fine("Executing " + sql);
        try {
            ensureValidConnection();
            connection.createStatement().execute(sql.statementToString());
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
        if (logger.isLoggable(Level.FINE))
            logger.fine("Executing " + sql);
        try {
            ensureValidConnection();
            return connection.createStatement().executeQuery(sql.statementToString());
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
            return connection.prepareStatement(statement.statementToString());
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
     *  <code>false</code> zum RollBack.
     * @throws SQLException
     */
    void close(boolean commit) throws SQLException {
        if (connection == null)
            return;
        try {
            if (commit)
                connection.commit();
            else
                connection.rollback();
        }
        catch ( SQLException e )
        {
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
    /** Die Datenbank, auf der wir arbeiten. */
    final Database database;
    /** Die Datenbankverbindung, auf der wir arbeiten; später eventuell nicht mehr eine eigene */
    Connection connection = null;
    /** Logger dieser Klasse */
    final static Logger logger = Logger.getLogger("de.tarent.dblayer");
}
