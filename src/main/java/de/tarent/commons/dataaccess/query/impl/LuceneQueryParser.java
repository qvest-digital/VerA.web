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

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.BooleanClause.Occur;

import de.tarent.commons.dataaccess.DataAccessException;
import de.tarent.commons.dataaccess.Q;
import de.tarent.commons.dataaccess.query.AbstractQueryContext;
import de.tarent.commons.dataaccess.query.QueryParser;

public class LuceneQueryParser extends AbstractQueryContext implements QueryParser {
	public void parse(Object o) {
		try {
			if (o instanceof Q.LuceneQuery) {
				o = ((Q.LuceneQuery) o).getLuceneQuery();
			} else if (o instanceof String) {
				o = Q.lucene((String) o).getLuceneQuery();
			}
			
			if (o == null) {
				// Nothing
			} else if (o instanceof TermQuery) {
				Term term = ((TermQuery) o).getTerm();
				getQueryFilter().filterAttributeEqualWithPattern(term.field(), term.text());
			} else if (o instanceof PrefixQuery) {
				Term term = ((PrefixQuery) o).getPrefix();
				getQueryFilter().filterAttributeEqualWithPattern(term.field(), term.text() + "*");
			} else if (o instanceof WildcardQuery) {
				Term term = ((WildcardQuery) o).getTerm();
				getQueryFilter().filterAttributeEqualWithPattern(term.field(), term.text());
			} else if (o instanceof BooleanQuery) {
				BooleanClause booleanClause[] = ((BooleanQuery)o).getClauses();
				List and = new ArrayList(booleanClause.length);
				List or = new ArrayList(booleanClause.length);
				List not = new ArrayList(booleanClause.length);
				
				for (int i = 0; i < booleanClause.length; i++) {
					if (booleanClause[i].getOccur() == Occur.MUST) {
						and.add(booleanClause[i].getQuery());
					} else if (booleanClause[i].getOccur() == Occur.SHOULD) {
						or.add(booleanClause[i].getQuery());
					} else if (booleanClause[i].getOccur() == Occur.MUST_NOT) {
						not.add(booleanClause[i].getQuery());
					}
				}
				
				if (!and.isEmpty())
					getQueryVisitor().and(and.toArray());
				if (!or.isEmpty())
					getQueryVisitor().or(or.toArray());
				if (!not.isEmpty())
					getQueryVisitor().not(not.toArray());
			} else {
				throw new IllegalArgumentException("Unsupported type " + o.getClass().getName());
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new DataAccessException(e);
		}
	}
}
