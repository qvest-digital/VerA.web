package de.tarent.dblayer.persistence;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.engine.*;
import de.tarent.dblayer.sql.statement.*;

/**
 * Implementations of this Interface should contain information
 * for the mapping of a business object to the underlaying database system.
 *
 * <p>How te mapping is configured is an implementation detail. The interface provides
 * base Statements for select, insert, update and delete together with the knowledge of the objects fileds.</p>
 */
public interface DBMapping {

    public static final String STMT_SELECT_ONE = "stmtSelectOne";
    public static final String STMT_SELECT_ALL = "stmtSelectAll";

    /**
     * Returns the Query for the supplied ID, containing all possible fields for this query.
     *
     * <p>Be carefull, this Select instance may be the same for multiple running threads.
     * If you need to modify it, you should create a clone first.</p>
     */
    public Select getQuery(String statementID);

    /**
     * Retuns the Fields, contained in the query for the supplied ID
     */
    public Field[] getQueryFields(String statementID);

    /**
     * Returns an insert statement for this business object. The Fields of the
     * insert should be set over the ParamValue mechanism.
     *
     * <p>Be carefull, this Select instance may be the same for multiple running threads.
     * If you need to modify it, you should create a clone first.</p>
     */
    public Insert getInsert();

    /**
     * Returns an insert update for this business object. The Fields of the
     * insert should be set over the ParamValue mechanism.
     *
     * <p>Be carefull, this Select instance may be the same for multiple running threads.
     * If you need to modify it, you should create a clone first.</p>
     */
    public Update getUpdate();

    /**
     * Returns an delete update for this business object. The Fields of the
     * insert should be set over the ParamValue mechanism.
     *
     * <p>Be carefull, this Select instance may be the same for multiple running threads.
     * If you need to modify it, you should create a clone first.</p>
     */
    public Delete getDelete();

    /**
     * Returns the original (full) property name of a property.
     * If the propertyName was not to long, we return the supplied propertyName.
     *
     * @param propertyName the property name as contained in the result set.
     */
    public String getOriginalPropertyName(String propertyName);

    /**
     * Returns the database column name for the propery of the business object.
     */
    public String getColumnNameByProperty(String propertyName);

    /**
     * Returns the primaryKeys field.
     * If this mapping has more than one pk, the first one is returned.
     */
    public Field getPkField();
}
