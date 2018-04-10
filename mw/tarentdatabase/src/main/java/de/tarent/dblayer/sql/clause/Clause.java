package de.tarent.dblayer.sql.clause;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;

/**
 * Base interface for all clauses.
 *
 * @author Wolfgang Klein
 */
public interface Clause extends SetDbContext, Cloneable {
    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.
     *
     * @deprecated use {@link #clauseToString(DBContext)} instead
     * @return string representation of the clause model
     */
	public String clauseToString();

    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.<br>
     * This method MAY as a side effect change the {@link DBContext} of this
     * {@link Clause} to the given one.<br>
     * TODO: This method should be able to throw qualified exceptions
     *
     * @param dbContext the db layer context to use for formatting parameters
     * @return string representation of the clause model
     */
    public String clauseToString(DBContext dbContext);

    /**
     * Returns an independent clone of this close
     * @see java.lang.Object#clone()
     */
    public Object clone();
}
