package de.tarent.commons.messages;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
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

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageHelper {
    private static final int STATIC_PUBLIC_MODIFIER = 9;

    private MessageHelper() {
    }

    public static void init() {
        StackTraceElement ste[];
        try {
            throw new Exception();
        } catch (Exception e) {
            ste = e.getStackTrace();
        }
        boolean foundThisClass = false;
        for (int i = 0; i < ste.length; i++) {
            if (foundThisClass) {
                init(ste[i].getClassName());
                break;
            }
            if (ste[i].getClassName().equals(MessageHelper.class.getName())) {
                foundThisClass = true;
            }
        }
    }

    public static void init(Class clazz) {
        init(clazz.getName());
    }

    public static void init(String classname) {
        init(classname, classname.replace('.', '/'));
    }

    public static void init(String classname, String bundlename) {
        try {
            Class clazz = Class.forName(classname);
            String source = "Source path is not available.";
            ResourceBundle bundle = null;

            try {
                bundle = ResourceBundle.getBundle(bundlename);
                source = getResourceName(clazz.getClassLoader(), bundlename, bundle.getLocale());
            } catch (MissingResourceException e) {
                System.err.println(e);
                e.printStackTrace();
                // nothing here, please note that bundle can be null.
            } catch (NullPointerException e) {
                System.err.println(e);
                e.printStackTrace();
                // nothing here, source path not found is default message.
            }

            Field fields[] = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getModifiers() == STATIC_PUBLIC_MODIFIER) {
                    String key = fields[i].getName();
                    String message;
                    if (bundle != null) {
                        try {
                            message = bundle.getString(key);
                        } catch (MissingResourceException e) {
                            message = "No message for key '" + key + "' in resource bundle '" + bundlename + "' found.";
                        }
                    } else {
                        message = "No message for key '" + key + "' found, resource bundle '" + bundlename + "' is missing.";
                    }

                    if (fields[i].getType().equals(String.class)) {
                        fields[i].set(null, message);
                    } else if (fields[i].getType().equals(Message.class)) {
                        fields[i].set(null, new MessageImpl(key, source, message));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getResourceName(ClassLoader loader, String bundlename, Locale locale) {
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append(bundlename.replace('.', '/'));
            if (locale.toString().length() != 0) {
                buffer.append("_").append(locale.toString());
            }
            buffer.append(".properties");

            URL resource = loader.getResource(buffer.toString());
            if (resource == null) {
                throw new NullPointerException("Resource for \"" + buffer + "\" can not be null.");
            }
            return resource.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
