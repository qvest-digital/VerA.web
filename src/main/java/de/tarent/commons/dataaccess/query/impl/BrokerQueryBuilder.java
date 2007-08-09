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

import java.util.LinkedList;
import java.util.List;

import de.tarent.commons.dataaccess.query.AbstractQueryContext;
import de.tarent.commons.dataaccess.query.QueryBuilder;

public class BrokerQueryBuilder extends AbstractQueryContext implements QueryBuilder {
	public static final Object EMPTY_RESULT = new Object();

	private boolean emptyInQuery = false;
	private List parsedQuery = new LinkedList();

	public void filterAttributeEqualWithPattern(String attribute, String pattern) {
		if (pattern == null) {
			parsedQuery.add(new String[] { attribute, "=", "NULL" });
			return;
		}
		
		String op = pattern.indexOf("*") != -1 || pattern.indexOf("?") != -1 ?
				" RE " :
				"=";
		if (op.equals(" RE "))
			pattern = pattern.replaceAll("\\*", ".*").replaceAll("\\?", ".");
		
		parsedQuery.add(new String[] { attribute, op, pattern });
	}

	public void filterAttributeEqualWithOneOf(String attribute, String[] values) {
		if (values == null || values.length == 0) {
			emptyInQuery = true;
			return;
		}
		
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			stringBuffer.append(values[i] != null ? values[i] : "NULL");
			if (i < (values.length - 1))
				stringBuffer.append(", ");
		}
		
		parsedQuery.add(new String[] { attribute, " IN ", stringBuffer.toString() });		
	}

	public void filterAttributeLowerThanPattern(String attribute, String pattern) {
		parsedQuery.add(new String[] { attribute, " < ", pattern });
	}

	public void filterAttributeLowerOrEqualsThanPattern(String attribute, String pattern) {
		parsedQuery.add(new String[] { attribute, " <= ", pattern });
	}

	public void filterAttributeGreaterThanPattern(String attribute, String pattern) {
		parsedQuery.add(new String[] { attribute, " > ", pattern });
	}

	public void filterAttributeGreaterOrEqualsThanPattern(String attribute, String pattern) {
		parsedQuery.add(new String[] { attribute, " >= ", pattern });
	}

	public Object getQuery() {
		if (emptyInQuery)
			return EMPTY_RESULT;
		return parsedQuery;
	}
}
