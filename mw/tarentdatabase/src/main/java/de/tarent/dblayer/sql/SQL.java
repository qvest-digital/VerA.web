package de.tarent.dblayer.sql;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Function;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.InsertUpdate;
import de.tarent.dblayer.sql.statement.Procedure;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Sequence;
import de.tarent.dblayer.sql.statement.Update;
import lombok.extern.log4j.Log4j2;

/**
 * This class serves as a factory for SQL statements and parts thereof.
 *
 * @author kleinw
 */
@Log4j2
public class SQL {
    /**
     * This method returns a non-distinct {@link Select} {@link Statement}
     * fitting the given execution context.
     *
     * @param context db layer execution context.
     */
    static public Select Select(DBContext context) {
        Select statement = new Select(false);
        statement.setDBContext(context);
        return statement;
    }

    /**
     * This method returns a non-distinct {@link Select} {@link Statement}
     * fitting the default execution context which currently is a PostgreSQL
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
        Select statement = new Select(true);
        statement.setDBContext(context);
        return statement;
    }

    /**
     * This method returns a distinct {@link Select} {@link Statement}
     * fitting the default execution context which currently is a PostgreSQL
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
     * fitting the default execution context which currently is a PostgreSQL
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
     * fitting the default execution context which currently is a PostgreSQL
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
     * fitting the default execution context which currently is a PostgreSQL
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
     * fitting the default execution context which currently is a PostgreSQL
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
     * fitting the default execution context which currently is a PostgreSQL
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
     * context which currently is a PostgreSQL DBMS. You had better use the
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
     * context which currently is a PostgreSQL DBMS. You had better use the
     * method based on the db execution context.
     *
     * @deprecated use {@link #Function(DBContext, String)} instead.
     */
    static public Function Function(String function) {
        return new Function(function);
    }

    /**
     * This Methods returns a Procedure-Statement for the given SQL-Procedure.
     * You can add params via the {@link Procedure#addParam(Object)} call, they are
     * appended in the order you call them.
     *
     * @param dbx  DBContext to use.
     * @param name Name of the Procedure
     * @return Procedure
     */
    static public Procedure Procedure(DBContext dbx, String name) {
        return new Procedure(dbx, name);
    }

    /**
     * This method formats a value according to the supplied db layer context.
     *
     * It formats {@link Clause Clauses} using their {@link Clause#clauseToString(DBContext)}
     * method. Other {@link SetDbContext} implementing classes are handled by first setting
     * their {@link DBContext} attribute and then calling their {@link Object#toString()}
     * method.
     *
     * All remaining classes are formatted using the helper methods of the class
     * {@link Format} which knows explicitely how to handle
     * Characters, Strings, Boolean, Date, Statement, and some collection framework
     * classes, while all other Objects are formatted using their respective
     * <code>.toString()</code> method.
     *
     * A <code>null</code> value is returned unchanged.
     *
     * @param context db layer execution context, null is allowed here
     * @param value   value to format
     */
    static public final String format(DBContext context, Object value) {
        if (value instanceof Clause) {
            return ((Clause) value).clauseToString(context); // maybe better in brackets?
        }
        if (value instanceof SetDbContext) {
            ((SetDbContext) value).setDBContext(context);
            return value.toString();
        }
        return Format.defaultFormat(value);
    }
}
