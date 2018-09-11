package de.tarent.commons.datahandling;

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
    /**
     * Hold a list of setter. (Propertyname -> setter-method)
     */
    private Map setter = new HashMap();
    /**
     * Hold a list of getter. (Propertyname -> getter-method)
     */
    private Map getter = new HashMap();

    /**
     * format for converting a date to a string
     */
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
                if (methodName.equals("getClass")) {
                    continue;
                }

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
     */
    public String getPropertyName(String methodName, int prefixLength) {
        if (methodName.length() > prefixLength) {
            return
              methodName.substring(prefixLength, prefixLength + 1).toLowerCase() +
                methodName.substring(prefixLength + 1);
        } else {
            return null;
        }
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
            Method invokeMethod = (Method) setter.get(property);
            if (invokeMethod == null) {
                throw new RuntimeException("No setter found!");
            }
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
     */
    public String getProperty(Object bean, String property) {
        try {
            Method invokeMethod = (Method) getter.get(property);
            if (invokeMethod == null) {
                throw new RuntimeException("No getter found!");
            }
            if (invokeMethod.getReturnType().equals(Date.class)) {
                Object result = invokeMethod.invoke(bean, new Object[] {});
                return result == null ? null : (String) (new SimpleDateFormat(dateOutputFormat)).format(result);
            } else {
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
