package de.tarent.dblayer.sql;

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

import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This tool runs a sequence of SQL-Queries defined in an input-file (e.g. a log-file)
 *
 * It can compare the result of this queries on two different databases
 *
 * If the results do not equal or if the query results in an error, a detailed report will be logged so that you can analyze
 * the problem afterwards.
 *
 * By this, you can test if your applications can simply run on a different database or need modifications
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 */
@Log4j2
public class SQLQueryTester {
    private String sqlQueriesPath = System.getProperty("user.home") + File.separator + "veraweb.log";
    private String confPath = System.getProperty("user.home") + File.separator + "query_tester.conf";
    private String dbConfPath1 = System.getProperty("user.home") + File.separator + "db1.conf";
    private String dbConfPath2 = System.getProperty("user.home") + File.separator + "db2.conf";
    private String logFilePath = System.getProperty("user.home") + File.separator + "sql_query.log";

    private Map config;
    private Map dbConfig1;
    private Map dbConfig2;

    private List includes;

    private List sqlQueries;

    // whether to stop when an error occurs
    private boolean returnOnFailure = false;

    private boolean logDuplicates = false;

    private boolean filterSchema = true;

    private short logType = LOG_TYPE_VERAWEB;

    // maximum count of sql-queries to process. set to '-1' for no limit
    private int maxCount = -1;

    // The name of the PostgreSQL-Schema used (Has to be filtered out in Queries)
    private String pgSQLSchema = "colibri";

    private int valueMismatches;
    private int typeMismatches;
    private int nullValues;
    private int warnings;
    private int exceptions;

    private List knownMessages;

    public final static short LOG_TYPE_VERAWEB = 0;
    public final static short LOG_TYPE_PFJDBC = 1;

    public SQLQueryTester(String[] pCmdLineArgs) {
        config = new HashMap();
        dbConfig1 = new HashMap();
        dbConfig2 = new HashMap();
        includes = new ArrayList();
        sqlQueries = new ArrayList();
        knownMessages = new ArrayList();

        valueMismatches = 0;
        typeMismatches = 0;
        nullValues = 0;
        warnings = 0;
        exceptions = 0;

        // read cmdline-args
        processCmdLineArgs(pCmdLineArgs);
    }

