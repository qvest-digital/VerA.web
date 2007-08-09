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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import de.tarent.commons.dataaccess.backend.DataAccessBackend;

public class BackendConfig implements ConfigFactory.Config {
	/**
	 * Contains a mapping from backend name to backend instance.
	 */
	private final Map dataAccessBackends;

	public BackendConfig() {
		this.dataAccessBackends = new LinkedHashMap();
	}

	public BackendConfig(Map dataAccessBackends) {
		this.dataAccessBackends = new LinkedHashMap();
		this.dataAccessBackends.putAll(dataAccessBackends);
	}

	public DataAccessBackend getDataAccessBackend(String backendName) {
		return (DataAccessBackend) dataAccessBackends.get(backendName);
	}

	public void putAll(BackendConfig backendConfig) {
		for (Iterator it = backendConfig.getInternalMap().entrySet().iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			String backendName = (String) entry.getKey();
			DataAccessBackend dataAccessBackend = (DataAccessBackend) entry.getValue();
			
			putDataAccessBackend(backendName, dataAccessBackend);
		}
	}

	public void putDataAccessBackend(String backendName, DataAccessBackend dataAccessBackend) {
		dataAccessBackends.put(backendName, dataAccessBackend);
	}

	Map getInternalMap() {
		return dataAccessBackends;
	}

	public int size() {
		return dataAccessBackends.size();
	}

	public void clear() {
		dataAccessBackends.clear();
	}
}
