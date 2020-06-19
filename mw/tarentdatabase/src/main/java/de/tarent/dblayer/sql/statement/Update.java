package de.tarent.dblayer.sql.statement;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.sql.ParamHolder;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.From;
import de.tarent.dblayer.sql.clause.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This {@link Statement} models SQL <code>UPDATE</code> statements.
 *
 * @author Wolfgang Klein
 */
public class Update extends AbstractStatement {
    //
    // protected constants
    //
    /**
     * the String "<code> SET </code>"
     */
    private static String SET = " SET ";

    //
    // protected member variables
    //
    /**
     * the name of the table to insert into
     */
    private String _table = null;
    ;
    /**
     * the list of column names ({@link String}) having values to update
     */
    private final List _columns;
    /**
     * the list of value objects in sync with {@link #_columns}
     */
    private final List _values;

    /**
     * {@link From} {@link Clause} listing all tables for the <code>FROM</code> part
     */
    private final From _fromClause = new From();
    /**
     * {@link Clause} modelling the <code>WHERE</code> part
     */
    private Clause _whereClause = null;

    //
    // constructors
    //

    /**
     * This constructor creates a simple {@link Update} {@link Statement}.
     */
    public Update() {
        _columns = new ArrayList();
        _values = new ArrayList();
    }

    /**
     * This constructor creates an {@link Update} {@link Statement} initialized
     * for the given number of values to update.
     */
    public Update(int initialCapacity) {
        _columns = new ArrayList(initialCapacity);
        _values = new ArrayList(initialCapacity);
    }

    /**
     * Appends the parameters of the paramHolder to the supplied list.
     * The order of the params is determined by the order of appearance
     * of the params in the holder object.
     *
     * @param paramList A list to take up ParamValue ebjects.
     * @see ParamHolder#getParams(List)
     */
    public void getParams(List paramList) {
        addParams(paramList, _values);
        if (_whereClause instanceof ParamHolder) {
            ((ParamHolder) _whereClause).getParams(paramList);
        }
    }

    //
    // public methods
    //

    /**
     * This method sets the name of the table to update.
     */
    public Update table(String table) {
        _table = table;
        return this;
    }

    /**
     * This method sets the value for the given column of the data records to update.<br>
     * Actually this method only appends the pair (column, value) to a list and
     * doesn't check whether there already is a pair for the same column in the list.
     * Your code will have to keep an eye on this itself.
     *
     * @param column name of the column of the value; only the local part is used
     * @param value  value to update; this value will be formatted fitting its type
     *               and the {@link DBContext} this {@link Update} is operated in.
     * @return this {@link Update} instance
     * @see #remove(String)
     */
    public Update update(String column, Object value) {
        _columns.add(column.substring(column.lastIndexOf('.') + 1));
        _values.add(value);
        return this;
    }

    /**
     * This method removes the value of the given column from the collection of
     * values to update.<br>
     * Actually this method removes only the first occurance of the column therein.
     * Generally this should be <em>the</em> occurance but as already mentioned
     * {@link #update(String, Object) above} {@link Update} doesn't guarantee it
     * keeps at most one value per column.
     *
     * @param column the column the value assignment of which is to be removed
     * @return the value until now assigned to the column
     * @see #update(String, Object)
     */
    public Object remove(String column) {
        int index = _columns.indexOf(column);
        if (index != -1) {
            _columns.remove(index);
            return _values.remove(index);
        }
        return null;
    }

    /**
     * This method adds the parameter name to the tables to update.
     */
    public Update from(String from) {
        _fromClause.addTable(from);
        return this;
    }

    /**
     * This method sets the condition {@link Clause} for the <code>WHERE</code>
     * part of the <code>UPDATE</code> statement.
     */
    public Update where(Clause clause) {
        _whereClause = clause;
        return this;
    }

