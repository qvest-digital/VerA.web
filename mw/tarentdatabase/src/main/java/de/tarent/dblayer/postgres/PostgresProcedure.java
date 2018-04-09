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

/**
 * 
 */
package de.tarent.dblayer.postgres;

import java.util.Iterator;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.statement.Procedure;



/**
 * @author kirchner
 *
 */
public class PostgresProcedure extends Procedure {

	/**
	 * Default-Constructor @see {@link Procedure#Procedure(DBContext, String)}
	 * @param dbx
	 * @param name
	 */
	public PostgresProcedure(DBContext dbx, String name) {
		super(dbx, name);
	}

	public String statementToString() throws SyntaxErrorException {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM ");
		sb.append(_name);
		sb.append("(");
		Iterator it = _params.iterator();
		boolean first = true;
		while(it.hasNext()){
			if(!first){
				sb.append(", ");
			}
			else 
				first = false;
			
			sb.append(SQL.format(getDBContext(), it.next()));
		}
		sb.append(")");
		return sb.toString();
	}


}
