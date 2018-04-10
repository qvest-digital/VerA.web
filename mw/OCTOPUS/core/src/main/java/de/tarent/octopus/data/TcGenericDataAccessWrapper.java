package de.tarent.octopus.data;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
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

import org.apache.commons.logging.Log;

import de.tarent.octopus.logging.LogFactory;

/**
 * Zugriffsschicht zur Datenbank.
 * Mit allgemeinen Mehtoden.
 *
 * Hier werden High-Level Funktionen zum Zugriff auf Daten bereit gestellt.
 * <br>
 * <br>Im Moment sind ein paar Funktionen da, die das Cachen von Anfragen ermöglichen sollen.
 * Sie funktionieren aber noch nicht wirklich (also eher experimentell).
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcGenericDataAccessWrapper {
    //
    // geschützte Member
    //
    protected TarDBConnection dbConnection;

    protected Connection jdbcConnection;

    protected List resultSets;
    protected List resultSetCommands;
    protected int resultSetCache;
    protected Map dirtyDataSections;

    protected static Log logger = LogFactory.getLog(TcGenericDataAccessWrapper.class);

    protected static List allDataAccessWrappers = Collections.synchronizedList(new ArrayList());

    protected String schema = null;

    protected long creationTimeMillis = 0;
    protected final static long MAX_TIME_MILLIS = 600000;

    /**
     * Hier werden die aktuellen Benutzungslocks gezählt
     */
    private int useCount = 0;
    /**
     * Mutex für das Erzeugen der {@link #jdbcConnection Datenbankverbindung}
     */
    private final Object connectionMutex = new Object();
    /**
     * Mutex für das Verwalten des {@link #useCount}
     */
    private final Object useMutex = new Object();
    /**
     * Flag: {@link #jdbcConnection Datenbankverbindung} soll baldmöglichst geschlossen werden
     */
    private boolean pendingDisconnect = false;
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
        logger.debug("Wrapperlistengröße: " + allDataAccessWrappers.size());
    }

    //
    // Getter und Setter
    //

    /**
     * Das konfigurierende {@link TarDBConnection}-Objekt
     */
    public TarDBConnection getDbConnection() {
        return dbConnection;
    }

    /**
     * Das zugrundeliegende {@link Connection}-Objekt
     */
    public Connection getJdbcConnection() {
        return jdbcConnection;
    }

    /**
     * Verbindungsinformationen zur Datenbank
     */
    public void setDBConnection(TarDBConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.schema = dbConnection.getSchema();
    }

    /**
     * Die Größe des Puffers für das Cachen von Resultsets
     */
    public void setResultSetCacheSize(int resultSetCount) {
        resultSetCache = resultSetCount;
    }

    /** Der statische Logger dieser Klasse */
    //    public void setLogger(Logger logger) {
    //        TcGenericDataAccessWrapper.logger = logger;
    //    }

    //
    // öffentliche Methoden
    //

    /**
     * Diese Methode liefert, ob der Wrapper 'alt', also älter als
     * {@link #MAX_TIME_MILLIS} ist.
     *
     * @return <code>true</code>, falls der Wrapper alt ist.
     */
    public boolean isOld() {
        return System.currentTimeMillis() - creationTimeMillis > MAX_TIME_MILLIS;
    }

    /**
     * Diese Methode sichert zu, dass in diesem Objekt eine gültige
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
     * Diese Methode meldet einen Disconnect der Verbindung zur nächsten Gelegenheit
     * (d.h. dann, wenn der Benutzungszähler auf 0 absinkt) an.
     *
     * @throws SQLException
     */
    public void disconnect() throws SQLException {
        use();
        pendingDisconnect = true;
        logger.debug("Requesting disconnect");
        unUse();
    }

    /**
     * Diese Methode erhöht den Benutzungszähler. Hierdurch wird ein Schließen der
     * zugrunde liegenden {@link #getJdbcConnection() Verbindung} mindestens bis
     * zum zugehörigen {@link #unUse()} verzögert.<br>
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
            logger.debug("Benutzungen: " + useCount);
        }
    }

    /**
     * Diese Methode verringert den Benutzungszähler und führt gegebenenfalls ein verzögertes
     * Freigeben der Verbindung aus. Zur Benutzung siehe {@link #use()}.
     *
     * @throws SQLException
     */
    public void unUse() throws SQLException {
        synchronized (useMutex) {
            useCount -= 1;
            logger.debug("Benutzungen: " + useCount);
            if (useCount <= 0 && pendingDisconnect) {
                logger.debug("Executing pending disconnect");
                doDisconnect();
            }
        }
    }

    /**
     * Setzt ein Flagg um an zu zeigen, wenn sich die Daten in einem Bereich geändert haben
     * und z.B. ein neues Resultset erzeugt werden soll.
     *
     * @param section Bezeichner für den Bereich der Daten. Dieser Bezeichner kann beliebig sein,
     *                solange gewährleistet ist, daß zugriffe auf den gleichen Bereich auch den gleichen Bezeichner verwenden.
     */
    public void setDirtyDataSection(String section) {
        // Irgend einen Wert != null rein setzen
        if (section != null) {
            dirtyDataSections.put(section, "X");
        }
    }

    /**
     * Löscht das Flagg, daß anzeigt, das sich die Daten in einem Bereich geändert haben
     *
     * @param section Bezeichner für den Bereich der Daten. Dieser Bezeichner kann beliebig sein,
     *                solange gewährleistet ist, daß zugriffe auf den gleichen Bereich auch den gleichen Bezeichner verwenden.
     */
    public void removeDirtyDataSection(String section) {
        dirtyDataSections.remove(section);
    }

    /**
     * Setzt das Ditry Flag für eine Section bei allen Workern
     *
     * @param section Bezeichner für den Bereich der Daten. Dieser Bezeichner kann beliebig sein,
     *                solange gewährleistet ist, daß zugriffe auf den gleichen Bereich auch den gleichen Bezeichner verwenden.
     */
    public void setDirtyDataSectionOnAll(String section) {
        for (; ; ) {
            try {
                synchronized (allDataAccessWrappers) {
                    Iterator itWrappers = allDataAccessWrappers.iterator();
                    while (itWrappers.hasNext()) {
                        TcGenericDataAccessWrapper wrapper =
                                (TcGenericDataAccessWrapper) ((WeakReference) itWrappers.next()).get();
                        if (wrapper == null) {
                            itWrappers.remove();
                        } else {
                            wrapper.setDirtyDataSection(section);
                        }
                    }
                }
                return;
            } catch (ConcurrentModificationException e) {
                logger.info("Concurrent Modification in setDirtyDataSectionOnAll --- starting again", e);
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    //
    // geschützte Methoden für einen generischen Zugriff auf Daten durch abgeleitete Klassen
    //

    /**
     * Liefert eine Map mit einem Datensatz.
     * Der Datensatz ist der erste eines Selects.
     *
     * @param tableName   Tabelle, die selektiert werden soll.
     * @param whereClause String mit einer where Bedingung.
     * @return Einen Datensatz mit den Spaltennamen als Keys und den Feldern als String Values.
     */
    protected Map getFirstRowFromSelect(String tableName, String whereClause) throws TcDataAccessException {
        try {
            use();
            String sqlQuery = "SELECT * FROM " + tableName;
            if (whereClause != null && whereClause.length() != 0) {
                sqlQuery += " WHERE " + whereClause;
            }
            ResultSet cursor = getResultSet(sqlQuery, true, tableName);

            if (cursor.first()) {
                Map out = new HashMap();
                String[] fieldNames = getFieldList(cursor, tableName);
                for (int i = 0; i < fieldNames.length; i++) {
                    String value = cursor.getString(i + 1);
                    if (value != null) {
                        out.put(fieldNames[i], value);
                    } else {
                        out.put(fieldNames[i], "");
                    }
                }
                return out;
            }
            throw new TcDataAccessException("Kein Datensatz mit dieser Bedingung vorhanden!");

        } catch (java.sql.SQLException sqle) {
            logger.error("Fehler beim DB Zugriff", sqle);
            throw new TcDataAccessException("Fehler beim DB Zugriff", sqle);
        } catch (ClassNotFoundException cnfe) {
            logger.error("Fehler beim DB Zugriff. Kann Datenbanktreiber anscheinend nicht finden.", cnfe);
            throw new TcDataAccessException("Fehler beim DB Zugriff. Kann Datenbanktreiber anscheinend nicht finden.", cnfe);
        } finally {
            try {
                unUse();
            } catch (SQLException e) {
                logger.warn("Fehler beim Freigeben der Datenverbindung.", e);
            }
        }
    }

    /**
     * Liefert eine Map mit einem Datensatz.
     * Der Datensatz ist der in offset angegebene, beginnend mit 1
     *
     * @param tableName   Tabelle, die selektiert werden soll.
     * @param whereClause String mit einer where Bedingung.
     * @param offset      Position des gewünschten Datensatzes
     * @return Einen Datensatz mit den Spaltennamen als Keys und den Feldern als String Values.
     */
    protected Map getOneRowFromSelect(String tableName, String whereClause, int offset) throws TcDataAccessException {
        try {
            use();
            String sql = "SELECT * FROM " + tableName;
            if (whereClause != null && whereClause.length() != 0) {
                sql += " WHERE " + whereClause;
            }
            logger.debug("SQL[0]: " + sql);
            ResultSet cursor = getResultSet(sql, false, tableName);

            if (cursor.absolute(offset)) {
                Map out = new HashMap();
                String[] fieldNames = getFieldList(cursor, tableName);
                for (int i = 0; i < fieldNames.length; i++) {
                    String value = cursor.getString(i + 1);
                    if (value != null) {
                        out.put(fieldNames[i], value);
                    } else {
                        out.put(fieldNames[i], "");
                    }
                }
                return out;
            }
            throw new TcDataAccessException("Kein Datensatz mit dieser Bedingung und diesem Offset vorhanden!");
        } catch (java.sql.SQLException sqle) {
            logger.error("Fehler beim DB Zugriff", sqle);
            throw new TcDataAccessException("Fehler beim DB Zugriff", sqle);
        } catch (ClassNotFoundException cnfe) {
            logger.error("Fehler beim DB Zugriff. Kann Datenbanktreiber anscheinend nicht finden.", cnfe);
            throw new TcDataAccessException("Fehler beim DB Zugriff. Kann Datenbanktreiber anscheinend nicht finden.", cnfe);
        } finally {
            try {
                unUse();
            } catch (SQLException e) {
                logger.warn("Fehler beim Freigeben der Datenverbindung.", e);
            }
        }
    }

    /**
     * Erzeugt ein ResultSet und gibt dies zurück.
     *
     * Wenn cache gesetzt ist, wird das ResultSet aus einer Schlange genommen,
     * wenn bereits eines für das gleiche Kommando gemacht wurde.
     * Bzw. wird es wird ein neues erzeugt und in der Schlange abgelegt.
     *
     * @param cmd         SQL Commando, muss für gleiche Resultsets auch gleich sein.
     * @param cache       Soll das Resultset gecacht werden`
     * @param dataSection Bezeichner, der den Bereich aus dem die Daten sind bezeichnet und benutzt werden kann um ein
     *                    Dirty-Flag ab zu fragen.
     *                    Kann null sein, wenn aktualität egal ist.
     * @return ResultSet der Ergebnissmenge
     */
    protected ResultSet getResultSet(String cmd, boolean cache, String dataSection)
            throws SQLException, ClassNotFoundException {

        try {
            use();
            connect();

            if (!cache) {
                logger.trace("Erzeuge neues, ungecachtes ResultSet");
                Statement stmt = jdbcConnection.createStatement();
                return stmt.executeQuery(cmd);
            }

            int index = resultSetCommands.indexOf(cmd);

            ResultSet cursor = null;

            logger.trace("dirtyDataSections.get( " + dataSection + " ): " + dirtyDataSections.get(dataSection));
            // Wenn das Resultset gepuffert und noch gültig ist
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
                logger.trace("Habe bestehendes ResultSet geholt: " + cursor);
            } else {
                Statement stmt = jdbcConnection.createStatement();
                cursor = stmt.executeQuery(cmd);
                resultSetCommands.add(cmd);
                resultSets.add(cursor);
                // sonst ein neues
                removeDirtyDataSection(dataSection);

                logger.trace(
                        "resultSetCache/resultSets.size(): " + resultSetCache + "/" + resultSets.size() + "   =>" + resultSets);
                while (resultSets.size() > resultSetCache) {
                    ResultSet oldCursor = (ResultSet) resultSets.remove(0);
                    oldCursor.close();
                    logger.trace("Habe altes Resultset geschlossen: " + oldCursor);
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
     * @param tableName   Tabelle, die selektiert werden soll.
     * @param whereClause String mit einer where Bedingung.
     * @param reccord     Datensatz mit den Spaltennamen als Keys und den Feldern als String Values.
     * @return Anzahl geänderter Datensätze
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
            if (whereClause != null && whereClause.length() != 0) {
                sql += " WHERE " + whereClause;
            }
            logger.debug("SQL[0]: " + sql);

            return doSql(sql, tableName);
        } catch (java.sql.SQLException sqle) {
            logger.error("Fehler beim DB Zugriff", sqle);
            throw new TcDataAccessException("Fehler beim DB Zugriff", sqle);
        } catch (ClassNotFoundException cnfe) {
            logger.error("Fehler beim DB Zugriff. Kann Datenbanktreiber anscheinend nicht finden.", cnfe);
            throw new TcDataAccessException("Fehler beim DB Zugriff. Kann Datenbanktreiber anscheinend nicht finden.", cnfe);
        }
    }

    /**
     * Füht einen SQL befehl aus, der kein Resultset liefert.
     *
     * @param sql         Das SQL Kommando
     * @param dataSection Bezeichner, der den Bereich aus dem die Daten sind bezeichnet und benutzt werden kann um ein
     *                    Dirty-Flag ab zu fragen.
     *                    Kann null sein, wenn aktualität egal ist.
     * @return Ergebnis der Aktion, wenn diese eines zurück liefert
     */
    protected int doSql(String sql, String dataSection) throws SQLException, ClassNotFoundException {
        if (dataSection != null) {
            logger.debug("dirtyDataSections.get( " + dataSection + " ): " + dirtyDataSections.get(dataSection));
        }
        setDirtyDataSection(dataSection);
        if (dataSection != null) {
            logger.debug("dirtyDataSections.get( " + dataSection + " ): " + dirtyDataSections.get(dataSection));
        }
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
    // geschützte Hilfsmethoden
    //

    /**
     * Liefert die Felder eines Resultsets, also die Spaltennamen zurück.
     *
     * Diese werden im Moment noch für jede Anfrage neu ausgewertet, kommen
     * später aber aus eimem Puffer. Um dies zu realisieren muss ein Key
     * mit übergeben werden, der für diese Anordnung von Feldnamen eindeutig ist.
     *
     * @param cursor   Das Result Set
     * @param cacheKey Ein Key, der für diese Anordnung von Feldnamen eindeutig ist. Darunter kann das Ergebniss dieser Anfrage
     *                 dann abgelegt werden.
     * @return Ein Array mit den Feldnamen der Spalten in der richtigen Reihenfolge. Vorsicht: Die erste Spalte liegt im Array
     * bei [0], wärend der erste Spalte in einem ResultSet mit 1 anfängt.
     */
    protected String[] getFieldList(ResultSet cursor, Object cacheKey)
            throws java.sql.SQLException {

        ResultSetMetaData rsmd = cursor.getMetaData();
        String[] out = new String[rsmd.getColumnCount()];

        for (int i = 0; i < out.length; i++) {
            String colName = rsmd.getColumnName(i + 1);
            out[i] = colName;
        }

        return out;
    }

    /**
     * Diese Methode räumt die statische {@link #allDataAccessWrappers WrapperListe} auf.
     */
    private static void cleanWrapperList() {
        try {
            synchronized (allDataAccessWrappers) {
                for (Iterator itWrappers = allDataAccessWrappers.iterator(); itWrappers.hasNext(); ) {
                    if (((WeakReference) itWrappers.next()).get() == null) {
                        itWrappers.remove();
                    }
                }
            }
        } catch (ConcurrentModificationException cme) {
        }
    }

    /**
     * Diese Methode führt den eigentlichen Disconnect der {@link #jdbcConnection Verbindung}
     * zur Datenbank aus.
     *
     * @throws SQLException
     */
    private void doDisconnect() throws SQLException {
        synchronized (connectionMutex) {
            pendingDisconnect = false;
            if (jdbcConnection != null) {
                try {
                    jdbcConnection.close();
                } finally {
                    jdbcConnection = null;
                }
            }
        }
    }
}
