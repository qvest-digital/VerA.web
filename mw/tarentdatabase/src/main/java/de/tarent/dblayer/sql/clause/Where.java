package de.tarent.dblayer.sql.clause;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.engine.SetDbContextImpl;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.ParamHolder;
import java.util.List;
import de.tarent.dblayer.sql.ParamValue;
import de.tarent.dblayer.sql.ParamHolder;

/**
 * This {@link Clause} represents the <code>WHERE</code> part of a
 * <code>SELECT</code> or <code>UPDATE</code> statement.
 *
 * @author Wolfgang Klein
 */
public class Where extends SetDbContextImpl implements Clause, ParamHolder {
    //
    // public constants
    //
    /** the String "<code> WHERE </code>" */
	final static public String WHERE = " WHERE ";
    /** the String "<code> OR </code>" */
	final static public String OR = " OR ";
    /** the String "<code> AND </code>" */
	final static public String AND = " AND ";
    /** the String "<code> NOT </code>" */
	final static public String NOT = " NOT ";
    /** the String "<code> NOT OR </code>" */
	final static public String NOTOR= " NOT OR ";
    /** the String "<code> NOT AND </code>" */
	final static public String NOTAND = " NOT AND ";

    //
    // protected members
    //
    /** relation of column and value */
    String _relation;
    /** column in relation to the value; non null iff column value relation is used */
    String _column;
    /** column in relation to the first column */
    String _secondColumn;
    /** value in relation to the column */
    Object _value;

    /** left side parameter of the expression */
    Clause _left;
    /** right side parameter of the expression */
    Clause _right;
    /** expression connecting the parameters */
    String _expression;

    //
    // constructors
    //
    /**
     * This constructor accepts a (non null) column name, a value and a relation.
     * The value is later formatted.
     */
    public Where(String column, Object value, String relation, boolean relateColumns) {
	_column = column;
	_relation = relation;
	_left = null;
	_right = null;
	_expression = null;

	if (relateColumns)
		_secondColumn = value.toString();
	else
		_value = value;

    }

	public Where(String column, Object value, String relation) {
	if (column == null)
	    throw new IllegalArgumentException("Column value relations require a non null column name.");
		_column = column;
		_value = value;
		_relation = relation;
	_left = null;
	_right = null;
	_expression = null;
	}

    /** This constructor accepts two {@link Clause Clauses} and an expression. */
	public Where(Clause left, Clause right, String expression) {
	_column = null;
	_value = null;
	_relation = null;
		_left = left;
		_right = right;
		_expression = expression;
	}

	/** This constructor accepts one {@link Clause Clause} and an expression. */
	public Where(Clause clause, String expression) {
	_column = null;
	_value = null;
	_relation = null;
		_left = null;
		_right = clause;
		_expression = expression;
	}

    /**
     * {@see ParamHolder#getParams(List)}
     */
    public void getParams(List paramList) {
	addObjectToParamList(paramList, _value);
	addObjectToParamList(paramList, _left);
	addObjectToParamList(paramList, _right);
    }

    /**
     * Helper method for adding Objects which may be ParmValues or contain ParmValues to the paramList.
     */
    protected void addObjectToParamList(List paramList, Object object) {
	if (object instanceof ParamValue)
	    paramList.add(object);
	else if (object instanceof ParamHolder)
	    ((ParamHolder)object).getParams(paramList);
    }

    //
    // public static factory methods
    //
    /** This method creates a single argument <code>OR</code> {@link Clause}. */
	static public Where or(Where clause) {
		return new Where(clause, null, OR);
	}

    /** This method creates an <code>OR</code> {@link Clause}. */
	static public Where or(Clause left, Clause right) {
		return new Where(left, right, OR);
	}

    /** This method creates an <code>AND</code> {@link Clause}. */
	static public Where and(Clause left, Clause right) {
		return new Where(left, right, AND);
	}

    /** This method creates a <code>NOT OR</code> {@link Clause}. */
	static public Where notOr(Clause left, Clause right) {
		return new Where(left, right, NOTOR);
	}

    /** This method creates a <code>NOT AND</code> {@link Clause}. */
	static public Where notAnd(Clause left, Clause right) {
		return new Where(left, right, NOTAND);
	}

    /** This method creates a {@link WhereList} {@link Clause}. */
	static public WhereList list() {
		return new WhereList();
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
		clauseToString(this, sb, true, dbContext);
		return sb.toString();
	}

    //
    // helper methods
    //
    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.
     *
     * @deprecated use {@link #clauseToString(Clause, StringBuffer, boolean, DBContext)} instead
     */
	public void clauseToString(Clause where, StringBuffer sb, boolean parent) {
	clauseToString(where, sb, parent, getDBContext());
    }

    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.
     */
    public static void clauseToString(Clause clause, StringBuffer sb, boolean parent, DBContext context) {
		if (clause instanceof Where) {
			Where where = (Where)clause;
			if (where._column == null) {
		if (!parent)
		    sb.append('(');
		if (where._left != null)
		    clauseToString(where._left, sb, false, context);
		if (where._expression != null)
		    sb.append(where._expression);
		if (where._right != null)
		    clauseToString(where._right, sb, false, context);
		if (!parent)
		    sb.append(')');
	    } else if (where._relation != null) {
				sb.append(where._column);
				sb.append(where._relation);
				//IN relation needs brackets when value does not contains an inner statement
				if ((Expr.IN.trim().equals(where._relation.toUpperCase().trim()) || Expr.NOTIN.trim().equals(where._relation.toUpperCase().trim())) && where._value != null && !(where._value instanceof Clause))
				{
					sb.append('(');
					sb.append(SQL.format(context, where._value));
					sb.append(')');
				}
				else if (where._value != null)
				{
					sb.append(SQL.format(context, where._value));
				}
				else if (where._value == null && where._secondColumn != null)
				{
					sb.append(where._secondColumn);
				}
			}
		} else {
			sb.append(clause.clauseToString(context));
		}
	}

    /**
     * Returns an independent clone of this statement.
     * ATTENTION: The value element of the expression will no be copied
     * @see java.lang.Object#clone()
     */
    public Object clone() {
	try {
	    Where theClone = (Where)super.clone();
	    if (_left != null)
		theClone._left = (Clause)_left.clone();
	    if (_right != null)
		theClone._right = (Clause)_right.clone();
	    if (_value instanceof ParamValue)
		theClone._value = ((ParamValue)_value).clone();
	    return theClone;
	}
	catch(CloneNotSupportedException e) {
		throw new InternalError();
	}
    }

}
