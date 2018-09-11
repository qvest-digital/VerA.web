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

import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * <p>This class provides convenient methods for setting environment-variables like PATH(Windows)/LD_LIBRARY_PATH(UNIX) or
 * CLASSPATH</p>
 *
 * <p><b>Note that the init()-method which makes the variables writable is based on a assumption which is not part of the
 * Java-Standard and therefore is no
 * guarantee that it works with all Java-Environments. It has been tested with official Sun-JREs 1.4 and 1.5 on Windows and
 * Linux</b></p>
 *
 * <p>A better solution is maybe a custom ClassLoader which is loaded at application start by setting -Dbootstrap
 * .classloader=my-class-loader at JVM-startup.</p>
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 */
public abstract class EnvironmentVariableTool {
    /**
     * The java-property-name for the LIBRARY_PATH-Environment-Variable
     */
    public final static String LIBRARY_PATH = "java.library.path";
    /**
     * The java-property-name for the CLASSPATH-Environment-Variable
     */
    public final static String CLASSPATH = "java.class.path";

    private final static Logger logger = Logger.getLogger(EnvironmentVariableTool.class.getName());

    // This class cannot be instantiated
    private EnvironmentVariableTool() {
    }

    private static void init() {
        // Workaround needed for beeing able to set java.library.path
        // TODO replace this hack by better method

        Class clazz = ClassLoader.class;
        try {
            Field field = clazz.getDeclaredField("sys_paths");
            boolean accessible = field.isAccessible();
            if (!accessible) {
                field.setAccessible(true);
            }

            // Reset it to null so that whenever "System.loadLibrary" is called, it will be reconstructed with the changed value.
            field.set(clazz, null);
        } catch (Exception pExcp) {
            logger.warning("Setting sys_paths accessible failed");
        }
    }

    private static boolean addEntryToVariable(String pEntry, String pVariable) {
        init();

        // Check if it is already in there
        if (System.getProperty(pVariable) != null && System.getProperty(pVariable).indexOf(pEntry) == -1) {
            // Add entry
            System.setProperty(pVariable, System.getProperty(pVariable) + System.getProperty("path.separator") + pEntry);
            return false;
        } else {
            return true;
        }
    }

    private static boolean addEntriesToVariable(String[] pEntries, String pVariable) {
        boolean alreadyPresent = false;

        if (pEntries != null) {
            for (int i = 0; i < pEntries.length; i++) {
                if (addEntryToVariable(pEntries[i], pVariable)) {
                    alreadyPresent = true;
                }
            }
        }

        return alreadyPresent;
    }

    /**
     * Adds an array of entries to the CLASSPATH-Environment-Variable
     *
     * @param pEntries an array of entries to be added to the CLASSPATH-Environment-Variable
     * @return if one of the entries already existed in the CLASSPATH-Environment-Variable
     */

    public static boolean addEntriesToClassPath(String[] pEntries) {
        return addEntriesToVariable(pEntries, CLASSPATH);
    }

    /**
     * Adds an entry to the CLASSPATH-Environment-Variable
     *
     * @param pEntry the entry to be added to the CLASSPATH-Environment-Variable
     * @return if the entry already existed in the CLASSPATH-Environment-Variable
     */

    public static boolean addEntryToClassPath(String pEntry) {
        return addEntryToVariable(pEntry, CLASSPATH);
    }

    /**
     * Adds an array of entries to the PATH(Windows)- or LD_LIBRARY_PATH(Unix)-Environment-Variable
     *
     * @param pEntries an array of entries to be added to the PATH(Windows)- or LD_LIBRARY_PATH(Unix)-Environment-Variable
     * @return if one of the entries already existed in the PATH(Windows)- or LD_LIBRARY_PATH(Unix)-Environment-Variable
     */

    public static boolean addEntriesToLibraryPath(String[] pEntries) {
        return addEntriesToVariable(pEntries, LIBRARY_PATH);
    }

    /**
     * Adds an entry to the PATH(Windows)- or LD_LIBRARY_PATH(Unix)-Environment-Variable
     *
     * @param pEntry the entry to be added to the PATH(Windows)- or LD_LIBRARY_PATH(Unix)-Environment-Variable
     * @return if the entry already existed in the PATH(Windows)- or LD_LIBRARY_PATH(Unix)-Environment-Variable
     */

    public static boolean addEntryToLibraryPath(String pEntry) {
        return addEntryToVariable(pEntry, LIBRARY_PATH);
    }
}
