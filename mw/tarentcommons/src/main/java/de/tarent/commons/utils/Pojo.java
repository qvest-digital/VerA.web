package de.tarent.commons.utils;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2018 Dominik George (d.george@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
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

import de.tarent.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Util class for dealing with plain old java objects (POJOs)
 */
public class Pojo {
    private static final Log logger = LogFactory.getLog(Pojo.class);

    static Object[] emptyObjectArray = new Object[] {};

    private static Map methodCache = new HashMap();
    private static Map readablePropertyTypCache = new HashMap();
    private static Map writeablePropertyTypCache = new HashMap();
    private static Map allPropertyTypCache = new HashMap();

    /**
     * Convenience method for getSetMethod(pojo, property, false).
     * Returns the first Method which may be a setXXX Method for the supplied pojo and property,
     * according to the Java Bean Specification
     *
     * @return the setter for the property, or null if no mehtod matches
     */
    public static Method getSetMethod(Object pojo, String property) {
        return getSetMethod(pojo, property, false);
    }

    /**
     * Returns the first Method which may be a setXXX Method for the supplied pojo and property,
     * according to the Java Bean Specification
     *
     * @param pojo               target pojo for the property
     * @param property           target property
     * @param ignorePropertyCase if an exact case match of the metod name is forced
     * @return the setter for the property, or null if no mehtod matches
     */
    public static Method getSetMethod(Object pojo, String property, boolean ignorePropertyCase) {
        return getSetMethod(pojo.getClass(), property, ignorePropertyCase);
    }

    /**
     * Returns the first Method which may be a setXXX Method for the supplied pojo and property,
     * according to the Java Bean Specification
     *
     * @param property           target property
     * @param ignorePropertyCase if an exact case match of the metod name is forced
     * @return the setter for the property, or null if no mehtod matches
     */
    public static Method getSetMethod(Class clazz, String property, boolean ignorePropertyCase) {
        // try to lookup in the cache
        String methodKey = clazz.getName() + "#set" + property;
        Method cachedMethod = (Method) methodCache.get(methodKey);
        if (cachedMethod != null) {
            return cachedMethod;
        }

        // find the new one
        Method[] methods = clazz.getMethods();
        String set = "set" + StringTools.capitalizeFirstLetter(property);
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getParameterTypes().length == 1 &&
              ((ignorePropertyCase && methods[i].getName().equalsIgnoreCase(set))
                || methods[i].getName().equals(set))) {
                methodCache.put(methodKey, methods[i]);
                return methods[i];
            }
        }
        return null;
    }

    /**
     * Sets the supplied data to the pojo, using the supplied method.
     * If the data has a wrong type, the ConverterRegistry is used to convert it to the target type.
     * If the data is null and the target is a primitive, a meaningfull default is used.
     *
     * @throws IllegalArgumentException if the data conversion fails, or if the method signature does not have one parameter.
     * @throws RuntimeException         if the reflection call fails with any cause
     */
    public static void set(Object pojo, Method setter, Object data) {
        Class[] params = setter.getParameterTypes();
        if (params.length != 1) {
            throw new IllegalArgumentException("Signature must have exactly one paraemter");
        }
        try {
            logger.trace("performing Pojo.set() '" + setter + "' with data: '" + data + "'");
            setter.invoke(pojo, new Object[] { ConverterRegistry.convert(data, params[0]) });
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error on setting property of '" + pojo.getClass() + "' with method '" + setter + "'",
              e.getCause());
        } catch (Exception e) {
            throw new RuntimeException("Error on setting property of '" + pojo.getClass() + "' with method '" + setter + "'", e);
        }
        // nothing to return
    }

    /**
     * Sets the property of a pojo.
     * Short method for getSetMethod()+set(). For multiple calls it is faster to
     * use getSetMethod() once and set() for every property assignment, because the mehtod lookup is expensive.
     *
     * @throws IllegalArgumentException if no such property exists, or the supplied data does not match the signature.
     */
    public static void set(Object pojo, String property, Object data) {
        set(pojo, property, data, false);
    }

    /**
     * Sets the property of a pojo.
     * Short method for getSetMethod()+set(). For multiple calls it is faster to
     * use getSetMethod() once and set() for every property assignment, because the mehtod lookup is expensive.
     *
     * @throws IllegalArgumentException if no such property exists, or the supplied data does not match the signature.
     */
    public static void set(Object pojo, String property, Object data, boolean ignorePropertyCase) {
        Method m = getSetMethod(pojo, property, ignorePropertyCase);
        if (m == null) {
            throw new IllegalArgumentException(
              "No set method for the property '" + property + "' on '" + pojo.getClass().getName() + "'");
        }
        set(pojo, m, data);
    }

    /**
     * Returns the first Method which may be a getXXX Method for the supplied pojo and property,
     * according to the Java Bean Specification
     *
     * @return the getter for the property, or null if no mehtod matches
     */
    public static Method getGetMethod(Object pojo, String property) {
        return getGetMethod(pojo, property, false);
    }

    /**
     * Returns the first Method which may be a getXXX Method for the supplied pojo and property,
     * according to the Java Bean Specification
     *
     * @return the getter for the property, or null if no mehtod matches
     */
    public static Method getGetMethod(Object pojo, String property, boolean ignorePropertyCase) {

        // try to lookup in the cache
        String methodKey = pojo.getClass().getName() + "#get/is" + property;
        Method cachedMethod = (Method) methodCache.get(methodKey);
        if (cachedMethod != null) {
            return cachedMethod;
        }

        // find the new one
        Method[] methods = pojo.getClass().getMethods();
        String get = "get" + StringTools.capitalizeFirstLetter(property);
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getParameterTypes().length == 0 &&
              ((ignorePropertyCase && methods[i].getName().equalsIgnoreCase(get))
                || methods[i].getName().equals(get))) {
                methodCache.put(methodKey, methods[i]);
                return methods[i];
            }
        }
        // search fo an boolean isXXX
        String is = "is" + StringTools.capitalizeFirstLetter(property);
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getParameterTypes().length == 0 &&
              ((ignorePropertyCase && methods[i].getName().equalsIgnoreCase(is))
                || methods[i].getName().equals(is))) {
                methodCache.put(methodKey, methods[i]);
                return methods[i];
            }
        }
        return null;
    }

    /**
     * Returns the value of the property represented by the supplied method.
     *
     * @throws IllegalArgumentException if the method signature have parameters.
     * @throws RuntimeException         if the reflection call fails with any cause
     */
    public static Object get(Object pojo, Method getter) {
        if (getter.getParameterTypes().length != 0) {
            throw new IllegalArgumentException("Signature must have no paraemters");
        }
        try {
            logger.trace("performing Pojo.get() '" + getter + "'");
            return getter.invoke(pojo, emptyObjectArray);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error on getting property of '" + pojo + "' with method '" + getter + "'", e.getCause());
        } catch (Exception e) {
            throw new RuntimeException("Error on getting property of '" + pojo + "' with method '" + getter + "'", e);
        }
    }

    /**
     * Returns the value of the property represented by the supplied method.
     * Short method for getGetMethod()+get(). For multiple calls it is faster to
     * use getGetMethod() once and get() for every property assignment, because the mehtod lookup is expensive.
     *
     * @throws IllegalArgumentException if no matching method was found
     */
    public static Object get(Object pojo, String property) {
        return get(pojo, property, false);
    }

    /**
     * Returns the value of the property represented by the supplied method.
     * Short method for getGetMethod()+get(). For multiple calls it is faster to
     * use getGetMethod() once and get() for every property assignment, because the mehtod lookup is expensive.
     *
     * @throws IllegalArgumentException if no matching method was found
     */
    public static Object get(Object pojo, String property, boolean ignorePropertyCase) {
        Method m = getGetMethod(pojo, property);
        if (m == null) {
            throw new IllegalArgumentException(
              "No get method for the property '" + property + "' on '" + pojo.getClass().getName() + "'");
        }
        return get(pojo, m);
    }

    /**
     * Return all readable or writeable property names of the given pojo object.
     * Uses an {@link #allPropertyTypCache internal static cache} for holding
     * these list, also fill the {@link #methodCache method cache}.
     *
     * @param pojo
     */
    public static Set getPropertyNames(Object pojo) {
        return getPropertyTypes(pojo).keySet();
    }

    /**
     * Return all readable or writeable property names of the given pojo object
     * as map which contains the property name as key and the type as value.
     * Uses an {@link #allPropertyTypCache internal static cache} for holding
     * these list, also fill the {@link #methodCache method cache}.
     *
     * @param pojo
     */
    public static Map getPropertyTypes(Object pojo) {
        String className = pojo.getClass().getName();
        Map map = (Map) allPropertyTypCache.get(className);
        if (map != null) {
            return map;
        }

        map = new LinkedHashMap();
        Method m[] = pojo.getClass().getMethods();

        for (int i = 0; i < m.length; i++) {
            if (
              m[i].getName().startsWith("get") &&
                m[i].getName().length() > 3 &&
                m[i].getParameterTypes().length == 0 && !
                m[i].getReturnType().equals(Boolean.TYPE) && !
                m[i].getReturnType().equals(Void.TYPE)) {
                String property = m[i].getName().substring(3, 4).toLowerCase() +
                  m[i].getName().substring(4);
                map.put(property, m[i].getReturnType());
                String methodKey = m[i].getName() + "#get/is" + property;
                methodCache.put(methodKey, m[i]);
            } else if (
              m[i].getName().startsWith("is") &&
                m[i].getName().length() > 2 &&
                m[i].getParameterTypes().length == 0 &&
                m[i].getReturnType().equals(Boolean.TYPE)) {
                String property = m[i].getName().substring(2, 3).toLowerCase() +
                  m[i].getName().substring(3);
                map.put(property, Boolean.TYPE);
                String methodKey = className + "#get/is" + property;
                methodCache.put(methodKey, m[i]);
            } else if (
              m[i].getName().startsWith("set") &&
                m[i].getName().length() > 3 &&
                m[i].getParameterTypes().length == 1 &&
                m[i].getReturnType().equals(Void.TYPE)) {
                String property = m[i].getName().substring(3, 4).toLowerCase() +
                  m[i].getName().substring(4);
                map.put(property, m[i].getParameterTypes()[0]);
                String methodKey = className + "#set" + property;
                methodCache.put(methodKey, m[i]);
            }
        }

        map = Collections.unmodifiableMap(map);
        allPropertyTypCache.put(className, map);

        return map;
    }

    /**
     * Return all readable property names of the given pojo object.
     * Uses an {@link #readablePropertyTypCache internal static cache} for holding
     * these list, also fill the {@link #methodCache method cache}.
     *
     * @param pojo
     */
    public static Set getReadablePropertyNames(Object pojo) {
        return getReadablePropertyTypes(pojo).keySet();
    }

    /**
     * Return all readable or writeable property names of the given pojo object
     * as map which contains the property name as key and the type as value.
     * Uses an {@link #readablePropertyTypCache internal static cache} for holding
     * these list, also fill the {@link #methodCache method cache}.
     *
     * @param pojo
     */
    public static Map getReadablePropertyTypes(Object pojo) {
        String className = pojo.getClass().getName();
        Map map = (Map) readablePropertyTypCache.get(className);
        if (map != null) {
            return map;
        }

        map = new LinkedHashMap();
        Method m[] = pojo.getClass().getMethods();

        for (int i = 0; i < m.length; i++) {
            if (
              m[i].getName().startsWith("get") &&
                m[i].getName().length() > 3 &&
                m[i].getParameterTypes().length == 0 && !
                m[i].getReturnType().equals(Boolean.TYPE) && !
                m[i].getReturnType().equals(Void.TYPE) && !
                m[i].getReturnType().equals(Class.class)) {
                String property = m[i].getName().substring(3, 4).toLowerCase() +
                  m[i].getName().substring(4);
                map.put(property, m[i].getReturnType());
                String methodKey = m[i].getName() + "#get/is" + property;
                methodCache.put(methodKey, m[i]);
            } else if (
              m[i].getName().startsWith("is") &&
                m[i].getName().length() > 2 &&
                m[i].getParameterTypes().length == 0 &&
                m[i].getReturnType().equals(Boolean.TYPE)) {
                String property = m[i].getName().substring(2, 3).toLowerCase() +
                  m[i].getName().substring(4);
                map.put(property, Boolean.TYPE);
                String methodKey = className + "#get/is" + property;
                methodCache.put(methodKey, m[i]);
            }
        }

        map = Collections.unmodifiableMap(map);
        readablePropertyTypCache.put(className, map);

        return map;
    }

    /**
     * Return all writeable property names of the given pojo object.
     * Uses an {@link #writeablePropertyTypCache internal static cache} for holding
     * these list, also fill the {@link #methodCache method cache}.
     *
     * @param pojo
     */
    public static Set getWriteablePropertyNames(Object pojo) {
        return getWriteablePropertyTypes(pojo).keySet();
    }

    /**
     * Return all readable or writeable property names of the given pojo object
     * as map which contains the property name as key and the type as value.
     * Uses an {@link #writeablePropertyTypCache internal static cache} for holding
     * these list, also fill the {@link #methodCache method cache}.
     *
     * @param pojo
     */
    public static Map getWriteablePropertyTypes(Object pojo) {
        String className = pojo.getClass().getName();
        Map map = (Map) writeablePropertyTypCache.get(className);
        if (map != null) {
            return map;
        }

        map = new LinkedHashMap();
        Method m[] = pojo.getClass().getMethods();

        for (int i = 0; i < m.length; i++) {
            if (
              m[i].getName().startsWith("set") &&
                m[i].getName().length() > 3 &&
                m[i].getParameterTypes().length == 1 &&
                m[i].getReturnType().equals(Void.TYPE)) {
                String property = m[i].getName().substring(3, 4).toLowerCase() +
                  m[i].getName().substring(4);
                map.put(property, m[i].getParameterTypes()[0]);
                String methodKey = className + "#set" + property;
                methodCache.put(methodKey, m[i]);
            }
        }

        map = Collections.unmodifiableMap(map);
        writeablePropertyTypCache.put(className, map);

        return map;
    }
}
