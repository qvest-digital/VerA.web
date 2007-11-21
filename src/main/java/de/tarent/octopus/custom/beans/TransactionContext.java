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
 * Diese Klasse stellt einen Transaktionskontext f�r Massenoperationen dar.
 * 
 * @author mikel
 */
public class TransactionContext implements ExecutionContext {
    //
    // Konstruktor und Finalizer
    //
    /**
     * Dieser Konstruktor legt die �bergebene {@link Database}-Referenz ab. 
     * 
     * @param database die zu nutzende {@link Database}.
     */
    TransactionContext(Database database) {
        super();
        assert database != null;
        this.database = database;
    }
    
    /**
     * Dieser Finalizer schlie�t die eventuell noch offene Verbindung mit
     * einem RollBack.
     */
    protected void finalize() throws Throwable {
        rollBack();
        super.finalize();
    }

    //
    // �ffentliche Methoden
    //
    /**
     * Diese Methode f�hrt das �bergebene Statement aus.
     * 
     * @param sql auszuf�hrendes Statement
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
            throw new BeanException("Fehler beim Ausf�hren eines Transaktion-Statements", e);
        }
    }
    
    /**
     * Diese Methode f�hrt das �bergebene {@link Select}-{@link Statement}
     * aus und erwartet als Resultat ein {@link ResultSet}, das dann
     * zur�ckgegeben wird. 
     * 
     * @param sql auszuf�hrendes {@link Select}-{@link Statement}
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
            throw new BeanException("Fehler beim Ausf�hren eines Transaktion-Statements", e);
        }
    }
    
    /**
     * Diese Methode bereitet das �bergebene {@link Statement} vor. 
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
     * @return zugeh�rige {@link Database}
     */
    public Database getDatabase() {
        return database;
    }
    
    /**
     * Diese Methode f�hrt ein Commit der angefallenen �nderungen aus und
     * schlie�t dann die Verbindung.
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
     * Diese Methode f�hrt ein RollBack der angefallenen �nderungen aus und
     * schlie�t dann die Verbindung.
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
    // gesch�tzte Hilfsmethoden
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
     * Diese Methode schlie�t gegebenenfalls die zu Grunde liegende
     * Verbindung nach einem Commit oder RollBack. 
     * 
     * @param commit Flag: <code>true</code> f�hrt zum Commit,
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
                   logger.warn("Fehler beim abschlie�enden Schlie�en einer Transaktionsverbindung", e);
            }
            connection = null;
        }
    }
    
    //
    // gesch�tzte Member
    //
    /** Die Datenbank, auf der wir arbeiten. */
    final Database database;
    /** Die Datenbankverbindung, auf der wir arbeiten; sp�ter eventuell nicht mehr eine eigene */
    Connection connection = null;
    /** Die Datenbankverbindung, auf der wir arbeiten; sp�ter eventuell nicht mehr eine eigene */
    java.sql.Statement statement = null;
    /** Logger dieser Klasse */
    final static Logger logger = Logger.getLogger("de.tarent.dblayer");
}
