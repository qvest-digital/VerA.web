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

package de.tarent.commons.datahandling.entity;

/**
 * An <code>EntityProperty</code> describes an {@link Entity}'s
 * property.
 * 
 * <p>Subclasses can implement building sets of the properties,
 * localization, etc.</p>
 * 
 * <p>Using <code>EntityProperty</code> instances over strings 
 * in methods makes those places type-safe.</p>
 * 
 * <p>Note for implementors:
 * <ul>
 * <li>instances of subclasses should not be publically creatable</li>
 * <li>subclasses should implement a mechanism that registers the instances
 * with a set or list</li>
 * <li>subclasses should add more specialized query methods</li>
 * </ul>
 * </p>
 */
public abstract class EntityProperty {
	
	protected final String key;
	
	/**
	 * Creates a new <code>EntityProperty</code> with a
	 * fixed name.
	 * 
	 * @param key
	 */
	protected EntityProperty(String key)
	{
		this.key = key;
	}
	
	/**
	 * Returns the property's identifying key.
	 * 
	 *  <p>This override allows nicer usage of <code>EntityProperty</code>
	 *  instances in string concatenation scenarios.</p>
	 */
	public final String toString() {
		return key;
	}
	
	/**
	 * Returns the property's identifying key.
	 */ 
	public final String getKey() {
		return key;
	}
	
    /**
     * Should be implemented to return the property's
     * user readable label translated into the respective
     * language.
     * 
     * @return
     */
	public abstract String getLabel();
}
