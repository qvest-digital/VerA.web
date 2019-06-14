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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;

import de.tarent.commons.logging.LogFactory;
import de.tarent.octopus.server.Closeable;

/**
 * This class is a wrapper for {@link ResultSet} instances implementing the {@link List}
 * interface to access the rows of the result set which in turn are represented by a
 * single result object; by default this is a {@link ResultMap} instance.<br>
 * This class holds a {@link Runnable} that is executed upon finalisation. To completely
 * wrap the {@link ResultSet} this object can be used to automatically close it and
 * probably also some underlying objects.<br>
 * You should do this only very carefully, though, especially if you reserve a database
 * connection for this List as a pool can easily be exhausted before garbage collection
 * frees these connections for reuse.
 *
 * @author Christoph Jerolimov
 */
import lombok.extern.log4j.Log4j2;@Log4j2
public class ResultList implements List, Closeable {
    /**
     * The {@link ResultSet} operated upon.
     */
    private final ResultSet resultSet;
    /**
     * The single result object returned by {@link #get(int)} and {@link ResultIt#next()}.
     */
    private final Object object;
    /**
     * The single result iterator returned by {@link #iterator()}.
     */
    private final ResultIt iterator;
    /**
     * The {@link Runnable} to run upon finalisation.
     */
    private final Runnable runFinally;
    /**
     * Flag: Was the runFinally allready called?
     */
    private boolean runFinallyCalled = false;
    /**
     * Flag: Is the ResultSet positioned on a readable position?
     */
    private boolean canread = false;

    //
    // constructors
    //

    /**
     * This constructor creates a {@link ResultList} instance using defaults
     * for the result object and finalisation object.<br>
     * You are required to take care of closing the {@link ResultSet} (unless
     * finalisation during garbage collection is early enough) and maybe other
     * entities it depends upon, too.
     *
     * @param resultSet the {@link ResultSet} to wrap
     */
    public ResultList(ResultSet resultSet) throws SQLException {
        this(resultSet, new ResultMap(resultSet));
    }

    /**
     * This constructor creates a {@link ResultList} instance using defaults
     * for the result object.<br>
     * You are required to take care of closing the {@link ResultSet} (unless
     * finalisation during garbage collection is early enough) and maybe other
     * entities it depends upon, too, using the {@link Runnable} parameter.
     *
     * @param runFinally the {@link Runnable} to run during finalisation
     * @param resultSet  the {@link ResultSet} to wrap
     */
    public ResultList(Runnable runFinally, ResultSet resultSet) throws SQLException {
        this(runFinally, resultSet, new ResultMap(resultSet));
    }

    /**
     * This constructor creates a {@link ResultList} instance using defaults
     * for the finalisation object.<br>
     * You are required to take care of closing the {@link ResultSet} (unless
     * finalisation during garbage collection is early enough) and maybe other
     * entities it depends upon, too.
     *
     * @param resultSet    the {@link ResultSet} to wrap
     * @param resultObject the {@link Object} representing the current result set row
     */
    public ResultList(ResultSet resultSet, Object resultObject) throws SQLException {
        this(null, resultSet, resultObject);
    }

    /**
     * This constructor creates a {@link ResultList} instance.<br>
     * You are required to take care of closing the {@link ResultSet} (unless
     * finalisation during garbage collection is early enough) and maybe other
     * entities it depends upon, too, using the {@link Runnable} parameter.
     *
     * @param runFinally   the {@link Runnable} to run during finalisation
     * @param resultSet    the {@link ResultSet} to wrap
     * @param resultObject the {@link Object} representing the current result set row
     */
    public ResultList(Runnable runFinally, ResultSet resultSet, Object resultObject) {
        this.runFinally = runFinally;
        this.resultSet = resultSet;
        this.object = resultObject;
        this.iterator = new ResultIt();
    }

    //
    // public methods
    //

    /**
     * This method returns the wrapped {@link ResultSet}.
     */
    public ResultSet getResultSet() throws SQLException {
        return resultSet;
    }

    /**
     * This method returns the {@link ResultSetMetaData} of the wrapped
     * {@link ResultSet}.
     */
    public ResultSetMetaData getMetaData() throws SQLException {
        return resultSet.getMetaData();
    }

    //
    // interface {@link List}
    //

    /**
     * Returns the number of elements in this {@link List}. If this {@link List}
     * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.<br>
     * Calculating the size of the {@link List} requires changing the position
     * of the cursor of the wrapped {@link ResultSet}:<br>
     * <code>resultSet.last(); return resultSet.getRow()</code><br>
     * This most probably invalidates the object returned by {@link #get(int)}
     * and any {@link Iterator} returned by {@link #iterator()}.
     *
     * @return the number of elements in this {@link List}
     * @see #iterator()
     * @see List#size()
     */
    public int size() {
        try {
            canread = resultSet.last();
            if (canread) {
                return resultSet.getRow();
            }
        } catch (SQLException e) {
            logger.warn("Error calculating the size of the ResultSet", e);
        }
        return 0;
    }

