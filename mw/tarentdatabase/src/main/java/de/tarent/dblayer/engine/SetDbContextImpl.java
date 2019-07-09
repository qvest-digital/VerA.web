package de.tarent.dblayer.engine;
import de.tarent.dblayer.sql.SQL;

/**
 * This class is a minimal {@link SetDbContext} implementation. It contains
 * public getter and setter methods and a private member variable to access
 * the db layer context.
 *
 * @author Michael Klink
 */
public class SetDbContextImpl implements SetDbContext {
    //
    // getter and setter methods
    //

    /**
     * This method returns the database execution context. A <code>null</code>
     * value must be interpreted as a sensible default environment.
     *
     * @return database execution context
     */
    public DBContext getDBContext() {
        return dbContext;
    }

    /**
     * This method injects the database execution context.
     *
     * @param dbc database execution context
     * @see de.tarent.dblayer.engine.SetDbContext#setDBContext(de.tarent.dblayer.engine.DBContext)
     */
    public void setDBContext(DBContext dbc) {
        this.dbContext = dbc;
    }

    //
    // protected inner classes
    //

    /**
     * This helper class allows to easily format a literal just in time.
     */
    protected class LiteralWrapper {
        /**
         * The constructor stores the given literal locally.
         */
        public LiteralWrapper(Object literal) {
            this.literal = literal;
        }

        /**
         * This method returns the formatted literal.
         */
        public String toString() {
            return SQL.format(getDBContext(), literal);
        }

        /**
         * the literal itself
         */
        final Object literal;
    }

    //
    // member variables
    //
    /**
     * database execution context
     */
    private DBContext dbContext = null;
}
