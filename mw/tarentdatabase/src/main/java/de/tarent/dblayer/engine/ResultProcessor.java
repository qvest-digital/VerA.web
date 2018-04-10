package de.tarent.dblayer.engine;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is an interface for iteration over a result set using the visitor pattern.
 * With this method, the error handling is done by the surrounding framework.
 */
public interface ResultProcessor {

    /**
     * This method will be called by the framework code for each row in the result set.
     *
     * @param rownum the current row number, <b>starting at 0<b>
     * @param rs the result set to process, positioned at a new row.
     */
    public void process(ResultSet rs) throws SQLException;

}
