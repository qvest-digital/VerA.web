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

import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.Pool;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.statement.Insert;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Log4j2
public class CopyDBWorker {
    public static final String TABLE_NAME_PATTERN = "t%";
    private static final List ignoreList = new ArrayList();

    /**
     * Param Definition
     */
    public static String[] INPUT_copy = { "poolNameFrom", "schemeFrom", "poolNameTo", "schemeTo", "tablesToIgnore" };

    /**
     * copying table-data (from all tables in the given scheme which name starts with TABLE_NAME_PATTERN) from the db in
     * poolNameFrom into the db in poolNameTo. Array-type data like Byte[] (e.g. Pictures) is not supported.
     *
     * Both db's must be equivalent in terms of tables, table names and column names. They may differ in column types
     * while dblayer can handle these differences.
     *
     * //TODO should be augmented to let you specify the involved tables (e.g. by param list).
     *
     * @param poolNameFrom   pool name of the database where the data derived from
     * @param schemeFrom     scheme name of the data donating tables
     * @param poolNameTo     pool name of the database where the data should go to
     * @param schemeTo       scheme name of the data recieving tables
     * @param tablesToIgnore names of tables (space-separated) that should be ignored
     */
    public void copy(String poolNameFrom, String schemeFrom, String poolNameTo, String schemeTo, String tablesToIgnore) {
        assert poolNameFrom != null;
        assert poolNameTo != null;

        logger.info("Starting to copy database scheme " + schemeFrom + " from pool " + poolNameFrom + " to scheme " + schemeTo +
          " in pool " + poolNameTo + " ignoring tables " + tablesToIgnore + ".");
        StringTokenizer strTok = new StringTokenizer(tablesToIgnore != null ? tablesToIgnore : "");
        while (strTok.hasMoreTokens()) {
            ignoreList.add(strTok.nextToken());
        }

        try {
            Pool from = DB.getPool(poolNameFrom);

            DBContext contextTo = DB.getDefaultContext(poolNameTo);
            Connection fromConnection = from.getConnection();
            DatabaseMetaData metaDataFrom = fromConnection.getMetaData();

            String[] types = { "TABLE" };
            ResultSet tables = metaDataFrom.getTables(null, schemeFrom, TABLE_NAME_PATTERN, types);
            Object value;
            Insert insert;
            String tableName, tableNameFrom, tablenameTo, sel;
            Statement stm;
            ResultSet selectResult;
            ResultSetMetaData meta;
            while (tables.next()) {
                tableName = tables.getString("TABLE_NAME");

                if (ignoreList.contains(tableName.toLowerCase())) {
                    logger.info("skipping table " + tableName);
                    continue;
                }

                logger.info("transferring table " + tableName);
                tableNameFrom = schemeFrom + "." + tableName;
                tablenameTo = schemeTo + "." + tableName;

                sel = "SELECT * FROM " + tableNameFrom;
                stm = fromConnection.createStatement();
                stm.execute(sel);
                selectResult = stm.getResultSet();
                meta = selectResult.getMetaData();

                // Inserts ueber dblayer -> Abstraktion vom DB-Typ

                preInsertTable(tablenameTo, contextTo);

                while (selectResult.next()) {
                    insert = SQL.Insert(contextTo).table(tablenameTo);
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        value = selectResult.getObject(i);
                        // we don't support blobs
                        // Insert doesn't support null values
                        if (value != null && !value.getClass().isArray()) {
                            insert.insert(meta.getColumnName(i), value);
                        }
                    }
                    try {
                        insert.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                selectResult.close();
                afterInsertTable(tablenameTo, contextTo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("CopyDB finished.");
    }

    /**
     * Actions to be done before inserting data-dump
     *
     * @param tableName full qualified name (scheme.tabelName)
     * @param context
     */
    private void preInsertTable(String tableName, DBContext context) {
        try {
            // Ziel-Tabelle leeren
            SQL.Delete(context).from(tableName).execute();

            // Ziel-Tabelle Trigger aus
            DB.getStatement(context).execute("alter table " + tableName + " disable trigger all");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actions to be done after inserting data-dump
     *
     * @param tableName full qualified name (scheme.tabelName)
     * @param context
     */
    private void afterInsertTable(String tableName, DBContext context) {
        try {
            // Ziel-Tabelle Trigger an
            DB.getStatement(context).execute("alter table " + tableName + " enable trigger all");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
