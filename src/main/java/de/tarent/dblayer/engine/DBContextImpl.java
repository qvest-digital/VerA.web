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

package de.tarent.dblayer.engine;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This is the default implementation of the DBLayer Database Execution Context Interface
 * {@link DBContext}.
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public class DBContextImpl implements DBContext {
    
    String poolName;
    Connection con = null;

    /** Sets the pool identifier */
    public void setPoolName(String newPoolName) {
        this.poolName = newPoolName;
    }    
    
    /**
     * Returns the Pool identifier for this DBContext
     */
    public String getPoolName() {
        return this.poolName;
    }

    /**
     * Returns the Pool Object for this DBContext
     */
    public Pool getPool() {
        return DB.getPool(this.getPoolName());
    }

	public Connection getDefaultConnection() throws SQLException {
		if(con==null||con.isClosed()){
            Pool pool = DB.getPool(poolName);
            if (pool == null) 
                throw new RuntimeException("no pool configured for '"+getPoolName()+"'");
			con = pool.getConnection();
		}
		return con;
	}    
}