/*
 * tarent-database,
 * jdbc database library
 * Copyright (c) 2005-2006 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-database'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: InsertUpdate.java,v 1.9 2007/06/14 14:51:57 dgoema Exp $
 */
package de.tarent.dblayer.sql.statement;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.InsertKeys;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.clause.Expr;

/**
 * This helper class allows to use a single class for both SQL <code>INSERTs</code>
 * and <code>UPDATEs</code>. Only when you want to execute the statement you have
 * to decide what you want to do.
 *
 * @author Wolfgang Klein
 */
public class InsertUpdate extends AbstractStatement {
    //
    // public methods
    //
    /**
     * This method sets the value for the given column of the data record to insert
     * or update.<br>
     * Actually this method only appends the pair (column, value) to a list and
     * doesn't check whether there already is a pair for the same column in the list.
     * Your code will have to keep an eye on this itself.
     *
     * @param column name of the column of the value; only the local part is used
     * @param value value to insert; this value will be formatted fitting its type
     *  and the {@link DBContext} this {@link InsertUpdate} is operated in.
     * @return this {@link InsertUpdate} instance
     */
	public InsertUpdate add(String column, Object value) {
		_values.put(column, value);
		return this;
	}

    /** This method sets the name of the table to insert into or update. */
	public InsertUpdate table(String table) {
		_table = table;
		return this;
	}

    /**
     * This method creates an {@link Insert} {@link Statement} and feeds it
     * with the data already put into this {@link InsertUpdate}.
     *
     * @return an {@link Insert} reflecting this {@link InsertUpdate}
     * @throws SyntaxErrorException
     */
	public Insert insert() throws SyntaxErrorException {
		Insert insert = SQL.Insert(getDBContext()).table(_table);

		Iterator k = _values.keySet().iterator();
		Iterator v = _values.values().iterator();
		while (k.hasNext()) {
			insert.insert((String) k.next(), v.next());
		}
		return insert;
	}

    /**
     * This method creates an {@link Insert} {@link Statement} and feeds it
     * with the data already put into this {@link InsertUpdate}.
     *
     * @return an {@link Insert} reflecting this {@link InsertUpdate}
     * @throws SyntaxErrorException
     */
	public Insert insert(DBContext dbx) throws SyntaxErrorException {
		Insert insert = SQL.Insert(dbx).table(_table);

		Iterator k = _values.keySet().iterator();
		Iterator v = _values.values().iterator();
		while (k.hasNext()) {
			insert.insert((String) k.next(), v.next());
		}
		return insert;
	}

	/**
     * This method creates an {@link Update} {@link Statement} and feeds it
     * with the data already put into this {@link InsertUpdate}.
     *
     * @return an {@link Update} reflecting this {@link InsertUpdate}
     */
	public Update update() {
		Update update = SQL.Update(getDBContext()).table(_table);

		Iterator k = _values.keySet().iterator();
		Iterator v = _values.values().iterator();
		while (k.hasNext()) {
			update.update((String) k.next(), v.next());
		}
		return update;
	}

	/**
     * This method creates an {@link Update} {@link Statement} and feeds it
     * with the data already put into this {@link InsertUpdate}.
     *
     * @return an {@link Update} reflecting this {@link InsertUpdate}
     */
	public Update update(DBContext dbx) {
		Update update = SQL.Update(dbx).table(_table);

		Iterator k = _values.keySet().iterator();
		Iterator v = _values.values().iterator();
		while (k.hasNext()) {
			update.update((String) k.next(), v.next());
		}
		return update;
	}

    /**
     * This method executes the modelled <code>INSERT</code> statement within the
     * db layer pool with the given name and returns a Map with Key "updateCount" and value
     * as the Number of affected rows or the Columns with autogenerated Keys as Keys
     * and the new Key as Value
     *
     * @param pool the connection pool in which to operate.
     */
    public int executeInsert(String pool) throws SQLException {
        if (_values.size() > 0) {
            Insert insert = insert();
            return DB.insert(pool, insert);
        }
        return 0;
    }

