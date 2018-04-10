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

import de.tarent.commons.datahandling.entity.AttributeSource;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class AttributeSourceRS implements AttributeSource {

    ResultSet rs;
    List attributeNames;
    DBMapping dbMapping;

    //mapping from attributeNameAliases to column names
    Map columnNameAlias = null;

    public AttributeSourceRS(ResultSet rs) {
        this.rs = rs;
    }

    public AttributeSourceRS(ResultSet rs, List attributeNames) {
        this.rs = rs;
        this.attributeNames = attributeNames;
    }

    public AttributeSourceRS(ResultSet rs, DBMapping dbMapping) {
        this.rs = rs;
        this.dbMapping = dbMapping;
    }

    public AttributeSourceRS(ResultSet rs, List attributeNames, DBMapping dbMapping) {
        this.rs = rs;
        this.attributeNames = attributeNames;
        this.dbMapping = dbMapping;
    }

    /**
     * returns the attribute value from the result set.
     *
     * TODO: integrate a configurable type mapping
     */
    public Object getAttribute(String attributeName) {
        if (columnNameAlias != null && columnNameAlias.containsKey(attributeName)) {
            attributeName = (String) columnNameAlias.get(attributeName);
        }
        try {
            Object result = null;
            // HACK for the oracle jdbc driver:
            // On prepared statements the oracle returns a java.sql.Date instead of an java.sql.Timestamp,
            // like on an direct executed statement.
            // TODO: integrate a configurable type mapping
            if ("creationdate".equalsIgnoreCase(attributeName) || "updatedate".equalsIgnoreCase(attributeName)) {
                result = rs.getTimestamp(attributeName);
            } else {
                result = rs.getObject(attributeName);
            }
            return result;
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    /**
     * returns the Java type of values in the column
     * with the given attribute name.
     */
    public Class getAttributeType(String attributeName) {
        if (columnNameAlias != null && columnNameAlias.containsKey(attributeName)) {
            attributeName = (String) columnNameAlias.get(attributeName);
        }

        String className;
        try {
            className = rs.getMetaData().getColumnClassName(rs.findColumn(attributeName));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Class returnClass;
        try {
            returnClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return returnClass;
    }

    public List getAttributeNames() {
        if (attributeNames == null) {
            try {
                ResultSetMetaData md = rs.getMetaData();
                int count = md.getColumnCount();
                attributeNames = new ArrayList(count);
                for (int i = 1; i <= count; i++) {
                    String columnName = md.getColumnName(i);
                    String originalPropertyName = columnName;
                    // test if we have to use an alias for the column name
                    if (dbMapping != null) {
                        originalPropertyName = dbMapping.getOriginalPropertyName(originalPropertyName);
                        if (!originalPropertyName.equals(columnName)) {
                            if (columnNameAlias == null) {
                                columnNameAlias = new HashMap();
                            }
                            columnNameAlias.put(originalPropertyName, columnName);
                        }
                    }
                    attributeNames.add(originalPropertyName);
                }
            } catch (SQLException sqle) {
                throw new RuntimeException(sqle);
            }
        }

        return attributeNames;
    }

}
