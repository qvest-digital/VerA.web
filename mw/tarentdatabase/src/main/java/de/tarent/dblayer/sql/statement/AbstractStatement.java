package de.tarent.dblayer.sql.statement;

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
import de.tarent.dblayer.engine.SetDbContextImpl;
import de.tarent.dblayer.sql.*;
import de.tarent.dblayer.sql.statement.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Abstract base class for statements.
 * Intended for sharing common funtionality.
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public abstract class AbstractStatement extends SetDbContextImpl implements Statement {

    public void getParams(List paramList) {
        // no params to add in the default implementation
    }

    /**
     * Helper method for adding all containing ParamValues in a the supplied <code>objectList</code> to the
     * <code>paramList</code>.
     *
     * @param paramList  List for adding the ParamValue instances
     * @param objectList List of arbitrary objects. Some objects of this list may be ParamValue or ParamHolder instances.
     */
    protected void addParams(List paramList, List objectList) {
        for (Iterator iter = objectList.iterator(); iter.hasNext(); ) {
            Object item = iter.next();
            if (item instanceof ParamValue) {
                paramList.add(item);
            } else if (item instanceof ParamHolder) {
                ((ParamHolder) item).getParams(paramList);
            }
        }
    }

    /**
     * Creates an ExtPreparedStatement of this statement which is already compiled.
     *
     * @param dbContext The database context in which the statenemt exists.
     */
    public ExtPreparedStatement prepare(DBContext dbContext) throws SQLException {
        ExtPreparedStatement stmt = new ExtPreparedStatement(this);
        stmt.prepare(dbContext);
        return stmt;
    }

    /**
     * Creates an ExtPreparedStatement of this statement which is already compiled.
     * This method creates the PreparedStatement in the same DBContext as the Statement.
     * Therefore the DBContext of the Select must be set.
     *
     * <p><b>Attention:<b> Setting a DBContext and calling prepare() may cause errors,
     * if the same statement is used in multiple threads.</p>
     */
    public ExtPreparedStatement prepare() throws SQLException {
        if (getDBContext() == null) {
            throw new IllegalStateException("The DBContext must be set before calling Statement#prepare()");
        }
        ExtPreparedStatement stmt = new ExtPreparedStatement(this);
        stmt.prepare(getDBContext());
        return stmt;
    }
}
