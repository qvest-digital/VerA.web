package de.tarent.dblayer.sql;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import org.apache.commons.logging.Log;

import de.tarent.commons.logging.LogFactory;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.clause.Function;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.InsertUpdate;
import de.tarent.dblayer.sql.statement.Procedure;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Sequence;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.mssql.MSSQLFormat;
import de.tarent.dblayer.oracle.OracleSelect;
import de.tarent.dblayer.oracle.OracleFormat;
import de.tarent.dblayer.postgres.PostgresProcedure;
import de.tarent.dblayer.engine.Pool;
import de.tarent.dblayer.mssql.MSSQLSelect;

/**
 * This class serves as a factory for SQL statements and parts thereof.
 *
 * @author kleinw
 */
public class SQL {
	//
    // non-public members
    //
    /** logger of this class */
    final static Log logger = LogFactory.getLog(SQL.class);
    //
    // public static factory methods
    //
    /**
     * This method returns a non-distinct {@link Select} {@link Statement}
     * fitting the given execution context.
     *
     * @param context db layer execution context.
     */
    static public Select Select(DBContext context) {
	Select statement = null;
	if (isOracle(context)) {
	    statement = new OracleSelect(false);
	} else if(isMSSQL(context)) {
	    statement = new MSSQLSelect(false);
	} else
	    statement = new Select(false);
	statement.setDBContext(context);
	return statement;
	}

    /**
     * This method returns a non-distinct {@link Select} {@link Statement}
     * fitting the default execution context which currently is a PostgresQL
     * DBMS. You had better use the method based on the db execution context.
     *
     * @deprecated use {@link #Select(DBContext)} instead.
     */
    static public Select Select() {
		return new Select(false);
	}

    /**
     * This method returns a distinct {@link Select} {@link Statement}
     * fitting the given execution context.
     *
     * @param context db layer execution context.
     */
    static public Select SelectDistinct(DBContext context) {
	Select statement = null;
	if (isOracle(context)) {
	    statement = new OracleSelect(true);
	} else if(isMSSQL(context)) {
	    statement = new MSSQLSelect(true);
	}
	else
	    statement = new Select(true);
	statement.setDBContext(context);
	return statement;
	}

    /**
     * This method returns a distinct {@link Select} {@link Statement}
     * fitting the default execution context which currently is a PostgresQL
     * DBMS. You had better use the method based on the db execution context.
     *
     * @deprecated use {@link #SelectDistinct(DBContext)} instead.
     */
    static public Select SelectDistinct() {
		return new Select(true);
	}

    /**
     * This method returns an {@link Insert} {@link Statement}
     * fitting the given execution context.
     *
     * @param context db layer execution context.
     */
    static public Insert Insert(DBContext context) {
	// no db-dependent implementation switches at the moment
		Insert statement = new Insert();
	statement.setDBContext(context);
	return statement;
	}

    /**
     * This method returns an {@link Insert} {@link Statement}
     * fitting the default execution context which currently is a PostgresQL
     * DBMS. You had better use the method based on the db execution context.
     *
     * @deprecated use {@link #Insert(DBContext)} instead.
     */
    static public Insert Insert() {
		return new Insert();
	}

    /**
     * This method returns an {@link Update} {@link Statement}
     * fitting the given execution context.
     *
     * @param context db layer execution context.
     */
    static public Update Update(DBContext context) {
	// no db-dependent implementation swichtes at the moment
		Update statement = new Update();
	statement.setDBContext(context);
	return statement;
	}

    /**
     * This method returns an {@link Update} {@link Statement}
     * fitting the default execution context which currently is a PostgresQL
     * DBMS. You had better use the method based on the db execution context.
     *
     * @deprecated use {@link #Update(DBContext)} instead.
     */
	static public Update Update() {
		return new Update();
	}

    /**
     * This method returns a {@link Delete} {@link Statement}
     * fitting the given execution context.
     *
     * @param context db layer execution context.
     */
    static public Delete Delete(DBContext context) {
	// no db-dependent implementation swichtes at the moment
		Delete statement = new Delete();
	statement.setDBContext(context);
	return statement;
	}

    /**
     * This method returns a {@link Delete} {@link Statement}
     * fitting the default execution context which currently is a PostgresQL
     * DBMS. You had better use the method based on the db execution context.
     *
     * @deprecated use {@link #Delete(DBContext)} instead.
     */
	static public Delete Delete() {
		return new Delete();
	}

    /**
     * This method returns an {@link InsertUpdate} {@link Statement}
     * fitting the given execution context.
     *
     * @param context db layer execution context.
     */
    static public InsertUpdate InsertUpdate(DBContext context) {
	// no db-dependent implementation swichtes at the moment
		InsertUpdate statement = new InsertUpdate();
	statement.setDBContext(context);
	return statement;
	}

    /**
     * This method returns an {@link InsertUpdate} {@link Statement}
     * fitting the default execution context which currently is a PostgresQL
     * DBMS. You had better use the method based on the db execution context.
     *
     * @deprecated use {@link #InsertUpdate(DBContext)} instead.
     */
	static public InsertUpdate InsertUpdate() {
	    return new InsertUpdate();
	}

