/*
 * $Id: TransactionContext.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 24.11.2005
 */
package de.tarent.octopus.custom.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import de.tarent.dblayer.engine.DB;
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
        if (logger.isInfoEnabled())
            logger.info(sql);
        try {
            ensureValidConnection();
            statement.execute(sql.statementToString());
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
        if (logger.isInfoEnabled())
            logger.info(sql);
        try {
            ensureValidConnection();
            return connection.createStatement().executeQuery(sql.statementToString());
        } catch (SQLException e) {
            throw new BeanException("Fehler beim Ausführen eines Transaktion-Statements", e);
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
    // geschützte Hilfsmethoden
    //
    /**
     * Diese Methode garantiert, dass in {@link #connection} eine
     * Datenbankverbindung bereit liegt, deren AutoCommit deaktiviert ist.
     */
    void ensureValidConnection() throws SQLException {
        if (connection == null) {
            connection = DB.getConnection(database.getModuleName());
            connection.setAutoCommit(false);
            statement = connection.createStatement();
        }
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
        	statement.close();
            if (commit)
                connection.commit();
            else
                connection.rollback();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                   logger.warn("Fehler beim abschließenden Schließen einer Transaktionsverbindung", e);
            }
            connection = null;
        }
    }
    
    //
    // geschützte Member
    //
    /** Die Datenbank, auf der wir arbeiten. */
    final Database database;
    /** Die Datenbankverbindung, auf der wir arbeiten; später eventuell nicht mehr eine eigene */
    Connection connection = null;
    /** Die Datenbankverbindung, auf der wir arbeiten; später eventuell nicht mehr eine eigene */
    java.sql.Statement statement = null;
    /** Logger dieser Klasse */
    final static Logger logger = Logger.getLogger("de.tarent.dblayer");
}
