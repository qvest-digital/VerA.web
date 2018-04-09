/*
 * tarent-database,
 * jdbc database library
 * Copyright (c) 2005-2006 tarent GmbH
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
 * interest in the program 'tarent-database'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.dblayer.resource;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Diese Klasse liefert Resourcen.
 * 
 * @author mikel
 * @author Sebastian Mancke, Tarent GmbH
 */
public class Resources {

    /*
     * Konstanten
     */
    private static final String OCTOPUS_BUNDLE_NAME = "de.tarent.dblayer.resource.DBLayer";

    /*
     * Variablen
     */
    private final ResourceBundle bundle;
    private final Resources parent;
    private final static Resources instance = new Resources();

    /*
     * Singelton-Zugriff
     */
    /**
     * Diese Methode liefert ein Singleton f�r das Standardb�ndel.
     */
    public final static Resources getInstance() {
        return instance;
    }
    
    /*
     * Konstruktoren
     */
    /**
     * Dieser Konstruktor liefert eine Instanz zum Standardb�ndel ohne
     * Parent.
     */
    public Resources() {
        this(null);
    }

    /**
     * Dieser Konstruktor liefert eine Instanz zum �bergebenen B�ndel
     * ohne Parent.
     */
    public Resources(String bundleName) {
        this(bundleName, null);
    }

    /**
     * Dieser Konstruktor liefert eine Instanz zum �bergebenen B�ndel
     * und zum �bergebenen Parent.
     */
    public Resources(String bundleName, Resources parent) {
        this.parent = parent;
        if (bundleName == null)
            bundleName = OCTOPUS_BUNDLE_NAME;
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle(bundleName);
        } catch(MissingResourceException mre) {
        }
        bundle = resourceBundle;
    }

    /*
     * Methoden
     */
    /**
     * Diese Methode liefert den Wert zu einem Schl�ssel.
     * 
     * @param key Schl�ssel
     * @return Wert
     */
    public String get(String key) {
        if (bundle != null) try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
        }
        return (parent != null) ? parent.get(key) : '!' + key + '!';
    }

    /**
     * Diese Methode holt den Wert zu einem Schl�ssel, interpretiert
     * ihn als Message, in die die �bergebenen Parameter eingesetzt
     * werden, und gibt die ausgef�llte Message zur�ck.
     *  
     * @param key Schl�ssel
     * @param params Parameter
     * @return ausgef�llte Message
     */
    public String get(String key, Object[] params) {
        return MessageFormat.format(get(key), params);
    }

    /**
     * Diese Methode holt den Wert zu einem Schl�ssel, interpretiert
     * ihn als Message, in die der �bergebene Parameter eingesetzt
     * wird, und gibt die ausgef�llte Message zur�ck.
     *  
     * @param key Schl�ssel
     * @param param Parameter
     * @return ausgef�llte Message
     * @see #get(String, Object[])
     */
    public String get(String key, Object param) {
        return get(key, new Object[]{param});
    }

    /**
     * Diese Methode holt den Wert zu einem Schl�ssel, interpretiert
     * ihn als Message, in die die �bergebene Parameter eingesetzt
     * werden, und gibt die ausgef�llte Message zur�ck.
     *  
     * @param key Schl�ssel
     * @param param1 Parameter
     * @param param2 Parameter
     * @return ausgef�llte Message
     * @see #get(String, Object[])
     */
    public String get(String key, Object param1, Object param2) {
        return get(key, new Object[]{param1, param2});
    }

    /**
     * Diese Methode holt den Wert zu einem Schl�ssel, interpretiert
     * ihn als Message, in die die �bergebene Parameter eingesetzt
     * werden, und gibt die ausgef�llte Message zur�ck.
     *  
     * @param key Schl�ssel
     * @param param1 Parameter
     * @param param2 Parameter
     * @param param3 Parameter
     * @return ausgef�llte Message
     * @see #get(String, Object[])
     */
    public String get(String key, Object param1, Object param2, Object param3) {
        return get(key, new Object[]{param1, param2, param3});
    }

}
