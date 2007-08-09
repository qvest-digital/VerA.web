/*
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.tarent.commons.dataaccess.DataAccessException;
import de.tarent.commons.dataaccess.QueryProcessor;
import de.tarent.commons.dataaccess.StoreProcessor;
import de.tarent.commons.dataaccess.backend.AbstractDataAccessBackend;
import de.tarent.commons.dataaccess.backend.QueryingStrategy;
import de.tarent.commons.dataaccess.backend.QueryingWithQueryBuilder;
import de.tarent.commons.dataaccess.backend.StoringAttributeSets;
import de.tarent.commons.dataaccess.backend.StoringStrategy;
import de.tarent.commons.dataaccess.config.ConfigFactory;
import de.tarent.commons.dataaccess.config.MappingRules;
import de.tarent.commons.dataaccess.data.AttributeSet;
import de.tarent.commons.dataaccess.data.AttributeSetImpl;
import de.tarent.commons.dataaccess.data.AttributeSetList;
import de.tarent.commons.dataaccess.data.AttributeSetListImpl;
import de.tarent.commons.dataaccess.query.QueryBuilder;
import de.tarent.commons.dataaccess.query.impl.BrokerQueryBuilder;
import de.tarent.octopus.client.OctopusConnection;
import de.tarent.octopus.client.OctopusConnectionFactory;
import de.tarent.octopus.client.OctopusResult;
import de.tarent.octopus.client.OctopusTask;

public class BrokerDataAccessBackend extends AbstractDataAccessBackend implements QueryingWithQueryBuilder, StoringAttributeSets {
	private InMemoryTransactionAdapter inMemoryTransactionAdapter;

	private OctopusConnection octopusConnection;

	private String type;

	private String serviceURL;

	private String username;

	private String password;

	private String module;
	
	public BrokerDataAccessBackend() {
		init();
	}

	public void init() {
		inMemoryTransactionAdapter = new InMemoryTransactionAdapter(this);
		Map configMap = new HashMap();
		configMap.put("type", type);
		configMap.put("serviceURL", serviceURL);
		configMap.put("username", username);
		configMap.put("password", password);
		configMap.put("module", module);
		OctopusConnectionFactory.getInstance().setConfiguration("broker", configMap);
		octopusConnection = OctopusConnectionFactory.getInstance().getConnection("broker");
	}

	/** {@inheritDoc} */
	public QueryingStrategy getQueryingStrategy() {
		return this;
	}

	/** {@inheritDoc} */
	public StoringStrategy getStoringStrategy() {
		return this;
	}

	public QueryBuilder getQueryBuilder() {
		return new BrokerQueryBuilder();
	}

	/**
	 * @param view - Brokername
	 * @param query - Brokerquery
	 * @return AttributeSetList
	 */
	public Object executeQuery(QueryProcessor queryProcessor, Object query) {
		if (query == BrokerQueryBuilder.EMPTY_RESULT)
			return new AttributeSetListImpl();
		else if (!(query instanceof List))
			throw new IllegalArgumentException("Query was not of Class AttributeSet!");
		
		AttributeSetList result = new AttributeSetListImpl();
		
		OctopusTask octopusTask = octopusConnection.getTask("getData")
			.add("view", getBrokerView(queryProcessor))
			.add("filter", transformParsedQueryToBrokerFilter(queryProcessor, (List)query))
			.add("count", new Integer(-1));
		OctopusResult octopusResult;
		try {
			octopusResult = octopusTask.invoke();
		} catch (RuntimeException e) {
			throw new DataAccessException(e);
		}
		
		// OctopusResult ist eine Liste von Maps
		List responseEntry = (List) octopusResult.getData("list");
		Iterator responseEntryIter = responseEntry.iterator();
		while (responseEntryIter.hasNext()) {
			Map responseMapEntry = (Map) responseEntryIter.next();
			AttributeSet resultSet = new AttributeSetImpl();
			Iterator responseMapEntryIter = responseMapEntry.entrySet().iterator();
			while (responseMapEntryIter.hasNext()) {
				Map.Entry responseMapEntryItem = (Map.Entry)responseMapEntryIter.next();
				resultSet.setAttribute((String) responseMapEntryItem.getKey(), responseMapEntryItem.getValue());
			}
			result.add(resultSet);
		}
		return result;
	}

	public void store(StoreProcessor storeProcessor, AttributeSet attributeSet) {
		if (getPrimaryKeyFromAttributeSet(storeProcessor, attributeSet) == null) {
			OctopusTask octopusTask = octopusConnection.getTask("insertData")
				.add("view", getBrokerView(storeProcessor))
				.add("value", transformAttributeSetToBrokerValues(storeProcessor, attributeSet))
				.add("result", "map");
			OctopusResult octopusResult;
			try {
				octopusResult = octopusTask.invoke();
			} catch (RuntimeException e) {
				throw new DataAccessException(e);
			}
			
			// Datenbank-generierte ID in das Objekt schreiben.
			Map resultMap = (Map) ((List) octopusResult.getData("result")).get(0);
			if (resultMap != null) {
				for (Iterator it = resultMap.entrySet().iterator(); it.hasNext(); ) {
					Map.Entry entry = (Map.Entry) it.next();
					attributeSet.setAttribute(
							transformBrokerColumnToAttributeName(storeProcessor, (String) entry.getKey()),
							entry.getValue());
				}
			}
		} else {
			OctopusTask octopusTask = octopusConnection.getTask("updateData")
				.add("view", getBrokerView(storeProcessor))
				.add("value", transformAttributeSetToBrokerValues(storeProcessor, attributeSet))
				.add("filter", getPrimaryKey(storeProcessor) + "=" + getPrimaryKeyFromAttributeSet(storeProcessor, attributeSet));
			try {
				octopusTask.invoke();
			} catch (RuntimeException e) {
				throw new DataAccessException(e);
			}
		}
	}

	public void delete(StoreProcessor storeProcessor, AttributeSet attributeSet) {
		OctopusTask octopusTask = octopusConnection.getTask("deleteData")
			.add("view", getBrokerView(storeProcessor))
			.add("filter", transformAttributeSetToBrokerValues(storeProcessor, attributeSet));
		try {
			octopusTask.invoke();
		} catch (RuntimeException e) {
			throw new DataAccessException(e);
		}
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

	private String getPrimaryKeyFromAttributeSet(StoreProcessor storeProcessor, AttributeSet attributeSet) {
		return (String) attributeSet.getAttribute(getPrimaryKey(storeProcessor));
	}

	private List transformAttributeSetToBrokerValues(StoreProcessor storeProcessor, AttributeSet attributeSet) {
		List brokerValues = new Vector();
		for (Iterator it = attributeSet.getAttributeNames().iterator(); it.hasNext();) {
			String column = (String) it.next();
			if (column.equals(getPrimaryKey(storeProcessor)))
				continue;
			brokerValues.add(column + "=" + attributeSet.getAttribute(column));
		}
		return brokerValues;
	}

	private List transformParsedQueryToBrokerFilter(QueryProcessor queryProcessor, List parsedQueryList) {
		MappingRules mappingRules = ConfigFactory.getMappingConfig().getRules(
				queryProcessor.getBackendName(),
				queryProcessor.getRequestedType().getName());
		
		List brokerFilter = new Vector();
		for (Iterator it = parsedQueryList.iterator(); it.hasNext();) {
			String query[] = (String[]) it.next();
			String column = mappingRules.transformResultToBackend(query[0]);
			if (column == null)
				throw new IllegalArgumentException("Mapping rules removed query for attribute '" + query[0] + "'.");
			brokerFilter.add(column + query[1] + query[2]);
		}
		return brokerFilter;
	}	

	private String transformBrokerColumnToAttributeName(StoreProcessor storeProcessor, String brokerColumn) {
		MappingRules mappingRules = ConfigFactory.getMappingConfig().getRules(
				storeProcessor.getBackendName(),
				storeProcessor.getAffectedType().getName());
		
		if (mappingRules == null)
			return brokerColumn;
		return mappingRules.transformResultToBackend(brokerColumn);
	}

	private String getBrokerView(QueryProcessor queryProcessor) {
		Map parameters = ConfigFactory.getMappingConfig().getParameters(
				queryProcessor.getBackendName(),
				queryProcessor.getRequestedType().getName());
		if (parameters == null)
			throw new IllegalArgumentException(
					"No parameters configured for the backend " +
					queryProcessor.getBackendName() +
					" and the class name " +
					queryProcessor.getRequestedType().getName() +
					". Need 'brokerview'.");
		String brokerview = (String) parameters.get("brokerview");
		if (brokerview == null || brokerview.length() == 0)
			throw new IllegalArgumentException(
					"No parameter 'brokerview' for the backend " +
					queryProcessor.getBackendName() + 
					" and the class name " +
					queryProcessor.getRequestedType().getName() +
					" found.");
		return brokerview;
	}

	private String getBrokerView(StoreProcessor storeProcessor) {
		Map parameters = ConfigFactory.getMappingConfig().getParameters(
				storeProcessor.getBackendName(),
				storeProcessor.getAffectedType().getName());
		if (parameters == null)
			throw new IllegalArgumentException(
					"No parameters configured for the backend " +
					storeProcessor.getBackendName() +
					" and the class name " +
					storeProcessor.getAffectedType().getName() +
					". Need 'brokerview'.");
		String brokerview = (String) parameters.get("brokerview");
		if (brokerview == null || brokerview.length() == 0)
			throw new IllegalArgumentException(
					"No parameter 'brokerview' for the backend " +
					storeProcessor.getBackendName() + 
					" and the class name " +
					storeProcessor.getAffectedType().getName() +
					" found.");
		return brokerview;
	}

	private String getPrimaryKey(StoreProcessor storeProcessor) {
		Map parameters = ConfigFactory.getMappingConfig().getParameters(
				storeProcessor.getBackendName(),
				storeProcessor.getAffectedType().getName());
		if (parameters == null)
			throw new IllegalArgumentException(
					"No parameters configured for the backend " +
					storeProcessor.getBackendName() +
					" and the class name " +
					storeProcessor.getAffectedType().getName() +
					". Need primary 'key'.");
		String key = (String) parameters.get("key");
		if (key == null || key.length() == 0)
			throw new IllegalArgumentException(
					"No primary 'key' for the backend " +
					storeProcessor.getBackendName() + 
					" and the class name " +
					storeProcessor.getAffectedType().getName() +
					" found.");
		return key;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}
}
