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

public class LdapQueryBuilder extends AbstractQueryContext implements QueryBuilder {
	private final StringBuffer stringBuffer = new StringBuffer();

	public LdapQueryBuilder() {
	}

	public void setQueryVisitor(QueryVisitor queryVisitor) {
		if (!(queryVisitor instanceof TraversingVisitor))
			throw new IllegalArgumentException("Can only interact with TraversingVisitor.");
		
		TraversingVisitor traversingVisitor = (TraversingVisitor)queryVisitor;
		
		traversingVisitor.setEmptyVisitorListener(null);
		traversingVisitor.setTermVisitorListener(null);
		traversingVisitor.setAndVisitorListener(new AndVisitorListener());
		traversingVisitor.setOrVisitorListener(new OrVisitorListener());
		traversingVisitor.setXorVisitorListener(new XorVisitorListener());
		traversingVisitor.setNotVisitorListener(new NotVisitorListener());
	}

	public void filterAttributeEqualWithPattern(String attribute, String pattern) {
		stringBuffer.append(attribute + "=" + pattern);
	}

	public void filterAttributeEqualWithOneOf(String attribute, String[] values) {
		// Not tested.
		throw new IllegalArgumentException("Currently not supported.");
	}

	public void filterAttributeLowerThanPattern(String attribute, String pattern) {
		// Not tested.
		throw new IllegalArgumentException("Currently not supported.");
	}

	public void filterAttributeLowerOrEqualsThanPattern(String attribute, String pattern) {
		// Not tested.
		throw new IllegalArgumentException("Currently not supported.");
	}

	public void filterAttributeGreaterThanPattern(String attribute, String pattern) {
		// Not tested.
		throw new IllegalArgumentException("Currently not supported.");
	}

	public void filterAttributeGreaterOrEqualsThanPattern(String attribute, String pattern) {
		// Not tested.
		throw new IllegalArgumentException("Currently not supported.");
	}

	private class AndVisitorListener extends AbstractVisitorListener {
		public void handleBeforeAll(int entryCount) {
			stringBuffer.append("(&(");
		}
		
		public void handleBeforeEntry(boolean first, boolean last) {
		}
		
		public void handleAfterEntry(boolean first, boolean last) {
			stringBuffer.append(last ? ")" : ")(");
		}
		
		public void handleAfterAll(int entryCount) {
			stringBuffer.append(")");
		}
	}

	private class OrVisitorListener extends AbstractVisitorListener {
		public void handleBeforeAll(int entryCount) {
			stringBuffer.append("(|(");
		}
		
		public void handleBeforeEntry(boolean first, boolean last) {
		}
		
		public void handleAfterEntry(boolean first, boolean last) {
			stringBuffer.append(last ? ")" : ")(");
		}
		
		public void handleAfterAll(int entryCount) {
			stringBuffer.append(")");
		}
	}

	private class XorVisitorListener extends AbstractVisitorListener {
		public void handleBeforeAll(int entryCount) {
			stringBuffer.append("(^(");
		}
		
		public void handleBeforeEntry(boolean first, boolean last) {
		}
		
		public void handleAfterEntry(boolean first, boolean last) {
			stringBuffer.append(last ? ")" : ")(");
		}
		
		public void handleAfterAll(int entryCount) {
			stringBuffer.append(")");
		}
	}

	private class NotVisitorListener extends AbstractVisitorListener {
		public void handleBeforeAll(int entryCount) {
			stringBuffer.append("(!(");
		}
		
		public void handleBeforeEntry(boolean first, boolean last) {
		}
		
		public void handleAfterEntry(boolean first, boolean last) {
			stringBuffer.append(last ? ")" : ")(");
		}
		
		public void handleAfterAll(int entryCount) {
			stringBuffer.append(")");
		}
	}

	public String getExpression() {
		return stringBuffer.toString();
	}

	public Object getQuery() {
		return getExpression();
	}
}
