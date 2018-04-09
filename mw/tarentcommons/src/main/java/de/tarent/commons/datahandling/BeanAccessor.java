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

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tarent.commons.utils.Tools;
import de.tarent.commons.utils.VariableDateFormat;

/**
 * holds information of setter and getter of a bean.
 */
public class BeanAccessor {
	/** Hold a list of setter. (Propertyname -> setter-method) */
	private Map setter = new HashMap();
	/** Hold a list of getter. (Propertyname -> getter-method) */
	private Map getter = new HashMap();

	/** format for converting a date to a string*/
	private String dateOutputFormat = "dd.MM.yyyy";

	/**
	 * Constructor which load information about setters and getters.
	 */
	public BeanAccessor(Class clazz) {
		Method methods[] = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			String methodName = methods[i].getName();

			if (methodName.startsWith("set") && methods[i].getParameterTypes().length == 1) {
				String propertyName = getPropertyName(methodName, 3);
				Tools.putIfNotNull(setter, propertyName, methods[i]);
			} else if (methodName.startsWith("get") && methods[i].getParameterTypes().length == 0) {
				if (methodName.equals("getClass"))
					continue;

				String propertyName = getPropertyName(methodName, 3);
				Tools.putIfNotNull(getter, propertyName, methods[i]);
			} else if (methodName.startsWith("is") && methods[i].getParameterTypes().length == 0) {
				String propertyName = getPropertyName(methodName, 2);
				Tools.putIfNotNull(getter, propertyName, methods[i]);
			}
		}
	}

	/**
	 * Get the propertyname of a setter or getter method.
	 *
	 * @param methodName
	 * @param prefixLength
	 * @return
	 */
	public String getPropertyName(String methodName, int prefixLength) {
		if (methodName.length() > prefixLength)
			return
					methodName.substring(prefixLength, prefixLength + 1).toLowerCase() +
					methodName.substring(prefixLength + 1);
		else
			return null;
	}

	/**
	 * Set the property of a bean.
	 *
	 * @param bean
	 * @param property
	 * @param value
	 */
	public void setProperty(Object bean, String property, Object value) {
		// TODO This should be automatic transform the fields, e.g. from string to integer.
		try {
			Method invokeMethod = (Method)setter.get(property);
			if (invokeMethod == null)
				throw new RuntimeException("No setter found!");
			if (invokeMethod.getParameterTypes()[0].equals(Date.class)) {
				invokeMethod.invoke(bean, new Object[] {
						value == null ? null : (new VariableDateFormat()).analyzeString((String) value) });
			} else if (invokeMethod.getParameterTypes()[0].equals(Integer.class)) {
				invokeMethod.invoke(bean, new Object[] {
						value == null ? null : new Integer(value.toString()) });
			} else {
				invokeMethod.invoke(bean, new Object[] { value });
			}
		} catch (Exception e) {
			throw new RuntimeException("Error: " +
					" Could not set property '" + property + "' with " +
					"'" + value + "' (" + value.getClass() + ").", e);
		}
	}

	/**
	 * Get the property of a bean.
	 *
	 * @param bean
	 * @param property
	 * @return
	 */
	public String getProperty(Object bean, String property) {
		try {
			Method invokeMethod = (Method)getter.get(property);
			if (invokeMethod == null)
				throw new RuntimeException("No getter found!");
			if (invokeMethod.getReturnType().equals(Date.class)) {
				Object result = invokeMethod.invoke(bean, new Object[] {});
				return result == null ? null : (String) (new SimpleDateFormat(dateOutputFormat)).format(result);
			}
			else {
				Object result = invokeMethod.invoke(bean, new Object[] {});
				return result == null ? null : result.toString();
			}
		} catch (Exception e) {
			throw new RuntimeException("Fehler!" +
					" Konnte Property " + property + " nicht lesen.", e);
		}
	}

	/**
	 * @return Return all setable property names.
	 */
	public List getSetableProperties() {
		return new ArrayList(setter.keySet());
	}

	/**
	 * @return Return all getable property names.
	 */
	public List getGetableProperties() {
		return new ArrayList(getter.keySet());
	}

	public String getDateOutputFormat() {
		return dateOutputFormat;
	}

	public void setDateOutputFormat(String dateOutputFormat) {
		this.dateOutputFormat = dateOutputFormat;
	}
}