    /**
     * Removes all of the elements from this collection (optional operation).<br>
     * Not supported by {@link ResultList}.
     *
     * @throws UnsupportedOperationException as the <tt>clear</tt> method is
     *                                       not supported by this collection.
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns <tt>true</tt> if this collection contains no elements.<br>
     * Calculating the size of this {@link List} requires changing the position
     * of the cursor of the wrapped {@link ResultSet}:<br>
     * <code>return @{link #size()} == 0</code><br>
     * This most probably invalidates the object returned by {@link #get(int)}
     * and any {@link Iterator} returned by {@link #iterator()}.
     *
     * @return <tt>true</tt> if this collection contains no elements
     * @see #iterator()
     * @see List#isEmpty()
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns an array containing all of the elements in this collection.  If
     * the collection makes any guarantees as to what order its elements are
     * returned by its iterator, this method must return the elements in the
     * same order.<p>
     * TODO: implement properly
     *
     * @return an array containing all of the elements in this collection
     */
    public Object[] toArray() {
        return new Object[] {};
    }

    /**
     * Returns the element at the specified position in this list.<br>
     * Be careful! This method actually always returns the same object
     * as long as it is positioned on a valid position, it only changes
     * the current row of the wrapped {@link ResultSet}.<br>
     * Obviously a call of this method therefore invalidates any object
     * returned by this method itself before and any {@link Iterator}
     * returned by {@link #iterator()} while it itself is in turn most
     * probably invalidated by any use of {@link #size()}, {@link #isEmpty()},
     * this method itself, {@link #iterator()} and any operation of an
     * {@link Iterator} on this {@link ResultList}.
     *
     * @param index index of element to return.
     * @return the element at the specified position in this list.
     */
    public Object get(int index) {
        try {
            if (resultSet.absolute(index + 1)) {
                return object;
            }
        } catch (SQLException e) {
            logger.error("Error positioning ResultSet on position " + (index + 1), e);
        }
        return null;
    }

