package de.tarent.commons.ui;

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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 *
 * @author Jens Neumaier, tarent GmbH
 *
 */
public class Messages {

    private static ResourceBundle currentResourceBundle = ResourceBundle.getBundle("de.tarent.commons.messages.GUI", Locale.getDefault()); //$NON-NLS-1$

    private Messages() {
	// do not instantiate
    }

    /**
     * This method returns the external String representation for a key from the global ResourceBundle
     *
     * @param key
     * @return String appendant to key; if not found <code>'!' + key + '!'</code> will be returned
     */
    public static String getString(String key) {
	try {
	    return currentResourceBundle.getString(key);

	} catch (MissingResourceException e) {
	    String bundleSuffix = "";
	    if(!"".equals(currentResourceBundle.getLocale().getLanguage())) {
		bundleSuffix += "_"+currentResourceBundle.getLocale().getLanguage();
	    }
	    if(!"".equals(currentResourceBundle.getLocale().getCountry())) {
		bundleSuffix += "_"+currentResourceBundle.getLocale().getCountry();
	    }

	    return '!' + key + '!';
	}
    }

    /**
     * This method returns the external String representation for a key searching in the appropriate package
     * ResourceBundle first and afterwards checking the global ResourceBundle if necessary<br>
     * <br>
     * Example in a maven based repository structure:<br>
     * Calling from package de.tarent.contact.selector.actions the default package ResourceBundle will be
     * expected at src/main/resources/de/tarent/contact/selector/actions.properties
     *
     * @param callingObject Object you are calling this method from (<code>this</code>) or object from the package whose ResourceBundle should be used
     * @param key
     * @return String appendant to key; if not found <code>'!' + key + '!'</code> will be returned
     */
    public static String getString(Object callingObject, String key) {
	String callingPackage = callingObject.getClass().getPackage().getName();

	ResourceBundle packageBundle = null;

	// searching for package ResourceBundle; otherwise searching key in global ResourceBundle
	try {
	    packageBundle = ResourceBundle.getBundle(callingPackage, currentResourceBundle.getLocale());
	} catch (MissingResourceException e) {
	    return getString(key);
	}

	// searching key in package ResourceBundle; otherwise searching key in global ResourceBundle
	try {
	    return packageBundle.getString(key);
	} catch (MissingResourceException e) {
	    return getString(key);
	}
    }

    /*
     * getFormattedString-methods using global ResourceBundle only
     */
    /**
     * Auxiliary method
     *
     * @see Messages#getFormattedString(String, Object[])
     */
    public static String getFormattedString(String key, Object argument1) {
	return getFormattedString(key, argument1, null, null, null);
    }

    /**
     * Auxiliary method
     *
     * @see Messages#getFormattedString(String, Object[])
     */
    public static String getFormattedString(String key, Object argument1, Object argument2) {
	return getFormattedString(key, argument1, argument2, null, null);
    }

    /**
     * Auxiliary method
     *
     * @see Messages#getFormattedString(String, Object[])
     */
    public static String getFormattedString(String key, Object argument1, Object argument2, Object argument3) {
	return getFormattedString(key, argument1, argument2, argument3, null);
    }

    /**
     * Auxiliary method
     *
     * @see Messages#getFormattedString(String, Object[])
     */
    public static String getFormattedString(String key, Object argument1, Object argument2, Object argument3, Object argument4) {
	List arguments = new ArrayList(4);

	if (argument1 != null) arguments.add(argument1);
	if (argument2 != null) arguments.add(argument2);
	if (argument3 != null) arguments.add(argument3);
	if (argument4 != null) arguments.add(argument4);

	return getFormattedString(key, arguments.toArray());
    }

    /**
     * Auxiliary method
     *
     * @see Messages#getFormattedString(String, Object[])
     */
    public static String getFormattedString(String key, List arguments) {
	return getFormattedString(key, arguments.toArray());
    }

    /**
     * Gets the external String representation for key from the global ResourceBundle
     * and returns the formatted String according to java.text.MessageFormat
     *
     * @see MessageFormat#format(java.lang.String, java.lang.Object[])
     */
    public static String getFormattedString(String key, Object[] arguments) {
	return MessageFormat.format(getString(key), arguments);
    }

    /*
     * getFormattedString-methods using package and global ResourceBundle
     */
    /**
     * Auxiliary method
     *
     * @see Messages#getFormattedString(Object, String, Object[])
     */
    public static String getFormattedString(Object callingObject, String key, Object argument1) {
	return getFormattedString(callingObject, key, argument1, null, null, null);
    }

    /**
     * Auxiliary method
     *
     * @see Messages#getFormattedString(Object, String, Object[])
     */
    public static String getFormattedString(Object callingObject, String key, Object argument1, Object argument2) {
	return getFormattedString(callingObject, key, argument1, argument2, null, null);
    }

    /**
     * Auxiliary method
     *
     * @see Messages#getFormattedString(Object, String, Object[])
     */
    public static String getFormattedString(Object callingObject, String key, Object argument1, Object argument2, Object argument3) {
	return getFormattedString(callingObject, key, argument1, argument2, argument3, null);
    }

    /**
     * Auxiliary method
     *
     * @see Messages#getFormattedString(Object, String, Object[])
     */
    public static String getFormattedString(Object callingObject, String key, Object argument1, Object argument2, Object argument3, Object argument4) {
	List arguments = new ArrayList(4);

	if (argument1 != null) arguments.add(argument1);
	if (argument2 != null) arguments.add(argument2);
	if (argument3 != null) arguments.add(argument3);
	if (argument4 != null) arguments.add(argument4);

	return getFormattedString(callingObject, key, arguments.toArray());
    }

    /**
     * Auxiliary method
     *
     * @see Messages#getFormattedString(Object, String, Object[])
     */
    public static String getFormattedString(Object callingObject, String key, List arguments) {
	return getFormattedString(callingObject, key, arguments.toArray());
    }

    /**
     * Gets the external String representation for key from the package or global ResourceBundle
     * and returns the formatted String according to java.text.MessageFormat
     *
     * @see Messages#getString(Object, String)
     * @see MessageFormat#format(java.lang.String, java.lang.Object[])
     */
    public static String getFormattedString(Object callingObject, String key, Object[] arguments) {
	return MessageFormat.format(getString(callingObject, key), arguments);
    }

    /**
     * This methods sets the active locale for all used ResourceBundles
     *
     * @param locale locale to set
     */
    public static void setLocale(Locale locale) {
	currentResourceBundle = PropertyResourceBundle.getBundle("de.tarent.commons.messages.GUI",locale); //$NON-NLS-1$
    }
}
