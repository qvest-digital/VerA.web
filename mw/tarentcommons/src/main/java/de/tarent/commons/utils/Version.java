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

/**
 * This class provides simple access to version and package information.
 * There are two ways to use this class:
 *
 * 1. To use Version in your project copy this class to the main directory of your
 * project and change the package declaration accordingly.
 * 2.Keep the Version- class in de.tarent.commons.util and call the init-Method
 * passing an object from your package:
 * <pre>
 *
 * package de.tarent.mypackage;
 * class MyMainClass {
 *  public MyMainClass() {
 * 	  de.tarent.commons.util.Version.init(this);
 *  }
 * }
 * </pre>
 *
 * Information like version-numbers, titles and vendors are read from the .jars manifest
 * file, so this information appears only, if compiled als Jar-File with a proper setup
 * build system (Maven works).
 *
 * @author Martin Ley
 */
public class Version {

    private static String name;

    private static String specificationTitle;

    private static String specificationVendor;

    private static String specificationVersion;

    private static String implementationTitle;

    private static String implementationVendor;

    private static String implementationVersion;

    /**
     * initializes Version with package information from itself
     */
    static {
        gatherPackageInfo(Version.class.getPackage());
    }

    /**
     * Initialize Version with Object o. All package information is retrieved from the passed
     * object.
     *
     * @param o object to retrieve package information from
     */
    public static void init(Object o) {
        gatherPackageInfo(o.getClass().getPackage());
    }

    /**
     * Retrieves package information from passed Package p
     *
     * If some version, title or vendor information is not available, the corresponding
     * String is set to ""
     *
     * @param p
     */
    private static void gatherPackageInfo(Package p) {
        // name of this package.
        name = p.getName();

        // title of the specification of this package.
        String s = p.getSpecificationTitle();
        specificationTitle = s != null ? s : "";

        // version of the specification of this package.
        s = p.getSpecificationVersion();
        specificationVersion = s != null ? s : "";

        // vendor of the specification of this package.
        s = p.getSpecificationVendor();
        specificationVendor = s != null ? s : "";

        // title of the implementation of this package.
        s = p.getImplementationTitle();
        implementationTitle = s != null ? s : "";

        // version of the implementation of this package.
        s = p.getImplementationVersion();
        implementationVersion = s != null ? s : "";

        // vendor of the implementation of this package.
        s = p.getImplementationVendor();
        implementationVendor = s != null ? s : "";
    }

    public static String getName() {
        return name;
    }

    public static String getVersion() {
        return implementationTitle + " " + implementationVersion;
    }

    public static String getSpecification() {
        return "Package " + name + " " + specificationTitle + " "
          + specificationVersion + " " + specificationVendor;
    }

    public static String getImplementation() {
        return "Package " + name + " " + implementationTitle + " "
          + implementationVersion + " " + implementationVendor;
    }

    /**
     * Not implemated yet.
     */
    public static String getCopyright() {
        return "not available";
    }

    /**
     * Not implemated yet.
     */
    public static String getExtModules() {
        return "not available";
    }
}
