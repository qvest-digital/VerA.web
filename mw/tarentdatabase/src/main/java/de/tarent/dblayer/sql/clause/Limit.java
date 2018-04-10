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

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.engine.SetDbContextImpl;

/**
 * This {@link Clause} represents the <code>LIMIT</code> and
 * <code>OFFSET</code> parts of a <code>SELECT</code> statement.
 *
 * @author Simon B�hler
 */
public class Limit extends SetDbContextImpl implements Clause {
    //
    // public constants
    //
    /** the String "<code> LIMIT </code>" */
    final static private String LIMIT = " LIMIT ";
    /** the String "<code> OFFSET </code>" */
    final static private String OFFSET = " OFFSET ";

    //
    // protected members
    //
    /** the <code>LIMIT</code> number */
    private Integer _limit;
    /** the <code>OFFSET</code> number */
    private Integer _offset;

    //
    // constructors
    //
    /**
     * This constructor takes limit and offset as primitive <code>int</code>s.
     */
	public Limit(int limit, int offset) {
	this(new Integer(limit), new Integer(offset));
    }

    /**
     * This constructor takes limit and offset as {@link Integer Integers}.
     */
	public Limit(Integer limit,Integer offset) {
	    _limit = limit;
	    _offset = offset;
	}

    //
    // public getters and setters
    //
	/** the limit */
    public int getLimit() {
	return _limit.intValue();
    }

    /** the offset */
    public int getOffset() {
	return _offset.intValue();
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
     * @see de.tarent.dblayer.sql.clause.Clause#clauseToString(de.tarent.dblayer.engine.DBContext)
     */
    public String clauseToString(DBContext dbContext) {
	setDBContext(dbContext);
		StringBuffer sb = new StringBuffer();
		clauseToString(sb);
		return sb.toString();
	}

    /**
     * This method attaches the actual <code>LIMIT</code> and <code>OFFSET</code>
     * clauses to a {@link StringBuffer}.
     */
	public void clauseToString(StringBuffer sb) {
	    if (_limit.intValue()> 0) {
		    sb.append(LIMIT);
		    sb.append(_limit);
		}
	    if (_offset.intValue() > 0) {
		    sb.append(OFFSET);
		    sb.append(_offset);
		}
	}

    public Object clone() {
	try {
	    return super.clone();
	}
	catch(CloneNotSupportedException e) {
		throw new InternalError();
	}
    }

}
