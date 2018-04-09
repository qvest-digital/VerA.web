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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author kleinw
 *
 *	Hier wird ein ResultSet ausgelesen
 *	und in List oder Mapobjekte umgewandelt.
 *
 *	HINWEIS : Date-Objekte werden hier nach Long
 *	gewandelt, da sich dieser Datentyp f�r den
 *	SOAPTransfer besser eignet.
 *
 */
public class ResultSetReader {

	final static public List list(Collection listColumns, Result result) throws SQLException {
		List resultList = new ArrayList();
		try {
			if (listColumns.size() == 1) {
				while (result.resultSet().next()) {
					getRow(result, listColumns, resultList);
				}
			} else {
				int size = listColumns.size();
				while (result.resultSet().next()) {
					List row = new ArrayList(size);
					getRow(result, listColumns, row);
					resultList.add(row);
				}
			}
		} catch (SQLException e) {
			throw (e);
		} finally {
			if (result != null)
				result.close();
		}
		return resultList;
	}

	private static void getRow(Result result, Collection getColumns, List row) throws SQLException {
		for (Iterator it = getColumns.iterator(); it.hasNext();) {
			Object[] get = (Object[])it.next();
			String column = (String)get[0];
			Class type = (Class)get[2];

			if (type.equals(String.class))
				row.add(result.resultSet().getString(column));
			else if (type.equals(Integer.class))
				row.add(new Integer(result.resultSet().getInt(column)));
			else if (type.equals(Timestamp.class)) {
				Timestamp tstmp = result.resultSet().getTimestamp(column);
				if (tstmp != null)
					row.add(new Long(tstmp.getTime()));
				else
					row.add(null);
			} else
				row.add(result.resultSet().getObject(column));
		}
	}
}
