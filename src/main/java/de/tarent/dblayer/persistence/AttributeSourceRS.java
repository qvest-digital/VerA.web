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

package de.tarent.dblayer.persistence;

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
        if (columnNameAlias != null && columnNameAlias.containsKey(attributeName))
            attributeName = (String)columnNameAlias.get(attributeName);
        try {
            Object result = null;
            // HACK for the oracle jdbc driver: 
            // On prepared statements the oracle returns a java.sql.Date instead of an java.sql.Timestamp, 
            // like on an direct executed statement.
            // TODO: integrate a configurable type mapping
            if ("creationdate".equalsIgnoreCase(attributeName) || "updatedate".equalsIgnoreCase(attributeName))
                result = rs.getTimestamp(attributeName);
            else
                result = rs.getObject(attributeName);
            return result;
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
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
                            if (columnNameAlias == null)
                                columnNameAlias = new HashMap();
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

