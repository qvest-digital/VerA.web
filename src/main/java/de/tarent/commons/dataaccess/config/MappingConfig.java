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

package de.tarent.commons.dataaccess.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MappingConfig implements ConfigFactory.Config, Cloneable {
	/**
	 * Contains a mapping from backend nane to an configuration map
	 * which contains one or more mapping rules to an class pattern.
	 */
	private final Map mappingRules;
	
	private final Map parameters;

	/**
	 * Contains a mapping cache for the mapping rules where all rules
	 * for one class (full qualified class name instead of an pattern)
	 * are combined and directly accessable.
	 */
	private final Map mappingRulesCache;
	
	public MappingConfig() {
		this.mappingRules = new LinkedHashMap();
		this.mappingRulesCache = new HashMap();
		this.parameters = new HashMap();
	}

	public MappingConfig(Map mappingConfigurations) {
		this.mappingRules = new LinkedHashMap();
		this.mappingRules.putAll(mappingConfigurations);
		this.mappingRulesCache = new HashMap();
		this.parameters = new HashMap();
	}
	
	public MappingConfig(Map mappingConfigurations, Map parameters) {
		this.mappingRules = new LinkedHashMap();
		this.mappingRules.putAll(mappingConfigurations);
		this.mappingRulesCache = new HashMap();
		this.parameters = new HashMap();
		this.parameters.putAll(parameters);
	}

	public void addAll(MappingConfig mappingConfig) {
		this.mappingRules.putAll(mappingConfig.getInternalMap());
		this.parameters.putAll(mappingConfig.getParameters());
//		
//		for (Iterator it = mappingConfig.getInternalMap().entrySet().iterator(); it.hasNext(); ) {
//			Map.Entry entry = (Map.Entry) it.next();
//			String backendName = (String) entry.getKey();
//			Map classNamePatternToMappingRulesList = (Map) entry.getValue();
//
//			for (Iterator it2 = classNamePatternToMappingRulesList.entrySet().iterator(); it2.hasNext(); ) {
//				Map.Entry entry2 = (Map.Entry) it2.next();
//				String classNamePattern = (String) entry2.getKey();
//				List mappingRulesList = (List) entry2.getValue();
//				
//				this.mappingRules.put(classNamePattern, mappingRulesList);
//
//				for (Iterator it3 = mappingRulesList.iterator(); it3.hasNext();) {
//					MappingRules rules = (MappingRules) it3.next();	
//					addMappingRules(backendName, classNamePattern, rules);
//				}
//			}
//		}
	}

	public void addMappingRules(String backendName, String classNamePattern, MappingRules rules) {
		Map classNamePatternToMappingRulesList = (Map) mappingRules.get(backendName);
		if (classNamePatternToMappingRulesList == null) {
			classNamePatternToMappingRulesList = new LinkedHashMap();
			mappingRules.put(backendName, classNamePatternToMappingRulesList);
		}
		
		List mappingRulesList = (List) classNamePatternToMappingRulesList.get(classNamePattern);
		if (mappingRulesList == null) {
			mappingRulesList = new LinkedList();
			classNamePatternToMappingRulesList.put(classNamePattern, mappingRulesList);
		}
		mappingRulesList.add(rules);
		
		mappingRulesCache.clear();
	}

	public MappingRules getRules(String backendName, String className) {
		MappingRules rules = (MappingRules) mappingRulesCache.get(backendName + "$" + className);
		if (rules != null)
			return rules;
		
		rules = new MappingRules();
		Map classNamePatternToMappingRulesList = (Map) mappingRules.get(backendName);
		if (classNamePatternToMappingRulesList == null)
			return rules;
		
		for (Iterator it = classNamePatternToMappingRulesList.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			
			if (className.matches((String) entry.getKey())) {
				for (Iterator it2 = ((List) entry.getValue()).iterator(); it2.hasNext(); ) {
					rules.addRules((MappingRules) it2.next());
				}
			}
		}
		return rules;
	}

	public void setParameters(String backendName, String className, Map parameterMap) {
		Map classNameToParameterMap = (Map) parameters.get(backendName);
		if (classNameToParameterMap == null) {
			classNameToParameterMap = new HashMap();
		}
		classNameToParameterMap.put(className, parameterMap);
		parameters.put(backendName, classNameToParameterMap);
	}

	public Map getParameters(String backendName, String className) {
		Map classNameToParameterMap = (Map) parameters.get(backendName);
		if (classNameToParameterMap == null)
			return null;
		return (Map) classNameToParameterMap.get(className);
	}

	public Map getParameters() {
		return parameters;
	}

	Map getInternalMap() {
		return mappingRules;
	}

	public int size() {
		int size = 0;
		for (Iterator it = mappingRules.entrySet().iterator(); it.hasNext(); ) {
			size += ((Map) ((Map.Entry) it.next()).getValue()).size();
		}
		return size;
	}

	public void clear() {
		mappingRules.clear();
		mappingRulesCache.clear();
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
