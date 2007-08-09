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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.tarent.commons.dataaccess.backend.DataAccessBackend;
import de.tarent.commons.dataaccess.backend.QueryingStrategy;
import de.tarent.commons.dataaccess.backend.QueryingWithQueryBuilder;
import de.tarent.commons.dataaccess.backend.QueryingWithOwnFilter;
import de.tarent.commons.dataaccess.backend.impl.MemoryDataAccessBackend;
import de.tarent.commons.dataaccess.config.ConfigFactory;
import de.tarent.commons.dataaccess.config.MappingRules;
import de.tarent.commons.dataaccess.data.AttributeSet;
import de.tarent.commons.dataaccess.data.AttributeSetList;
import de.tarent.commons.dataaccess.data.AttributeSetListImpl;
import de.tarent.commons.dataaccess.query.QueryBuilder;
import de.tarent.commons.dataaccess.query.QueryFilter;
import de.tarent.commons.dataaccess.query.QueryParser;
import de.tarent.commons.dataaccess.query.QueryVisitor;
import de.tarent.commons.dataaccess.query.QueryVisitorListener;
import de.tarent.commons.dataaccess.query.impl.LdapQueryParser;
import de.tarent.commons.dataaccess.query.impl.LuceneQueryParser;
import de.tarent.commons.dataaccess.query.impl.ManagmentQueryParser;
import de.tarent.commons.dataaccess.query.impl.NullQueryParser;
import de.tarent.commons.dataaccess.query.impl.TraversingVisitor;
import de.tarent.commons.datahandling.entity.AttributeSource;
import de.tarent.commons.datahandling.entity.EntityFactory;
import de.tarent.commons.datahandling.entity.EntityFactoryRegistry;
import de.tarent.commons.datahandling.entity.LookupContext;
import de.tarent.commons.datahandling.entity.LookupContextImpl;
import de.tarent.commons.utils.Pojo;

/**
 * <p>These {@link QueryProcessor} class handle the most "magic" arround the
 * data loading or querying. It supports different
 * {@link QueryingStrategy querying strategies}
 * and different {@link #responseMode response modes}.</p>
 * 
 * <p>As querying strategy it supports two mechanism:</p>
 * 
 * <p><strong>First the {@link QueryingWithQueryBuilder builders}:</strong><br/>
 * This strategy is for all backends which can really query an data storage with
 * an filter. For example RDBMS (with SQL where clausel), (some) OOMS (with
 * example beans or attribute sets), directories (like LDAP with there own query
 * syntax), or something else.<br>
 * 
 * In this way the processor will transform any query in the needed format with
 * an appropriable {@link QueryBuilder query builder}. So you can query your
 * data with an lucene query not only from an lucene backend, but also from an
 * LDAP or database backend.
 * </p>
 * 
 * <p><strong>Second the {@link QueryingWithOwnFilter filters}:</strong><br/>
 * This strategy is for all backend which must filter manually the data. For
 * example OOMS (with object navigation instead of a query API) or our
 * {@link MemoryDataAccessBackend in memory backend}.<br>
 * 
 * In this way the processor will transform any query with an
 * {@link QueryVisitor visitor}, some additional
 * {@link QueryVisitorListener visitor events} and the
 * {@link QueryFilter filter interface}.
 * </p>
 * 
 * @author Christoph Jerolimov, tarent GmbH
 * @author Timo Pick, tarent GmbH
 */
public class QueryProcessor extends ObjectProcessor {
	/**
	 * Parameter for the {@link #responseMode} which request a list of beans
	 * (objects)
	 */
	public static final Class RESPONSE_MODE_BEAN_LIST = Object.class;

	/**
	 * Parameter for the {@link #responseMode} which request an
	 * {@link AttributeSetList} which contains {@link AttributeSet}s.
	 */
	public static final Class RESPONSE_MODE_ATTRIBUTE_SET_LIST = AttributeSetList.class;

	/**
	 * Internal class of <code>org.apache.lucene.search.Query</code>. Only if
	 * lucene is in the class path we will support also support lucene as an
	 * query dialect.
	 */
	private static final Class LUCENE_QUERY = getQueryClassOrNull("org.apache.lucene.search.Query");

	/**
	 * Internal class of <code>javax.managment.QueryEval</code>.
	 */
	private static final Class MANAGEMENT_QUERY = getQueryClassOrNull("javax.management.QueryEval");

	/**
	 * The configured backend name for lazy loading the mapping configuration.
	 */
	private final String backendName;

