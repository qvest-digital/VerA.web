package de.tarent.dblayer.persistence;

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

import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.engine.*;
import de.tarent.dblayer.sql.statement.*;

/**
 * Implementations of this Interface should contain information
 * for the mapping of a business object to the underlaying database system.
 *
 * <p>How te mapping is configured is an implementation detail. The interface provides
 * base Statements for select, insert, update and delete together with the knowledge of the objects fileds.</p>
 */
public interface DBMapping {

    public static final String STMT_SELECT_ONE = "stmtSelectOne";
    public static final String STMT_SELECT_ALL = "stmtSelectAll";

    /**
     * Returns the Query for the supplied ID, containing all possible fields for this query.
     *
     * <p>Be carefull, this Select instance may be the same for multiple running threads.
     * If you need to modify it, you should create a clone first.</p>
     */
    public Select getQuery(String statementID);

    /**
     * Retuns the Fields, contained in the query for the supplied ID
     */
    public Field[] getQueryFields(String statementID);

    /**
     * Returns an insert statement for this business object. The Fields of the
     * insert should be set over the ParamValue mechanism.
     *
     * <p>Be carefull, this Select instance may be the same for multiple running threads.
     * If you need to modify it, you should create a clone first.</p>
     */
    public Insert getInsert();

    /**
     * Returns an insert update for this business object. The Fields of the
     * insert should be set over the ParamValue mechanism.
     *
     * <p>Be carefull, this Select instance may be the same for multiple running threads.
     * If you need to modify it, you should create a clone first.</p>
     */
    public Update getUpdate();

    /**
     * Returns an delete update for this business object. The Fields of the
     * insert should be set over the ParamValue mechanism.
     *
     * <p>Be carefull, this Select instance may be the same for multiple running threads.
     * If you need to modify it, you should create a clone first.</p>
     */
    public Delete getDelete();

    /**
     * Returns the original (full) property name of a property.
     * If the propertyName was not to long, we return the supplied propertyName.
     *
     * @param propertyName the property name as contained in the result set.
     */
    public String getOriginalPropertyName(String propertyName);

    /**
     * Returns the database column name for the propery of the business object.
     */
    public String getColumnNameByProperty(String propertyName);

    /**
     * Returns the primaryKeys field.
     * If this mapping has more than one pk, the first one is returned.
     */
    public Field getPkField();

}
