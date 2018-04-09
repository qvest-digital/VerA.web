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
package de.tarent.commons.ui;

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