	/**
	 * The {@link QueryingStrategy} instance of the {@link DataAccessBackend},
	 * normally it is the same class and instance.
	 */
	private final QueryingStrategy queryingStrategy;

	/**
	 * Specify the response classes which this instance must return. Actually
	 * available is an list of beans ({@link #RESPONSE_MODE_BEAN_LIST}),
	 * a list of attribute sets ({@link #RESPONSE_MODE_ATTRIBUTE_SET_LIST} or
	 * an {@link #RESPONSE_MODE_RESULT_SET}.
	 */
	private final Class responseMode;

	/**
	 * Requested class type which specified the data convertion configuration.
	 * If the {@link #responseMode} is an {@link #RESPONSE_MODE_BEAN_LIST} this
	 * is also the type of the beans.
	 */
	private final Class requestedType;

	/** Expression */
	private final Object baseExpr;

	/** Order (List of Strings or String[]  ??? ) */
	private final Object baseOrder;

	/** First entry inclusive, starting with zero. */
	private final Integer firstEntryIndex;

	/** Last entry inclusive, ending with size minus one. */
	private final Integer lastEntryIndex;

	/**
	 * Create an new {@link QueryProcessor} instance with all necessary 
	 * information for create an query strategy.
	 * 
	 * @see You can directly call #execute() after you have an instance of this
	 * class.
	 * 
	 * @param backendName This is required for lazy loading the mapping
	 * configuration.
	 * @param dataAccessBackend The backend which will return the querying
	 * strategy with the method {@link DataAccessBackend#getQueryingStrategy()}.
	 * @param responseMode The expected response mode which must be one of the
	 * constants {@link #RESPONSE_MODE_BEAN_LIST} or
	 * {@link #RESPONSE_MODE_ATTRIBUTE_SET_LIST}.
	 * @param requestedType Requested class type. If this parameter is null the
	 * result list will not filtered for any type. Notice that many backends
	 * require an requested type for decide from where they must load the beans.
	 * @param baseExpr Filter expression. If this parameter is null the result
	 * list will not filtered for any attributes or one any other way.
	 * @param baseOrder Order information, could be null.
	 * @param firstEntryIndex Index (0-based) of the first entry which will be
	 * returned. If this parameter is null this filter will be deactivted. 
	 * @param lastEntryIndex Index (0-based) of the last entry which will be
	 * returned. If this parameter is null this filter will be deactivted.
	 * 
	 * @throws NullPointerException If the {@link #responseMode} is null, the
	 * {@link DataAccessBackend} is null or the DataAccessBackend return no
	 * {@link QueryingStrategy}.
	 */
	public QueryProcessor(
			String backendName,
			DataAccessBackend dataAccessBackend,
			Class responseMode,
			Class requestedType,
			Object baseExpr,
			Object baseOrder,
			Integer firstEntryIndex,
			Integer lastEntryIndex) {
		
		if (dataAccessBackend == null)
			throw new NullPointerException("DataAccessBackend should not be null.");
		this.backendName = backendName;
		this.queryingStrategy = dataAccessBackend.getQueryingStrategy();
		if (this.queryingStrategy == null)
			throw new NullPointerException("QueryingStrategy should not be null.");
		
		this.responseMode = responseMode;
		if (this.responseMode == null)
			throw new NullPointerException("ResponseMode should not be null.");
		
		this.requestedType = requestedType;
		
		this.baseExpr = baseExpr;
		this.baseOrder = baseOrder;
		this.firstEntryIndex = firstEntryIndex;
		this.lastEntryIndex = lastEntryIndex;
	}

