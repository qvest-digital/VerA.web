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

import de.tarent.commons.dataaccess.Q;
import de.tarent.commons.dataaccess.QueryProcessor;
import junit.framework.TestCase;

public class LuceneQueryParserTest extends TestCase {
	public void testEmpty() {
		LuceneQueryParser luceneQueryParser = new LuceneQueryParser();
		CountingVisitor countingVisitor = new CountingVisitor(new TraversingVisitor());
		CountingFilter countingFilter = new CountingFilter(null);
		QueryProcessor.bind(null, luceneQueryParser, countingVisitor, countingFilter);
		
		luceneQueryParser.parse(null);
		
		assertEquals(0, countingVisitor.getCount());
		assertEquals(0, countingFilter.getCount());
	}

	public void testSimpleStringCompare() {
		LuceneQueryParser luceneQueryParser = new LuceneQueryParser();
		CountingVisitor countingVisitor = new CountingVisitor(new TraversingVisitor());
		CountingFilter countingFilter = new CountingFilter(null);
		QueryProcessor.bind(null, luceneQueryParser, countingVisitor, countingFilter);
		
		Object query = Q.lucene("key:value");
		
		luceneQueryParser.parse(query);
		assertEquals(0, countingVisitor.getCount());
		assertEquals(1, countingFilter.getCount());
	}

	public void testTwoStringCompare() {
		LuceneQueryParser luceneQueryParser = new LuceneQueryParser();
		CountingVisitor countingVisitor = new CountingVisitor(new TraversingVisitor());
		CountingFilter countingFilter = new CountingFilter(null);
		QueryProcessor.bind(null, luceneQueryParser, countingVisitor, countingFilter);
		
		Object or = Q.lucene("key:value OR key:value");
		Object and = Q.lucene("key:value AND key:value");
		
		luceneQueryParser.parse(or);
		assertEquals(1, countingVisitor.getCount());
		assertEquals(2, countingFilter.getCount());
		
		countingVisitor.reset();
		countingFilter.reset();
		
		luceneQueryParser.parse(and);
		assertEquals(1, countingVisitor.getCount());
		assertEquals(2, countingFilter.getCount());
	}
}
