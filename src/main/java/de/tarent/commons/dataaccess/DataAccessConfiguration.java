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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import de.tarent.commons.dataaccess.backend.DataAccessBackend;
import de.tarent.commons.dataaccess.config.ConfigFactory;
import de.tarent.commons.dataaccess.config.ConfigParser;
import de.tarent.commons.dataaccess.config.MappingRules;

/**
 * Configuration class.
 * 
 * @author Christoph Jerolimov, tarent GmbH
 * @author Timo Pick, tarent GmbH
 */
public class DataAccessConfiguration {
	public DataAccessBackend getDataAccessBackend(String backendName) {
		return ConfigFactory.getBackendConfig().getDataAccessBackend(backendName);
	}

	public void putDataAccessBackend(String backendName, DataAccessBackend dataAccessBackend) {
		ConfigFactory.getBackendConfig().putDataAccessBackend(backendName, dataAccessBackend);
	}

	public MappingRules getMappingRules(String backendName, String className) {
		return ConfigFactory.getMappingConfig().getRules(backendName, className);
	}
	
	public Map getParameters(String backendName, String className) {
		return ConfigFactory.getMappingConfig().getParameters(backendName, className);
	}
	
	public void setParameters(String backendName, String className, Map parameterMap) {
		ConfigFactory.getMappingConfig().setParameters(backendName, className, parameterMap);
	}
	
	public void addMappingRules(String backendName, String classNamePattern, MappingRules rules) {
		ConfigFactory.getMappingConfig().addMappingRules(backendName, classNamePattern, rules);
	}

	public void parseXmlConfig(InputStream inputStream) throws IOException {
		parseXmlConfig(new InputStreamReader(inputStream, "UTF-8"));
	}

	public void parseXmlConfig(InputStream inputStream, String encoding) throws IOException {
		parseXmlConfig(new InputStreamReader(inputStream, encoding));
	}

	public void parseXmlConfig(Reader reader) {
		ConfigParser configParser = new ConfigParser();
		configParser.setUp(true, true);
		configParser.parse(reader);
		
		ConfigFactory.getBackendConfig().putAll(configParser.getBackendConfig());
		ConfigFactory.getMappingConfig().addAll(configParser.getMappingConfig());
		
		configParser.tearDown();
	}
}
