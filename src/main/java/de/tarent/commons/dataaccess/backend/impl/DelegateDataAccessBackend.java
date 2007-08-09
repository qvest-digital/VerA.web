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

import java.util.List;

import de.tarent.commons.dataaccess.QueryProcessor;
import de.tarent.commons.dataaccess.StoreProcessor;
import de.tarent.commons.dataaccess.backend.DataAccessBackend;
import de.tarent.commons.dataaccess.backend.QueryingStrategy;
import de.tarent.commons.dataaccess.backend.QueryingWithOwnFilter;
import de.tarent.commons.dataaccess.backend.QueryingWithQueryBuilder;
import de.tarent.commons.dataaccess.backend.StoringAttributeSets;
import de.tarent.commons.dataaccess.backend.StoringObjectTrees;
import de.tarent.commons.dataaccess.backend.StoringStrategy;
import de.tarent.commons.dataaccess.data.AttributeSet;
import de.tarent.commons.dataaccess.query.QueryBuilder;
import de.tarent.commons.dataaccess.query.QueryFilter;
import de.tarent.commons.dataaccess.query.QueryParser;
import de.tarent.commons.dataaccess.query.QueryVisitor;

/**
 * {@link QueryBackend} implementation which backend EVERY method
 * call to the underlying {@link #backend backend instance}.
 * 
 * @author Christoph Jerolimov, tarent GmbH
 */
public class DelegateDataAccessBackend implements DataAccessBackend {
	/** Delegate instance */
	private final DataAccessBackend delegate;

	/**
	 * Create an new delegation instance.
	 * 
	 * @param backend
	 */
	public DelegateDataAccessBackend(DataAccessBackend delegate) {
		if (delegate == null)
			throw new NullPointerException("Delegate can not be null.");
		this.delegate = delegate;
		init();
	}

	public DataAccessBackend getBackend() {
		return delegate;
	}

	/** {@inheritDoc} */
	public void init() {
		delegate.init();
	}

	/** {@inheritDoc} */
	public QueryingStrategy getQueryingStrategy() {
		QueryingStrategy queryingStrategy = delegate.getQueryingStrategy();
		if (queryingStrategy instanceof QueryingWithOwnFilter) {
			return new QueryingWithOwnFilterDelegate((QueryingWithOwnFilter) queryingStrategy);
		} else if (queryingStrategy instanceof QueryingWithQueryBuilder) {
			return new QueryingWithQueryBuilderDelegate((QueryingWithQueryBuilder) queryingStrategy);
		} else {
			throw new IllegalArgumentException("Illegal querying strategy: " +
					queryingStrategy.getClass().getName());
		}
	}

	/** {@inheritDoc} */
	public StoringStrategy getStoringStrategy() {
		StoringStrategy storingStrategy = delegate.getStoringStrategy();
		if (storingStrategy instanceof StoringAttributeSets) {
			return new StoringAttributeSetsDelegate((StoringAttributeSets) storingStrategy);
		} else if (storingStrategy instanceof StoringObjectTrees) {
			return new StoringObjectTreesDelegate((StoringObjectTrees) storingStrategy);
		} else {
			throw new IllegalArgumentException("Illegal storing strategy: " +
					storingStrategy.getClass().getName());
		}
	}

	/** {@inheritDoc} */
	public void begin() {
		delegate.begin();
	}

	/** {@inheritDoc} */
	public void commit() {
		delegate.commit();
	}

	/** {@inheritDoc} */
	public void rollback() {
		delegate.rollback();
	}

	public class QueryingWithOwnFilterDelegate implements QueryingWithOwnFilter {
		private final QueryingWithOwnFilter delegate;
		
		protected QueryingWithOwnFilterDelegate(QueryingWithOwnFilter delegate) {
			this.delegate = delegate;
		}
		
		public List query(QueryProcessor queryProcessor, QueryParser queryParser, QueryVisitor queryVisitor) {
			return delegate.query(queryProcessor, queryParser, queryVisitor);
		}
	}

	public class QueryingWithQueryBuilderDelegate implements QueryingWithQueryBuilder {
		private final QueryingWithQueryBuilder delegate;
		
		protected QueryingWithQueryBuilderDelegate(QueryingWithQueryBuilder delegate) {
			this.delegate = delegate;
		}
		