    public void run() {
        // read settings
        if (!parseConfFile(confPath, config) || !parseConfFile(dbConfPath1, dbConfig1) ||
          !parseConfFile(dbConfPath2, dbConfig2)) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not parse config-files. Aborting...");
            }
            System.exit(1);
        }

        // Read SQL-Queries
        if (!parseSQLQueriesFile(sqlQueriesPath)) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not parse SQL-Queries-files. Aborting...");
            }
            System.exit(1);
        }

        // run test
        int errors = runTest(returnOnFailure);
        if (errors == 0) {
            if (logger.isInfoEnabled()) {
                logger.info("All Queries succeeded!");
            }
            System.exit(0);
        } else if (errors == -1) {
            if (logger.isInfoEnabled()) {
                logger.info("Could not connect to databases!");
            }
            System.exit(1);
        } else {
            if (logger.isInfoEnabled()) {
                logger.info(
                  errors + " of " + sqlQueries.size() + " queries failed.\r\nEXCEPTIONS: " + exceptions + "\r\nWARNINGS: " +
                    warnings + "\r\nVALUE-MISMATCHES: " + valueMismatches + "\r\nTYPE-MISMATCHES: " + typeMismatches +
                    "\r\nNULL-VALUES: " + nullValues + "\r\nHave a look into log for details");
            }
            System.exit(2);
        }
    }

    public static void main(String[] pCmdLineArgs) {
        new SQLQueryTester(pCmdLineArgs).run();
    }

    private void processCmdLineArgs(String[] pCmdLineArgs) {
        if (pCmdLineArgs.length >= 1) {
            sqlQueriesPath = pCmdLineArgs[0];
        }
        if (pCmdLineArgs.length >= 2) {
            dbConfPath1 = pCmdLineArgs[1];
        }
        if (pCmdLineArgs.length >= 3) {
            dbConfPath2 = pCmdLineArgs[2];
        }
        if (pCmdLineArgs.length >= 4) {
            logFilePath = pCmdLineArgs[3];
        }
    }

    private boolean parseConfFile(String pFilePath, Map pConfig) {
        if (logger.isInfoEnabled()) {
            logger.info("Parsing configuration (" + pFilePath + ")...");
        }
        if (pFilePath != null) {
            File dbConfFile = new File(pFilePath);

            if (dbConfFile.exists()) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dbConfFile)));

                    while (reader.ready()) {
                        String line = reader.readLine();

                        if (-1 != line.indexOf("=")) {
                            int index = line.indexOf("=");
                            pConfig.put(line.substring(0, index), line.substring(index + 1));
                        }
                    }
                    return true;
                } catch (Exception pExcp) {
                    if (logger.isWarnEnabled()) {
                        logger.warn(pExcp.getLocalizedMessage());
                    }
                    return false;
                }
            }
        }
        return false;
    }

    private void initIncludes(List pIncludes) {
        if (config.get("INCLUDE") != null) {
            StringTokenizer includeString = new StringTokenizer((String) config.get("INCLUDE"));

            while (includeString.hasMoreTokens()) {
                pIncludes.add(includeString.nextToken());
            }
        }
        if (pIncludes.size() == 0) {
            pIncludes.add("ALL");
        }
    }

    private void parseVeraWebLog(String pLine, List pSQLQueries, boolean pFilterSchema, String pSchema) {
        if (pLine != null && pSQLQueries != null && pLine.indexOf('-') != -1) {
            String logText = pLine.substring(pLine.indexOf('-') + 2);

            if (logText != null
              && logText.length() > 0
              && logText.indexOf(" ") != -1
              && (includes.contains("ALL")
              || includes.contains(logText.substring(0, logText.indexOf(" ")).toUpperCase()))
            ) {
                if (pFilterSchema) {
                    pSQLQueries.add(filterSchema(logText, pSchema));
                } else {
                    pSQLQueries.add(logText);
                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug("logText: " + logText);
            }
        }
    }

    private void parsePFJDBCLog(String pLine, List pSQLQueries, boolean pFilterSchema, String pSchema) {
        if (pLine != null && pSQLQueries != null && pLine.indexOf(" SQL :") != -1) {
            String logText = pLine.substring(pLine.indexOf(" SQL :") + 6);

            if (includes.contains("ALL")
              || includes.contains(logText.substring(0, logText.indexOf(" ")).toUpperCase())) {
                if (pFilterSchema) {
                    pSQLQueries.add(filterSchema(logText, pSchema));
                } else {
                    pSQLQueries.add(logText);
                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug("logText: " + logText);
            }
        }
    }

    private boolean parseSQLQueriesFile(String pFilePath) {
        if (logger.isInfoEnabled()) {
            logger.info("Parsing SQL-Queries...");
        }

        initIncludes(includes);

        if (pFilePath != null) {
            File dbConfFile = new File(pFilePath);

            if (dbConfFile.exists()) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dbConfFile)));

                    while (reader.ready() && (maxCount == -1 || sqlQueries.size() < maxCount)) {
                        String line = reader.readLine();

                        if (logType == LOG_TYPE_VERAWEB) {
                            parseVeraWebLog(line, sqlQueries, filterSchema, pgSQLSchema);
                        } else if (logType == LOG_TYPE_PFJDBC) {
                            parsePFJDBCLog(line, sqlQueries, filterSchema, pgSQLSchema);
                        }

                        if (logger.isDebugEnabled()) {
                            logger.debug("line: " + line);
                        }
                    }
                    if (logger.isInfoEnabled()) {
                        logger.info("Count of SQL-Queries is " + sqlQueries.size());
                    }
                } catch (Exception pExcp) {
                    if (logger.isWarnEnabled()) {
                        logger.warn(pExcp.getLocalizedMessage());
                    }
                    pExcp.printStackTrace();
                    return false;
                }
            }
        }

        return true;
    }

    private Connection connectToDB(Map pDBConfig) {
        String driverClassName = (String) pDBConfig.get("DRIVER");
        String dbURL = (String) pDBConfig.get("URL");
        String username = (String) pDBConfig.get("USER");
        String password = (String) pDBConfig.get("PASSWORD");

        if (driverClassName == null || dbURL == null || username == null || password == null) {
            return null;
        }

        if (logger.isInfoEnabled()) {
            logger.info(
              "Connecting to DB...\r\nusing DRIVER <" + driverClassName + ">\r\nusing URL <" + dbURL + ">\r\nusing USER <" +
                username + ">\r\nusing PASSWORD <" + password + ">");
        }
        try {
            // Load JDBC-driver
            Class.forName(driverClassName);

            // connect to DB
            Connection conn = DriverManager.getConnection(dbURL, username, password);

            return conn;
        } catch (Exception pExcp) {
            if (logger.isWarnEnabled()) {
                logger.warn(pExcp.getLocalizedMessage());
            }
        }

        return null;
    }

    /**
     * Runs the SQL-Query-Test
     *
     * @param pReturnOnFailure whether the test should stop when an error occurs
     * @return count of occured errors
     */

    private int runTest(boolean pReturnOnFailure) {
        int count = 0;
        int errors = 0;

        Connection conn1 = connectToDB(dbConfig1);
        Connection conn2 = connectToDB(dbConfig2);

        if (conn1 == null || conn2 == null) {
            return -1;
        }

        try {
            Statement stmnt1 = conn1.createStatement();
            Statement stmnt2 = conn2.createStatement();

            Iterator it = sqlQueries.iterator();

            while (it.hasNext() && (maxCount == -1 || count <= maxCount)) {
                boolean success = true;
                count++;
                String sqlQuery = (String) it.next();
                ResultSet queryResult1 = null;
                ResultSet queryResult2 = null;
                try {
                    stmnt1.execute(sqlQuery);
                    queryResult1 = stmnt1.getResultSet();
                } catch (SQLException pExcp) {
                    if (!knownMessages.contains(pExcp.getLocalizedMessage()) || logDuplicates) {
                        if (logger.isWarnEnabled()) {
                            logger.warn(
                              "(" + count + "/" + sqlQueries.size() + ") " + dbConfig1.get("NAME") + ": " + sqlQuery + " " +
                                pExcp.getLocalizedMessage());
                        }
                    }

                    if (!knownMessages.contains(pExcp.getLocalizedMessage())) {
                        knownMessages.add(pExcp.getLocalizedMessage());
                    }

                    if (success) {
                        errors++;
                    }
                    success = false;
                    exceptions++;
                    if (pReturnOnFailure) {
                        return errors;
                    }
                }

                try {
                    stmnt2.execute(sqlQuery);
                    queryResult2 = stmnt2.getResultSet();
                } catch (SQLException pExcp) {
                    if (!knownMessages.contains(pExcp.getLocalizedMessage()) || logDuplicates) {
                        if (logger.isWarnEnabled()) {
                            logger.warn(
                              "(" + count + "/" + sqlQueries.size() + ") " + dbConfig2.get("NAME") + ": " + sqlQuery + " " +
                                pExcp.getLocalizedMessage());
                        }
                    }

                    if (!knownMessages.contains(pExcp.getLocalizedMessage())) {
                        knownMessages.add(pExcp.getLocalizedMessage());
                    }

                    if (success) {
                        errors++;
                    }
                    success = false;
                    exceptions++;
                    if (pReturnOnFailure) {
                        return errors;
                    }
                }

                if (!resultSetsEqual(queryResult1, queryResult2, sqlQuery)) {
                    if (success) {
                        errors++;
                    }
                    success = false;
                    if (pReturnOnFailure) {
                        return errors;
                    }
                }

                if (queryResult1 != null && queryResult1.getWarnings() != null) {
                    if (success) {
                        errors++;
                    }
                    success = false;
                    warnings++;
                    if (logger.isWarnEnabled()) {
                        logger.warn("(" + count + "/" + sqlQueries.size() + ") " + dbConfig1.get("NAME") + ": " + sqlQuery + " " +
                          queryResult1.getWarnings().getLocalizedMessage());
                    }
                }
                if (queryResult2 != null && queryResult2.getWarnings() != null) {
                    if (success) {
                        errors++;
                    }
                    success = false;
                    warnings++;
                    if (logger.isWarnEnabled()) {
                        logger.warn("(" + count + "/" + sqlQueries.size() + ") " + dbConfig2.get("NAME") + ": " + sqlQuery + " " +
                          queryResult1.getWarnings().getLocalizedMessage());
                    }
                }
                if (queryResult1 != null) {
                    queryResult1.close();
                }
                if (queryResult2 != null) {
                    queryResult2.close();
                }
                if (count % 1000 == 0) {
                    if (logger.isInfoEnabled()) {
                        logger.info("At Statement " + count + " of " + sqlQueries.size() + ".");
                    }
                }
            }
        } catch (Exception pExcp) {
            if (logger.isWarnEnabled()) {
                logger.warn(pExcp.getLocalizedMessage());
            }
            pExcp.printStackTrace();
            errors++;
            if (pReturnOnFailure) {
                return errors;
            }
        }

        return errors;
    }

    /**
     * Compares two <code>ResultSet</code>s
     *
     * @param pResultSet1 first <code>ResultSet</code> for comparison
     * @param pResultSet2 second <code>ResultSet</code> for comparison
     * @return <code>true</code> if Result-Sets are equal
     */

    private boolean resultSetsEqual(ResultSet pResultSet1, ResultSet pResultSet2, String pSQLQuery) {
        try {
            if (pResultSet1 != null && pResultSet2 != null) {
                if (pResultSet1.getMetaData().getColumnCount() == pResultSet2.getMetaData().getColumnCount()) {
                    while (pResultSet1.next() && pResultSet2.next()) {
                        for (int i = 1; i <= pResultSet1.getMetaData().getColumnCount(); i++) {
                            if (!pResultSet1.getObject(i).equals(pResultSet2.getObject(i))) {
                                if (!pResultSet1.getObject(i).getClass().equals(pResultSet2.getObject(i).getClass())) {
                                    typeMismatches++;
                                    if (logger.isWarnEnabled()) {
                                        logger.warn("result-set-object-types are not equal\r\n" + pSQLQuery + "\r\nTYPES " +
                                          dbConfig1.get("NAME") + ": " + pResultSet1.getObject(i).getClass().getName() +
                                          " <-> " + dbConfig2.get("NAME") + ": " +
                                          pResultSet2.getObject(i).getClass().getName());
                                    }
                                } else {
                                    valueMismatches++;
                                    if (logger.isWarnEnabled()) {
                                        logger.warn("result-set-object-values are not equal\r\n" + pSQLQuery + "\r\nVALUES " +
                                          dbConfig1.get("NAME") + ": " + pResultSet1.getObject(i).toString() + " <-> " +
                                          dbConfig2.get("NAME") + ": " + pResultSet2.getObject(i).toString());
                                    }
                                }

                                return false;
                            }

                            if (logger.isTraceEnabled()) {
                                logger.trace(dbConfig1.get("NAME") + ": " + pResultSet1.getObject(i).toString());
                                logger.trace(dbConfig2.get("NAME") + ": " + pResultSet2.getObject(i).toString());
                            }
                        }
                    }
                } else {
                    if (logger.isWarnEnabled()) {
                        logger.warn("ResultSet-Column-Count is not equal.\r\n" + pSQLQuery);
                    }
                    return false;
                }
            } else {
                if (pResultSet1 != pResultSet2) {
                    if (pResultSet1 == null) {
                        if (logger.isDebugEnabled()) {
                            nullValues++;
                            if (logger.isWarnEnabled()) {
                                logger.warn("result set #1 is null\r\n" + pSQLQuery);
                            }
                        } else if (pResultSet2 == null) {
                            nullValues++;
                            if (logger.isWarnEnabled()) {
                                logger.warn("result set #2 is null\r\n" + pSQLQuery);
                            }
                        }
                    }
                    return false;
                }
            }
        } catch (Exception pExcp) {
            if (logger.isWarnEnabled()) {
                logger.warn(pExcp.getLocalizedMessage() + "\r\n" + pSQLQuery);
            }
            return false;
        }
        return true;
    }

    /**
     * Filters out a Schema in an SQL-Query. Needed for running Queries against an MS-SQL-DB which does not use schemas
     *
     * @param pSQLQuery the SQL-Query-String
     * @param pSchema   The name of the Schema to be filtered out
     * @return the query-string which has been filtered
     */

    private String filterSchema(String pSQLQuery, String pSchema) {
        if (logger.isTraceEnabled()) {
            logger.trace("SQL-Query before: <" + pSQLQuery + ">");
            logger.trace("Schema: <" + pSchema + ">");
        }

        if (pSQLQuery.indexOf(pSchema + ".") != -1) {
            logger.trace("Schema found in query... filtering");
            pSQLQuery = pSQLQuery.replaceAll(pSchema + ".", "");
        }

        if (logger.isTraceEnabled()) {
            logger.trace("SQL-Query after: <" + pSQLQuery + ">");
        }
        return pSQLQuery;
    }
}
