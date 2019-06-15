package de.tarent.dblayer.sql.clause;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2018 Dominik George (d.george@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
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

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.engine.SetDbContextImpl;
import de.tarent.dblayer.sql.ParamHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This {@link Clause} represents a collection of {@link Clause Clauses}
 * connected by boolean operators to form the condition of the <code>WHERE</code>
 * part of a <code>SELECT</code> or <code>UPDATE</code> statement.
 *
 * @author Wolfgang Klein
 */
public class WhereList extends SetDbContextImpl implements Clause, ParamHolder {
    //
    // protected members
    //
    /**
     * list of {@link Clause Clauses} and boolean operators
     */
    ArrayList _list = new ArrayList();

    //
    // public methods
    //

    /**
     * Appends the parameters of the paramHolder to the supplied list.
     * The order of the params is determined by the order of appearance
     * of the params in the holder object.
     *
     * @param paramList A list to take up ParamValue ebjects.
     * @see ParamHolder#getParams(List)
     */
    public void getParams(List paramList) {
        for (Iterator iter = _list.iterator(); iter.hasNext(); ) {
            Object item = iter.next();
            if (item instanceof ParamHolder) {
                ((ParamHolder) item).getParams(paramList);
            }
        }
    }

    /**
     * This method adds a {@link Clause} to the list of <code>WHERE</code>
     * clauses.<br>
     * This method may only be called to add the first {@link Clause} to
     * the list.
     *
     * @param clause the {@link Clause} to add
     * @return this {@link WhereList} instance
     */
    public WhereList add(Clause clause) {
        _list.add(clause);
        return this;
    }

    /**
     * This method adds a {@link Clause} to the list of <code>WHERE</code>
     * clauses. If there already are {@link Clause Clauses} in the list
     * the new one is added after an <code>OR</code>.
     *
     * @param clause the {@link Clause} to add
     * @return this {@link WhereList} instance
     */
    public WhereList addOr(Clause clause) {
        if (_list.size() != 0) {
            _list.add(Where.OR);
        }
        _list.add(clause);
        return this;
    }

    /**
     * This method adds a {@link Clause} to the list of <code>WHERE</code>
     * clauses. If there already are {@link Clause Clauses} in the list
     * the new one is added after an <code>AND</code>.
     *
     * @param clause the {@link Clause} to add
     * @return this {@link WhereList} instance
     */
    public WhereList addAnd(Clause clause) {
        if (_list.size() != 0) {
            _list.add(Where.AND);
        }
        _list.add(clause);
        return this;
    }

    /**
     * This method returns the size of the list of <code>WHERE</code>
     * clauses including {@link Clause Clauses} and boolean operators.
     *
     * @return size of the list of <code>WHERE</code> clauses
     */
    public int size() {
        return _list.size();
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
     * @return string representation of the clause model, never <code>null</code>
     * @see de.tarent.dblayer.sql.clause.Clause#clauseToString(de.tarent.dblayer.engine.DBContext)
     */
    public String clauseToString(DBContext dbContext) {
        if (_list.size() == 0) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        sb.append('(');
        for (Iterator it = _list.iterator(); it.hasNext(); ) {
            Object next = it.next();
            if (next instanceof Where) {
                Where.clauseToString((Where) next, sb, false, dbContext);
            } else if (next instanceof Clause) {
                sb.append(((Clause) next).clauseToString(dbContext));
            } else if (next instanceof String) {
                sb.append(next);
            }
        }
        sb.append(')');
        return sb.toString();
    }

    /**
     * Returns an independent clone of this statement.
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            WhereList theClone = (WhereList) super.clone();
            theClone._list = (ArrayList) _list.clone();
            return theClone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}
