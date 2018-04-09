/*
 * tarent-database,
 * jdbc database library
 * Copyright (c) 2005-2006 tarent GmbH
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
 * interest in the program 'tarent-database'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 06.04.2006
 */

package de.tarent.dblayer.engine;

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
 *
 */
public class SQLCache {

    private static final org.apache.commons.logging.Log logger = LogFactory.getLog(SQLCache.class);

    /** This map stores the cached statements. The keys follow
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
    throws IOException
    {
        StringBuffer text = new StringBuffer();

        BufferedReader input = null;

        input = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = input.readLine())!=null)
        {
            text.append(line);
            text.append(System.getProperty("line.separator"));
        }
        input.close();

        return text.toString();
    }

    /** Retrieve an SQL-statement as a String. It is either read from
     * the cache or, if it is not in the cache or is marked as uncacheable,
     * from a file.
     *
     * @param dbx DBContext, used to differentiate cache-partitions.
     * @param sqlfile The SQLFile to fetch.
     * @return The statment as a String.
     */
    public static String getSQLFromFile( DBContext dbx, SQLFile sqlfile ) {
        String sqlStatement = "";

        try {
            String key = "{"+dbx.getPoolName()+"}"+sqlfile.getCanonicalPath();

            if ( sqlfile.isCacheable() && sqlCache.containsKey( key ) ) {
                sqlStatement = (String)sqlCache.get( key );
                if ( logger.isTraceEnabled()) {
                    logger.trace("Found cached statement '"+key+"'" );
                }
            }
            else {
                sqlStatement = readTextFile( sqlfile );
                if ( logger.isDebugEnabled()) {
                    logger.debug("Read uncached statement '"+key+"'" );
                }
                if ( sqlfile.isCacheable() ) {
                    sqlCache.put( key, sqlStatement );
                    if ( logger.isDebugEnabled()) {
                        logger.debug("Statement '"+key+"' cached." );
                    }
                }
            }

        }
        catch ( IOException ex ) {
            logger.error("Could not read sql-file "+sqlfile.getAbsolutePath() );
        }

        return sqlStatement;
    }

    /** Retrieve an SQL-statement as a String. It is either read from
     * the cache or, if it is not in the cache or is marked as uncacheable,
     * from a file.
     *
     * @param poolname The name of the DB-pool, used to differentiate between
     *                 cache-partitions.
     * @param sqlfile The SQLFile to fetch.
     * @return The statment as a String.
     */
    public static String getSQLFromFile( String poolname, SQLFile sqlfile ) {
        String sqlStatement = "";

        try {
            String key = "{"+poolname+"}"+sqlfile.getCanonicalPath();

            if ( sqlfile.isCacheable() && sqlCache.containsKey( key ) ) {
                sqlStatement = (String)sqlCache.get( key );
                if ( logger.isTraceEnabled()) {
                    logger.trace("Found cached statement '"+key+"'" );
                }
            }
            else {
                sqlStatement = readTextFile( sqlfile );
                if ( logger.isDebugEnabled()) {
                    logger.debug("Read uncached statement '"+key+"'" );
                }
                if ( sqlfile.isCacheable() ) {
                    sqlCache.put( key, sqlStatement );
                    if ( logger.isDebugEnabled()) {
                        logger.debug("Statement '"+key+"' cached." );
                    }
                }
            }

        }
        catch ( IOException ex ) {
            logger.error("Could not read sql-file "+sqlfile.getAbsolutePath() );
        }

        return sqlStatement;
    }

}
