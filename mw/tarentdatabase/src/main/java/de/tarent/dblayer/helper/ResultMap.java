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
 * Created on 15.03.2005
 */
package de.tarent.dblayer.helper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;

import de.tarent.commons.logging.LogFactory;

/**
 * This class is a wrapper for {@link ResultSet} instances implementing the {@link Map}
 * interface to access the current result set row. It does NOT provide for any means of
 * closing the result set or other instances it depends upon.
 *
 * @author Christoph Jerolimov
 */
public class ResultMap implements Map {

    private static final Log logger = LogFactory.getLog(ResultMap.class);

	private final ResultSet resultSet;
	private final List columns;
	private final int size;

	public ResultMap(ResultSet resultSet) throws SQLException {
		this.resultSet = resultSet;

		ResultSetMetaData rsmd = resultSet.getMetaData();
		size = rsmd.getColumnCount();
		columns = new ArrayList(size);
		for (int i = 1; i <= size; i++)
			columns.add(rsmd.getColumnName(i));
	}

	public int size() {
		return size;
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean containsKey(Object key) {
		return columns.contains(key);
	}

	public boolean containsValue(Object value) {
		try {
            if (value == null) {
                for (int i = 1; i <= size; i++)
                    if (resultSet.getObject(i) == null)
                        return true;

            } else {
                for (int i = 1; i <= size; i++)
                    if (value.equals(resultSet.getObject(i)))
                        return true;
            }
		} catch (SQLException e) {
            logger.error("Error checking for value <" + value + "> in current result set row." , e);
		}
		return false;
	}

	public Collection values() {
		List list = new ArrayList(size);
		try {
			for (int i = 1; i <= size; i++)
				list.add(resultSet.getObject(i));
		} catch (SQLException e) {
            logger.error("Error reading all the values of the current result set row." , e);
		}
		return Collections.unmodifiableCollection(list);
	}

	public void putAll(Map t) {
		throw new UnsupportedOperationException();
	}

	public Set entrySet() {
		return new EntrySet();
	}

	public Set keySet() {
		return Collections.unmodifiableSet(new HashSet(columns));
	}

	public Object get(Object key) {
		try {
			int c = columns.indexOf(key) + 1;
			if (c < 1 || c > size)
				return null;
			else
				return resultSet.getObject(c);
		} catch (SQLException e) {
            logger.error("Error reading value for key <" + key+ "> of the current result set row." , e);
			return null;
		}
	}

	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	public Object put(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

	public String toString() {
		return getClass().getName() + " " + columns;
	}

	private class EntrySet implements Set {
		public boolean add(Object arg0) {
			throw new UnsupportedOperationException();
		}

		public boolean addAll(Collection arg0) {
			throw new UnsupportedOperationException();
		}

		public void clear() {
			throw new UnsupportedOperationException();
		}

		public boolean contains(Object o) {
			return ResultMap.this.containsKey(o);
		}

		public boolean containsAll(Collection arg0) {
			for (Iterator it = iterator(); it.hasNext(); ) {
				Map.Entry entry = (Map.Entry)it.next();
				if (!contains(entry.getKey())) return false;
			}
			return true;
		}

		public boolean isEmpty() {
			return ResultMap.this.isEmpty();
		}

		public Iterator iterator() {
			return new EntrySetIterator();
		}

		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		public boolean removeAll(Collection arg0) {
			throw new UnsupportedOperationException();
		}

		public boolean retainAll(Collection arg0) {
			throw new UnsupportedOperationException();
		}

		public int size() {
			return ResultMap.this.size();
		}

		// TODO implement this
		public Object[] toArray() {
			throw new UnsupportedOperationException();
		}

		// TODO implement this
		public Object[] toArray(Object[] arg0) {
			throw new UnsupportedOperationException();
		}
	}

	private class EntrySetIterator implements Iterator {
		private Iterator columnIterator = columns.iterator();
		private String currentColumn = null;

		public boolean hasNext() {
			return columnIterator.hasNext();
		}

		public Object next() {
			currentColumn = (String)columnIterator.next();
			return new Map.Entry() {
				public Object getKey() {
					return currentColumn;
				}

				public Object getValue() {
					return get(currentColumn);
				}

				public Object setValue(Object arg0) {
					throw new UnsupportedOperationException();
				}
			};
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
