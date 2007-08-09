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

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.management.AttributeValueExp;
import javax.management.Query;
import javax.management.QueryExp;
import javax.management.StringValueExp;
import javax.management.ValueExp;

import de.tarent.commons.dataaccess.DataAccessException;
import de.tarent.commons.dataaccess.query.AbstractQueryContext;
import de.tarent.commons.dataaccess.query.QueryParser;

public class ManagmentQueryParser extends AbstractQueryContext implements QueryParser {
	private static Class MATCH_QUERY_EXP;
	private static Method MATCH_QUERY_EXP_GET_ATTRIBUTE;
	private static Method MATCH_QUERY_EXP_GET_PATTERN;

	private static Class BINARY_REL_QUERY_EXP;
	private static Method BINARY_REL_QUERY_EXP_GET_OPERATOR;
	private static Method BINARY_REL_QUERY_EXP_GET_LEFT_VALUE;
	private static Method BINARY_REL_QUERY_EXP_GET_RIGHT_VALUE;

	private static Class OR_QUERY_EXP;
	private static Method OR_QUERY_EXP_GET_LEFT_EXP;
	private static Method OR_QUERY_EXP_GET_RIGHT_EXP;

	private static Class AND_QUERY_EXP;
	private static Method AND_QUERY_EXP_GET_LEFT_EXP;
	private static Method AND_QUERY_EXP_GET_RIGHT_EXP;

	private static Class IN_QUERY_EXP;
	private static Method IN_QUERY_EXP_GET_CHECKED_VALUE;
	private static Method IN_QUERY_EXP_GET_GET_EXPLICIT_VALUES;

