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

import java.util.HashMap;
import java.util.Map;

/**
 * See {@link #registerEntityFactory(Class, EntityFactory)},
 * {@link #unregisterEntityFactory(Class, EntityFactory)} and
 * {@link #getEntityFactory(Class)}.
 */
public class EntityFactoryRegistry {
	private static final Map entityFactories = new HashMap();

	/**
	 * Register an {@link EntityFactory} for the given instantiation class name.
	 *
	 * @param instantiationClass
	 * @param entityFactory
	 */
	public static void registerEntityFactory(Class instantiationClass, EntityFactory entityFactory) {
		entityFactories.put(instantiationClass, entityFactory);
	}

	/**
	 * Unregister an {@link EntityFactory} for the given instantiation class
	 * name.
	 *
	 * @param instantiationClass
	 * @param entityFactory
	 */
	public static void unregisterEntityFactory(Class instantiationClass, EntityFactory entityFactory) {
		entityFactories.remove(instantiationClass);
	}

	/**
	 * Return an {@link EntityFactory} for the given instantiation class name.
	 * If none {@link EntityFactory} has registered it will return an new
	 * {@link DefaultEntityFactory} instance.
	 *
	 * @param instantiationClass
	 * @return
	 */
	public static EntityFactory getEntityFactory(Class instantiationClass) {
		if (instantiationClass == null)
			return null;

		EntityFactory entityFactory = (EntityFactory) entityFactories.get(instantiationClass);
		if (entityFactory == null) {
			entityFactory = new DefaultEntityFactory(instantiationClass);
			entityFactories.put(instantiationClass, entityFactory);
		}
		return entityFactory;
	}

	/**
	 * Return an {@link EntityFactory} for the given instantiation class name.
	 * If none {@link EntityFactory} has registered it will return an new
	 * {@link DefaultEntityFactory} instance initialized with the given
	 * <code>keyName</code>.
	 *
	 * @param instantiationClass
	 * @return
	 */
	public static EntityFactory getEntityFactory(Class instantiationClass, String keyName) {
		if (instantiationClass == null)
			return null;

		EntityFactory entityFactory = (EntityFactory) entityFactories.get(instantiationClass);
		if (entityFactory == null) {
			entityFactory = new DefaultEntityFactory(instantiationClass, keyName);
			entityFactories.put(instantiationClass, entityFactory);
		}
		return entityFactory;
	}
}
