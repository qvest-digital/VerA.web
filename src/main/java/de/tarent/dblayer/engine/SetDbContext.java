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
 * $Id: SetDbContext.java,v 1.2 2007/06/14 14:51:57 dgoema Exp $
 * 
 * Created on 18.04.2006
 */
package de.tarent.dblayer.engine;

/**
 * This interface is to be implemented by all classes that allow a
 * {@link DBContext} to be set using a fitting setter method. It is
 * used for dependency injection. 
 * 
 * @author Michael Klink
 */
public interface SetDbContext {
    /**
     * This method injects the database execution context.
     * 
     * @param dbc database execution context
     */
    public void setDBContext(DBContext dbc);
}
