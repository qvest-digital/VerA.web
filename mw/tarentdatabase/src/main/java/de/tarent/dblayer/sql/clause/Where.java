package de.tarent.dblayer.sql.clause;

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
import de.tarent.dblayer.engine.SetDbContextImpl;
import de.tarent.dblayer.sql.ParamHolder;
import de.tarent.dblayer.sql.ParamValue;
import de.tarent.dblayer.sql.SQL;

import java.util.List;

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
    /**
     * the String "<code> WHERE </code>"
     */
    final static public String WHERE = " WHERE ";
    /**
     * the String "<code> OR </code>"
     */
    final static public String OR = " OR ";
    /**
     * the String "<code> AND </code>"
     */
    final static public String AND = " AND ";
    /**
     * the String "<code> NOT </code>"
     */
    final static public String NOT = " NOT ";
    /**
     * the String "<code> NOT OR </code>"
     */
    final static public String NOTOR = " NOT OR ";
    /**
     * the String "<code> NOT AND </code>"
     */
    final static public String NOTAND = " NOT AND ";

    //
    // protected members
    //
    /**
     * relation of column and value
     */
    String _relation;
    /**
     * column in relation to the value; non null iff column value relation is used
     */
    String _column;
    /**
     * column in relation to the first column
     */
    String _secondColumn;
    /**
     * value in relation to the column
     */
    Object _value;

    /**
     * left side parameter of the expression
     */
    Clause _left;
    /**
     * right side parameter of the expression
     */
    Clause _right;
    /**
     * expression connecting the parameters
     */
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

        if (relateColumns) {
            _secondColumn = value.toString();
        } else {
            _value = value;
        }
    }

    public Where(String column, Object value, String relation) {
        if (column == null) {
            throw new IllegalArgumentException("Column value relations require a non null column name.");
        }
        _column = column;
        _value = value;
        _relation = relation;
        _left = null;
        _right = null;
        _expression = null;
    }

    /**
     * This constructor accepts two {@link Clause Clauses} and an expression.
     */
    public Where(Clause left, Clause right, String expression) {
        _column = null;
        _value = null;
        _relation = null;
        _left = left;
        _right = right;
        _expression = expression;
    }

    /**
     * This constructor accepts one {@link Clause Clause} and an expression.
     */
    public Where(Clause clause, String expression) {
        _column = null;
        _value = null;
        _relation = null;
        _left = null;
        _right = clause;
        _expression = expression;
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
        addObjectToParamList(paramList, _value);
        addObjectToParamList(paramList, _left);
        addObjectToParamList(paramList, _right);
    }

    /**
     * Helper method for adding Objects which may be ParmValues or contain ParmValues to the paramList.
     */
    protected void addObjectToParamList(List paramList, Object object) {
        if (object instanceof ParamValue) {
            paramList.add(object);
        } else if (object instanceof ParamHolder) {
            ((ParamHolder) object).getParams(paramList);
        }
    }

    //
    // public static factory methods
    //

    /**
     * This method creates a single argument <code>OR</code> {@link Clause}.
     */
    static public Where or(Where clause) {
        return new Where(clause, null, OR);
    }

    /**
     * This method creates an <code>OR</code> {@link Clause}.
     */
    static public Where or(Clause left, Clause right) {
        return new Where(left, right, OR);
    }

    /**
     * This method creates an <code>AND</code> {@link Clause}.
     */
    static public Where and(Clause left, Clause right) {
        return new Where(left, right, AND);
    }

    /**
     * This method creates a <code>NOT OR</code> {@link Clause}.
     */
    static public Where notOr(Clause left, Clause right) {
        return new Where(left, right, NOTOR);
    }

    /**
     * This method creates a <code>NOT AND</code> {@link Clause}.
     */
    static public Where notAnd(Clause left, Clause right) {
        return new Where(left, right, NOTAND);
    }

    /**
     * This method creates a {@link WhereList} {@link Clause}.
     */
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
            Where where = (Where) clause;
            if (where._column == null) {
                if (!parent) {
                    sb.append('(');
                }
                if (where._left != null) {
                    clauseToString(where._left, sb, false, context);
                }
                if (where._expression != null) {
                    sb.append(where._expression);
                }
                if (where._right != null) {
                    clauseToString(where._right, sb, false, context);
                }
                if (!parent) {
                    sb.append(')');
                }
            } else if (where._relation != null) {
                sb.append(where._column);
                sb.append(where._relation);
                //IN relation needs brackets when value does not contains an inner statement
                if ((Expr.IN.trim().equals(where._relation.toUpperCase().trim()) ||
                  Expr.NOTIN.trim().equals(where._relation.toUpperCase().trim())) && where._value != null &&
                  !(where._value instanceof Clause)) {
                    sb.append('(');
                    sb.append(SQL.format(context, where._value));
                    sb.append(')');
                } else if (where._value != null) {
                    sb.append(SQL.format(context, where._value));
                } else if (where._value == null && where._secondColumn != null) {
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
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            Where theClone = (Where) super.clone();
            if (_left != null) {
                theClone._left = (Clause) _left.clone();
            }
            if (_right != null) {
                theClone._right = (Clause) _right.clone();
            }
            if (_value instanceof ParamValue) {
                theClone._value = ((ParamValue) _value).clone();
            }
            return theClone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}
