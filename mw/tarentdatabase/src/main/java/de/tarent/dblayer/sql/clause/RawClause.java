package de.tarent.dblayer.sql.clause;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContextImpl;

/**
 * This {@link Clause} represents SQL statement parts that are given
 * in raw SQL text instead of modelled in a structured way.<br>
 * Because of the danger of SQL injection (or simply sloppy written
 * SQL) this class should only be used to support features not (yet)
 * explicitely modelled by the db layer classes.
 */
public class RawClause extends SetDbContextImpl implements Clause {

    //
    // protected members
    //
    /**
     * raw SQL in a {@link String}
     */
    String string;
    /**
     * raw SQL in a {@link StringBuffer}
     */
    StringBuffer buffer;

    //
    // constructors
    //

    /**
     * This constructor accepts the raw SQL from a {@link String} object.
     */
    public RawClause(String string) {
        assert string != null;
        this.buffer = null;
        this.string = string;
    }

    /**
     * This constructor accepts the raw SQL from a {@link StringBuffer} object.
     * The buffer is not fixed yet, it can be manipulated until finally a
     * {@link #clauseToString()} version is called.
     */
    public RawClause(StringBuffer buffer) {
        this.buffer = buffer;
        this.string = null;
    }

    //
    // interface {@link Clause}
    //

    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.
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
        return buffer != null ? buffer.toString() : string;
    }

    //
    // class {@link Object}
    //

    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.
     *
     * @return string representation of the clause model
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return clauseToString(getDBContext());
    }

    /**
     * Returns an independent clone of this statement.
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            RawClause theClone = (RawClause) super.clone();
            if (buffer != null) {
                theClone.buffer = new StringBuffer(buffer.toString());
            }
            return theClone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}
