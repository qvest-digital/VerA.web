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

package de.tarent.dblayer.persistence;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple list of parameters and values.
 * 
 * @see de.tarent.dblayer.persistence.AbstractDAO#getEntityByIdFilterList(DBContext, String, ParamList)
 * 
 * @author Hendrik Helwich
 */
public class ParamList {
    
    private List params;

    /**
     * Adds a parameter and its value to the list.
     * 
     * @param   name 
     *          The name of the parameter
     * @param   value
     *          The value of the parameter
     * @return  The reference of this object
     */
    public ParamList add(String name, Object value) {
        if (params == null)
            params = new LinkedList();
        params.add(new Object[] { name, value });
        return this;
    }
    
    /**
     * Returns the list of parameters and its values.
     * 
     * @return  The list of parameters and its values.
     *          An Element of this list is a two-dimensional 
     *          {@link Object} array. The first element of this array is
     *          a {@link java.lang.String} object.
     */
    public List getParams() {
        return params;
    }
}
