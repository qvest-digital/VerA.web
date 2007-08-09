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

package de.tarent.commons.dataaccess.backend.impl;

import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

import de.tarent.commons.dataaccess.query.AbstractQueryContext;
import de.tarent.commons.dataaccess.query.AbstractVisitorListener;
import de.tarent.commons.dataaccess.query.QueryFilter;
import de.tarent.commons.dataaccess.query.FilterSummery;
import de.tarent.commons.dataaccess.query.QueryVisitor;
import de.tarent.commons.dataaccess.query.impl.TraversingVisitor;
import de.tarent.commons.datahandling.BeanAccessor;

public class MemoryObjectFilter extends AbstractQueryContext implements QueryFilter {
	private static WeakHashMap beanAccessorCache = new WeakHashMap();

	private Object o;
	private Stack stack;

	public MemoryObjectFilter(Object o) {
		this.o = o;
		this.stack = new Stack();
		this.stack.push(new FilterSummery.AND());
	}

	public void setQueryVisitor(QueryVisitor queryVisitor) {
		TraversingVisitor traversingVisitor = (TraversingVisitor)queryVisitor;
		
		traversingVisitor.setEmptyVisitorListener(null);
		traversingVisitor.setTermVisitorListener(null);
		traversingVisitor.setAndVisitorListener(new AndVisitorListener());
		traversingVisitor.setOrVisitorListener(new OrVisitorListener());
		traversingVisitor.setXorVisitorListener(new XorVisitorListener());
		traversingVisitor.setNotVisitorListener(new NotVisitorListener());
	}

	public void filterAttributeEqualWithPattern(String attribute, String pattern) {
		boolean match;
		Object value = getAttribute(attribute);
		if (value != null) {
			match = value.equals(pattern);
		} else {
			match = pattern == null || pattern.length() == 0;
		}
		
		peekStackEntry().addMatchResult(match);
	}

	public void filterAttributeEqualWithOneOf(String attribute, String[] values) {
		boolean match;
		Object value = getAttribute(attribute);
		if (value != null) {
			match = matchOneEntry(value, values);
		} else {
			match = isOneEntryEmpty(values);
		}
		
		peekStackEntry().addMatchResult(match);
	}

	private boolean matchOneEntry(Object value, String[] values) {
		for (int i = 0; i < values.length; i++) {
			if (value.equals(values[i]))
				return true;
		}
		return false;
	}

	private boolean isOneEntryEmpty(String[] values) {
		for (int i = 0; i < values.length; i++) {
			if (values[i] == null || values[i].length() == 0)
				return true;
		}
		return false;
	}

	public void filterAttributeLowerThanPattern(String attribute, String pattern) {
		throw new IllegalArgumentException(getClass().getSimpleName() + " does not support this filter.");
	}

	public void filterAttributeLowerOrEqualsThanPattern(String attribute, String pattern) {
		throw new IllegalArgumentException(getClass().getSimpleName() + " does not support this filter.");
	}

	public void filterAttributeGreaterThanPattern(String attribute, String pattern) {
		throw new IllegalArgumentException(getClass().getSimpleName() + " does not support this filter.");
	}

	public void filterAttributeGreaterOrEqualsThanPattern(String attribute, String pattern) {
		throw new IllegalArgumentException(getClass().getSimpleName() + " does not support this filter.");
	}

	private Object getAttribute(String attribute) {
		if (o instanceof Map) {
			return ((Map) o).get(attribute);
		} else {
			BeanAccessor beanAccessor = (BeanAccessor) beanAccessorCache.get(o.getClass());
			if (beanAccessor == null) {
				beanAccessor = new BeanAccessor(o.getClass());
				beanAccessorCache.put(o.getClass(), beanAccessor);
			}
			if (beanAccessor.getGetableProperties().contains(attribute))
				return beanAccessor.getProperty(o, attribute);
			else
				return null;
		}
	}

	public boolean match() {
		if (stack.size() != 1)
			throw new IllegalStateException("Illegal stack size " + stack.size() + ". Must be 1.");
		return peekStackEntry().getMatchResult();
	}

	public void pushStackEntry(FilterSummery stackEntry) {
		stack.push(stackEntry);
	}

	public FilterSummery peekStackEntry() {
		return (FilterSummery) stack.peek();
	}

	public FilterSummery popStackEntry() {
		return (FilterSummery) stack.pop();
	}
	
	private class AndVisitorListener extends AbstractVisitorListener {
		public void handleBeforeAll(int entryCount) {
			pushStackEntry(new FilterSummery.AND());
		}
		
		public void handleAfterAll(int entryCount) {
			boolean match = popStackEntry().getMatchResult();
			peekStackEntry().addMatchResult(match);
		}
	}

	private class OrVisitorListener extends AbstractVisitorListener {
		public void handleBeforeAll(int entryCount) {
			pushStackEntry(new FilterSummery.OR());
		}
		
		public void handleAfterAll(int entryCount) {
			boolean match = popStackEntry().getMatchResult();
			peekStackEntry().addMatchResult(match);
		}
	}

	private class XorVisitorListener extends AbstractVisitorListener {
		public void handleBeforeAll(int entryCount) {
			pushStackEntry(new FilterSummery.XOR());
		}
		
		public void handleAfterAll(int entryCount) {
			boolean match = popStackEntry().getMatchResult();
			peekStackEntry().addMatchResult(match);
		}
	}

	private class NotVisitorListener extends AbstractVisitorListener {
		public void handleBeforeAll(int entryCount) {
			pushStackEntry(new FilterSummery.NOT());
		}
		
		public void handleAfterAll(int entryCount) {
			boolean match = popStackEntry().getMatchResult();
			peekStackEntry().addMatchResult(match);
		}
	}
}
