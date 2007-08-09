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
import de.tarent.commons.dataaccess.query.QueryFilter;
import de.tarent.commons.dataaccess.query.QueryParser;
import de.tarent.commons.dataaccess.query.QueryVisitor;

public class CountingFilter extends AbstractQueryContext implements QueryFilter {
	private final QueryFilter delegate;
	private int count = 0;

	public CountingFilter(QueryFilter delegate) {
		this.delegate = delegate;
	}

	public void filterAttributeEqualWithPattern(String attribute, String pattern) {
		count++;
		if (delegate != null)
			delegate.filterAttributeEqualWithPattern(attribute, pattern);
	}

	public void filterAttributeEqualWithOneOf(String attribute, String[] values) {
		count++;
		if (delegate != null)
			delegate.filterAttributeEqualWithOneOf(attribute, values);
	}

	public void filterAttributeLowerThanPattern(String attribute, String pattern) {
		count++;
		if (delegate != null)
			delegate.filterAttributeLowerThanPattern(attribute, pattern);
	}

	public void filterAttributeLowerOrEqualsThanPattern(String attribute, String pattern) {
		count++;
		if (delegate != null)
			delegate.filterAttributeLowerOrEqualsThanPattern(attribute, pattern);
	}

	public void filterAttributeGreaterThanPattern(String attribute, String pattern) {
		count++;
		if (delegate != null)
			delegate.filterAttributeGreaterThanPattern(attribute, pattern);
	}

	public void filterAttributeGreaterOrEqualsThanPattern(String attribute, String pattern) {
		count++;
		if (delegate != null)
			delegate.filterAttributeGreaterOrEqualsThanPattern(attribute, pattern);
	}

	public void reset() {
		count = 0;
	}

	public int getCount() {
		return count;
	}

	public void setQueryParser(QueryParser queryParser) {
		super.setQueryParser(queryParser);
		if (delegate != null)
			delegate.setQueryParser(queryParser);
	}

	public void setQueryVisitor(QueryVisitor queryVisitor) {
		super.setQueryVisitor(queryVisitor);
		if (delegate != null)
			delegate.setQueryVisitor(queryVisitor);
	}

	public void setQueryFilter(QueryFilter queryFilter) {
		super.setQueryFilter(queryFilter);
		if (delegate != null)
			delegate.setQueryFilter(queryFilter);
	}
}
