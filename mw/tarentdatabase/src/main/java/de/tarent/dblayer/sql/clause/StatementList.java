package de.tarent.dblayer.sql.clause;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.engine.SetDbContextImpl;
import de.tarent.dblayer.sql.SQL;

/**
 * This class represents literal enumeration parts of a SQL statement.
 *
 * @author Christoph Jerolimov
 */
public final class StatementList extends SetDbContextImpl implements Clause {

    //
    // protected members
    //
    /** separator characters for list entries */
    private final static String SEPARATOR = ", ";
    /** literal enumeration */
    Collection _list;

	//
    // constructors
    //
    /** This constructor accepts a {@link Collection} as enumeration. */
	public StatementList(Collection list) {
		_list = list;
	}

    /** This constructor accepts a {@link Iterator} as enumeration. */
	public StatementList(Iterator it) {
		_list = new ArrayList();
		while (it.hasNext())
			_list.add(it.next());
	}

    public boolean isEmpty() {
	return _list == null || _list.size() == 0;
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
     * a PostgresQL DBMS.
     *
     * @return string representation of the clause model
     * @see de.tarent.dblayer.sql.clause.Clause#clauseToString()
     * @deprecated use {@link #clauseToString(DBContext)} instead
     */
    final public String clauseToString() {
	return clauseToString(getDBContext());
    }

    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.
     *
     * @param dbContext the db layer context to use for formatting parameters
     * @return string representation of the clause model
     * @see de.tarent.dblayer.sql.clause.Clause#clauseToString(DBContext)
     */
    public String clauseToString(DBContext dbContext) {
	StringBuffer sb = new StringBuffer();
	statementListToString(dbContext, this, sb);
	return sb.toString();
    }

    //
    // class {@link Object}
    //
    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.
     *
     * @return string representation of the clause model
     * @see java.lang.Object#toString()
     */
	public String toString() {
		return clauseToString(getDBContext());
    }

    //
    // helper methods
    //
    /**
     * This method generates a string representation of a {@link StatementList}
     * for use in SQL statements.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.
     *
     * @deprecated use {@link #statementListToString(DBContext, StatementList, StringBuffer)} instead
     * @return string representation of the clause model
     */
    public final static StringBuffer statementListToString(StatementList sl, StringBuffer sb) {
	return statementListToString(sl.getDBContext(), sl, sb);
    }

    /**
     * This method generates a string representation of a {@link StatementList}
     * for use in SQL statements.
     *
     * @param dbContext the db layer context to use for formatting parameters
     * @return string representation of the clause model
     */
    public final static StringBuffer statementListToString(DBContext dbContext, StatementList sl, StringBuffer sb) {
	sb.append("(");
	if (sl._list != null) {
	    Iterator it = sl._list.iterator();
	    while (it.hasNext()) {
		sb.append(SQL.format(dbContext, it.next()));
		if (it.hasNext())
		    sb.append(SEPARATOR);
	    }
	}
	sb.append(")");
		return sb;
	}

    /**
     * Returns an independent clone of this statement.
     * @see java.lang.Object#clone()
     */
    public Object clone() {
	try {
	    StatementList theClone = (StatementList)super.clone();
	    theClone._list = new ArrayList(_list);
	    return theClone;
	}
	catch(CloneNotSupportedException e) {
		throw new InternalError();
	}
    }

}
