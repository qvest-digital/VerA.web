/* $Id: TcGenericDataAccessWrapper.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.data;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 
 * Zugriffsschicht zur Datenbank.
 * Mit allgemeinen Mehtoden.
 *
 * Hier werden High-Level Funktionen zum Zugriff auf Daten bereit gestellt.
 * <br>
 * <br>Im Moment sind ein paar Funktionen da, die das Cachen von Anfragen erm�glichen sollen.
 * Sie funktionieren aber noch nicht wirklich (also eher experimentell).
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcGenericDataAccessWrapper {
    //
    // Konstruktoren
    //
    /**
     * Inititalisierung mit Verbindungsinformationen zur Datenbank
     *
     * @param dbConnection Verbindungsinformationen zur Datenbank
     */
    public TcGenericDataAccessWrapper(TarDBConnection dbConnection) {
        this();
        this.dbConnection = dbConnection;
		this.schema = dbConnection.getSchema();
    }

    /**
     * Inititalisierung ohne Informationen zur Datenbank
     */
    public TcGenericDataAccessWrapper() {
        creationTimeMillis = System.currentTimeMillis();
        resultSetCache = 5;
        resultSets = new ArrayList();
        resultSetCommands = new ArrayList();
        dirtyDataSections = new HashMap();

        allDataAccessWrappers.add(new WeakReference(this));
        cleanWrapperList();
        logger.fine("Wrapperlistengr��e: " + allDataAccessWrappers.size());
    }

    //
    // Getter und Setter
    //
    /** Das konfigurierende {@link TarDBConnection}-Objekt */
    public TarDBConnection getDbConnection() {
        return dbConnection;
    }

    /** Das zugrundeliegende {@link Connection}-Objekt */
    public Connection getJdbcConnection() {
        return jdbcConnection;
    }

    /** Verbindungsinformationen zur Datenbank */
    public void setDBConnection(TarDBConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.schema = dbConnection.getSchema();
    }

    /** Die Gr��e des Puffers f�r das Cachen von Resultsets */
    public void setResultSetCacheSize(int resultSetCount) {
        resultSetCache = resultSetCount;
    }

    /** Der statische Logger dieser Klasse */
    public void setLogger(Logger logger) {
        TcGenericDataAccessWrapper.logger = logger;
    }

    //
    // �ffentliche Methoden
    //
    /**
     * Diese Methode liefert, ob der Wrapper 'alt', also �lter als
     * {@link #MAX_TIME_MILLIS} ist.
     * 
     * @return <code>true</code>, falls der Wrapper alt ist.
     */
    public boolean isOld() {
        return System.currentTimeMillis() - creationTimeMillis > MAX_TIME_MILLIS;
    }
    
    /**
     * Diese Methode sichert zu, dass in diesem Objekt eine g�ltige
     * {@link #getJdbcConnection() Datenbankverbindung} vorliegt.
     * 
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void connect() throws SQLException, ClassNotFoundException {
        synchronized (connectionMutex) {
            if (jdbcConnection == null || jdbcConnection.isClosed()) {
                // Verbinden mit Datenbank
                Class.forName(dbConnection.get("driver"));
                jdbcConnection =
                    DriverManager.getConnection(
                        dbConnection.get("url"),
                        dbConnection.get("username"),
                        dbConnection.get("password"));
            }
        }
    }

    /**
     * Diese Methode meldet einen Disconnect der Verbindung zur n�chsten Gelegenheit
     * (d.h. dann, wenn der Benutzungsz�hler auf 0 absinkt) an. 
     * 
     * @throws SQLException
     */
    public void disconnect() throws SQLException {
        use();
        pendingDisconnect = true;
        logger.fine("Requesting disconnect");
        unUse();
    }
    
    /**
     * Diese Methode erh�ht den Benutzungsz�hler. Hierdurch wird ein Schlie�en der
     * zugrunde liegenden {@link #getJdbcConnection() Verbindung} mindestens bis
     * zum zugeh�rigen {@link #unUse()} verz�gert.<br>
     * Diese Methode sollte immer im Zusammenspiel mit {@link #unUse()} verwendet
     * werden:
     * <pre><code>
     * try {
     *     {@link #use()};
     *     {@link #connect()};
     *     [... {@link #getJdbcConnection() Verbindung} nutzen ...]
     * } finally {
     *     {@link #unUse()};
     * }
     * </code></pre>
     */
    public void use() {
        synchronized (useMutex) {
            useCount += 1;
            logger.finer("Benutzungen: " + useCount);
        }
    }

    /**
     * Diese Methode verringert den Benutzungsz�hler und f�hrt gegebenenfalls ein verz�gertes
     * Freigeben der Verbindung aus. Zur Benutzung siehe {@link #use()}.
     * 
     * @throws SQLException 
     */
    public void unUse() throws SQLException {
        synchronized (useMutex) {
            useCount -= 1;
            logger.finer("Benutzungen: " + useCount);
            if (useCount <= 0 && pendingDisconnect) {
                logger.fine("Executing pending disconnect");
                doDisconnect();
            }
        }
    }
    
    /**
     * Setzt ein Flagg um an zu zeigen, wenn sich die Daten in einem Bereich ge�ndert haben
     * und z.B. ein neues Resultset erzeugt werden soll.
     *
     * @param section Bezeichner f�r den Bereich der Daten. Dieser Bezeichner kann beliebig sein,
     *        solange gew�hrleistet ist, da� zugriffe auf den gleichen Bereich auch den gleichen Bezeichner verwenden.
     */
    public void setDirtyDataSection(String section) {
        // Irgend einen Wert != null rein setzen 
        if (section != null)
            dirtyDataSections.put(section, "X");
    }

    /**
     * L�scht das Flagg, da� anzeigt, das sich die Daten in einem Bereich ge�ndert haben
     *
     * @param section Bezeichner f�r den Bereich der Daten. Dieser Bezeichner kann beliebig sein,
     *        solange gew�hrleistet ist, da� zugriffe auf den gleichen Bereich auch den gleichen Bezeichner verwenden.
     */
    public void removeDirtyDataSection(String section) {
        dirtyDataSections.remove(section);
    }

    /**
     * Setzt das Ditry Flag f�r eine Section bei allen Workern
     *
     * @param section Bezeichner f�r den Bereich der Daten. Dieser Bezeichner kann beliebig sein,
     *        solange gew�hrleistet ist, da� zugriffe auf den gleichen Bereich auch den gleichen Bezeichner verwenden.
     */
    public void setDirtyDataSectionOnAll(String section) {
        for (;;) {
            try {
                synchronized (allDataAccessWrappers) {
                    Iterator itWrappers = allDataAccessWrappers.iterator();
                    while (itWrappers.hasNext()) {
                        TcGenericDataAccessWrapper wrapper = (TcGenericDataAccessWrapper) ((WeakReference)itWrappers.next()).get();
                        if (wrapper == null)
                            itWrappers.remove();
                        else
                            wrapper.setDirtyDataSection(section);
                    }
                }
                return;
            } catch (ConcurrentModificationException e) {
                logger.log(Level.INFO, "Concurrent Modification in setDirtyDataSectionOnAll --- starting again", e);
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    //
    // gesch�tzte Methoden f�r einen generischen Zugriff auf Daten durch abgeleitete Klassen
    //
    /**
     * Liefert eine Map mit einem Datensatz.
     * Der Datensatz ist der erste eines Selects.
     * 
     * @param tableName Tabelle, die selektiert werden soll.
     * @param whereClause String mit einer where Bedingung.
     * 
     * @return Einen Datensatz mit den Spaltennamen als Keys und den Feldern als String Values.
     */
    protected Map getFirstRowFromSelect(String tableName, String whereClause) throws TcDataAccessException {
        try {
            use();
            String sqlQuery = "SELECT * FROM " + tableName;
            if (whereClause != null && whereClause.length() != 0)
                sqlQuery += " WHERE " + whereClause;
            ResultSet cursor = getResultSet(sqlQuery, true, tableName);

            if (cursor.first()) {
                Map out = new HashMap();
                String[] fieldNames = getFieldList(cursor, tableName);
                for (int i = 0; i < fieldNames.length; i++) {
                    String value = cursor.getString(i + 1);
                    if (value != null)
                        out.put(fieldNames[i], value);
                    else
                        out.put(fieldNames[i], "");
                }
                return out;
            }
            throw new TcDataAccessException("Kein Datensatz mit dieser Bedingung vorhanden!");

        } catch (java.sql.SQLException sqle) {
            logger.log(Level.SEVERE, "Fehler beim DB Zugriff", sqle);
            throw new TcDataAccessException("Fehler beim DB Zugriff", sqle);
        } catch (ClassNotFoundException cnfe) {
            logger.log(Level.SEVERE, "Fehler beim DB Zugriff. Kann Datenbanktreiber anscheinend nicht finden.", cnfe);
            throw new TcDataAccessException("Fehler beim DB Zugriff. Kann Datenbanktreiber anscheinend nicht finden.", cnfe);
        } finally {
            try {
                unUse();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Fehler beim Freigeben der Datenverbindung.", e);
            }
        }
    }

    /**
     * Liefert eine Map mit einem Datensatz.
     * Der Datensatz ist der in offset angegebene, beginnend mit 1
     * 
     * @param tableName Tabelle, die selektiert werden soll.
     * @param whereClause String mit einer where Bedingung.
     * @param offset Position des gew�nschten Datensatzes
     * 
     * @return Einen Datensatz mit den Spaltennamen als Keys und den Feldern als String Values.
     */
    protected Map getOneRowFromSelect(String tableName, String whereClause, int offset) throws TcDataAccessException {
        try {
            use();
            String sql = "SELECT * FROM " + tableName;
            if (whereClause != null && whereClause.length() != 0)
                sql += " WHERE " + whereClause;
            logger.finer("SQL[0]: " + sql);
            ResultSet cursor = getResultSet(sql, false, tableName);

            if (cursor.absolute(offset)) {
                Map out = new HashMap();
                String[] fieldNames = getFieldList(cursor, tableName);
                for (int i = 0; i < fieldNames.length; i++) {
                    String value = cursor.getString(i + 1);
                    if (value != null)
                        out.put(fieldNames[i], value);
                    else
                        out.put(fieldNames[i], "");
                }
                return out;
            }
            throw new TcDataAccessException("Kein Datensatz mit dieser Bedingung und diesem Offset vorhanden!");
        } catch (java.sql.SQLException sqle) {
            logger.log(Level.SEVERE, "Fehler beim DB Zugriff", sqle);
            throw new TcDataAccessException("Fehler beim DB Zugriff", sqle);
        } catch (ClassNotFoundException cnfe) {
            logger.log(Level.SEVERE, "Fehler beim DB Zugriff. Kann Datenbanktreiber anscheinend nicht finden.", cnfe);
            throw new TcDataAccessException("Fehler beim DB Zugriff. Kann Datenbanktreiber anscheinend nicht finden.", cnfe);
        } finally {
            try {
                unUse();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Fehler beim Freigeben der Datenverbindung.", e);
            }
        }
    }

    /**
     * Erzeugt ein ResultSet und gibt dies zur�ck.
     *
     * Wenn cache gesetzt ist, wird das ResultSet aus einer Schlange genommen,
     * wenn bereits eines f�r das gleiche Kommando gemacht wurde.
     * Bzw. wird es wird ein neues erzeugt und in der Schlange abgelegt.
     *
     * @param cmd SQL Commando, muss f�r gleiche Resultsets auch gleich sein.
     * @param cache Soll das Resultset gecacht werden`
     * @param dataSection Bezeichner, der den Bereich aus dem die Daten sind bezeichnet und benutzt werden kann um ein Dirty-Flag ab zu fragen.
     *                    Kann null sein, wenn aktualit�t egal ist.     
     *
     * @return ResultSet der Ergebnissmenge
     */
    protected ResultSet getResultSet(String cmd, boolean cache, String dataSection)
        throws SQLException, ClassNotFoundException {

        try {
            use();
            connect();

            if (!cache) {
                logger.finest("Erzeuge neues, ungecachtes ResultSet");
                Statement stmt = jdbcConnection.createStatement();
                return stmt.executeQuery(cmd);
            }

            int index = resultSetCommands.indexOf(cmd);

            ResultSet cursor = null;

            logger.finest("dirtyDataSections.get( " + dataSection + " ): " + dirtyDataSections.get(dataSection));
            // Wenn das Resultset gepuffert und noch g�ltig ist
            if (index >= 0 && dirtyDataSections.get(dataSection) == null) {
                // Hole das gepufferte
                // Wenn dieses Set nicht schon das letzte ist,
                // soll es ans Ende gestellt werden.
                if (index < resultSetCache - 1) {
                    cursor = (ResultSet) resultSets.remove(index);
                    resultSetCommands.remove(index);
                    resultSets.add(cursor);
                    resultSetCommands.add(cmd);
                } else {
                    cursor = (ResultSet) resultSets.get(index);
                }
                logger.finest("Habe bestehendes ResultSet geholt: " + cursor);
            } else {
                Statement stmt = jdbcConnection.createStatement();
                cursor = stmt.executeQuery(cmd);
                resultSetCommands.add(cmd);
                resultSets.add(cursor);
                // sonst ein neues
                removeDirtyDataSection(dataSection);

                logger.finest(
                    "resultSetCache/resultSets.size(): " + resultSetCache + "/" + resultSets.size() + "   =>" + resultSets);
                while (resultSets.size() > resultSetCache) {
                    ResultSet oldCursor = (ResultSet) resultSets.remove(0);
                    oldCursor.close();
                    logger.finest("Habe altes Resultset geschlossen: " + oldCursor);
                    resultSetCommands.remove(0);
                }
            }

            return cursor;
        } finally {
            unUse();
        }
    }

    /**
     * Aktualisiert eine Auswahl auf einer Tabelle
     * 
     * @param tableName Tabelle, die selektiert werden soll.
     * @param whereClause String mit einer where Bedingung.
     * @param reccord Datensatz mit den Spaltennamen als Keys und den Feldern als String Values.
     * 
     * @return Anzahl ge�nderter Datens�tze 
     */
   protected int doUpdate(String tableName, String whereClause, Map reccord) throws TcDataAccessException {
       try {
           StringBuffer sqlKeyList = new StringBuffer();
           StringBuffer sqlValueList = new StringBuffer();
           Iterator e = reccord.keySet().iterator();
           if (e.hasNext()) {
               String nextKey = (String) e.next();
               while (e.hasNext()) {
                   sqlKeyList.append(nextKey).append(", ");
                   sqlValueList.append("'").append(reccord.get(nextKey).toString()).append("', ");
                   nextKey = (String) e.next();
               }
               sqlKeyList.append(nextKey);
               sqlValueList.append("'").append(reccord.get(nextKey).toString()).append("'");
           }

           String sql = "UPDATE " + tableName + " (" + sqlKeyList + ") VALUES (" + sqlValueList + ")";
           if (whereClause != null && whereClause.length() != 0)
               sql += " WHERE " + whereClause;
           logger.finer("SQL[0]: " + sql);

           return doSql(sql, tableName);
       } catch (java.sql.SQLException sqle) {
           logger.log(Level.SEVERE, "Fehler beim DB Zugriff", sqle);
           throw new TcDataAccessException("Fehler beim DB Zugriff", sqle);
       } catch (ClassNotFoundException cnfe) {
           logger.log(Level.SEVERE, "Fehler beim DB Zugriff. Kann Datenbanktreiber anscheinend nicht finden.", cnfe);
           throw new TcDataAccessException("Fehler beim DB Zugriff. Kann Datenbanktreiber anscheinend nicht finden.", cnfe);
       }
   }

   /**
    * F�ht einen SQL befehl aus, der kein Resultset liefert.
    *
    * @param sql Das SQL Kommando
    * @param dataSection Bezeichner, der den Bereich aus dem die Daten sind bezeichnet und benutzt werden kann um ein Dirty-Flag ab zu fragen.
    *                    Kann null sein, wenn aktualit�t egal ist.     
    * @return Ergebnis der Aktion, wenn diese eines zur�ck liefert
    */
   protected int doSql(String sql, String dataSection) throws SQLException, ClassNotFoundException {
       if (dataSection != null)
           logger.fine("dirtyDataSections.get( " + dataSection + " ): " + dirtyDataSections.get(dataSection));
       setDirtyDataSection(dataSection);
       if (dataSection != null)
           logger.fine("dirtyDataSections.get( " + dataSection + " ): " + dirtyDataSections.get(dataSection));
       try {
           use();
           connect();
           Statement stmt = jdbcConnection.createStatement();
           return stmt.executeUpdate(sql);
       } finally {
           unUse();
       }
   }

   //
   // gesch�tzte Hilfsmethoden
   //
    /**
     * Liefert die Felder eines Resultsets, also die Spaltennamen zur�ck.
     *
     * Diese werden im Moment noch f�r jede Anfrage neu ausgewertet, kommen 
     * sp�ter aber aus eimem Puffer. Um dies zu realisieren muss ein Key
     * mit �bergeben werden, der f�r diese Anordnung von Feldnamen eindeutig ist.
     *
     * @param cursor Das Result Set
     * @param cacheKey Ein Key, der f�r diese Anordnung von Feldnamen eindeutig ist. Darunter kann das Ergebniss dieser Anfrage dann abgelegt werden.
     * @return Ein Array mit den Feldnamen der Spalten in der richtigen Reihenfolge. Vorsicht: Die erste Spalte liegt im Array bei [0], w�rend der erste Spalte in einem ResultSet mit 1 anf�ngt.
     */
    protected String[] getFieldList(ResultSet cursor, Object cacheKey)
        throws java.sql.SQLException, ClassNotFoundException {

        ResultSetMetaData rsmd = cursor.getMetaData();
        String[] out = new String[rsmd.getColumnCount()];

        for (int i = 0; i < out.length; i++) {
            String colName = rsmd.getColumnName(i + 1);
            out[i] = colName;
        }

        return out;
    }

    /**
     * Diese Methode r�umt die statische {@link #allDataAccessWrappers WrapperListe} auf.
     */
    private static void cleanWrapperList() {
        try {
            synchronized (allDataAccessWrappers) {
                for (Iterator itWrappers = allDataAccessWrappers.iterator(); itWrappers.hasNext();) {
                    if (((WeakReference)itWrappers.next()).get() == null)
                        itWrappers.remove();
                }
            }
        } catch(ConcurrentModificationException cme) {
        }
    }
    
    /**
     * Diese Methode f�hrt den eigentlichen Disconnect der {@link #jdbcConnection Verbindung}
     * zur Datenbank aus. 
     * 
     * @throws SQLException
     */
    private void doDisconnect() throws SQLException {
        synchronized (connectionMutex) {
            pendingDisconnect = false;
            if (jdbcConnection != null) try {
                jdbcConnection.close();
            } finally {
                jdbcConnection = null;
            }
        }
    }
    
    //
    // gesch�tzte Member
    //
    protected TarDBConnection dbConnection;

    protected Connection jdbcConnection;

    protected List resultSets;
    protected List resultSetCommands;
    protected int resultSetCache;
    protected Map dirtyDataSections;

    protected static Logger logger = Logger.getLogger(TcGenericDataAccessWrapper.class.getName());

    protected static List allDataAccessWrappers = Collections.synchronizedList(new ArrayList());

    protected String schema = null;
    
    protected long creationTimeMillis = 0;
    protected final static long MAX_TIME_MILLIS = 600000;
    
    /** Hier werden die aktuellen Benutzungslocks gez�hlt */
    private int useCount = 0;
    /** Mutex f�r das Erzeugen der {@link #jdbcConnection Datenbankverbindung} */
    private final Object connectionMutex = new Object();
    /** Mutex f�r das Verwalten des {@link #useCount} */
    private final Object useMutex = new Object();
    /** Flag: {@link #jdbcConnection Datenbankverbindung} soll baldm�glichst geschlossen werden */
    private boolean pendingDisconnect = false;
}
