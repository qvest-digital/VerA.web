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

public class CountingVisitor extends AbstractQueryContext implements QueryVisitor {
	private final QueryVisitor delegate;
	private int count = 0;

	public CountingVisitor(QueryVisitor delegate) {
		this.delegate = delegate;
	}

	public void empty() {
		count++;
		if (delegate != null)
			delegate.empty();
	}

	public void term(Object object) {
		count++;
		if (delegate != null)
			delegate.term(object);
	}

	public void and(Object[] objects) {
		count++;
		if (delegate != null)
			delegate.and(objects);
	}

	public void or(Object[] objects) {
		count++;
		if (delegate != null)
			delegate.or(objects);
	}

	public void xor(Object[] objects) {
		count++;
		if (delegate != null)
			delegate.xor(objects);
	}

	public void not(Object[] objects) {
		count++;
		if (delegate != null)
			delegate.not(objects);
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
