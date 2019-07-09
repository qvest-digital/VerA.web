package de.tarent.dblayer.engine;
import java.util.*;

/**
 * This is an Octopus Worker Class for opening and closing
 * a set of Database Pools.
 */
public class PoolWorker {
    /**
     * The Param Name of the Pool definitions within the Octopus Context
     */
    public static final String P_POOL_DEFINITIONS = "CONTENT:poolDefinitions";

    /**
     * The key for the pool identifier within the pool definition map
     */
    public static final String KEY_POOL_NAME = "poolName";

    /**
     * Param Definition
     */
    public static String[] INPUT_openPools = { P_POOL_DEFINITIONS };

    /**
     * Opens all the pools provided in the list of pool definitions.
     * For each Map in the pool definition a pool is opened. The keys
     * of the Map are the same as expected by {@link de.tarent.dblayer.engine.DBPool}.
     * Additional the map <b>must have</b> a Field <code>poolName</code> with a String-identifier
     * for the pool.
     *
     * @param poolDefinitions a list of Maps containing the pool definitions
     */
    public void openPools(List poolDefinitions) {
        for (Iterator iter = poolDefinitions.iterator(); iter.hasNext(); ) {
            Map poolDefinition = (Map) iter.next();
            String poolName = (String) poolDefinition.get(KEY_POOL_NAME);
            DB.openPool(poolName, poolDefinition);
        }
    }

    /**
     * Param Definition
     */
    public static String[] INPUT_closePools = { P_POOL_DEFINITIONS };

    /**
     * Closes all the pools provided in the list of pool definitions.
     * Each map in the list <b>must have</b> a Field <code>poolName</code>, with a String-identifier
     * for the pool to close.
     *
     * @param poolDefinitions a list of Maps containing the pool definitions
     */
    public void closePools(List poolDefinitions) {
        for (Iterator iter = poolDefinitions.iterator(); iter.hasNext(); ) {
            Map poolDefinition = (Map) iter.next();
            String poolName = (String) poolDefinition.get(KEY_POOL_NAME);
            DB.closePool(poolName);
        }
    }
}
