/*
 * $Id: Function.java,v 1.5 2007/06/14 14:51:57 dgoema Exp $
 */
package de.tarent.dblayer.sql;

import de.tarent.dblayer.sql.statement.Procedure;

/**
 * Wrapper for a {@link String} instance used to prevent formatting methods from
 * escaping and quoting it.
 *
 * @deprecated use {@link de.tarent.dblayer.sql.clause.RawClause} or {@link Procedure} instead
 * @author Wolfgang Klein
 */
public class Function {
    /**
     * This constructor stores the given {@link String} parameter locally.
     */
    public Function(String function) {
        _function = function;
    }

    /**
     * This override of {@link Object#toString()} returns the {@link String}
     * stored during construction.
     */
    public String toString() {
        return _function;
    }

    /** this is the {@link String} this wrapper class is to wrap */
    private final String _function;
}
