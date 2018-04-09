package de.tarent.dblayer.persistence;

import java.util.HashMap;
import java.util.Map;

/** This class is a registry for data access objects.
 *
 * @author Martin Pelzer, tarent GmbH
 *
 */
public class DAORegistry {

	private static Map<Class, AbstractDAO> daos = new HashMap<Class, AbstractDAO>();

	/** registers a new DAO at the registry and its entity
	 * factory at the EntityFactoryRegistry
	 *
	 * @param dao
	 * @param bean
	 */
	public static void registerDAO(AbstractDAO dao, Class bean) {
		daos.put(bean, dao);
	}

	/** returns the appropriate DAO for a given bean
	 *
	 * @param bean
	 * @return
	 */
	public static AbstractDAO getDAOForBean(Class bean) {
		return daos.get(bean);
	}

	/** returns the appropriate DAO for a given bean
	 *
	 * @param bean
	 * @return
	 */
	public static AbstractDAO getDAOForBean(Object bean) {
		return daos.get(bean.getClass());
	}

}
