package de.tarent.dblayer.sql;
import java.sql.SQLException;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.sql.statement.ExtPreparedStatement;

/**
 * All statement classes for use in the db layer context have to implement
 * this interface.
 *
 * @author Wolfgang Klein
 */
public interface Statement extends SetDbContext, ParamHolder {
    //
    // public constants
    //
    /**
     * the String "<code>SELECT </code>"
     */
    final static public String SELECT = "SELECT ";
    /**
     * the String "<code>SELECT DISTINCT </code>"
     */
    final static public String SELECTDISTINCT = "SELECT DISTINCT ";
    /**
     * the String "<code>INSERT INTO </code>"
     */
    final static public String INSERT = "INSERT INTO ";
    /**
     * the String "<code>UPDATE </code>"
     */
    final static public String UPDATE = "UPDATE ";
    /**
     * the String "<code>DELETE </code>"
     */
    final static public String DELETE = "DELETE ";
    /**
     * the String "<code>FROM </code>"
     */
    final static public String FROM = "FROM ";

    //
    // public methods
    //

    /**
     * This method creates the {@link DBContext} sensitive {@link String} representation
     * of the modelled SQL {@link Statement}.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgreSQL DBMS.
     */
    public String statementToString() throws SyntaxErrorException;

    /**
     * This method executes the modelled statement within the {@link DBContext}
     * of this {@link Statement}.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise an exception is thrown or a default db layer context is assumed
     * which for now is a PostgreSQL DBMS.
     *
     * @throws IllegalStateException if no DBContext is set.
     * @throws SQLException          if an SQL error occures.
     */
    public Object execute() throws SQLException;

    /**
     * Creates an ExtPreparedStatement of this statement which is already compiled.
     *
     * @param dbContext The database context in which the statenemt exists.
     */
    public ExtPreparedStatement prepare(DBContext dbContext) throws SQLException;

    /**
     * Creates an ExtPreparedStatement of this statement which is already compiled.
     * This method creates the PreparedStatement in the same DBContext as the Statement.
     * Therefore the DBContext of the Select must be set.
     *
     * <p><b>Attention:<b> Setting a DBContext and calling prepare() may cause errors,
     * if the same statement is used in multiple threads.</p>
     */
    public ExtPreparedStatement prepare() throws SQLException;
}
