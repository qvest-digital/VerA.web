package de.tarent.dblayer.sql.statement;

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
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
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
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import de.tarent.commons.datahandling.entity.AttributeSource;
import de.tarent.commons.datahandling.entity.ParamSet;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.InsertKeys;
import de.tarent.dblayer.engine.ResultProcessor;
import de.tarent.dblayer.sql.ParamValue;
import de.tarent.dblayer.sql.ParamValueList;
import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.SyntaxErrorException;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This is an holder for an java.sql.PreparedStatement,
 * where the Parameters can be set by String keys.
 */
@Log4j2
public class ExtPreparedStatement implements ParamSet {
    String sqlCode;
    // String List of the param names
    List params;
    PreparedStatement stmtDelegate;

    // table Java-Type (class object) => Integer(JDBC-Type-Contant)
    Map typeMapping;
    static Map defaultTypeMapping;

    static {
        defaultTypeMapping = new HashMap();
        defaultTypeMapping.put(java.util.Date.class, new Integer(Types.DATE));
    }

    /**
     * Creates a new ExtPreparedStatement.
     *
     * @param sqlCode The code of the sql statement
     * @param params  A list of the parameter names of the PrepareStatement with the same order as they occur in the Statement.
     */
    public ExtPreparedStatement(String sqlCode, List params) {
        this.sqlCode = sqlCode;
        this.params = params;
    }

    /**
     * Creates a new ExtPreparedStatement. From the supplied DBLayer statement.
     * The statementToString() method is called for the code and the params are derived from the ParamValue objects, which are
     * not set yet.
     *
     * @param statement source of the sql statement
     * @throws RuntimeException if the underlaying statement statementToString creation fails
     */
    public ExtPreparedStatement(Statement statement) {
        try {
            sqlCode = statement.statementToString();
        } catch (SyntaxErrorException se) {
            throw new RuntimeException(se);
        }

        ParamValueList paramValueList = new ParamValueList();
        statement.getParams(paramValueList);

        int count = 0;
        for (Iterator iter = paramValueList.iterator(); iter.hasNext(); ) {
            ParamValue pv = (ParamValue) iter.next();
            if (!pv.isSet()) {
                count++;
            }
        }

        params = new ArrayList(count);
        for (Iterator iter = paramValueList.iterator(); iter.hasNext(); ) {
            ParamValue pv = (ParamValue) iter.next();
            if (!pv.isSet()) {
                params.add(pv.getName());
            }
        }
    }

    /**
     * Returns the underlaying PreparedStatement
     */
    public PreparedStatement getPreparedStatement() {
        return stmtDelegate;
    }

    public void prepare(DBContext cntx) throws SQLException {
        prepare(cntx, false, false);
    }

    public void prepareScrollable(DBContext cntx) throws SQLException {
        prepare(cntx, false, true);
    }

    public void prepare(DBContext cntx, boolean autogenerateKeys) throws SQLException {
        prepare(cntx, autogenerateKeys, false);
    }

    public void prepare(DBContext cntx, boolean autogenerateKeys, boolean scrollable) throws SQLException {
        Connection con = cntx.getDefaultConnection();
        if (logger.isDebugEnabled()) {
            logger.debug("praparing statement with: " + sqlCode);
        }
        if (autogenerateKeys && (!con.getMetaData().supportsGetGeneratedKeys())) {
            logger.debug(
              "should prepare with returnKeyColumns, but returning autogenerated keys is not supported by jdbc driver");
        }

        if (autogenerateKeys && con.getMetaData().supportsGetGeneratedKeys()) {
            stmtDelegate = con.prepareStatement(sqlCode, java.sql.Statement.RETURN_GENERATED_KEYS);
        } else {
            int rsType = scrollable ? ResultSet.TYPE_SCROLL_INSENSITIVE : ResultSet.TYPE_FORWARD_ONLY;
            stmtDelegate = con.prepareStatement(sqlCode, rsType, ResultSet.CONCUR_READ_ONLY);
        }
    }

    public void prepare(DBContext cntx, String[] returnKeyColumns) throws SQLException {
        Connection con = cntx.getDefaultConnection();
        if (logger.isDebugEnabled()) {
            logger.debug("praparing statement with: " + sqlCode);
        }
        if (con.getMetaData().supportsGetGeneratedKeys()) {
            if (logger.isDebugEnabled()) {
                logger.debug("setting returnKeyColumns to: " + Arrays.asList(returnKeyColumns));
            }
            stmtDelegate = con.prepareStatement(sqlCode, returnKeyColumns);
        } else {
            logger.debug(
              "should prepare with returnKeyColumns, but returning autogenerated keys is not supported by jdbc driver");
            stmtDelegate = con.prepareStatement(sqlCode);
        }
    }

