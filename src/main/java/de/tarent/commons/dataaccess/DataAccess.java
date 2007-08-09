/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
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
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.dataaccess;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.tarent.commons.dataaccess.backend.DataAccessBackend;
import de.tarent.commons.dataaccess.config.ConfigFactory;

/**
 * <p>The both classes {@link DataAccess} and {@link DataAccessTable} are the
 * primary place to go for dataaccess users. For backend configuration see
 * {@link DataAccessConfiguration}.</p>
 * 
 * <p>This class will support you for loading and saving data objects. Primary
 * format of your data will be beans. So every of the getEntry method will
 * return one bean. Also every of the getEntries method will return a list of
 * beans.</p>
 * 
 * @author Christoph Jerolimov, tarent GmbH
 * @author Timo Pick, tarent GmbH
 */
public class DataAccess implements TransactionSupport {
	/** Backend name */
	private final String backendName;
	/** Delegate instance */
	private final DataAccessBackend backend;

	/**
	 * <p>Create a new instance of this class. It will load the
	 * {@link DataAccessBackend} with the name <code>default</code>.</p>
	 * 
	 * @throws NullPointerException If no default backend could be found.
	 */
	public DataAccess() {
		this("default");
	}

	/**
	 * <p>Create a new instance of this class. It will load the
	 * {@link DataAccessBackend} with the given <code>backendName</code>.</p>
	 * 
	 * @param backendName The requested backend name.
	 * @throws NullPointerException If no backend could be found.
	 */
	public DataAccess(String backendName) {
		this.backendName = backendName;
		this.backend = ConfigFactory.getBackend(backendName);
	}

	/**
	 * <p>Create a new instance of this class with the given
	 * {@link DataAccessBackend}. Parameter should not be null.</p>
	 * 
	 * @param dataAccessBackend
	 * @throws NullPointerException If the dataAccessBackend is null.
	 */
	public DataAccess(String backendName, DataAccessBackend dataAccessBackend) {
		if (dataAccessBackend == null)
			throw new NullPointerException("DataAccessBackend should not be null");
		this.backendName = backendName;
		this.backend = dataAccessBackend;
	}

	/**
	 * <p>Returns the underlaying {@link DataAccessBackend}.</p>
	 * 
	 * @return The underlaying {@link DataAccessBackend}.
	 */
	public DataAccessBackend getBackend() {
		return backend;
	}

	/**
	 * <p>Return one entry for the given class, or null. The underlying
	 * {@link DataAccessBackend} MAY throw an exception if more than
	 * one entry was found.</p>
	 * 
	 * @see #getEntries(Class)
	 * 
	 * @param requestedType Requested class type.
	 */
	public Object getEntry(Class requestedType) {
		return getEntry(requestedType, null);
	}

	/**
	 * <p>Return all entries for the requested class type in an list.
	 * List can be empty, but never null.</p>
	 * 
	 * @see #getEntries(Class, Expression)
	 * 
	 * @param requestedType Requested class type.
	 */
	public List getEntries(Class requestedType) {
		return getEntries(requestedType, null);
	}

	/**
	 * <p>Return one entry for the requested class type which agree with the
	 * given filter expression or null if no entry will match. The underlying
	 * {@link DataAccessBackend} MAY throw an exception if more than
	 * one entry was found.</p>
	 * 
	 * @param requestedType Requested class type.
	 * @param expr Filter expression.
	 */
	public Object getEntry(Class requestedType, Object expr) {
		List result = getEntries(requestedType, expr);
		if (result.isEmpty())
			return null;
		else
			return result.get(0);
	}

	/**
	 * <p>Return all entries for the requested class type which agree with the
	 * given filter expression. List can be empty, but never null.</p>
	 * 
	 * @param requestedType Requested class type.
	 * @param expr Filter expression.
	 */
	public List getEntries(Class requestedType, Object expr) {
		return getEntries(requestedType, expr, null, null, null);
	}

	/**
	 * <p>Return all entries for the requested class type which agree with the
	 * given filter expression limited by the first and last entry index.
	 * Ordered by the <code>order</code> instance.</p>
	 * 
	 * @param requestedType Requested class type. If this parameter is null the
	 * result list will not filtered for any type. Notice that many backends
	 * require an requested type for decide from where they must load the beans.
	 * @param expr Filter expression. If this parameter is null the result list
	 * will not filtered for any attributes or one any other way.
	 * @param order Order information, could be null.
	 * @param firstEntryIndex Index (0-based) of the first entry which will be
	 * returned. If this parameter is null this filter will be deactivted. 
	 * @param lastEntryIndex Index (0-based) of the last entry which will be
	 * returned. If this parameter is null this filter will be deactivted.
	 */
	public List getEntries(
			Class requestedType,
			Object expr,
			Object order,
			Integer firstEntryIndex,
			Integer lastEntryIndex) {
		return (List) new QueryProcessor(
				backendName,
				backend,
				QueryProcessor.RESPONSE_MODE_BEAN_LIST,
				requestedType,
				expr,
				order,
				firstEntryIndex,
				lastEntryIndex).execute();
	}

	/**
	 * <p>Store the given <code>object</code> in the underlaying
	 * {@link DataAccessBackend}, supported by an new {@link StoreProcessor}
	 * instance.</p>
	 * 
	 * @param object Object to store.
	 */
	public void store(Object object) {
		new StoreProcessor(backendName, backend, object.getClass()).store(object);
	}

	/**
	 * <p>Store all the objects from the given <code>collection</code> in the
	 * underlaying {@link DataAccessBackend}, supported by one new
	 * {@link StoreProcessor} instance.</p>
	 * 
	 * @param collection Objects to store.
	 */
	public void store(Collection collection) {
		StoreProcessor storeProcessor = new StoreProcessor(backendName, backend, null);
		for (Iterator it = collection.iterator(); it.hasNext(); ) {
			storeProcessor.store(it.next());
		}
	}

	/**
	 * <p>Delete the given <code>object</code> from the underlaying
	 * {@link DataAccessBackend}, supported by an new {@link StoreProcessor}
	 * instance.</p>
	 * 
	 * @param object Object to delete.
	 */
	public void delete(Object object) {
		new StoreProcessor(backendName, backend, object.getClass()).delete(object);
	}

	/**
	 * <p>Delete all the objects from the given <code>collection</code> from the
	 * underlaying {@link DataAccessBackend}, supported by one new
	 * {@link StoreProcessor} instance.</p>
	 * 
	 * @param collection Objects to delete.
	 */
	public void delete(Collection collection) {
		StoreProcessor storeProcessor = new StoreProcessor(backendName, backend, null);
		for (Iterator it = collection.iterator(); it.hasNext(); ) {
			storeProcessor.delete(it.next());
		}
	}

	/** {@inheritDoc} */
	public void begin() {
		backend.begin();
	}

	/** {@inheritDoc} */
	public void commit() {
		backend.commit();
	}

	/** {@inheritDoc} */
	public void rollback() {
		backend.rollback();
	}
}
