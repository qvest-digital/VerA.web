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

import java.util.*;

import de.tarent.dblayer.engine.DB;

/**
 * This is an Octopus Worker Class for opening and closing
 * a set of Database Pools.
 *
 * @author Sebastian Mancke, tarent GmbH
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