    /**
     * This method returns a {@link Sequence} {@link Statement}
     * fitting the given execution context.
     *
     * @param context db layer execution context.
     */
    static public Sequence Sequence(DBContext context) {
	// no db-dependent implementation swichtes at the moment
		Sequence statement = new Sequence();
	statement.setDBContext(context);
	return statement;
	}

    /**
     * This method returns a {@link Sequence} {@link Statement}
     * fitting the default execution context which currently is a PostgresQL
     * DBMS. You had better use the method based on the db execution context.
     *
     * @deprecated use {@link #Sequence(DBContext)} instead.
     */
	static public Sequence Sequence() {
	    return new Sequence();
	}

    /**
     * This method returns a {@link WhereList}
     * fitting the given execution context.
     *
     * @param context db layer execution context.
     */
    static public WhereList WhereList(DBContext context) {
	// no db-dependent implementation swichtes at the moment
	WhereList whereList = new WhereList();
	whereList.setDBContext(context);
	return whereList;
    }

    /**
     * This method returns a {@link WhereList} fitting the default execution
     * context which currently is a PostgresQL DBMS. You had better use the
     * method based on the db execution context.
     *
     * @deprecated use {@link #WhereList(DBContext)} instead.
     */
	static public WhereList WhereList() {
	    return new WhereList();
	}

    /**
     * This method returns a {@link Function}
     * fitting the given execution context.
     *
     * @param context db layer execution context.
     */
    static public Function Function(DBContext context, String function) {
	return new Function(context, function);
    }

    /**
     * This method returns a {@link Function} fitting the default execution
     * context which currently is a PostgresQL DBMS. You had better use the
     * method based on the db execution context.
     *
     * @deprecated use {@link #Function(DBContext, String)} instead.
     */
	static public Function Function(String function) {
	    return new Function(function);
	}

	/**
	 * This Methods returns a Procedure-Statement for the given SQL-Procedure.
	 * You can add params via the {@link Procedure#addParam(String)}-call, they are
	 * appended in the order you call them.
	 * @param dbx DBContext to use.
	 * @param name Name of the Procedure
	 * @return Procedure
	 */
	static public Procedure Procedure(DBContext dbx, String name){
		if(isPostgres(dbx))
			return new PostgresProcedure(dbx, name);
		else
			return new Procedure(dbx, name);
	}

    /**
     * This method formats a value according to the supplied db layer context.<br>
     * It formats {@link Clause Clauses} using their {@link Clause#clauseToString(DBContext)}
     * method. Other {@link SetDbContext} implementing classes are handled by first setting
     * their {@link DBContext} attribute and then calling their {@link Object#toString()}
     * method.<br>
     * All remaining classes are formatted using the helper methods of the classes
     * {@link Format} or {@link OracleFormat} which know explicitely how to handle
     * Characters, Strings, Boolean, Date, Statement, and some collection framework
     * classes, while all other Objects are formatted using their respective
     * <code>.toString()</code> method.<br>
     * A <code>null</code> value is returned unchanged.
     *
     * @param context db layer execution context, null is allowed here
     * @param value value to format
     */
    static public final String format(DBContext context, Object value) {
	if (value instanceof Clause)
	    return ((Clause)value).clauseToString(context); // maybe better in brackets?
	if (value instanceof SetDbContext) {
	    ((SetDbContext)value).setDBContext(context);
	    return value.toString();
	}
	if (isOracle(context)){
	    return OracleFormat.format(value);
	}else if(isMSSQL(context)){
		return MSSQLFormat.format(value);
	}else{
	    return Format.defaultFormat(value);
	}
    }

    //
    // protected helper methods
    //
    /**
     * This method decides whether the given {@link DBContext} operates in
     * an Oracle context or not. A <code>null</code> {@link DBContext} is
     * considered to represent a default context which currently is a
     * PostgresQL context.
     */
    static public boolean isOracle(DBContext context) {
	if (context == null)
	    return false;
	if (context.getPool() == null) {
	    logger.warn("DBContext contains no pool.");
	    return false;
	}
	return Pool.DB_ORACLE.equals(context.getPool().getTargetDB());
    }

    /**
     * This method decides whether the given {@link DBContext} operates in
     * an Oracle context or not. A <code>null</code> {@link DBContext} is
     * considered to represent a default context which currently is a
     * PostgresQL context.
     */
    static public boolean isPostgres(DBContext context) {
	if (context == null)
	    return true;
	if (context.getPool() == null) {
	    logger.warn("DBContext contains no pool.");
	    return true;
	}
	return Pool.DB_POSTGRESQL.equals(context.getPool().getTargetDB());
    }

    /**
     * This method decides whether the given {@link DBContext} operates in
     * an MSSQL context or not. A <code>null</code> {@link DBContext} is
     * considered to represent a default context which currently is a
     * PostgresQL context.
     */
    static public boolean isMSSQL(DBContext context) {
	if (context == null)
	    return false;
	if (context.getPool() == null) {
	    logger.warn("DBContext contains no pool.");
	    return false;
	}
	return Pool.DB_MSSQL.equals(context.getPool().getTargetDB());
    }

}
