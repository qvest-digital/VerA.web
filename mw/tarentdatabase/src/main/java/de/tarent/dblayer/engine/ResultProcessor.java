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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is an interface for iteration over a result set using the visitor pattern.
 * With this method, the error handling is done by the surrounding framework.
 */
public interface ResultProcessor {

    /**
     * This method will be called by the framework code for each row in the result set.
     *
     * @param rownum the current row number, <b>starting at 0<b>
     * @param rs the result set to process, positioned at a new row.
     */
    public void process(ResultSet rs) throws SQLException;

}
