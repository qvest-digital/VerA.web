package de.tarent.dblayer.sql.clause;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.engine.SetDbContextImpl;

/**
 * This {@link Clause} represents the <code>FROM</code> part of a
 * <code>SELECT</code> or <code>DELETE</code> statement.<br>
 * TODO: A table can be added multiple times to this clause. This
 * is not yet supported sensibly but it should be. Therefore an
 * additional method is required to add a table together with an
 * alias name; this alias name should be enforced to be unique.
 *
 * @author Wolfgang Klein
 */
public class From extends SetDbContextImpl implements Clause {

    //
    // protected members
    //
    /** this list contains the tables of this <code>FROM</code> clause */
    ArrayList _tables = new ArrayList();
    HashMap tableLabels = new HashMap();

    //
    // public constants
    //
    /** the String "<code> FROM </code>" */
	final static public String FROM = " FROM ";

    //
    // public methods
    //
    /** This method adds a table for this <code>FROM</code> clause. */
	public void addTable(String table) {
		_tables.add(table);
	}

	/** This method adds a table for this <code>FROM</code> clause. */
	public void addTable(String table, String label) {
		_tables.add(table);
		tableLabels.put(table, label);
	}

    /** This method returns the number of tables in this <code>FROM</code> clause. */
    public int size() {
	return _tables.size();
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
		StringBuffer sb = new StringBuffer();
		sb.append(FROM);
		for (Iterator it = _tables.iterator();it.hasNext();) {
			String tablename = (String)it.next();
			sb.append(tablename);
			if (tableLabels.get(tablename) != null)
				sb.append(" " + tableLabels.get(tablename).toString());
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

    /**
     * Returns an independent clone of this statement.
     * @see java.lang.Object#clone()
     */
    public Object clone() {
	try {
	    From theClone = (From)super.clone();
	    theClone._tables = (ArrayList)_tables.clone();
	    theClone.tableLabels = (HashMap) tableLabels.clone();
	    return theClone;
	}
	catch(CloneNotSupportedException e) {
		throw new InternalError();
	}
    }
}
