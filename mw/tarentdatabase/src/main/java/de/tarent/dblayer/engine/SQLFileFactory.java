package de.tarent.dblayer.engine;
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
     * @param cacheable This flag determines if the SQLFiles will be marked as cachable.
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
     * @param leafname The leaf-filename of the SQL-template.
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
     * @param cacheable true = only load each file once, false = reload every time
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
