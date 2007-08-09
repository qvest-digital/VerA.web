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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tarent.commons.dataaccess.backend.DataAccessBackend;
import de.tarent.commons.dataaccess.backend.QueryingStrategy;
import de.tarent.commons.dataaccess.backend.StoringStrategy;

public class DistributedDataAccessBackend implements DataAccessBackend {
	/** Delegate instances */
	private final Map delegateBackends;

	/** Determined parameter */
	private boolean determined = false;

	public DistributedDataAccessBackend() {
		this.delegateBackends = new LinkedHashMap();
	}

	public DistributedDataAccessBackend(Map delegateBackends) {
		this.delegateBackends = new LinkedHashMap();
		this.delegateBackends.putAll(delegateBackends);
	}

	public void register(Class classInstance, DataAccessBackend dataAccessBackend) {
		if (determined)
			throw new IllegalStateException("DistributedDataAccessBackend is already determined.");
		
		delegateBackends.put(classInstance, dataAccessBackend);
	}

	public void unregister(Class classInstance, DataAccessBackend dataAccessBackend) {
		if (determined)
			throw new IllegalStateException("DistributedDataAccessBackend is already determined.");
		
		delegateBackends.remove(classInstance);
	}

	public DataAccessBackend getBackend(Class classInstance) {
		if (!determined)
			determined = true;
		
		return (DataAccessBackend) delegateBackends.get(classInstance);
	}

	public DataAccessBackend getBackend(Object o) {
		if (!determined)
			determined = true;
		
		return (DataAccessBackend) delegateBackends.get(o.getClass());
	}

	public DataAccessBackend[] getBackends() {
		if (!determined)
			determined = true;
		
		List list = new ArrayList(delegateBackends.size());
		for (Iterator it = delegateBackends.keySet().iterator(); it.hasNext(); ) {
			DataAccessBackend dab = (DataAccessBackend) it.next();
			if (!list.contains(dab))
				list.add(dab);
		}
		return (DataAccessBackend[]) list.toArray(new DataAccessBackend[list.size()]);
	}

	public void init() {
		
	}

	/** {@inheritDoc} */
	public QueryingStrategy getQueryingStrategy() {
		return null;
	}

	/** {@inheritDoc} */
	public StoringStrategy getStoringStrategy() {
		return null;
	}

//	/** {@inheritDoc} */
//	public void store(Object o) {
//		getBackend(o).store(o);
//	}
//
//	/** {@inheritDoc} */
//	public void delete(Object o) {
//		getBackend(o).delete(o);
//	}

	/** {@inheritDoc} */
	public void begin() {
		DataAccessBackend dataAccessBackend[] = getBackends();
		for (int i = 0; i < dataAccessBackend.length; i++) {
			dataAccessBackend[i].begin();
		}
	}

	/** {@inheritDoc} */
	public void commit() {
		DataAccessBackend dataAccessBackend[] = getBackends();
		for (int i = 0; i < dataAccessBackend.length; i++) {
			dataAccessBackend[i].commit();
		}
	}

	/** {@inheritDoc} */
	public void rollback() {
		DataAccessBackend dataAccessBackend[] = getBackends();
		for (int i = 0; i < dataAccessBackend.length; i++) {
			dataAccessBackend[i].rollback();
		}
	}
}
