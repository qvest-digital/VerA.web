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