	/**
	 * <p>Finally (direct after creating an query processer instance) you can
	 * execute the query and will get your result in the format you
	 * {@link #responseMode requested} them.</p>
	 * 
	 * @return The query result.
	 * 
	 * @throws IllegalArgumentException If the {@link QueryingStrategy} or the
	 * {@link #responseMode} is not supported. See class description for
	 * supported strategies and response modes.
	 * @throws DataAccessException If an exception in the backend happend.
	 * @throws AssertionError If the {@link QueryingStrategy} return null or
	 * an illegal result format. This must not happend normally and show you an
	 * implemantation error in the {@link QueryingStrategy} or
	 * {@link DataAccessBackend} code.
	 */
	public Object execute() {
		
		// QUERY DATA
		
		Object result;
		if (queryingStrategy instanceof QueryingWithQueryBuilder) {
			result = executeQueryWithQueryBuilder((QueryingWithQueryBuilder) queryingStrategy);
		} else if (queryingStrategy instanceof QueryingWithOwnFilter) {
			result = executeQueryWithOwnFilter((QueryingWithOwnFilter) queryingStrategy);
		} else {
			throw new IllegalArgumentException(
					"Unsupported QueryingStrategy: " +
					queryingStrategy.getClass().getName());
		}
		
		if (result == null)
			throw new AssertionError(
					"Implementation error. Result from QueryingStrategy (" +
					queryingStrategy.getClass().getName() +
					") should not be null.");
		
		// TRANSFORM DATA
		
		if (RESPONSE_MODE_BEAN_LIST.equals(responseMode)) {
			if (result instanceof AttributeSetList) {
				return transformAttributeSetListToBeanList((AttributeSetList) result);
			} else if (result instanceof List) {
				return result;
			} else {
				throw new AssertionError(
						"Implementation error. Should not transform " +
						result.getClass().getName() + " to a bean list.");
			}
		} else if (RESPONSE_MODE_ATTRIBUTE_SET_LIST.equals(responseMode)) {
			if (result instanceof AttributeSetList)
				return result;
			else if (result instanceof List) {
				return transformBeanListToAttributeSetList((List) result);
			} else {
				throw new AssertionError(
						"Implementation error. Should not transform " +
						result.getClass().getName() + " to an attribute set.");
			}
		} else {
			throw new IllegalArgumentException("Illegal response mode: " + responseMode.getClass().getName());
		}
	}

	protected Object executeQueryWithQueryBuilder(QueryingWithQueryBuilder queryingWidthQueryBuilder) {
		// Create the parser and visitor for this query startegy.
		QueryParser queryParser = createQueryParser();
		QueryVisitor queryVisitor = createQueryVisitor();
		
		// Load QueryBuilder from the strategy instance.
		QueryBuilder queryBuilder = queryingWidthQueryBuilder.getQueryBuilder();
		
		// Bind query instances.
		bind(this, queryParser, queryVisitor, queryBuilder);
		
		// Set the additional parameter for order and limits.
		queryingWidthQueryBuilder.setOrder(getBaseOrder());
		queryingWidthQueryBuilder.setFirstEntryIndex(getFirstEntryIndex());
		queryingWidthQueryBuilder.setLastEntryIndex(getLastEntryIndex());
		
		// Parse and BUILD the query!
		queryParser.parse(getBaseExpr());
		
		// Execute the query!
		return queryingWidthQueryBuilder.executeQuery(this, queryBuilder.getQuery());
	}

	protected Object executeQueryWithOwnFilter(QueryingWithOwnFilter queryingWithOwnFilter) {
		// Create the parser and visitor for this query startegy.
		QueryParser queryParser = createQueryParser();
		QueryVisitor queryVisitor = createQueryVisitor();
		
		// Direct query the backend.
		return queryingWithOwnFilter.query(this, queryParser, queryVisitor);
	}

	protected List transformAttributeSetListToBeanList(AttributeSetList attributeSetList) {
		if (attributeSetList.isEmpty())
			return new ArrayList();
		
		List result = new ArrayList();
		
		// Could be null.
		MappingRules mappingRules = backendName == null ? null :
				ConfigFactory.getMappingConfig().getRules(
				backendName, requestedType.getName());
		Map parameters = backendName == null ? null :
				ConfigFactory.getMappingConfig().getParameters(
				backendName, requestedType.getName());
		String key = parameters == null ? null :
				(String) parameters.get("key");
		
		EntityFactory entityFactory = EntityFactoryRegistry.getEntityFactory(
				requestedType, key);
		LookupContext lookupContext = new LookupContextImpl();
		
		final Map attributeMapping = getAttributeMapping(attributeSetList.get(0), mappingRules);
		final List attributeNames = new LinkedList(attributeMapping.keySet());
		
		for (Iterator it = attributeSetList.iterator(); it.hasNext(); ) {
			final AttributeSet attributeSet = (AttributeSet) it.next();
			
			AttributeSource attributeSource = new AttributeSource() {
				public Object getAttribute(String attributeName) {
					attributeName = (String) attributeMapping.get(attributeName);
					return attributeSet.getAttribute(attributeName);
				}
				
				public Class getAttributeType(String attributeName) {
					return Pojo.getSetMethod(requestedType, attributeName, false).getParameterTypes()[0];
				}
				
				public List getAttributeNames() {
					return attributeNames;
				}
			};
			
			try {
				Object newInstance = requestedType.newInstance();
				entityFactory.fillEntity(newInstance, attributeSource, lookupContext);
				result.add(newInstance);
			} catch (InstantiationException e) {
				throw new DataAccessException(e);
			} catch (IllegalAccessException e) {
				throw new DataAccessException(e);
			}
		}
		
		return result;
	}

