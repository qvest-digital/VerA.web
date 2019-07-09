package de.tarent.dblayer.engine;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The SQLCache loads sql-statements from files and optionaly caches the statements
 * in order to avoid redundant I/O. The caching is per connection-pool.
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 * @author Robert Linden (r.linden@tarent.de)
 */
@Log4j2
public class SQLCache {
    /**
     * This map stores the cached statements. The keys follow
     * the pattern {poolname}fullpathname.
     */
    private static Map sqlCache = new HashMap();

    /**
     * Reads the contents of a file into a string.
     *
     * @param file File to be read.
     * @return File contents as String.
     */
    private static String readTextFile(File file)
      throws IOException {
        StringBuffer text = new StringBuffer();

        BufferedReader input = null;

        input = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = input.readLine()) != null) {
            text.append(line);
            text.append(System.getProperty("line.separator"));
        }
        input.close();

        return text.toString();
    }

    /**
     * Retrieve an SQL-statement as a String. It is either read from
     * the cache or, if it is not in the cache or is marked as uncacheable,
     * from a file.
     *
     * @param dbx     DBContext, used to differentiate cache-partitions.
     * @param sqlfile The SQLFile to fetch.
     * @return The statment as a String.
     */
    public static String getSQLFromFile(DBContext dbx, SQLFile sqlfile) {
        String sqlStatement = "";

        try {
            String key = "{" + dbx.getPoolName() + "}" + sqlfile.getCanonicalPath();

            if (sqlfile.isCacheable() && sqlCache.containsKey(key)) {
                sqlStatement = (String) sqlCache.get(key);
                if (logger.isTraceEnabled()) {
                    logger.trace("Found cached statement '" + key + "'");
                }
            } else {
                sqlStatement = readTextFile(sqlfile);
                if (logger.isDebugEnabled()) {
                    logger.debug("Read uncached statement '" + key + "'");
                }
                if (sqlfile.isCacheable()) {
                    sqlCache.put(key, sqlStatement);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Statement '" + key + "' cached.");
                    }
                }
            }
        } catch (IOException ex) {
            logger.error("Could not read sql-file " + sqlfile.getAbsolutePath());
        }

        return sqlStatement;
    }

    /**
     * Retrieve an SQL-statement as a String. It is either read from
     * the cache or, if it is not in the cache or is marked as uncacheable,
     * from a file.
     *
     * @param poolname The name of the DB-pool, used to differentiate between
     *                 cache-partitions.
     * @param sqlfile  The SQLFile to fetch.
     * @return The statment as a String.
     */
    public static String getSQLFromFile(String poolname, SQLFile sqlfile) {
        String sqlStatement = "";

        try {
            String key = "{" + poolname + "}" + sqlfile.getCanonicalPath();

            if (sqlfile.isCacheable() && sqlCache.containsKey(key)) {
                sqlStatement = (String) sqlCache.get(key);
                if (logger.isTraceEnabled()) {
                    logger.trace("Found cached statement '" + key + "'");
                }
            } else {
                sqlStatement = readTextFile(sqlfile);
                if (logger.isDebugEnabled()) {
                    logger.debug("Read uncached statement '" + key + "'");
                }
                if (sqlfile.isCacheable()) {
                    sqlCache.put(key, sqlStatement);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Statement '" + key + "' cached.");
                    }
                }
            }
        } catch (IOException ex) {
            logger.error("Could not read sql-file " + sqlfile.getAbsolutePath());
        }

        return sqlStatement;
    }
}
