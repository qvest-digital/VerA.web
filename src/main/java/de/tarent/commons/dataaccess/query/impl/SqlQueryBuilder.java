/*
 * tarent commons,
 * a set of common components and solutions
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
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.dataaccess.query.impl;

import de.tarent.commons.dataaccess.query.AbstractQueryContext;
import de.tarent.commons.dataaccess.query.AbstractVisitorListener;
import de.tarent.commons.dataaccess.query.QueryBuilder;
import de.tarent.commons.dataaccess.query.QueryVisitor;

public class SqlQueryBuilder extends AbstractQueryContext implements QueryBuilder {
	private final StringBuffer stringBuffer = new StringBuffer();

	public void setQueryVisitor(QueryVisitor queryVisitor) {
		if (!(queryVisitor instanceof TraversingVisitor))
			throw new IllegalArgumentException("Can only interact with TraversingVisitor.");
		
		TraversingVisitor traversingVisitor = (TraversingVisitor) queryVisitor;
		
		traversingVisitor.setEmptyVisitorListener(null);
		traversingVisitor.setTermVisitorListener(null);
		traversingVisitor.setAndVisitorListener(new AndVisitorListener());
		traversingVisitor.setOrVisitorListener(new OrVisitorListener());
		traversingVisitor.setXorVisitorListener(new XorVisitorListener());
		traversingVisitor.setNotVisitorListener(new NotVisitorListener());
	}

	public void filterAttributeEqualWithPattern(String attribute, String pattern) {
		if (pattern.indexOf("*") != -1 || pattern.indexOf("?") != -1)
			stringBuffer.append(attribute + " LIKE " + boxLike(pattern));
		else
			stringBuffer.append(attribute + "=" + boxValue(pattern));
	}

	public void filterAttributeEqualWithOneOf(String attribute, String[] values) {
		if (values.length == 0) {
			stringBuffer.append("FALSE");
			return;
		}
		stringBuffer.append(attribute).append(" IN (");
		for (int i = 0; i < values.length; i++) {
			stringBuffer.append(boxValue(values[i]));
			if (i < (values.length - 1))
				stringBuffer.append(", ");
		}
		stringBuffer.append(")");
	}

	public void filterAttributeLowerThanPattern(String attribute, String pattern) {
		stringBuffer.append(attribute + " < " + boxValue(pattern));
	}

	public void filterAttributeLowerOrEqualsThanPattern(String attribute, String pattern) {
		stringBuffer.append(attribute + " <= " + boxValue(pattern));
	}

	public void filterAttributeGreaterThanPattern(String attribute, String pattern) {
		stringBuffer.append(attribute + " > " + boxValue(pattern));
	}

	public void filterAttributeGreaterOrEqualsThanPattern(String attribute, String pattern) {
		stringBuffer.append(attribute + " >= " + boxValue(pattern));
	}

	private class AndVisitorListener extends AbstractVisitorListener {
		public void handleBeforeAll(int entryCount) {
			stringBuffer.append("(");
		}
		
		public void handleBeforeEntry(boolean first, boolean last) {
		}
		
		public void handleAfterEntry(boolean first, boolean last) {
			if (!last)
				stringBuffer.append(" AND ");
		}
		
		public void handleAfterAll(int entryCount) {
			stringBuffer.append(")");
		}
	}

	private class OrVisitorListener extends AbstractVisitorListener {
		public void handleBeforeAll(int entryCount) {
			stringBuffer.append("(");
		}
		
		public void handleBeforeEntry(boolean first, boolean last) {
		}
		
		public void handleAfterEntry(boolean first, boolean last) {
			if (!last)
				stringBuffer.append(" OR ");
		}
		
		public void handleAfterAll(int entryCount) {
			stringBuffer.append(")");
		}
	}

	private class XorVisitorListener extends AbstractVisitorListener {
		public void handleBeforeAll(int entryCount) {
			stringBuffer.append("(");
		}
		
		public void handleBeforeEntry(boolean first, boolean last) {
		}
		
		public void handleAfterEntry(boolean first, boolean last) {
			if (!last)
				stringBuffer.append(" XOR ");
		}
		
		public void handleAfterAll(int entryCount) {
			stringBuffer.append(")");
		}
	}

	private class NotVisitorListener extends AbstractVisitorListener {
		public void handleBeforeAll(int entryCount) {
			stringBuffer.append("(NOT (");
		}
		
		public void handleAfterAll(int entryCount) {
			stringBuffer.append("))");
		}
	}

	public String getExpression() {
		return stringBuffer.toString();
	}

	public String getWhere() {
		if (stringBuffer.length() > 0)
			return " WHERE " + stringBuffer.toString();
		else
			return "";
	}

	public String getSQL(String table) {
		return "SELECT * FROM " + table + getWhere();
	}

	public String getSQL(String table, String columns) {
		return "SELECT " + columns + " FROM " + table + getWhere();
	}

	private String boxValue(String value) {
		return "'" + value.replaceAll("\\'", "''") + "'";
	}

	private String boxLike(String value) {
		return "'" + value.
				replaceAll("\\'", "''").
				replaceAll("\\*", "%").
				replaceAll("\\?", "_") + "'";
	}

	public Object getQuery() {
		return getWhere();
	}
}
