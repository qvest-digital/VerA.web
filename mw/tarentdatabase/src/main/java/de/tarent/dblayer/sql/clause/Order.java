package de.tarent.dblayer.sql.clause;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContextImpl;

import java.util.List;

/**
 * This {@link Clause} represents the <code>ORDER BY</code> part
 * of a <code>SELECT</code> statement.
 *
 * @author Wolfgang Klein
 */
public class Order extends SetDbContextImpl implements Clause {
    //
    // public constants
    //
    /**
     * the String "<code> ORDER BY </code>"
     */
    static public final String ORDER = " ORDER BY ";
    /**
     * the String "<code> DESC</code>" for descending orders
     */
    static public final String DESC = " DESC";
    /**
     * the String "<code> ASC</code>" for ascending orders
     */
    static public final String ASC = " ASC";

    //
    // protected members
    //
    /**
     * The ordered {@link Collection} of order criteria. Each criterion actually
     * consists of a pair (an <code>Object[2]</code>) of a column name {@link
     * String} and a {@link Boolean} flag representing the order direction.
     */
    ArrayList orderby = new ArrayList();

    //
    // constructors
    //

    /**
     * the empty public constructor
     */
    public Order() {
    }

    /**
     * This private constructor creates an {@link Order} {@link Clause}
     * with one order criteria already set.
     *
     * @param column    name of the column to order by
     * @param ascending flag whether the order is ascending
     */
    private Order(String column, boolean ascending) {
        this();
        add(column, ascending);
    }

    //
    // public static factory methods
    //

    /**
     * This method creates an ascending {@link Order} criterion.
     */
    static public Order asc(String column) {
        return new Order(column, true);
    }

    /**
     * This method creates a descending {@link Order} criterion.
     */
    static public Order desc(String column) {
        return new Order(column, false);
    }

    //
    // public methods
    //

    /**
     * This method adds an ascending order criterion.
     */
    public Order andAsc(String column) {
        return add(column, true);
    }

    /**
     * This method adds a descending order criterion.
     */
    public Order andDesc(String column) {
        return add(column, false);
    }

    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.
     *
     * @param sb buffer to which the string representation of the clause model
     *           is to be appended
     */
    public void clauseToString(StringBuffer sb) {
        sb.append(ORDER);
        appendClause(sb);
    }

    //
    // interface {@link Clause}
    //

    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.
     *
     * @return string representation of the clause model
     * @see Clause#clauseToString()
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
        sb.append(ORDER);
        appendClause(sb);
        return sb.toString();
    }

    //
    // protected helper methods
    //

    /**
     * This method actually appends the <code>ORDER BY</code> clause
     * represented by this {@link Order} {@link Clause} to a {@link StringBuffer}.
     * As only column names are appended there is no need (yet --- maybe later
     * for different escaping of column names that are reserved inside a concrete
     * {@link DBContext}) for context specific evaluations.
     */
    protected void appendClause(StringBuffer sb) {
        Object o[];
        Iterator it = orderby.iterator();
        while (it.hasNext()) {
            o = (Object[]) it.next();
            sb.append(o[0]);
            if (o[1] == Boolean.TRUE) {
                sb.append(ASC);
            } else {
                sb.append(DESC);
            }
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
    }

    /**
     * This method adds an entry to the {@link #orderby list of order criteria}.
     * An entry actually consists of a pair (an <code>Object[2]</code>) of a
     * column name {@link String} and a {@link Boolean} flag representing the
     * order direction.
     *
     * @param column    column name of the order criterion
     * @param ascending flag whether the order is ascending
     * @return this {@link Order} instance itself
     */
    private Order add(String column, boolean ascending) {
        Object o[] = new Object[2];
        o[0] = column;
        o[1] = Boolean.valueOf(ascending);
        orderby.add(o);
        return this;
    }

    /**
     * Returns the a list of all columns in the orderBy
     */
    public List getColumns() {
        ArrayList list = new ArrayList(orderby.size());
        Iterator it = orderby.iterator();
        while (it.hasNext()) {
            Object[] o = (Object[]) it.next();
            list.add(o[0]);
        }
        return list;
    }

    /**
     * Returns list of sorting directions where true means ascending and false means descending
     *
     * @return
     */
    public List getSortDirections() {
        ArrayList list = new ArrayList(orderby.size());
        Iterator it = orderby.iterator();
        while (it.hasNext()) {
            Object[] o = (Object[]) it.next();
            list.add(o[1]);
        }
        return list;
    }

    /**
     * Returns an independent clone of this statement.
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            Order theClone = (Order) super.clone();
            theClone.orderby = (ArrayList) orderby.clone();
            return theClone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

}
