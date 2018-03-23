package de.tarent.octopus.resource;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Diese Klasse liefert Resourcen.
 *
 * @author mikel
 */
public class Resources {
    /*
     * Singelton-Zugriff
     */
    /**
     * Diese Methode liefert ein Singleton für das Standardbündel.
     */
    public final static Resources getInstance() {
        return instance;
    }

    /*
     * Konstruktoren
     */
    /**
     * Dieser Konstruktor liefert eine Instanz zum Standardbündel ohne
     * Parent.
     */
    public Resources() {
        this(null);
    }

    /**
     * Dieser Konstruktor liefert eine Instanz zum übergebenen Bündel
     * ohne Parent.
     */
    public Resources(String bundleName) {
        this(bundleName, null);
    }

    /**
     * Dieser Konstruktor liefert eine Instanz zum übergebenen Bündel
     * und zum übergebenen Parent.
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
     * Diese Methode liefert den Wert zu einem Schlüssel.
     *
     * @param key Schlüssel
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
     * Diese Methode holt den Wert zu einem Schlüssel, interpretiert
     * ihn als Message, in die die übergebenen Parameter eingesetzt
     * werden, und gibt die ausgefüllte Message zurück.
     *
     * @param key Schlüssel
     * @param params Parameter
     * @return ausgefüllte Message
     */
    public String get(String key, Object[] params) {
        return MessageFormat.format(get(key), params);
    }

    /**
     * Diese Methode holt den Wert zu einem Schlüssel, interpretiert
     * ihn als Message, in die der übergebene Parameter eingesetzt
     * wird, und gibt die ausgefüllte Message zurück.
     *
     * @param key Schlüssel
     * @param param Parameter
     * @return ausgefüllte Message
     * @see #get(String, Object[])
     */
    public String get(String key, Object param) {
        return get(key, new Object[]{param});
    }

    /**
     * Diese Methode holt den Wert zu einem Schlüssel, interpretiert
     * ihn als Message, in die die übergebene Parameter eingesetzt
     * werden, und gibt die ausgefüllte Message zurück.
     *
     * @param key Schlüssel
     * @param param1 Parameter
     * @param param2 Parameter
     * @return ausgefüllte Message
     * @see #get(String, Object[])
     */
    public String get(String key, Object param1, Object param2) {
        return get(key, new Object[]{param1, param2});
    }

    /**
     * Diese Methode holt den Wert zu einem Schlüssel, interpretiert
     * ihn als Message, in die die übergebene Parameter eingesetzt
     * werden, und gibt die ausgefüllte Message zurück.
     *
     * @param key Schlüssel
     * @param param1 Parameter
     * @param param2 Parameter
     * @param param3 Parameter
     * @return ausgefüllte Message
     * @see #get(String, Object[])
     */
    public String get(String key, Object param1, Object param2, Object param3) {
        return get(key, new Object[]{param1, param2, param3});
    }

    /*
     * Konstanten
     */
    private static final String OCTOPUS_BUNDLE_NAME = "de.tarent.octopus.resource.Octopus";

    /*
     * Variablen
     */
    private final ResourceBundle bundle;
    private final Resources parent;
    private final static Resources instance = new Resources();
}
