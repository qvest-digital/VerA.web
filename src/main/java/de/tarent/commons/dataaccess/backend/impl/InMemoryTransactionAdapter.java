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

package de.tarent.commons.dataaccess.backend.impl;

import java.util.LinkedList;
import java.util.List;

import de.tarent.commons.dataaccess.DataAccessException;
import de.tarent.commons.dataaccess.StoreProcessor;
import de.tarent.commons.dataaccess.backend.AbstractDataAccessBackend;
import de.tarent.commons.dataaccess.backend.DataAccessBackend;
import de.tarent.commons.dataaccess.backend.QueryingStrategy;
import de.tarent.commons.dataaccess.backend.StoringStrategy;

public class InMemoryTransactionAdapter extends AbstractDataAccessBackend {
	private final DataAccessBackend delegate;

	private volatile Boolean isCommiting;

	private List objectToStore;
	private List objectToDelete;

	public InMemoryTransactionAdapter(DataAccessBackend delegate) {
		this.delegate = delegate;
		init();
	}

	public void init() {
		isCommiting = Boolean.FALSE;
		objectToStore = new LinkedList();
		objectToDelete = new LinkedList();
	}

	/** {@inheritDoc} */
	public QueryingStrategy getQueryingStrategy() {
		throw new DataAccessException(
				"Method is not available in InMemoryTransactionAdapter. " +
				"Read class JavaDoc for legal usage.");
	}

	/** {@inheritDoc} */
	public StoringStrategy getStoringStrategy() {
		throw new DataAccessException(
				"Method is not available in InMemoryTransactionAdapter. " +
				"Read class JavaDoc for legal usage.");
	}

	/**
	 * <p>Please override this method like this:</p>
	 * 
	 * <pre><code>
	 * 	public void store(Object o) {
	 * 		if (isCommiting()) {
	 * 			// save the object with sense...
	 * 		} else {
	 * 			super.store(o);
	 * 		}
	 * 	}
	 * </code></pre>
	 * 
	 * <p>See {@link StorageBackend#store(Object)} for more information.</p>
	 */
	public void store(Object o) {
		if (!isCommiting()) {
			objectToStore.add(o);
		} else {
			throw new IllegalStateException("Can not store in-memory while commiting.");
		}
	}

	/**
	 * <p>Please override this method like this:</p>
	 * 
	 * <pre><code>
	 * 	public void delete(Object o) {
	 * 		if (isCommiting()) {
	 * 			// delete the object with sense...
	 * 		} else {
	 * 			super.delete(o);
	 * 		}
	 * 	}
	 * </code></pre>
	 * 
	 * <p>See {@link StorageBackend#delete(Object)} for more information.</p>
	 */
	public void delete(Object o) {
		if (!isCommiting()) {
			objectToDelete.add(o);
		} else {
			throw new IllegalStateException("Can not store in-memory while commiting.");
		}
	}

	/** {@inheritDoc} */
	public void begin() {
		rollback();
	}

	/** {@inheritDoc} */
	public synchronized void commit() {
		synchronized (this) {
			if (isCommiting.booleanValue())
				throw new IllegalStateException("Can not commit (again) while (already) commiting.");
			
			isCommiting = Boolean.TRUE;
			
			StoreProcessor storeProcessor = new StoreProcessor(null, delegate, null);
			
			// Delegate store
			for (int i = 0; i < objectToStore.size(); i++) {
				storeProcessor.store(objectToStore.get(i));
			}
			objectToStore.clear();
			
			// Delegate delete
			for (int i = 0; i < objectToDelete.size(); i++) {
				storeProcessor.delete(objectToDelete.get(i));
			}
			objectToDelete.clear();
			
			isCommiting = Boolean.FALSE;
		}
	}

	/** {@inheritDoc} */
	public synchronized void rollback() {
		synchronized (this) {
			objectToStore.clear();
			objectToDelete.clear();
		}
	}

	public boolean isCommiting() {
		return isCommiting.booleanValue();
	}

	public int getRestrainedStoreEntries() {
		return objectToStore.size();
	}

	public int getRestrainedDeleteEntries() {
		return objectToDelete.size();
	}

	public int getRestrainedEntries() {
		return getRestrainedStoreEntries() + getRestrainedDeleteEntries();
	}
}
