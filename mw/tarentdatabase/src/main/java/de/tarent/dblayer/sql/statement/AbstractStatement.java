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
 * $Id: AbstractStatement.java,v 1.6 2007/06/14 14:51:57 dgoema Exp $
 */
package de.tarent.dblayer.sql.statement;

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
     * Helper method for adding all containing ParamValues in a the supplied <code>objectList</code> to the <code>paramList</code>.
     *
     * @param paramList List for adding the ParamValue instances
     * @param objectList List of arbitrary objects. Some objects of this list may be ParamValue or ParamHolder instances.
     */
    protected void addParams(List paramList, List objectList) {
        for (Iterator iter = objectList.iterator(); iter.hasNext();) {
            Object item = iter.next();
            if (item instanceof ParamValue)
                paramList.add(item);
            else if (item instanceof ParamHolder)
                ((ParamHolder)item).getParams(paramList);
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
     *
     */
    public ExtPreparedStatement prepare() throws SQLException {
        if (getDBContext() == null)
            throw new IllegalStateException("The DBContext must be set before calling Statement#prepare()");
        ExtPreparedStatement stmt = new ExtPreparedStatement(this);
        stmt.prepare(getDBContext());
        return stmt;
    }
}
