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

package de.tarent.dblayer.octopus;

import java.util.*;

import de.tarent.dblayer.engine.DB;

/**
 * This is an Octopus Worker Class for opening and closing
 * a set of Database Pools.
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public class PoolWorker {
    /** The Param Name of the Pool definitions within the Octopus Context */
    public static final String P_POOL_DEFINITIONS = "CONTENT:poolDefinitions";

    /** The key for the pool identifier within the pool definition map */
    public static final String KEY_POOL_NAME = "poolName";

    /** Param Definition */
    public static String[] INPUT_openPools = {P_POOL_DEFINITIONS};
    /**
     * Opens all the pools provided in the list of pool definitions.
     * For each Map in the pool definition a pool is opened. The keys
     * of the Map are the same as expected by {@link de.tarent.dblayer.engine.DBPool#Pool(Map)}.
     * Additional the map <b>must have</b> a Field <code>poolName</code> with a String-identifier
     * for the pool.
     *
     * @param poolDefinitions a list of Maps containing the pool definitions
     */
    public void openPools(List poolDefinitions) {
        for (Iterator iter = poolDefinitions.iterator(); iter.hasNext();) {
            Map poolDefinition = (Map)iter.next();
            String poolName = (String)poolDefinition.get(KEY_POOL_NAME);
            DB.openPool(poolName, poolDefinition);
        }
    }

    /** Param Definition */
    public static String[] INPUT_closePools = {P_POOL_DEFINITIONS};
    /**
     * Closes all the pools provided in the list of pool definitions.
     * Each map in the list <b>must have</b> a Field <code>poolName</code>, with a String-identifier
     * for the pool to close.
     *
     * @param poolDefinitions a list of Maps containing the pool definitions
     */
    public void closePools(List poolDefinitions) {
        for (Iterator iter = poolDefinitions.iterator(); iter.hasNext();) {
            Map poolDefinition = (Map)iter.next();
            String poolName = (String)poolDefinition.get(KEY_POOL_NAME);
            DB.closePool(poolName);
        }
    }
}
