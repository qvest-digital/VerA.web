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

package de.tarent.commons.datahandling;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This util class help you to transform a bean (every object instance) with
 * all his properties in a map. Also the other way, from a map into a bean.
 *
 * @author Christoph Jerolimov
 */
public class BeanMapTransformator {
	/** BeanAccessor cache for more performance. */
	private Map beanAccessorCache = new HashMap();

	/** format for converting a date to a string*/
	private String dateOutputFormat = "dd.MM.yyyy";

	/**
	 * Return a new map with all getable property information of
	 * the given <code>bean</code>.
	 *
	 * @param bean
	 * @throws NullPointerException if bean is null.
	 * @return Map, never null.
	 */
	public Map transformBeanToMap(Object bean) {
		Map map = new HashMap();
		transformBeanToMap(bean, map);
		return map;
	}

	/**
	 * Return a new instance of the given <code>clazz</code>.
	 * All setable property information of the bean are already
	 * filled with the information of the map.
	 *
	 * @param map
	 * @param clazz
	 * @throw NullPointerException, if map or clazz are null.
	 * @return Bean, never null.
	 */
	public Object transformMapToBean(Map map, Class clazz) {
		try {
			Object bean = clazz.newInstance();
			transformMapToBean(map, bean);
			return bean;
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Transfer all information from the bean into the map.
	 *
	 * @param bean
	 * @param map
	 */
	public void transformBeanToMap(Object bean, Map map) {
		if (bean == null)
			throw new NullPointerException("Bean can not be null.");
		if (map == null)
			throw new NullPointerException("Map can not be null.");
		BeanAccessor accessor = getBeanAccessor(bean.getClass());

		for (Iterator it = accessor.getGetableProperties().iterator(); it.hasNext(); ) {
			String property = (String)it.next();
			map.put(property, accessor.getProperty(bean, property));
		}
	}

	/**
	 * Set all setable properties of the bean with the attributes
	 * of the map. (Only if the map is filled with this information.)
	 *
	 * @param map
	 * @param bean
	 */
	public void transformMapToBean(Map map, Object bean) {
		if (bean == null)
			throw new NullPointerException("Bean can not be null.");
		if (map == null)
			throw new NullPointerException("Map can not be null.");
		BeanAccessor accessor = getBeanAccessor(bean.getClass());

		for (Iterator it = accessor.getSetableProperties().iterator(); it.hasNext(); ) {
			String property = (String)it.next();
			if (map.containsKey(property))
				accessor.setProperty(bean, property, map.get(property));
		}
	}

	/**
	 * Return a cached {@link BeanAccessor} for the given <code>clazz</code>.
	 *
	 * @param clazz
	 * @return
	 */
	private BeanAccessor getBeanAccessor(Class clazz) {
		BeanAccessor accessor = (BeanAccessor)beanAccessorCache.get(clazz.getName());
		if (accessor == null) {
			accessor = new BeanAccessor(clazz);
			accessor.setDateOutputFormat(this.dateOutputFormat);
			beanAccessorCache.put(clazz.getName(), accessor);
		}
		return accessor;
	}

	public String getDateOutputFormat() {
		return dateOutputFormat;
	}

	public void setDateOutputFormat(String dateOutputFormat) {
		this.dateOutputFormat = dateOutputFormat;
	}
}