    /**
     * This method sets the condition {@link Clause} for the <code>WHERE</code>
     * part of the <code>UPDATE</code> statement to "(current where clause) AND
     * (additional clause)", or to "additional clause" if the current where clause
     * is <code>null</code>.
     */
    public Update whereAnd(Clause additionalClause) {
        if (_whereClause == null) {
            where(additionalClause);
        } else {
            where(Where.and(_whereClause, additionalClause));
        }
        return this;
    }

    /**
     * This method adds an equals expression to the current where list, connected by an ANT operator.
     * It is the same as .whereAnd(Expr.equal(columnName, value))
     */
    public Update whereAndEq(String columnName, Object value) {
        whereAnd(Expr.equal(columnName, value));
        return this;
    }

    /**
     * This method sets the condition {@link Clause} for the <code>WHERE</code>
     * part of the <code>UPDATE</code> statement to "(current where clause) OR
     * (additional clause)", or to "additional clause" if the current where clause
     * is <code>null</code>.
     */
    public Update whereOr(Clause additionalClause) {
        if (_whereClause == null) {
            where(additionalClause);
        } else {
            where(Where.or(_whereClause, additionalClause));
        }
        return this;
    }

    /**
     * This method executes the modelled <code>UPDATE</code> statement within the
     * db layer pool with the given name and returns the {@link Update} itself.
     *
     * @param pool the connection pool in which to operate.
     */
    public Update executeUpdate(String pool) throws SQLException {
        de.tarent.dblayer.engine.DB.update(pool, this);
        return this;
    }

    /**
     * This method executes the modelled <code>UPDATE</code> statement within the
     * db layer pool with the given name and returns the {@link Update} itself.
     *
     * @param dbx the DBContext on which to operate.
     */
    public Update executeUpdate(DBContext dbx) throws SQLException {
        de.tarent.dblayer.engine.DB.update(dbx, this);
        return this;
    }

    /**
     * This method returns the number of (column, value) pairs currently set for update.
     */
    public int size() {
        return _columns.size();
    }

    //
    // interface {@link Statement}
    //

    /**
     * This method executes the modelled <code>UPDATE</code> statement within the
     * {@link DBContext} of this {@link Update} instance.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgreSQL DBMS.<br>
     * This method actually calls {@link #executeUpdate(String)} using the pool name
     * of the {@link DBContext}.
     *
     * @see Statement#execute()
     */
    public Object execute() throws SQLException {
        return executeUpdate(getDBContext());
    }

    /**
     * This method creates the {@link DBContext} sensitive {@link String} representation
     * of the modelled SQL {@link Statement}.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgreSQL DBMS.
     *
     * @see Statement#statementToString()
     */
    public String statementToString() throws SyntaxErrorException {
        if (_table == null) {
            throw new SyntaxErrorException("A table to update is required for an UPDATE operation.");
        }
        if (_values.size() == 0) {
            throw new SyntaxErrorException("At least one value to update is required for an UPDATE operation.");
        }

        StringBuffer sb = new StringBuffer();
        sb.append(UPDATE).append(_table).append(SET);

        Iterator c = _columns.iterator();
        Iterator v = _values.iterator();

        while (c.hasNext()) {
            sb.append(c.next()).append('=').append(SQL.format(getDBContext(), v.next()));
            if (c.hasNext()) {
                sb.append(',');
            }
        }

        if (_fromClause.size() > 0) {
            sb.append(_fromClause.clauseToString(getDBContext()));
        }
        sb.append(' ');

        if (_whereClause != null) {
            String whereString = _whereClause.clauseToString(getDBContext());
            if (whereString.length() > 0) {
                sb.append(Where.WHERE).append(whereString);
            }
        }
        return sb.toString();
    }

    //
    // class {@link Object}
    //

    /**
     * This method creates the {@link DBContext} sensitive {@link String} representation
     * of the modelled SQL {@link Statement} using the method {@link #statementToString()}.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgreSQL DBMS.
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        try {
            return statementToString();
        } catch (SyntaxErrorException e) {
            return e.toString();
        }
    }
}
