package de.tarent.dblayer.resource;

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
        if (bundleName == null) {
            bundleName = OCTOPUS_BUNDLE_NAME;
        }
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle(bundleName);
        } catch (MissingResourceException mre) {
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
        if (bundle != null) {
            try {
                return bundle.getString(key);
            } catch (MissingResourceException e) {
            }
        }
        return (parent != null) ? parent.get(key) : '!' + key + '!';
    }

    /**
     * Diese Methode holt den Wert zu einem Schl�ssel, interpretiert
     * ihn als Message, in die die �bergebenen Parameter eingesetzt
     * werden, und gibt die ausgef�llte Message zur�ck.
     *
     * @param key    Schl�ssel
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
     * @param key   Schl�ssel
     * @param param Parameter
     * @return ausgef�llte Message
     * @see #get(String, Object[])
     */
    public String get(String key, Object param) {
        return get(key, new Object[] { param });
    }

    /**
     * Diese Methode holt den Wert zu einem Schl�ssel, interpretiert
     * ihn als Message, in die die �bergebene Parameter eingesetzt
     * werden, und gibt die ausgef�llte Message zur�ck.
     *
     * @param key    Schl�ssel
     * @param param1 Parameter
     * @param param2 Parameter
     * @return ausgef�llte Message
     * @see #get(String, Object[])
     */
    public String get(String key, Object param1, Object param2) {
        return get(key, new Object[] { param1, param2 });
    }

    /**
     * Diese Methode holt den Wert zu einem Schl�ssel, interpretiert
     * ihn als Message, in die die �bergebene Parameter eingesetzt
     * werden, und gibt die ausgef�llte Message zur�ck.
     *
     * @param key    Schl�ssel
     * @param param1 Parameter
     * @param param2 Parameter
     * @param param3 Parameter
     * @return ausgef�llte Message
     * @see #get(String, Object[])
     */
    public String get(String key, Object param1, Object param2, Object param3) {
        return get(key, new Object[] { param1, param2, param3 });
    }

}
