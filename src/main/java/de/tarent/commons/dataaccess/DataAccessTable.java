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

import java.util.Iterator;

import de.tarent.commons.dataaccess.backend.DataAccessBackend;
import de.tarent.commons.dataaccess.config.ConfigFactory;
import de.tarent.commons.dataaccess.data.AttributeSet;
import de.tarent.commons.dataaccess.data.AttributeSetList;

/**
 * <p>The both classes {@link DataAccess} and {@link DataAccessTable} are the
 * primary place to go for dataaccess users. For backend configuration see
 * {@link DataAccessConfiguration}.</p>
 * 
 * <p>This class will support you for loading and saving data objects. Primary
 * format of your data will be {@link AttributeSet attribute sets} or
 * {@link AttributeSetList list of attribute sets}.</p>
 * 
 * @author Christoph Jerolimov, tarent GmbH
 * @author Timo Pick, tarent GmbH
 */
public class DataAccessTable implements TransactionSupport {
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
	public DataAccessTable() {
		this("default");
	}

	/**
	 * <p>Create a new instance of this class. It will load the
	 * {@link DataAccessBackend} with the given <code>backendName</code>.</p>
	 * 
	 * @param backendName The requested backend name.
	 * @throws NullPointerException If no backend could be found.
	 */
	public DataAccessTable(String backendName) {
		this.backendName = backendName;
		this.backend = ConfigFactory.getBackend(backendName);
	}

	/**
	 * <p>Create a new instance of this class with the given
	 * {@link DataAccessBackend}. Parameter should not be null.</p>
	 * 
	 * @param backendName
	 * @param dataAccessBackend
	 * @throws NullPointerException If the dataAccessBackend is null.
	 */
	public DataAccessTable(String backendName, DataAccessBackend dataAccessBackend) {
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
	 * <p>Return all entries for the requested class type which agree with the
	 * given filter expression. List can be empty, but never null.</p>
	 * 
	 * @param requestedType Requested class type.
	 * @param expr Filter expression.
	 */
	public AttributeSetList getAttributeSetList(
			Class requestedType,
			Object expr) {
		return getAttributeSetList(requestedType, expr, null, null, null);
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
	public AttributeSetList getAttributeSetList(
			Class requestedType,
			Object expr,
			Object order,
			Integer firstEntryIndex,
			Integer lastEntryIndex) {
		return (AttributeSetList) new QueryProcessor(
				backendName,
				backend,
				QueryProcessor.RESPONSE_MODE_ATTRIBUTE_SET_LIST,
				requestedType,
				expr,
				order,
				firstEntryIndex,
				lastEntryIndex).execute();
	}

	/**
	 * <p>Store the given <code>attributeSet</code> in the underlaying
	 * {@link DataAccessBackend}, supported by an new {@link StoreProcessor}
	 * instance.</p>
	 * 
	 * @param attributeSet AttributeSet to store.
	 */
	public void store(Class affectedType, AttributeSet attributeSet) {
		new StoreProcessor(backendName, backend, affectedType).store(attributeSet);
	}

	/**
	 * <p>Store all {@link AttributeSet attribute sets} from the given
	 * <code>attributeSetList<(code> in the unterlaying
	 * {@link DataAccessBackend}, supported by one new {@link StoreProcessor}
	 * instance.</p>
	 * 
	 * @param attributeSetList AttributeSets to store.
	 */
	public void store(Class affectedType, AttributeSetList attributeSetList) {
		StoreProcessor storeProcessor = new StoreProcessor(backendName, backend, affectedType);
		for (Iterator it = attributeSetList.iterator(); it.hasNext(); ) {
			storeProcessor.store((AttributeSet) it.next());
		}
	}

	/**
	 * <p>Delete the given <code>attributeSet</code> from the underlaying
	 * {@link DataAccessBackend}, supported by an new {@link StoreProcessor}
	 * instance.</p>
	 * 
	 * @param attributeSet AttributeSet to delete.
	 */
	public void delete(Class affectedType, AttributeSet attributeSet) {
		new StoreProcessor(backendName, backend, affectedType).delete(attributeSet);
	}

	/**
	 * <p>Delete all {@link AttributeSet attribute sets} from the given
	 * <code>attributeSetList<(code> from the unterlaying
	 * {@link DataAccessBackend}, supported by one new {@link StoreProcessor}
	 * instance.</p>
	 * 
	 * @param attributeSetList AttributeSets to delete.
	 */
	public void delete(Class affectedType, AttributeSetList attributeSetList) {
		StoreProcessor storeProcessor = new StoreProcessor(backendName, backend, affectedType);
		for (Iterator it = attributeSetList.iterator(); it.hasNext(); ) {
			storeProcessor.delete((AttributeSet) it.next());
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
