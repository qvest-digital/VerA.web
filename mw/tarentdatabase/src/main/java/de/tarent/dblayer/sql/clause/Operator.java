package de.tarent.dblayer.sql.clause;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.engine.SetDbContextImpl;

/**
 * This {@link Clause} represents an operator connecting two
 * objects.
 */
public class Operator extends SetDbContextImpl implements Clause {
    //
    // protected members
    //
    /**
     * the {@link String} representation of the operator itself
     */
    String operator;
    /**
     * the left side argument of the operator
     */
    Object left = null;
    /**
     * the right side argument of the operator
     */
    Object right = null;

    //
    // constructors
    //

    /**
     * The constructor saves the String representation of the operator
     * itself.
     */
    public Operator(String operator) {
        assert operator != null;
        this.operator = operator;
    }

    //
    // public methods
    //

    /**
     * left side argument of type column name
     */
    public void setLeft(String column) {
        left = column;
    }

    /**
     * right side argument of type column name
     */
    public void setRight(String column) {
        right = column;
    }

    /**
     * left side argument of type literal or {@link Function function call}
     */
    public void setLeft(Object object) {
        left = new LiteralWrapper(object);
    }

    /**
     * right side argument of type literal or {@link Function function call}
     */
    public void setRight(Object object) {
        right = new LiteralWrapper(object);
    }

    //
    // interface {@link Clause}
    //

    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgreSQL DBMS.
     *
     * @return string representation of the clause model
     * @see de.tarent.dblayer.sql.clause.Clause#clauseToString()
     * @deprecated use {@link #clauseToString(DBContext)} instead
     */
    public String clauseToString() {
        return clauseToString(getDBContext());
    }

    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.
     *
     * @param dbContext the db layer context to use for formatting parameters
     * @return string representation of the clause model
     * @see de.tarent.dblayer.sql.clause.Clause#clauseToString(de.tarent.dblayer.engine.DBContext)
     */
    public String clauseToString(DBContext dbContext) {
        setDBContext(dbContext); // for LiteralWrappers
        StringBuffer buffer = new StringBuffer();
        buffer.append(left)
          .append(' ')
          .append(operator)
          .append(' ')
          .append(right);
        return buffer.toString();
    }

    /**
     * Returns an independent clone of this statement.
     * ATTENTION: The value element of the expression will no be copied
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            Operator theClone = (Operator) super.clone();
            return theClone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}
