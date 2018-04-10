/*
 * $Id: SetDbContext.java,v 1.2 2007/06/14 14:51:57 dgoema Exp $
 *
 * Created on 18.04.2006
 */
package de.tarent.dblayer.engine;

/**
 * This interface is to be implemented by all classes that allow a
 * {@link DBContext} to be set using a fitting setter method. It is
 * used for dependency injection.
 *
 * @author Michael Klink
 */
public interface SetDbContext {
    /**
     * This method injects the database execution context.
     *
     * @param dbc database execution context
     */
    public void setDBContext(DBContext dbc);
}
