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

import java.io.File;

/**
 * An SQLFile is a File with some additional options related to its role as a
 * cacheable sql-template.
 *
 * @author Robert Linden (r.linden@tarent.de)
 *
 */
public class SQLFile extends File {
    /** serialVersionUID */
	private static final long serialVersionUID = 3403719730441441449L;

	/** This flag determines if the SQLFile is cachable. */
    private boolean cacheable;

    /** Create a new SQLFile.
     * For convenience you should use the {@link SQLFileFactory}.
     *
     * @param basepath The path to the file.
     * @param leafname The name of the file.
     * @param cacheable Wether this sql-script should be cached (true)
     *                  or reloaded each time (false).
     */
    public SQLFile( File basepath, String leafname, boolean cacheable  ) {
        super( basepath, leafname+".sql" );
        this.cacheable = cacheable;
    }

    /** Returns the cacheable-flag.
     * @return true = this file should be cached, false = do not cache
     */
    public boolean isCacheable() {
        return cacheable;
    }

}
