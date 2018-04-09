/*
 * tarent-octopus bean extension,
 * an opensource webservice and webapplication framework (bean extension)
 * Copyright (c) 2006-2007 tarent GmbH
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
 * interest in the program 'tarent-octopus bean extension'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: BeanInsertStatement.java,v 1.4 2007/06/11 13:24:36 christoph Exp $
 *
 * Created on 25.01.2006
 */
package de.tarent.octopus.beans;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.dblayer.sql.statement.Insert;

/**
 * This class encapsulates an INSERT {@link PreparedStatement}operating in
 * a given {@link ExecutionContext} and using data from {@link Bean} instances.<br>
 * TODO: implement ID pre-fetches and post-fetches using aggregated BeanSelectStatements.
 *
 * @author Michael Klink
 */
class BeanInsertStatement extends BeanBaseStatement implements BeanStatement {
    //
    // constructor
    //
    /**
     * This constructor stores the given context and creates the prepared
     * statement.
     *
     * @param insert the db layer INSERT statement using {@link BeanBaseStatement#PLACE_HOLDER}
     *  for variables where bean field values are to be set.
     * @param fieldsInInsert fields to insert in the same order as their corresponding
     *  {@link BeanBaseStatement#PLACE_HOLDER} variables appear inside the statement
     * @param context execution context of the PreparedStatements
     */
    BeanInsertStatement(Insert insert, List fieldsInInsert, ExecutionContext context) throws BeanException {
        super(insert, context);
        this.fields = fieldsInInsert;
    }

    //
    // interface BeanStatement
    //
    /**
     * This method executes the insert using the given bean.
     *
     * @param bean this bean's attributes are fed into the prepared statement.
     * @return count of insert operations
     * @see de.tarent.octopus.beans.BeanStatement#execute(de.tarent.octopus.beans.Bean)
     */
    public int execute(Bean bean) throws BeanException {
        if (bean != null)
            try {
                preparedStatement.clearParameters();
                List params = new ArrayList();
                for (int index = 0; index < fields.size(); index++) {
                    String field = fields.get(index).toString();
                    Object value = bean.getField(field);
                    preparedStatement.setObject(index + 1, value);
                    params.add(value);
                }
                if (logger.isLoggable(Level.FINE))
                    logger.fine("PreparedStatement <" + sqlStatement + "> is called for parameters " + params + ".");
                return preparedStatement.executeUpdate();
            } catch (SQLException se) {
                throw new BeanException("Error executing the PreparedInsert <" + sqlStatement + "> using bean <" + bean + ">", se);
            }
        else
            logger.warning("execute method called without a bean instance.");
        return 0;
    }

    //
    // protected variables
    //
    /** Here the fields are listed corresponding to the variables to fill in the statement. */
    final List fields;

    /** logger of this class. */
    final static Logger logger = Logger.getLogger(BeanUpdateStatement.class.getName());
}
