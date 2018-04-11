package de.tarent.dblayer.engine;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.tarent.commons.logging.LogFactory;

/**
 * The SQLCache loads sql-statements from files and optionaly caches the statements
 * in order to avoid redundant I/O. The caching is per connection-pool.
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 * @author Robert Linden (r.linden@tarent.de)
 */
public class SQLCache {

    private static final org.apache.commons.logging.Log logger = LogFactory.getLog(SQLCache.class);

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
