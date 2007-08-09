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

package de.tarent.commons.dataaccess.query;

import de.tarent.commons.dataaccess.QueryProcessor;

public abstract class AbstractQueryContext implements QueryContext {
	private QueryProcessor queryProcessor;
	private QueryParser queryParser;
	private QueryVisitor queryVisitor;
	private QueryFilter queryFilter;

	public QueryProcessor getQueryProcessor() {
		return queryProcessor;
	}

	public void setQueryProcessor(QueryProcessor queryProcessor) {
		this.queryProcessor = queryProcessor;
	}

	public QueryParser getQueryParser() {
		return queryParser;
	}

	public void setQueryParser(QueryParser queryParser) {
		this.queryParser = queryParser;
	}

	public QueryVisitor getQueryVisitor() {
		return queryVisitor;
	}

	public void setQueryVisitor(QueryVisitor queryVisitor) {
		this.queryVisitor = queryVisitor;
	}

	public QueryFilter getQueryFilter() {
		return queryFilter;
	}

	public void setQueryFilter(QueryFilter queryFilter) {
		this.queryFilter = queryFilter;
	}
}
