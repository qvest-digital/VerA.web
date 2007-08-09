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

import de.tarent.commons.dataaccess.backend.DataAccessBackend;
import de.tarent.commons.dataaccess.backend.StoringAttributeSets;
import de.tarent.commons.dataaccess.backend.StoringObjectTrees;
import de.tarent.commons.dataaccess.backend.StoringStrategy;
import de.tarent.commons.dataaccess.data.AttributeListener;
import de.tarent.commons.dataaccess.data.AttributeSet;
import de.tarent.commons.utils.Pojo;

public class StoreProcessor extends ObjectProcessor {
	/**
	 * The configured backend name for lazy loading the mapping configuration.
	 */
	private final String backendName;

	/**
	 * The {@link StoringStrategy} instance of the {@link DataAccessBackend},
	 * normally it is the same class and instance.
	 */
	private final StoringStrategy storingStrategy;

	/**
	 * Affected class type which specified the data convertion configuration.
	 */
	private final Class affectedType;

	public StoreProcessor(
			String backendName,
			DataAccessBackend dataAccessBackend,
			Class affectedType) {
		
		if (dataAccessBackend == null)
			throw new NullPointerException("BackendName and DataAccessBackend should not be null.");
		this.backendName = backendName;
		this.storingStrategy = dataAccessBackend.getStoringStrategy();
		if (this.storingStrategy == null)
			throw new NullPointerException("StoringStrategy should not be null.");
		
		this.affectedType = affectedType;
	}

	public String getBackendName() {
		return backendName;
	}

	public Class getAffectedType() {
		return affectedType;
	}

	public void store(Object object) {
		if (storingStrategy instanceof StoringAttributeSets) {
			
			if (object instanceof AttributeSet) {
				storeAttributeSet((StoringAttributeSets)storingStrategy, (AttributeSet) object);
			} else {
				storeObjectAsAttributeSet((StoringAttributeSets)storingStrategy, object);
			}
			
		} else if (storingStrategy instanceof StoringObjectTrees) {
			
			storeObjectTree((StoringObjectTrees)storingStrategy, object);
			
		} else {
			throw new IllegalArgumentException(
					"Unsupported StoringStrategy: " +
					storingStrategy.getClass().getName());
		}
	}

	protected void storeAttributeSet(StoringAttributeSets storingAttributeSets, AttributeSet attributeSet) {
		storingAttributeSets.store(this, attributeSet);
	}

	protected void storeObjectAsAttributeSet(StoringAttributeSets storingAttributeSets, Object object) {
		seperateBeanArgumentsToAttributeSets(object, new Object[] {
				storingAttributeSets, Boolean.TRUE, Boolean.FALSE });
	}

	protected void storeObjectTree(StoringObjectTrees storingObjectTrees, Object object) {
		storingObjectTrees.store(this, object);
	}

	public void delete(Object object) {
		if (storingStrategy instanceof StoringAttributeSets) {
			
			if (object instanceof AttributeSet) {
				deleteAttributeSet((StoringAttributeSets)storingStrategy, (AttributeSet) object);
			} else {
				deleteObjectByAttributeSet((StoringAttributeSets)storingStrategy, object);
			}
			
		} else if (storingStrategy instanceof StoringObjectTrees) {
			
			deleteObjectTree((StoringObjectTrees)storingStrategy, object);
			
		} else {
			throw new IllegalArgumentException(
					"Unsupported StoringStrategy: " +
					storingStrategy.getClass().getName());
		}
	}

	protected void deleteAttributeSet(StoringAttributeSets storingAttributeSets, AttributeSet attributeSet) {
		storingAttributeSets.delete(this, attributeSet);
	}

	protected void deleteObjectByAttributeSet(StoringAttributeSets storingAttributeSets, Object object) {
		seperateBeanArgumentsToAttributeSets(object, new Object[] {
				storingAttributeSets, Boolean.FALSE, Boolean.TRUE });
	}

	protected void deleteObjectTree(StoringObjectTrees storingObjectTrees, Object object) {
		storingObjectTrees.delete(this, object);
	}

	protected void handleSeperatedBeanAttributeSets(
			final Object bean,
			final AttributeSet attributeSet,
			final Object[] parameters) {
		
		AttributeListener attributeListener = new AttributeListener() {
			public void handleAttributeChange(
					String attributeName,
					Object oldValue,
					Object newValue) {
				Pojo.set(bean, attributeName, newValue);
			}
		};
		attributeSet.addAttributeListener(attributeListener);
		
		if (((Boolean)parameters[1]).booleanValue())
			storeAttributeSet((StoringAttributeSets) parameters[0], attributeSet);
		if (((Boolean)parameters[2]).booleanValue())
			deleteAttributeSet((StoringAttributeSets) parameters[0], attributeSet);
		
		attributeSet.removeAttributeListener(attributeListener);
	}
}
