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

/* $Id: Function.java,v 1.8 2007/06/14 14:51:56 dgoema Exp $
 * Created on 02.09.2004
 */
package de.tarent.dblayer.sql.clause;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContextImpl;

/**
 * This class represents a SQL function consisting of a name and optionally
 * a number of parameters.
 */
public class Function extends SetDbContextImpl {
    //
    // protected member variables
    //
    /** name of the function */
    String name;
    /** list of parameter description of the function */
    ArrayList parameter = new ArrayList();

    //
    // constructors
    //
    /**
     * This constructor accepts only the name of the function.
     * 
     * @deprecated use {@link #Function(DBContext, String)} instead
     */
	public Function(String name) {
		this.name = name;
	}

    /**
     * This constructor accepts the name of the function and the
     * {@link DBContext} its literal parameters are to be formatted
     * according to.
     */
	public Function(DBContext dbc, String name) {
		this.name = name;
        setDBContext(dbc);
	}

    /**
     * This constructor accepts the name of the function, one column
     * parameter and a number of literal parameters. 
     */
	public Function(String name, String column, Collection param) {
		this.name = name;
		column(column);
		parameter(param);
	}

    /**
     * This constructor accepts the name of the function, a number of
     * literal parameters and one column parameter. 
     */
	public Function(String name, Collection param, String column) {
		this.name = name;
		parameter(param);
		column(column);
	}

    /**
     * This constructor accepts the name of the function and a number
     * of literal parameters
     */
	public Function(String name, Collection param) {
		this.name = name;
		parameter(param);
	}

    //
    // public method
    //
    /**
     * This method adds a number of literal parameters.
     */
	public Function parameter(Collection param) {
		for (Iterator it = param.iterator(); it.hasNext(); )
			parameter.add(new LiteralWrapper(it.next()));
		return this;
	}

    /**
     * This method adds a literal parameter.
     */
	public Function parameter(Object param) {
		parameter.add(new LiteralWrapper(param));
		return this;
	}

    /**
     * This method adds a column parameter.
     */
	public Function column(String column) {
		parameter.add(column);
		return this;
	}

    //
    // class {@link Object}
    //
    /**
     * This method returns a serialization of this {@link Function}
     * according to the current {@link DBContext}.
     */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(name);
		buffer.append("(");
		for (Iterator it = parameter.iterator(); it.hasNext(); ) {
			buffer.append(it.next());
			if (it.hasNext())
				buffer.append(", ");
		}
		buffer.append(")");
		return buffer.toString();
	}

    /**
     * Returns an independent clone of this statement.
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            Function theClone = (Function)super.clone();
            theClone.parameter = (ArrayList)parameter.clone();
            return theClone;
        }
        catch(CloneNotSupportedException e) {
        	throw new InternalError();
        }
      }   
}