    /**
     * This method executes the modelled <code>INSERT</code> statement within the
     * db layer pool with the given name and returns a Map with Key "updateCount" and value
     * as the Number of affected rows or the Columns with autogenerated Keys as Keys
     * and the new Key as Value
     *
     * @param dbx the DBContexst on which to operate.
     */
    public int executeInsert(DBContext dbx) throws SQLException {
        if (_values.size() > 0) {
            Insert insert = insert(dbx);
            return DB.insert(dbx, insert);
        }
        return 0;
    }

    /**
     * This method executes the modelled <code>INSERT</code> statement within the
     * db layer pool with the given name and returns a Map with Key "updateCount" and value
     * as the Number of affected rows or the Columns with autogenerated Keys as Keys
     * and the new Key as Value
     *
     * @param dbx the DBContexst on which to operate.
     */
    public InsertKeys executeInsertKeys(DBContext dbx) throws SQLException {
        if (_values.size() > 0) {
            Insert insert = insert(dbx);
            return DB.insertKeys(dbx, insert);
        }
        return InsertKeys.EMPTY_INSTANCE;
    }

    /**
     * This method executes the modelled <code>UPDATE</code> statement within the
     * db layer pool with the given name and returns the number of updated rows.
     * If the given column name is not <code>null</code> the update is executed
     * only on those data records having the given value in the given column.
     *
     * @param pool the connection pool in which to operate.
     * @param idColumn name of the column to check for the given value before update
     * @param idValue value to check the given column for before update
     */
    public void executeUpdate(String pool, String idColumn, Integer idValue) throws SQLException {
        if (_values.size() > 0) {
            Update update = update();
            if (idColumn != null)
                update.where(Expr.equal(idColumn, idValue));
            DB.update(pool, update);
        }
    }

    /**
     * This method executes the modelled <code>UPDATE</code> statement within the
     * db layer pool with the given name and returns the number of updated rows.
     * If the given column name is not <code>null</code> the update is executed
     * only on those data records having the given value in the given column.
     *
     * @param dbx the DBContext on which to operate
     * @param idColumn name of the column to check for the given value before update
     * @param idValue value to check the given column for before update
     */
    public void executeUpdate(DBContext dbx, String idColumn, Integer idValue) throws SQLException {
        if (_values.size() > 0) {
            Update update = update();
            if (idColumn != null)
                update.where(Expr.equal(idColumn, idValue));
            DB.update(dbx, update);
        }
    }

    //
    // interface {@link Statement}
    //
    /**
     * This method executes the modelled <code>INSERT</code> statement within the
     * {@link DBContext} of this {@link InsertUpdate} instance.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.<br>
     * This method actually calls {@link #executeInsert(String)} using the pool name
     * of the {@link DBContext}.
     *
     * @return the number of inserted rows
     * @see Statement#execute()
     */
	public Object execute() throws SQLException {
        return new Integer(executeInsert(getDBContext()));
    }

    /**
     * This method executes the modelled <code>INSERT</code> statement within the
     * {@link DBContext} of this {@link InsertUpdate} instance.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.<br>
     * This method actually calls {@link #executeInsert(String)} using the pool name
     * of the {@link DBContext}.
     *
     * @return the number of inserted rows
     * @see Statement#execute()
     */
	public Object execute(DBContext dbx) throws SQLException {
        return new Integer(executeInsert(dbx));
    }

	/**
     * This method generally creates the {@link DBContext} sensitive {@link String}
     * representation of the modelled SQL {@link Statement}. In this case though
     * it just returns <code>null</code> as it is not clear which kind of
     * statement to use here.
     *
     * @see Statement#statementToString()
     */
	public String statementToString() throws SyntaxErrorException {
		return null;
	}

    //
    // protected member variables
    //
    /** the name of the table to insert into or update */
    private String _table;
    /** mapping column name ({@link String}) to value object representing the data to insert or update */
    private final Map _values = new HashMap();
}
