package de.tarent.dblayer.engine;

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
import java.io.File;

/**
 * The SQLFileFactory creates SQLFiles and sets their options.
 *
 * @author Robert Linden (r.linden@tarent.de)
 */
public class SQLFileFactory {

    public static final boolean CACHE = true;
    public static final boolean NOCACHE = false;

    /**
     * The basepath from which the SQLFiles are loaded.
     */
    private File basepath;

    /**
     * This flag determines if the SQLFiles will be marked as cachable.
     */
    private boolean cacheable;

    /**
     * Create a new SQLFileFactory with default-options.
     */
    public SQLFileFactory() {
        this.basepath = new File(System.getProperty("user.dir"));
        this.cacheable = true;
    }

    /**
     * Create a new SQLFileFactory.
     *
     * @param basepath The basepath from which the SQLFiles are loaded.
     * @param cachable This flag determines if the SQLFiles will be marked as cachable.
     */
    public SQLFileFactory(String basepath, boolean cacheable) {
        if (basepath != null) {
            this.basepath = new File(basepath);
        } else {
            this.basepath = new File(System.getProperty("user.dir"));
        }

        this.cacheable = cacheable;
    }

    /**
     * Get a new SQLFile.
     *
     * @param filename The leaf-filename of the SQL-template.
     * @return An SQLFile with all options set according to this factory.
     */
    public SQLFile newSQLFile(String leafname) {
        SQLFile file = new SQLFile(basepath, leafname, cacheable);
        return file;
    }

    /**
     * Set the basepath from which the SQLFiles are loaded.
     *
     * @param basepath The basepath from which the SQLFiles are loaded.
     */
    public void setBasePath(File basepath) {
        if (basepath != null) {
            this.basepath = basepath;
        } else {
            this.basepath = new File(System.getProperty("user.dir"));
        }
    }

    /**
     * Set the basepath from which the SQLFiles are loaded.
     *
     * @param basepath The basepath from which the SQLFiles are loaded.
     */
    public void setBasePath(String basepath) {
        if (basepath != null) {
            this.basepath = new File(basepath);
        } else {
            this.basepath = new File(System.getProperty("user.dir"));
        }
    }

    /**
     * Get the basepath from which the SQLFiles are loaded.
     *
     * @return File-Object describing the basepath from which the SQLFiles are loaded.
     */
    public File getBasePath() {
        return basepath;
    }

    /**
     * Set the flag that determines if the SQLFiles will be marked as cachable.
     *
     * @param cachable true = only load each file once, false = reload every time
     */
    public void setCachable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    /**
     * Get the flag that determines if the SQLFiles will be marked as cachable.
     *
     * @return true = only load each file once, false = reload every time
     */
    public boolean getCachable() {
        return cacheable;
    }

}
