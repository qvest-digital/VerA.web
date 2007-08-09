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

import javax.management.AttributeValueExp;
import javax.management.Query;
import javax.management.QueryExp;
import javax.management.StringValueExp;
import javax.management.ValueExp;

import de.tarent.commons.dataaccess.QueryProcessor;
import de.tarent.commons.dataaccess.query.impl.ManagmentQueryParser;

import junit.framework.TestCase;

public class ManagmentQueryParserTest extends TestCase {
	public void testEmpty() {
		ManagmentQueryParser managmentQueryParser = new ManagmentQueryParser();
		CountingVisitor countingVisitor = new CountingVisitor(new TraversingVisitor());
		CountingFilter countingFilter = new CountingFilter(null);
		QueryProcessor.bind(null, managmentQueryParser, countingVisitor, countingFilter);
		
		managmentQueryParser.parse(null);
		assertEquals(0, countingVisitor.getCount());
		assertEquals(0, countingFilter.getCount());
	}

	public void testSimpleStringCompare() {
		ManagmentQueryParser managmentQueryParser = new ManagmentQueryParser();
		CountingVisitor countingVisitor = new CountingVisitor(new TraversingVisitor());
		CountingFilter countingFilter = new CountingFilter(null);
		QueryProcessor.bind(null, managmentQueryParser, countingVisitor, countingFilter);
		
		QueryExp queryExp = Query.anySubString(
				new AttributeValueExp("key"),
				new StringValueExp("value"));
		
		managmentQueryParser.parse(queryExp);
		assertEquals(0, countingVisitor.getCount());
		assertEquals(1, countingFilter.getCount());
	}

	public void testTwoStringCompare() {
		ManagmentQueryParser managmentQueryParser = new ManagmentQueryParser();
		CountingVisitor countingVisitor = new CountingVisitor(new TraversingVisitor());
		CountingFilter countingFilter = new CountingFilter(null);
		QueryProcessor.bind(null, managmentQueryParser, countingVisitor, countingFilter);
		
		QueryExp queryExp = Query.anySubString(
				new AttributeValueExp("key"),
				new StringValueExp("value"));
		QueryExp or = Query.or(queryExp, queryExp);
		QueryExp and = Query.and(queryExp, queryExp);
		
		managmentQueryParser.parse(or);
		assertEquals(1, countingVisitor.getCount());
		assertEquals(2, countingFilter.getCount());
		
		countingVisitor.reset();
		countingFilter.reset();
		
		managmentQueryParser.parse(and);
		assertEquals(1, countingVisitor.getCount());
		assertEquals(2, countingFilter.getCount());
	}

	public void testLowerAndGreaterThan() {
		// JMX query
		QueryExp queryExp1 = Query.lt(new AttributeValueExp("c1"), new StringValueExp("v1"));
		QueryExp queryExp2 = Query.leq(new AttributeValueExp("c2"), new StringValueExp("v2"));
		QueryExp queryExp3 = Query.gt(new AttributeValueExp("c3"), new StringValueExp("v3"));
		QueryExp queryExp4 = Query.geq(new AttributeValueExp("c4"), new StringValueExp("v4"));
		
		QueryExp queryExp5 = Query.and(queryExp1, queryExp2);
		QueryExp queryExp6 = Query.and(queryExp3, queryExp4);
		QueryExp queryExp7 = Query.and(queryExp5, queryExp6);
		
		assertEquals("c1 < 'v1'", getSql(queryExp1));
		assertEquals("c2 <= 'v2'", getSql(queryExp2));
		assertEquals("c3 > 'v3'", getSql(queryExp3));
		assertEquals("c4 >= 'v4'", getSql(queryExp4));
		assertEquals("(c1 < 'v1' AND c2 <= 'v2')", getSql(queryExp5));
		assertEquals("(c3 > 'v3' AND c4 >= 'v4')", getSql(queryExp6));
		assertEquals("((c1 < 'v1' AND c2 <= 'v2') AND (c3 > 'v3' AND c4 >= 'v4'))", getSql(queryExp7));
	}

	public void testEmptyInExpression() {
		QueryExp queryExp = Query.in(
				new AttributeValueExp("column"),
				new ValueExp[] {} );
		
		assertEquals("FALSE", getSql(queryExp));
	}

	public void testInExpression() {
		QueryExp queryExp = Query.in(
				new AttributeValueExp("column"),
				new ValueExp[] {
					new StringValueExp("value1"),
					new StringValueExp("value2"),
					new StringValueExp("value3") } );
		
		assertEquals("column IN ('value1', 'value2', 'value3')", getSql(queryExp));
	}

	private String getSql(QueryExp queryExp) {
		ManagmentQueryParser managmentQueryParser = new ManagmentQueryParser();
		TraversingVisitor traversingVisitor = new TraversingVisitor();
		SqlQueryBuilder sqlQueryBuilder = new SqlQueryBuilder();
		QueryProcessor.bind(null, managmentQueryParser, traversingVisitor, sqlQueryBuilder);
		managmentQueryParser.parse(queryExp);
		
		return (String) sqlQueryBuilder.getExpression();
	}
}
