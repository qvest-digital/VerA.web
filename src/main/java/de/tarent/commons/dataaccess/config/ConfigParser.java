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

import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.tarent.commons.dataaccess.DataAccessException;
import de.tarent.commons.dataaccess.backend.DataAccessBackend;

/**
 * <strong>Notice: This class is not thread safe.</strong>
 * 
 * @author Christoph Jerolimov, tarent GmbH
 */
public class ConfigParser {
	private XStream xstream;

	private BackendConfig backendConfig = null;
	private MappingConfig mappingConfig = null;

	private boolean parseBackend = false;
	private boolean parseMapping = false;

	public ConfigParser() {
		xstream = new XStream();
		
		xstream.alias("config", BackendConfig.class);
		xstream.alias("config", MappingConfig.class);
		
		xstream.alias("rule", MappingRules.class);
		xstream.alias("rename", MappingRules.Rename.class);
		xstream.alias("ignore", MappingRules.Ignore.class);	

		xstream.useAttributeFor(MappingRules.Rule.class, "result");
		xstream.useAttributeFor(MappingRules.Rule.class, "backend");
		xstream.useAttributeFor(MappingRules.Rule.class, "direction");
		xstream.useAttributeFor(MappingRules.Rule.class, "drop");

		xstream.registerConverter(new Converter() {
			public boolean canConvert(Class clazz) {
				return ConfigFactory.Config[].class.isAssignableFrom(clazz) ||
						BackendConfig.class.isAssignableFrom(clazz) ||
						MappingConfig.class.isAssignableFrom(clazz);
			}
			
			public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
				ConfigParser.this.marshal(value, writer, context);
			}
			
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				return ConfigParser.this.unmarshal(reader, context);
			}
		});
		
		xstream.registerConverter(new MappingRulesConverter());
	}

	public void setUp(boolean parseBackend, boolean parseMapping) {
		if (backendConfig != null || mappingConfig != null) {
			tearDown();
			throw new IllegalStateException(getClass().getSimpleName() + " already in use.");
		}
		tearDown();
		if (parseBackend) {
			this.backendConfig = ConfigFactory.getBackendConfig();
			this.parseBackend = true;
		}
		if (parseMapping) {
			this.mappingConfig = ConfigFactory.getMappingConfig();
			this.parseMapping = true;
		}
	}

	public void tearDown() {
		if (backendConfig != null)
			backendConfig = null;
		if (mappingConfig != null)
			mappingConfig = null;
		this.parseBackend = false;
		this.parseMapping = false;
	}

	private void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		if (value == null) {
			throw new NullPointerException("Value should not be null.");
		} else if (value instanceof ConfigFactory.Config[]) {
			ConfigFactory.Config[] configs = (ConfigFactory.Config[]) value;
			for (int i = 0; i < configs.length; i++) {
				marshal(configs[i], writer, context);
			}
		} else if (value instanceof BackendConfig) {
			marshalBackend(value, writer, context, (BackendConfig) value);
		} else if (value instanceof MappingConfig) {
			marshalMapping(value, writer, context, (MappingConfig) value);
		} else {
			throw new IllegalArgumentException("Illegal value type: " + value.getClass().getName());
		}
	}

	private void marshalBackend(Object value, HierarchicalStreamWriter writer, MarshallingContext context, BackendConfig backendConfig) {
		for (Iterator it = backendConfig.getInternalMap().entrySet().iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			
			if (!(entry.getValue() instanceof DataAccessBackend))
				continue;
			
			writer.startNode("dataaccess");
			writer.addAttribute("name", entry.getKey().toString());
			writer.startNode("class");
			writer.setValue(entry.getValue().getClass().getName());
			writer.endNode();
			writer.startNode("parameters");
			context.convertAnother(entry.getValue());
			writer.endNode();
			writer.endNode();
		}
	}

	private void marshalMapping(Object value, HierarchicalStreamWriter writer, MarshallingContext context, MappingConfig mappingConfig) {
		for (Iterator it = mappingConfig.getInternalMap().entrySet().iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			
			if (!(entry.getValue() instanceof Map))
				continue;
			
			String backendName = (String) entry.getKey();
			Map classNamePatternToMappingRulesList = (Map) entry.getValue();
			
			for (Iterator it2 = classNamePatternToMappingRulesList.entrySet().iterator(); it2.hasNext(); ) {
				
				Map.Entry entry2 = (Map.Entry) it2.next();
				String classNamePattern = (String) entry2.getKey();
				List mappingRulesList = (List) entry2.getValue();
				
				for (Iterator it3 = mappingRulesList.iterator(); it3.hasNext(); ) {
					
					MappingRules rules = (MappingRules) it3.next();
					
					writer.startNode("mapping");
					writer.addAttribute("for", backendName);
					writer.startNode("class");
					writer.setValue(classNamePattern);
					writer.endNode();
					writer.startNode("rules");
					context.convertAnother(rules.getRules());
					writer.endNode();
					writer.endNode();
					
				}
			}
		}
	}

	private Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		try {
			if (!reader.getNodeName().equals("config"))
				throw new IllegalArgumentException(
						"Unsupported tag name '" + reader.getNodeName() + "', " +
						"expected 'config'.");
			
			while (reader.hasMoreChildren()) {
				reader.moveDown();
				
				if (reader.getNodeName().equals("dataaccess")) {
					if (parseBackend)
						unmarshalBackend(reader, context);
				} else if (reader.getNodeName().equals("mapping")) {
					if (parseMapping)
						unmarshalMapping(reader, context);
				} else {
					throw new IllegalArgumentException(
							"Unsupported tag name '" + reader.getNodeName() + "', " +
							"expected 'dataaccess' or 'mapping'.");
				}
				
				reader.moveUp();
			}
			
			return null;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new DataAccessException(e);
		}
	}

	private void unmarshalBackend(HierarchicalStreamReader reader, UnmarshallingContext context) throws Exception {
		if (reader.getAttribute("name") == null || reader.getAttribute("name").length() == 0)
			throw new IllegalArgumentException(
					"Incomplete 'dataaccess' tag, " +
					"missing 'name' attribute.");
		String backendName = reader.getAttribute("name");
		
		reader.moveDown();
		if (!reader.getNodeName().equals("class"))
			throw new IllegalArgumentException(
					"Unsupported tag name '" + reader.getNodeName() + "', " +
					"expected 'class'.");
		if (reader.getValue() == null || reader.getValue().length() == 0)
			throw new IllegalArgumentException(
					"Incomplete 'class' tag, " +
					"missing content.");
		Class clazz = Class.forName(reader.getValue().trim());
		reader.moveUp();
		
		reader.moveDown();
		if (!reader.getNodeName().equals("parameters"))
			throw new IllegalArgumentException(
					"Unsupported tag name '" + reader.getNodeName() + "', " +
					"expected 'parameters'.");
		DataAccessBackend dataAccessBackend = (DataAccessBackend)context.convertAnother(reader, clazz);
		reader.moveUp();
		
		dataAccessBackend.init();
		
		backendConfig.putDataAccessBackend(backendName, dataAccessBackend);
	}
	
	private void unmarshalMapping(HierarchicalStreamReader reader, UnmarshallingContext context) throws Exception {
		if (reader.getAttribute("for") == null || reader.getAttribute("for").length() == 0)
			throw new IllegalArgumentException(
					"Incomplete 'mapping' tag, " +
					"missing 'for' attribute.");
		String backendName = reader.getAttribute("for");	
		reader.moveDown();
		if (!reader.getNodeName().equals("class"))
			throw new IllegalArgumentException(
					"Unsupported tag name '" + reader.getNodeName() + "', " +
					"expected 'class'.");
		if (reader.getValue() == null || reader.getValue().length() == 0)
			throw new IllegalArgumentException(
					"Incomplete 'class' tag, " +
					"missing content.");
		String classNamePattern = reader.getValue().trim();
		reader.moveUp();
		
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			
			if (reader.getNodeName().equals("parameters")) {
				unmarshalMappingParameters(reader, context, backendName, classNamePattern);
			} else if (reader.getNodeName().equals("rules")) {
				unmarshalMappingRules(reader, context, backendName, classNamePattern);
			} else { 
				throw new IllegalArgumentException(
						"Unsupported tag name '" + reader.getNodeName() + "', " +
						"expected 'parameters' or 'rules'.");
			}
			
			reader.moveUp();
		}
	}

	private void unmarshalMappingParameters(
			HierarchicalStreamReader reader,
			UnmarshallingContext context,
			String backendName,
			String classNamePattern) {
		
		Map parameterMap = new HashMap();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			parameterMap.put(reader.getNodeName().trim(), reader.getValue());
			reader.moveUp();
		}
		
		mappingConfig.setParameters(backendName, classNamePattern, parameterMap);
	}

	private void unmarshalMappingRules(
			HierarchicalStreamReader reader,
			UnmarshallingContext context,
			String backendName,
			String classNamePattern) {
		
		List ruleList = (List) context.convertAnother(reader, List.class);
		MappingRules rules = new MappingRules();
		for (int i = 0; i < ruleList.size(); i++) {
			rules.addRule((MappingRules.Rule) ruleList.get(i));
		}
		
		mappingConfig.addMappingRules(backendName, classNamePattern, rules);
	}

	public String format(BackendConfig backendConfig, MappingConfig mappingConfig) {
		return xstream.toXML(new ConfigFactory.Config[] {
				backendConfig,
				mappingConfig });
	}

	public void format(BackendConfig backendConfig, MappingConfig mappingConfig, Writer writer) {
		xstream.toXML(new ConfigFactory.Config[] {
				backendConfig,
				mappingConfig },
				writer);
	}

	public String formatBackends(BackendConfig backendConfig) {
		return xstream.toXML(backendConfig);
	}

	public void formatBackends(BackendConfig backendConfig, Writer writer) {
		xstream.toXML(backendConfig, writer);
	}

	public String formatMappings(MappingConfig mappingConfig) {
		return xstream.toXML(mappingConfig);
	}

	public void formatMappings(MappingConfig mappingConfig, Writer writer) {
		xstream.toXML(mappingConfig, writer);
	}

	public String formatBackend(String backendName, DataAccessBackend dataAccessBackend) {
		BackendConfig backendConfig = new BackendConfig();
		backendConfig.putDataAccessBackend(backendName, dataAccessBackend);
		return xstream.toXML(backendConfig);
	}

	public void formatBackend(String backendName, DataAccessBackend dataAccessBackend, Writer writer) {
		BackendConfig backendConfig = new BackendConfig();
		backendConfig.putDataAccessBackend(backendName, dataAccessBackend);
		xstream.toXML(backendConfig, writer);
	}

	public String formatMapping(String backendName, String classNamePattern, MappingRules rules, Map parameterMap) {
		MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addMappingRules(backendName, classNamePattern, rules);
		mappingConfig.setParameters(backendName, classNamePattern, parameterMap);
		return xstream.toXML(backendConfig);
	}

	public void formatMapping(String backendName, String classNamePattern, MappingRules rules, Map parameterMap, Writer writer) {
		MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addMappingRules(backendName, classNamePattern, rules);
		mappingConfig.setParameters(backendName, classNamePattern, parameterMap);
		xstream.toXML(backendConfig, writer);
	}

	public void parse(String xml) {
		xstream.fromXML(xml);
	}

	public void parse(Reader xml) {
		xstream.fromXML(xml);
	}

	public BackendConfig parseBackends(String xml) {
		try {
			setUp(true, false);
			xstream.fromXML(xml);
			return backendConfig;
		} finally {
			tearDown();
		}
	}

	public BackendConfig parseBackends(Reader xml) {
		try {
			setUp(true, false);
			xstream.fromXML(xml);
			return backendConfig;
		} finally {
			tearDown();
		}
	}

	public MappingConfig parseMappings(String xml) {
		try {
			setUp(false, true);
			xstream.fromXML(xml);
			return mappingConfig;
		} finally {
			tearDown();
		}
	}

	public MappingConfig parseMappings(Reader xml) {
		try {
			setUp(false, true);
			xstream.fromXML(xml);
			return mappingConfig;
		} finally {
			tearDown();
		}
	}

	public BackendConfig getBackendConfig() {
		return backendConfig;
	}

	public MappingConfig getMappingConfig() {
		return mappingConfig;
	}

	private static class MappingRulesConverter implements Converter {
		public boolean canConvert(Class clazz) {
			return MappingRules.Rule.class.isAssignableFrom(clazz);
		}
		
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			MappingRules.Rule rule = (MappingRules.Rule) value;
			writer.addAttribute("result", rule.getResult());
			writer.addAttribute("backend", rule.getBackend());
			writer.addAttribute("direction", rule.getDirection());
			writer.addAttribute("drop", Boolean.toString(rule.isLastRule()));
		}
		
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			if (reader.getNodeName().equals("rename")) {
				return MappingRules.rename(
						reader.getAttribute("result"),
						reader.getAttribute("backend"),
						reader.getAttribute("direction"),
						Boolean.valueOf(reader.getAttribute("drop")).booleanValue());
			} else if (reader.getNodeName().equals("ignore")) {
				return MappingRules.ignore(
						reader.getAttribute("result"),
						reader.getAttribute("backend"),
						reader.getAttribute("direction"),
						Boolean.valueOf(reader.getAttribute("drop")).booleanValue());
			} else {
				throw new IllegalArgumentException("Illegal rule tag name: " + reader.getNodeName());
			}
		}
	}
}