    /**
     * Removes the element at the specified position in this list (optional
     * operation).<br>
     * Not supported by {@link ResultList}.
     *
     * @throws UnsupportedOperationException as the <tt>remove</tt> method is
     *                                       not supported by this list.
     */
    public Object remove(int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * Inserts the specified element at the specified position in this list
     * (optional operation).<br>
     * Not supported by {@link ResultList}.
     *
     * @throws UnsupportedOperationException if the <tt>add</tt> method is not
     *                                       supported by this list.
     */
    public void add(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the index in this list of the first occurrence of the specified
     * element, or -1 if this list does not contain this element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt>,
     * or -1 if there is no such index.<br>
     * TODO: implement properly.
     */
    public int indexOf(Object o) {
        return -1;
    }

    /**
     * Returns the index in this list of the last occurrence of the specified
     * element, or -1 if this list does not contain this element.
     * More formally, returns the highest index <tt>i</tt> such that
     * <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt>,
     * or -1 if there is no such index.<br>
     * TODO: implement properly.
     */
    public int lastIndexOf(Object o) {
        return -1;
    }

    /**
     * Ensures that this collection contains the specified element (optional
     * operation).<br>
     * Not supported by {@link ResultList}.
     *
     * @throws UnsupportedOperationException <tt>add</tt> is not supported by
     *                                       this collection.
     */
    public boolean add(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns <tt>true</tt> if this collection contains the specified
     * element.  More formally, returns <tt>true</tt> if and only if this
     * collection contains at least one element <tt>e</tt> such that
     * <tt>(o==null ? e==null : o.equals(e))</tt>.<br>
     * TODO: implement properly
     */
    public boolean contains(Object o) {
        return false;
    }

    /**
     * Removes a single instance of the specified element from this
     * collection, if it is present (optional operation).<br>
     * Not supported by {@link ResultList}.
     *
     * @throws UnsupportedOperationException remove is not supported by this
     *                                       collection.
     */
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list at the specified position (optional operation).<br>
     * Not supported by {@link ResultList}.
     *
     * @throws UnsupportedOperationException if the <tt>addAll</tt> method is
     *                                       not supported by this list.
     */
    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds all of the elements in the specified collection to this collection
     * (optional operation).<br>
     * Not supported by {@link ResultList}.
     *
     * @throws UnsupportedOperationException as this collection does not
     *                                       support the <tt>addAll</tt> method.
     */
    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns <tt>true</tt> if this collection contains all of the elements
     * in the specified collection.<br>
     * TODO: implement properly
     */
    public boolean containsAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes all this collection's elements that are also contained in the
     * specified collection (optional operation).<br>
     * Not supported by {@link ResultList}.
     *
     * @throws UnsupportedOperationException as the <tt>removeAll</tt> method
     *                                       is not supported by this collection.
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Retains only the elements in this collection that are contained in the
     * specified collection (optional operation).<br>
     * Not supported by {@link ResultList}.
     *
     * @throws UnsupportedOperationException as the <tt>retainAll</tt> method
     *                                       is not supported by this Collection.
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an iterator over the elements in this collection.  There are no
     * guarantees concerning the order in which the elements are returned
     * (unless this collection is an instance of some class that provides a
     * guarantee).<br>
     * Be careful! This method actually always returns the same object, thus
     * this {@link List} supports only one concurrent {@link Iterator}, the
     * state of any {@link Iterator} formerly returned by this method is the
     * same as the state of this one.<br>
     * Additionally it repositions the wrapped {@link ResultSet} and thereby
     * invalidates any object returned by {@link #get(int)} or any object
     * returned by any other {@link Iterator} returned by this method before.<br>
     * The {@link Iterator} returned by this method actually iterates by using
     * the method {@link ResultSet#next()} of the wrapped {@link ResultSet}
     * and returning the same object again and again which in turn works with
     * the current row of the {@link ResultSet}.<br>
     * Any use of {@link #size()}, {@link #isEmpty()}, {@link #get(int)}, this
     * method itself and any operation of an {@link Iterator} on this
     * {@link ResultList} most probably invalidates the state of this
     * {@link Iterator} and any object returned by it.
     *
     * @return an <tt>Iterator</tt> over the elements in this collection
     */
    public Iterator iterator() {
        reset();
        return iterator;
    }

    /**
     * Returns a view of the portion of this list between the specified
     * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive.  (If
     * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
     * empty.)<br>
     * TODO: implement properly
     */
    public List subList(int fromIndex, int toIndex) {
        return null;
    }

    /**
     * Returns a list iterator of the elements in this list (in proper
     * sequence).<br>
     * TODO: implement properly
     *
     * @return a list iterator of the elements in this list (in proper
     * sequence).
     */
    public ListIterator listIterator() {
        return null;
    }

    /**
     * Returns a list iterator of the elements in this list (in proper
     * sequence), starting at the specified position in this list.  The
     * specified index indicates the first element that would be returned by
     * an initial call to the <tt>next</tt> method.  An initial call to
     * the <tt>previous</tt> method would return the element with the
     * specified index minus one.<br>
     * TODO: implement properly
     */
    public ListIterator listIterator(int index) {
        return null;
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element (optional operation).<br>
     * Not supported by {@link ResultList}.
     *
     * @throws UnsupportedOperationException if the <tt>set</tt> method is not
     *                                       supported by this list.
     */
    public Object set(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an array containing all of the elements in this collection;
     * the runtime type of the returned array is that of the specified array.
     * If the collection fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this collection.<br>
     * TODO: implement properly
     */
    public Object[] toArray(Object[] a) {
        if (a != null && a.length > 0) {
            a[0] = null;
        }
        return a;
    }

    //
    // class {@link Object}
    //

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return getClass().getName() + " " + object.toString();
    }

    /**
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object.<br>
     * The finalisation {@link Runnable} is called here if it was not called bevore.
     *
     * @throws Throwable the <code>Exception</code> raised by this method
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    //
    // interface {@link Closeable}
    //

    /**
     * This may be called to release the underlying ressources.
     * The finalisation {@link Runnable} is called here if it was not called before.
     */
    public synchronized void close() {
        if (runFinally != null && !runFinallyCalled) {
            runFinally.run();
            runFinallyCalled = true;
        }
    }

    //
    // non-public helper methods and classes
    //

    /**
     * This method resets the wrapped {@link ResultSet}.
     */
    private void reset() {
        try {
            logger.debug("Resetting the ResultSet.");
            canread = resultSet.first();
        } catch (SQLException e) {
            logger.error("Error resetting ", e);
        }
    }

    /**
     * This class is the taylored iterator for this {@link List} implementation.
     */
    private class ResultIt implements Iterator {
        /**
         * Returns <tt>true</tt> if the iteration has more elements. (In other
         * words, returns <tt>true</tt> if <tt>next</tt> would return an element
         * rather than throwing an exception.)<br>
         * Beware! This method may invalidate the object returned during the
         * most recent call to {@link #next()} or {@link ResultList#get(int)}.
         *
         * @return <tt>true</tt> if the iterator has more elements.
         */
        public boolean hasNext() {
            try {
                if (canread) {
                    return true;
                } else {
                    canread = ResultList.this.resultSet.next();
                    return canread;
                }
            } catch (SQLException e) {
                logger.error("Error advancing ResultSet cursor", e);
                return false;
            }
        }

        /**
         * Returns the next element in the iteration.<br>
         * Beware! This method always returns the same object as long as it
         * does not terminate by throwing an exception; any call of {@link #hasNext()},
         * {@link ResultList#get(int)}, {@link ResultList#size()}, {@link ResultList#isEmpty()}
         * or of course this method itself just makes this object operate on a different
         * {@link ResultSet} row.
         *
         * @return the next element in the iteration.
         * @throws NoSuchElementException iteration has no more elements.
         */
        public Object next() {
            if (canread || hasNext()) {
                canread = false;
                return object;
            } else {
                throw new NoSuchElementException();
            }
        }

        /**
         * Removes from the underlying collection the last element returned by the
         * iterator (optional operation).<br>
         * Not supported by {@link ResultList.ResultIt}.
         *
         * @throws UnsupportedOperationException if the <tt>remove</tt>
         *                                       operation is not supported by this Iterator.
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