	static {
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				try {
					MATCH_QUERY_EXP = Class.forName("javax.management.MatchQueryExp");
					MATCH_QUERY_EXP_GET_ATTRIBUTE = MATCH_QUERY_EXP.getMethod("getAttribute", null);
					MATCH_QUERY_EXP_GET_ATTRIBUTE.setAccessible(true);
					MATCH_QUERY_EXP_GET_PATTERN = MATCH_QUERY_EXP.getMethod("getPattern", null);
					MATCH_QUERY_EXP_GET_PATTERN.setAccessible(true);
					
					BINARY_REL_QUERY_EXP = Class.forName("javax.management.BinaryRelQueryExp");
					BINARY_REL_QUERY_EXP_GET_OPERATOR = BINARY_REL_QUERY_EXP.getMethod("getOperator", null);
					BINARY_REL_QUERY_EXP_GET_OPERATOR.setAccessible(true);
					BINARY_REL_QUERY_EXP_GET_LEFT_VALUE = BINARY_REL_QUERY_EXP.getMethod("getLeftValue", null);
					BINARY_REL_QUERY_EXP_GET_LEFT_VALUE.setAccessible(true);
					BINARY_REL_QUERY_EXP_GET_RIGHT_VALUE = BINARY_REL_QUERY_EXP.getMethod("getRightValue", null);
					BINARY_REL_QUERY_EXP_GET_RIGHT_VALUE.setAccessible(true);
					
					OR_QUERY_EXP = Class.forName("javax.management.OrQueryExp");
					OR_QUERY_EXP_GET_LEFT_EXP = OR_QUERY_EXP.getMethod("getLeftExp", null);
					OR_QUERY_EXP_GET_LEFT_EXP.setAccessible(true);
					OR_QUERY_EXP_GET_RIGHT_EXP = OR_QUERY_EXP.getMethod("getRightExp", null);
					OR_QUERY_EXP_GET_RIGHT_EXP.setAccessible(true);
					
					AND_QUERY_EXP = Class.forName("javax.management.AndQueryExp");
					AND_QUERY_EXP_GET_LEFT_EXP = AND_QUERY_EXP.getMethod("getLeftExp", null);
					AND_QUERY_EXP_GET_LEFT_EXP.setAccessible(true);
					AND_QUERY_EXP_GET_RIGHT_EXP = AND_QUERY_EXP.getMethod("getRightExp", null);
					AND_QUERY_EXP_GET_RIGHT_EXP.setAccessible(true);
					
					IN_QUERY_EXP = Class.forName("javax.management.InQueryExp");
					IN_QUERY_EXP_GET_CHECKED_VALUE = IN_QUERY_EXP.getMethod("getCheckedValue", null);
					IN_QUERY_EXP_GET_CHECKED_VALUE.setAccessible(true);
					IN_QUERY_EXP_GET_GET_EXPLICIT_VALUES = IN_QUERY_EXP.getMethod("getExplicitValues", null);
					IN_QUERY_EXP_GET_GET_EXPLICIT_VALUES.setAccessible(true);
					
					return null;
				} catch (Exception e) {
					throw new DataAccessException(e);
				}
			}
		});
	}

	public void parse(Object o) {
		try {
			QueryExp exp = (QueryExp) o;
			
			if (exp == null) {
				// Nothing
			} else if (MATCH_QUERY_EXP.isInstance(exp)) {
				AttributeValueExp attribute = (AttributeValueExp) MATCH_QUERY_EXP_GET_ATTRIBUTE.invoke(exp, null);
				String pattern = (String) MATCH_QUERY_EXP_GET_PATTERN.invoke(exp, null);
				getQueryFilter().filterAttributeEqualWithPattern(attribute.getAttributeName(), pattern);
			} else if (BINARY_REL_QUERY_EXP.isInstance(exp)) {
				int operator = ((Integer) BINARY_REL_QUERY_EXP_GET_OPERATOR.invoke(exp, null)).intValue();
				AttributeValueExp attribute = (AttributeValueExp) BINARY_REL_QUERY_EXP_GET_LEFT_VALUE.invoke(exp, null);
				StringValueExp pattern = (StringValueExp) BINARY_REL_QUERY_EXP_GET_RIGHT_VALUE.invoke(exp, null);
				if (operator == Query.EQ) {
					getQueryFilter().filterAttributeEqualWithPattern(attribute.getAttributeName(), pattern.getValue());
				} else if (operator == Query.LT) {
					getQueryFilter().filterAttributeLowerThanPattern(attribute.getAttributeName(), pattern.getValue());
				} else if (operator == Query.LE) {
					getQueryFilter().filterAttributeLowerOrEqualsThanPattern(attribute.getAttributeName(), pattern.getValue());
				} else if (operator == Query.GT) {
					getQueryFilter().filterAttributeGreaterThanPattern(attribute.getAttributeName(), pattern.getValue());
				} else if (operator == Query.GE) {
					getQueryFilter().filterAttributeGreaterOrEqualsThanPattern(attribute.getAttributeName(), pattern.getValue());
				} else {
					throw new IllegalArgumentException("Unsupported operator: " + operator);
				}
			} else if (IN_QUERY_EXP.isInstance(exp)) {
				AttributeValueExp attribute = (AttributeValueExp) IN_QUERY_EXP_GET_CHECKED_VALUE.invoke(exp, null);
				ValueExp[] valueExps = (ValueExp[]) IN_QUERY_EXP_GET_GET_EXPLICIT_VALUES.invoke(exp, null);
				String[] values = new String[valueExps.length];
				for (int i = 0; i < valueExps.length; i++) {
					values[i] = ((StringValueExp) valueExps[i]).getValue();
				}
				getQueryFilter().filterAttributeEqualWithOneOf(attribute.getAttributeName(), values);
			} else if (OR_QUERY_EXP.isInstance(exp)) {
				QueryExp left = (QueryExp) OR_QUERY_EXP_GET_LEFT_EXP.invoke(exp, null);
				QueryExp right = (QueryExp) OR_QUERY_EXP_GET_RIGHT_EXP.invoke(exp, null);
				getQueryVisitor().or(new Object[] { left, right });
			} else if (AND_QUERY_EXP.isInstance(exp)) {
				QueryExp left = (QueryExp) AND_QUERY_EXP_GET_LEFT_EXP.invoke(exp, null);
				QueryExp right = (QueryExp) AND_QUERY_EXP_GET_RIGHT_EXP.invoke(exp, null);
				getQueryVisitor().and(new Object[] { left, right });
			} else {
				throw new IllegalArgumentException("Unsupported type " + exp.getClass().getName());
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new DataAccessException(e);
		}
	}
}
