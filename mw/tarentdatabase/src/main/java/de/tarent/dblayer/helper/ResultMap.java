package de.tarent.dblayer.helper;

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

import lombok.extern.log4j.Log4j2;

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

/**
 * This class is a wrapper for {@link ResultSet} instances implementing the {@link Map}
 * interface to access the current result set row. It does NOT provide for any means of
 * closing the result set or other instances it depends upon.
 *
 * @author Christoph Jerolimov
 */
@Log4j2
public class ResultMap implements Map {
    private final ResultSet resultSet;
    private final List columns;
    private final int size;

    public ResultMap(ResultSet resultSet) throws SQLException {
        this.resultSet = resultSet;

        ResultSetMetaData rsmd = resultSet.getMetaData();
        size = rsmd.getColumnCount();
        columns = new ArrayList(size);
        for (int i = 1; i <= size; i++) {
            columns.add(rsmd.getColumnName(i));
        }
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
                for (int i = 1; i <= size; i++) {
                    if (resultSet.getObject(i) == null) {
                        return true;
                    }
                }
            } else {
                for (int i = 1; i <= size; i++) {
                    if (value.equals(resultSet.getObject(i))) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking for value <" + value + "> in current result set row.", e);
        }
        return false;
    }

    public Collection values() {
        List list = new ArrayList(size);
        try {
            for (int i = 1; i <= size; i++) {
                list.add(resultSet.getObject(i));
            }
        } catch (SQLException e) {
            logger.error("Error reading all the values of the current result set row.", e);
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
            if (c < 1 || c > size) {
                return null;
            } else {
                return resultSet.getObject(c);
            }
        } catch (SQLException e) {
            logger.error("Error reading value for key <" + key + "> of the current result set row.", e);
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
                Map.Entry entry = (Map.Entry) it.next();
                if (!contains(entry.getKey())) {
                    return false;
                }
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
            currentColumn = (String) columnIterator.next();
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
