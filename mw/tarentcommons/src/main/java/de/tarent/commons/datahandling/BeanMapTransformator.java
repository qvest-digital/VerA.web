package de.tarent.commons.datahandling;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
