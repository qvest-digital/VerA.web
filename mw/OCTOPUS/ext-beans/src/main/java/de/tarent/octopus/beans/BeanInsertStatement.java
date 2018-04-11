package de.tarent.octopus.beans;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
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
     * @param insert         the db layer INSERT statement using {@link BeanBaseStatement#PLACE_HOLDER}
     *                       for variables where bean field values are to be set.
     * @param fieldsInInsert fields to insert in the same order as their corresponding
     *                       {@link BeanBaseStatement#PLACE_HOLDER} variables appear inside the statement
     * @param context        execution context of the PreparedStatements
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
        if (bean != null) {
            try {
                preparedStatement.clearParameters();
                List params = new ArrayList();
                for (int index = 0; index < fields.size(); index++) {
                    String field = fields.get(index).toString();
                    Object value = bean.getField(field);
                    preparedStatement.setObject(index + 1, value);
                    params.add(value);
                }
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("PreparedStatement <" + sqlStatement + "> is called for parameters " + params + ".");
                }
                return preparedStatement.executeUpdate();
            } catch (SQLException se) {
                throw new BeanException("Error executing the PreparedInsert <" + sqlStatement + "> using bean <" + bean + ">",
                        se);
            }
        } else {
            logger.warning("execute method called without a bean instance.");
        }
        return 0;
    }

    //
    // protected variables
    //
    /**
     * Here the fields are listed corresponding to the variables to fill in the statement.
     */
    final List fields;

    /**
     * logger of this class.
     */
    final static Logger logger = Logger.getLogger(BeanUpdateStatement.class.getName());
}