    /**
     * For querys only, executes the Statement and iterates over the result set and calles the process method for each row.
     * Afterwards, the result set will be closed.
     *
     * @return the number of iterations
     */
    public int iterate(ResultProcessor processor) throws SQLException {
        int i = 0;
        ResultSet rs = null;
        try {
            rs = stmtDelegate.executeQuery();
            while (rs.next()) {
                processor.process(rs);
                i++;
            }
        } finally {
            DB.close(rs);
        }
        return i;
    }

    /**
     * Closes the Prepared Statement if it exist an is not closed already.
     */
    public void close() {
        DB.close(stmtDelegate);
    }

    /**
     * Add a type mapping for non standard types
     *
     * @param javaType      The java type, which schould be mapped
     * @param jdbcTypeConst The constant out of java.sql.Types, to which the Java type should be mapped
     */
    public void addTypeMapping(Class javaType, int jdbcTypeConst) {
        if (typeMapping == null) {
            typeMapping = new HashMap();
            typeMapping.putAll(defaultTypeMapping);
        }
        typeMapping.put(javaType, new Integer(jdbcTypeConst));
    }

    /**
     * clears all type mapping
     */
    public void clearTypeMapping() {
        if (typeMapping == null) {
            typeMapping = new HashMap();
        } else {
            typeMapping.clear();
        }
    }

    /**
     * Returns the configured typemapping for the supplied class
     */
    public Integer getJDBCTypeFor(Class javaType) {
        if (typeMapping != null) {
            return (Integer) typeMapping.get(javaType);
        }
        return (Integer) defaultTypeMapping.get(javaType);
    }

    /**
     * Clears all parameters of the prepared statement.
     *
     * @throws RuntimeException if the underlaying statement throws an SQLException
     */
    public void clearAttributes() {
        try {
            stmtDelegate.clearParameters();
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    // Mabe we should implement a isSet() method here
    //public boolean isSet() {

    /**
     * Sets the attribute <code>attributeName</code> of this structure to the supplied value.
     *
     * @throws RuntimeException if the underlaying statement does not accept the attribute
     */
    public void setAttribute(String attributeName, Object attributeValue) {
        boolean set = false;
        try {
            for (int i = 0; i < params.size(); i++) {
                // carefull: JDBC counts beginning at 1
                if (attributeName.equals(params.get(i))) {
                    if (attributeValue == null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("set parameter " + attributeName + " (" + i + ") to null");
                        }
                        stmtDelegate.setNull(i + 1, Types.NULL);
                    } else {
                        if (attributeValue instanceof java.util.Date && !(attributeValue instanceof java.sql.Date) &&
                          !(attributeValue instanceof java.sql.Timestamp)) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("set parameter " + attributeName + " (" + i + ") to " + attributeValue +
                                  " (converting it to java.sql.Timestamp)");
                            }
                            Timestamp timestamp = new Timestamp(((java.util.Date) attributeValue).getTime());
                            stmtDelegate.setTimestamp(i + 1, timestamp);
                        } else {
                            Integer targetType = getJDBCTypeFor(attributeValue.getClass());
                            if (targetType != null) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("set parameter " + attributeName + " (" + i + ") to " + attributeValue +
                                      " (using target type: " + targetType.intValue() + ")");
                                }
                                // mapping configured, so we use a target type here
                                stmtDelegate.setObject(i + 1, attributeValue, targetType.intValue());
                            } else {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("set parameter " + attributeName + " (" + i + ") to " + attributeValue);
                                }
                                // no mapping defined
                                stmtDelegate.setObject(i + 1, attributeValue);
                            }
                        }
                    }
                }
                set = true;
                // do not break, if there are more than one param with the same name, all have to be set
            }
        } catch (SQLException sqle) {
            RuntimeException re = new RuntimeException("error while setting attribute '" + attributeName + "' (with type '" +
              (attributeName == null ? "void" : attributeValue.getClass().getName()) + "')");
            re.initCause(sqle);
            throw re;
        }
        if (!set && logger.isDebugEnabled()) {
            logger.debug("not able to set the attribute " + attributeName + " for prepared statement");
        }
    }

    /**
     * Sets the attributes of this structure to the supplied attributes in the map.
     *
     * @throws RuntimeException if the underlaying statement does not accept the attributes
     */
    public void setAttributes(AttributeSource attributeSource) {
        for (Iterator iter = attributeSource.getAttributeNames().iterator(); iter.hasNext(); ) {
            String paramName = (String) iter.next();
            setAttribute(paramName, attributeSource.getAttribute(paramName));
        }
    }

    /**
     * Returns the last generated keys, if this statement is an insert operation.
     */
    public InsertKeys returnGeneratedKeys(DBContext dbc) throws SQLException {
        return DB.returnGeneratedKeys(dbc, getPreparedStatement(), sqlCode);
    }

    public List getAttributeNames() {
        return params;
    }

    public void setAttributeNames(List newAttributeNames) {
        this.params = newAttributeNames;
    }
}
