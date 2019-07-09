package de.tarent.dblayer.engine;
import java.io.File;

/**
 * An SQLFile is a File with some additional options related to its role as a
 * cacheable sql-template.
 *
 * @author Robert Linden (r.linden@tarent.de)
 */
public class SQLFile extends File {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3403719730441441449L;

    /**
     * This flag determines if the SQLFile is cachable.
     */
    private boolean cacheable;

    /**
     * Create a new SQLFile.
     * For convenience you should use the {@link SQLFileFactory}.
     *
     * @param basepath  The path to the file.
     * @param leafname  The name of the file.
     * @param cacheable Wether this sql-script should be cached (true)
     *                  or reloaded each time (false).
     */
    public SQLFile(File basepath, String leafname, boolean cacheable) {
        super(basepath, leafname + ".sql");
        this.cacheable = cacheable;
    }

    /**
     * Returns the cacheable-flag.
     *
     * @return true = this file should be cached, false = do not cache
     */
    public boolean isCacheable() {
        return cacheable;
    }
}
