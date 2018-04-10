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

    /** returns the Java type of values in the column
     * with the given attribute name.
     *
     */
    public Class getAttributeType(String attributeName) {
	if (columnNameAlias != null && columnNameAlias.containsKey(attributeName))
	    attributeName = (String)columnNameAlias.get(attributeName);

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
