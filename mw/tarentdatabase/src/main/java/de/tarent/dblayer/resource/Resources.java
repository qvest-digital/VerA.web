package de.tarent.dblayer.resource;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Diese Klasse liefert Resourcen.
 *
 * @author mikel
 * @author Sebastian Mancke, tarent GmbH
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
