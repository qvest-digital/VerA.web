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

import de.tarent.commons.dataaccess.backend.DataAccessBackend;

/**
 * Singleton class that provide starting and stopping different
 * {@link QueryBackend DataAccessBackends}.
 * 
 * @author Christoph Jerolimov, tarent GmbH
 */
public class ConfigFactory {
	private static BackendConfig backendConfig;
	private static MappingConfig mappingConfig;

	public static DataAccessBackend getBackend(String backendName) {
		DataAccessBackend dataAccessBackend = getBackendConfig().getDataAccessBackend(backendName);
		if (dataAccessBackend == null)
			throw new NullPointerException(
					"No backend with the name \"" + backendName + "\" found.");
		dataAccessBackend.begin();
		return dataAccessBackend;
	}

	public static BackendConfig getBackendConfig() {
		if (backendConfig == null)
			backendConfig = new BackendConfig();
		return backendConfig;
	}

	public static MappingConfig getMappingConfig() {
		if (mappingConfig == null)
			mappingConfig = new MappingConfig();
		return mappingConfig;
	}

	/**
	 * DataAccess configuration tagging interface.
	 */
	public static interface Config {
		// Nothing, just tagging classes.
	}
}