		public Object executeQuery(QueryProcessor queryProcessor, Object query) {
			return delegate.executeQuery(queryProcessor, query);
		}

		public Integer getFirstEntryIndex() {
			return delegate.getFirstEntryIndex();
		}

		public Integer getLastEntryIndex() {
			return delegate.getLastEntryIndex();
		}

		public Object getOrder() {
			return delegate.getOrder();
		}

		public QueryBuilder getQueryBuilder() {
			return new QueryBuilderDelegate(delegate.getQueryBuilder());
		}

		public void setFirstEntryIndex(Integer firstEntryIndex) {
			delegate.setFirstEntryIndex(firstEntryIndex);
		}

		public void setLastEntryIndex(Integer lastEntryIndex) {
			delegate.setLastEntryIndex(lastEntryIndex);
		}

		public void setOrder(Object order) {
			delegate.setOrder(order);
		}
	}

	public class StoringAttributeSetsDelegate implements StoringAttributeSets {
		private final StoringAttributeSets delegate;
		
		protected StoringAttributeSetsDelegate(StoringAttributeSets delegate) {
			this.delegate = delegate;
		}
		
		public void store(StoreProcessor storeProcessor, AttributeSet attributeSet) {
			delegate.store(storeProcessor, attributeSet);
		}

		public void delete(StoreProcessor storeProcessor, AttributeSet attributeSet) {
			delegate.delete(storeProcessor, attributeSet);
		}
	}

	public class StoringObjectTreesDelegate implements StoringObjectTrees {
		private final StoringObjectTrees delegate;
		
		protected StoringObjectTreesDelegate(StoringObjectTrees delegate) {
			this.delegate = delegate;
		}
		
		public void store(StoreProcessor storeProcessor, Object object) {
			delegate.store(storeProcessor, object);
		}

		public void store(StoreProcessor storeProcessor, Object object, int depth) {
			delegate.store(storeProcessor, object, depth);
		}

		public void delete(StoreProcessor storeProcessor, Object object) {
			delegate.delete(storeProcessor, object);
		}
	}

	public class QueryBuilderDelegate implements QueryBuilder {
		private final QueryBuilder delegate;
		
		public QueryBuilderDelegate(QueryBuilder delegate) {
			this.delegate = delegate;
		}

		public Object getQuery() {
			return delegate.getQuery();
		}

		public void filterAttributeEqualWithOneOf(String attribute, String[] values) {
			delegate.filterAttributeEqualWithOneOf(attribute, values);
		}

		public void filterAttributeEqualWithPattern(String attribute, String pattern) {
			delegate.filterAttributeEqualWithPattern(attribute, pattern);
		}

		public void filterAttributeGreaterOrEqualsThanPattern(String attribute, String pattern) {
			delegate.filterAttributeGreaterOrEqualsThanPattern(attribute, pattern);
		}

		public void filterAttributeGreaterThanPattern(String attribute, String pattern) {
			delegate.filterAttributeGreaterThanPattern(attribute, pattern);
		}

		public void filterAttributeLowerOrEqualsThanPattern(String attribute, String pattern) {
			delegate.filterAttributeLowerOrEqualsThanPattern(attribute, pattern);
		}

		public void filterAttributeLowerThanPattern(String attribute, String pattern) {
			delegate.filterAttributeLowerThanPattern(attribute, pattern);
		}

		public QueryFilter getQueryFilter() {
			return delegate.getQueryFilter();
		}

		public QueryParser getQueryParser() {
			return delegate.getQueryParser();
		}

		public QueryProcessor getQueryProcessor() {
			return delegate.getQueryProcessor();
		}

		public QueryVisitor getQueryVisitor() {
			return delegate.getQueryVisitor();
		}

		public void setQueryFilter(QueryFilter queryFilter) {
			delegate.setQueryFilter(queryFilter);
		}

		public void setQueryParser(QueryParser queryParser) {
			delegate.setQueryParser(queryParser);
		}

		public void setQueryProcessor(QueryProcessor queryProcessor) {
			delegate.setQueryProcessor(queryProcessor);
		}

		public void setQueryVisitor(QueryVisitor queryVisitor) {
			delegate.setQueryVisitor(queryVisitor);
		}
	}
}