	private Map getAttributeMapping(AttributeSet attributeSet, MappingRules mappingRules) {
		Map attributeMapping = new LinkedHashMap();
		if (mappingRules == null) {
			for (Iterator it = attributeSet.getAttributeNames().iterator(); it.hasNext(); ) {
				String attribute = (String) it.next();
				attributeMapping.put(attribute, attribute);
			}
		} else {
			for (Iterator it = attributeSet.getAttributeNames().iterator(); it.hasNext(); ) {
				String attributeBackend = (String) it.next();
				String attributeResult = mappingRules.transformBackendToResult(attributeBackend);
				if (attributeResult != null)
					attributeMapping.put(attributeResult, attributeBackend);
			}
		}
		return attributeMapping;
	}

	protected AttributeSetList transformBeanListToAttributeSetList(List beanList) {
		AttributeSetList result = new AttributeSetListImpl();
		Object parameters[] = new Object[] { result };
		
		for (Iterator it = beanList.iterator(); it.hasNext(); ) {
			seperateBeanArgumentsToAttributeSets(it.next(), parameters);
		}
		
		return result;
	}

	protected void handleSeperatedBeanAttributeSets(Object bean, AttributeSet attributeSet, Object[] parameters) {
		((AttributeSetList) parameters[0]).add(attributeSet);
	}

	protected QueryParser createQueryParser() {
		if (baseExpr == null)
			return new NullQueryParser();
		else if (baseExpr instanceof Q.LuceneQuery)
			return new LuceneQueryParser();
		else if (baseExpr instanceof Q.LdapQuery)
			return new LdapQueryParser();
		else if (LUCENE_QUERY != null && LUCENE_QUERY.isInstance(baseExpr))
			return new LuceneQueryParser();
		else if (MANAGEMENT_QUERY != null && MANAGEMENT_QUERY.isInstance(baseExpr))
			return new ManagmentQueryParser();
		else
			throw new DataAccessException("Unknown query type " + baseExpr.getClass().getName() + " (" + baseExpr + ")");
	}

	protected QueryVisitor createQueryVisitor() {
		return new TraversingVisitor();
	}

	protected QueryFilter createQueryFilter() {
		return null;
	}

	public Class getRequestedType() {
		return requestedType;
	}

	public Object getBaseExpr() {
		return baseExpr;
	}

	public Object getBaseOrder() {
		return baseOrder;
	}

	public Integer getFirstEntryIndex() {
		return firstEntryIndex;
	}

	public Integer getLastEntryIndex() {
		return lastEntryIndex;
	}

	public String getBackendName() {
		return backendName;
	}
	
	public static void bind(
			QueryProcessor queryProcessor,
			QueryParser queryParser,
			QueryVisitor queryVisitor,
			QueryFilter queryFilter) {
		
		if (queryProcessor != null) {
			queryProcessor.setQueryProcessor(queryProcessor);
			queryProcessor.setQueryParser(queryParser);
			queryProcessor.setQueryVisitor(queryVisitor);
			queryProcessor.setQueryFilter(queryFilter);
		}
		
		if (queryParser != null) {
			queryParser.setQueryProcessor(queryProcessor);
			queryParser.setQueryParser(queryParser);
			queryParser.setQueryVisitor(queryVisitor);
			queryParser.setQueryFilter(queryFilter);
		}
		
		if (queryVisitor != null) {
			queryVisitor.setQueryProcessor(queryProcessor);
			queryVisitor.setQueryParser(queryParser);
			queryVisitor.setQueryVisitor(queryVisitor);
			queryVisitor.setQueryFilter(queryFilter);
		}
		
		if (queryFilter != null) {
			queryFilter.setQueryProcessor(queryProcessor);
			queryFilter.setQueryParser(queryParser);
			queryFilter.setQueryVisitor(queryVisitor);
			queryFilter.setQueryFilter(queryFilter);
		}
	}

	/**
	 * Return a {@link Class} instance for the given <code>className</code> or
	 * null if any exeption is happend.
	 * 
	 * @param className Class name for loading.
	 * @return Class instance or null.
	 */
	private static Class getQueryClassOrNull(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		} catch (RuntimeException t) {
			return null;
		}
	}
}
