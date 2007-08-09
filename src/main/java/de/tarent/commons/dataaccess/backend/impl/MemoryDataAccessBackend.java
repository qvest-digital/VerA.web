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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.tarent.commons.dataaccess.QueryProcessor;
import de.tarent.commons.dataaccess.StoreProcessor;
import de.tarent.commons.dataaccess.backend.AbstractDataAccessBackend;
import de.tarent.commons.dataaccess.backend.QueryingStrategy;
import de.tarent.commons.dataaccess.backend.QueryingWithOwnFilter;
import de.tarent.commons.dataaccess.backend.StoringObjectTrees;
import de.tarent.commons.dataaccess.backend.StoringStrategy;
import de.tarent.commons.dataaccess.query.QueryParser;
import de.tarent.commons.dataaccess.query.QueryVisitor;

public class MemoryDataAccessBackend extends AbstractDataAccessBackend implements QueryingWithOwnFilter, StoringObjectTrees {
	private InMemoryTransactionAdapter inMemoryTransactionAdapter;

	private List objectCache = new ArrayList();

	public MemoryDataAccessBackend() {
		init();
	}

	public void init() {
		inMemoryTransactionAdapter = new InMemoryTransactionAdapter(this);
	}

	/** {@inheritDoc} */
	public QueryingStrategy getQueryingStrategy() {
		return this;
	}

	/** {@inheritDoc} */
	public StoringStrategy getStoringStrategy() {
		return this;
	}

	/** {@inheritDoc} */
	public void store(StoreProcessor storeProcessor, Object object) {
 		if (inMemoryTransactionAdapter.isCommiting()) {
 			if (!objectCache.contains(object))
 				objectCache.add(object);
 		} else {
 			inMemoryTransactionAdapter.store(object);
 		}
	}

	/** {@inheritDoc} */
	public void store(StoreProcessor storeProcessor, Object object, int depth) {
		if (depth != 1)
			throw new IllegalArgumentException("depth != 1 not supported currently");
		store(storeProcessor, object);
	}

	/** {@inheritDoc} */
	public void delete(StoreProcessor storeProcessor, Object object) {
		if (inMemoryTransactionAdapter.isCommiting()) {
			if (objectCache.contains(object))
 				objectCache.remove(object);
 		} else {
 			inMemoryTransactionAdapter.delete(object);
 		}
	}

	public List query(QueryProcessor queryProcessor, QueryParser queryParser, QueryVisitor queryVisitor) {
		List result = new LinkedList();
		
		if (queryProcessor.getRequestedType() != null && queryProcessor.getBaseExpr() != null) {
			for (int i = 0; i < objectCache.size(); i++) {
				if (queryProcessor.getRequestedType().isInstance(objectCache.get(i)))
					if (match(queryProcessor, queryParser, queryVisitor, objectCache.get(i)))
						result.add(objectCache.get(i));
			}
		} else if (queryProcessor.getRequestedType() != null && queryProcessor.getBaseExpr() == null) {
			for (int i = 0; i < objectCache.size(); i++) {
				if (queryProcessor.getRequestedType().isInstance(objectCache.get(i)))
					result.add(objectCache.get(i));
			}
		} else if (queryProcessor.getRequestedType() == null && queryProcessor.getBaseExpr() != null) {
			for (int i = 0; i < objectCache.size(); i++) {
				if (match(queryProcessor, queryParser, queryVisitor, objectCache.get(i)))
					result.add(objectCache.get(i));
			}
		} else if (queryProcessor.getRequestedType() == null && queryProcessor.getBaseExpr() == null) {
			return objectCache;
		}
		
		return result;
	}

	private boolean match(
			QueryProcessor queryProcessor,
			QueryParser queryParser,
			QueryVisitor queryVisitor,
			Object object) {
		
		MemoryObjectFilter memoryObjectFilter = new MemoryObjectFilter(object);
		
		QueryProcessor.bind(
				queryProcessor,
				queryParser,
				queryVisitor,
				memoryObjectFilter);
		
		queryParser.parse(queryProcessor.getBaseExpr());
		
		return memoryObjectFilter.match();
	}

	/** {@inheritDoc} */
	public void begin() {
		inMemoryTransactionAdapter.begin();
	}

	/** {@inheritDoc} */
	public void commit() {
		inMemoryTransactionAdapter.commit();
	}

	/** {@inheritDoc} */
	public void rollback() {
		inMemoryTransactionAdapter.rollback();
	}

	public int getObjectCacheSize() {
		return objectCache.size();
	}

	public boolean isCommiting() {
		return inMemoryTransactionAdapter.isCommiting();
	}

	public int getRestrainedStoreEntries() {
		return inMemoryTransactionAdapter.getRestrainedStoreEntries();
	}

	public int getRestrainedDeleteEntries() {
		return inMemoryTransactionAdapter.getRestrainedDeleteEntries();
	}

	public int getRestrainedEntries() {
		return inMemoryTransactionAdapter.getRestrainedEntries();
	}
}
