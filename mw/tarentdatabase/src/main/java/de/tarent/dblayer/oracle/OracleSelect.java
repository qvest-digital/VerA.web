package de.tarent.dblayer.oracle;

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
import java.util.Iterator;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.statement.Select;

/**
 * Oracle specific extension of the {@link Select} {@link Statement}.
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public class OracleSelect extends Select {
    //
    // constructors
    //

    /**
     * This constructor sets the distinctness flag of this <code>SELECT</code>
     * statement.
     *
     * @see Select#Select(boolean)
     */
    public OracleSelect(boolean distinct) {
        super(distinct);
    }

    //
    // interface {@link Statement}
    //

    /**
     * This method creates the {@link DBContext} sensitive {@link String} representation
     * of the modelled SQL {@link Statement}.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.<br>
     * This Oracle specific version serializes the {@link Limit} using
     * <code>rownum</code> magic as there is no better way to do this.
     * As a side effect the alias <code>dbLayerRownumber</code> must
     * not be used as a name alias of a selected column when using a
     * limit or offset.
     *
     * @see Select#statementToString()
     * @see Statement#statementToString()
     */
    public String statementToString() throws SyntaxErrorException {
        String baseStatement = super.statementToString();
        if (getLimit() == null) {
            return baseStatement;
        }

        // emulate the limit and offset using a filter on the oracle rownum
        StringBuffer sb = new StringBuffer(baseStatement.length() + 100);
        sb.append("SELECT ");
        addColumnAliasList(sb);
        sb.append(" FROM (");
        sb.append(" SELECT rownum as dbLayerRownumber,");
        addColumnAliasList(sb);
        sb.append(" FROM (");
        sb.append(baseStatement);
        sb.append(") WHERE rownum <= ");
        sb.append(getLimit().getOffset() + getLimit().getLimit());
        sb.append(") WHERE dbLayerRownumber > ");
        sb.append(getLimit().getOffset());

        return sb.toString();
    }

    //
    // class {@link Select}
    //

    /**
     * This method appends a <code>LIMIT</code> and/or an <code>OFFSET</code>
     * part of the <code>SELECT</code> statement to the given {@link StringBuffer}.<br>
     * This Oracle specific version serializes the {@link Limit} using
     * <code>rownum</code> magic in {@link #statementToString()} as there
     * is no better way to do this, and thus this method should do nothing.
     */
    protected void appendLimitStatement(StringBuffer sb) {
        // do nothing here
    }

    //
    // protected helper methods
    //

    /**
     * This method appends the aliases of the columns to select in the innermost
     * <code>SELECT</code> statement to the given {@link StringBuffer}.<br>
     * As we have to do some <code>rownum</code> magic on Oracle DBMS to emulate
     * <code>LIMIT</code> and <code>OFFSET</code> and this magic includes nested
     * <code>SELECT</code> statements the outer ones need this list to select
     * the requested data columns from the inner ones.
     */
    protected void addColumnAliasList(StringBuffer sb) {
        for (Iterator it = getColumnAliasList().iterator(); it.hasNext(); ) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
    }
}
