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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import de.tarent.commons.dataaccess.query.QueryParser;
import de.tarent.commons.dataaccess.query.QueryVisitor;

/**
 * {@link QueryBackend} implementation which backend EVERY method
 * call to the underlying {@link #backend backend instance}.
 * 
 * @author Christoph Jerolimov, tarent GmbH
 */
public class TimeLoggingDataAccessBackend extends DelegateDataAccessBackend {
	private final Log log;

	/**
	 * Create an new time logging instance.
	 * 
	 * @param backend
	 */
	public TimeLoggingDataAccessBackend(DataAccessBackend delegate) {
		super(delegate);
		log = LogFactory.getLog(getBackend().getClass());
	}

	/** {@inheritDoc} */
	public QueryingStrategy getQueryingStrategy() {
		QueryingStrategy queryingStrategy = super.getQueryingStrategy();
		if (queryingStrategy instanceof QueryingWithOwnFilter) {
			return new QueryingWithOwnFilterDelegate((QueryingWithOwnFilter) queryingStrategy);
		} else if (queryingStrategy instanceof QueryingWithQueryBuilder) {
			return new QueryingWithQueryBuilderDelegate((QueryingWithQueryBuilder) queryingStrategy);
		} else {
			throw new IllegalArgumentException("Unsupported querying strategy: " +
					queryingStrategy.getClass().getName());
		}
	}

	/** {@inheritDoc} */
	public StoringStrategy getStoringStrategy() {
		StoringStrategy storingStrategy = super.getStoringStrategy();
		if (storingStrategy instanceof StoringAttributeSets) {
			return new StoringAttributeSetsDelegate((StoringAttributeSets) storingStrategy);
		} else if (storingStrategy instanceof StoringObjectTrees) {
			return new StoringObjectTreesDelegate((StoringObjectTrees) storingStrategy);
		} else {
			throw new IllegalArgumentException("Unsupported storing strategy: " +
					storingStrategy.getClass().getName());
		}
	}

	public class QueryingWithOwnFilterDelegate extends DelegateDataAccessBackend.QueryingWithOwnFilterDelegate {
		protected QueryingWithOwnFilterDelegate(QueryingWithOwnFilter delegate) {
			super(delegate);
		}
		
		public List query(QueryProcessor queryProcessor, QueryParser queryParser, QueryVisitor queryVisitor) {
			long time = System.currentTimeMillis();
			try {
				return super.query(queryProcessor, queryParser, queryVisitor);
			} finally {
				if (log.isInfoEnabled())
					log.info("[" + (System.currentTimeMillis() - time) + "ms]" +
							" query " + objectToString(queryProcessor.getBaseExpr() +
							" for " + queryProcessor.getRequestedType()));
			}
		}
	}

	public class QueryingWithQueryBuilderDelegate extends DelegateDataAccessBackend.QueryingWithQueryBuilderDelegate {
		protected QueryingWithQueryBuilderDelegate(QueryingWithQueryBuilder delegate) {
			super(delegate);
		}
		
		public QueryBuilder getQueryBuilder() {
			return new QueryBuilderDelegate(super.getQueryBuilder());
		}
		
		public Object executeQuery(QueryProcessor queryProcessor, Object query) {
			long time = System.currentTimeMillis();
			try {
				return super.executeQuery(queryProcessor, query);
			} finally {
				if (log.isInfoEnabled())
					log.info("[" + (System.currentTimeMillis() - time) + "ms]" +
							" execute query " + objectToString(query) +
							" for " + queryProcessor.getRequestedType());
			}
		}
	}

	public class QueryBuilderDelegate extends DelegateDataAccessBackend.QueryBuilderDelegate {
		private long time = System.currentTimeMillis();
		
		public QueryBuilderDelegate(QueryBuilder delegate) {
			super(delegate);
		}
		
		public Object getQuery() {
			Object query = super.getQuery();
			if (log.isInfoEnabled())
				log.info("[" + (System.currentTimeMillis() - time) + "ms]" +
						" create query " + objectToString(query) +
						" from " + objectToString(getQueryProcessor().getBaseExpr()));
			time = System.currentTimeMillis();
			return query;
		}
	}

	public class StoringAttributeSetsDelegate extends DelegateDataAccessBackend.StoringAttributeSetsDelegate {
		protected StoringAttributeSetsDelegate(StoringAttributeSets delegate) {
			super(delegate);
		}
		
		public void store(StoreProcessor storeProcessor, AttributeSet attributeSet) {
			long time = System.currentTimeMillis();
			try {
				super.store(storeProcessor, attributeSet);
			} finally {
				if (log.isInfoEnabled())
					log.info("[" + (System.currentTimeMillis() - time) + "ms]" +
							" store " + objectToString(attributeSet));
			}
		}

		public void delete(StoreProcessor storeProcessor, AttributeSet attributeSet) {
			long time = System.currentTimeMillis();
			try {
				super.delete(storeProcessor, attributeSet);
			} finally {
				if (log.isInfoEnabled())
					log.info("[" + (System.currentTimeMillis() - time) + "ms]" +
							" delete " + objectToString(attributeSet));
			}
		}
	}

	public class StoringObjectTreesDelegate extends DelegateDataAccessBackend.StoringObjectTreesDelegate {
		protected StoringObjectTreesDelegate(StoringObjectTrees delegate) {
			super(delegate);
		}
		
		public void store(StoreProcessor storeProcessor, Object object) {
			long time = System.currentTimeMillis();
			try {
				super.store(storeProcessor, object);
			} finally {
				if (log.isInfoEnabled())
					log.info("[" + (System.currentTimeMillis() - time) + "ms]" +
							" store object " + objectToString(object));
			}
		}

		public void store(StoreProcessor storeProcessor, Object object, int depth) {
			long time = System.currentTimeMillis();
			try {
				super.store(storeProcessor, object, depth);
			} finally {
				if (log.isInfoEnabled())
					log.info("[" + (System.currentTimeMillis() - time) + "ms]" +
							" store object " + objectToString(object) + " with depth " + depth);
			}
		}

		public void delete(StoreProcessor storeProcessor, Object object) {
			long time = System.currentTimeMillis();
			try {
				super.delete(storeProcessor, object);
			} finally {
				if (log.isInfoEnabled())
					log.info("[" + (System.currentTimeMillis() - time) + "ms]" +
							" delete object " + objectToString(object));
			}
		}
	}

	private static String objectToString(Object object) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("[");
		objectToString(stringBuffer, object);
		stringBuffer.append("]");
		return stringBuffer.toString();
	}

	private static void objectToString(StringBuffer stringBuffer, Object object) {
		if (object instanceof Object[]) {
			objectToString(stringBuffer, Arrays.asList((Object[]) object));
		} else if (object instanceof Collection) {
			stringBuffer.append("[");
			for (Iterator it = ((Collection)object).iterator(); it.hasNext(); ) {
				objectToString(stringBuffer, it.next());
				if (it.hasNext())
					stringBuffer.append(", ");
			}
			stringBuffer.append("]");
		} else {
			stringBuffer.append(object);
		}
	}
}
