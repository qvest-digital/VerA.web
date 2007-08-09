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

import de.tarent.commons.dataaccess.Q;
import de.tarent.commons.dataaccess.QueryProcessor;
import de.tarent.commons.dataaccess.query.impl.LuceneQueryParser;
import de.tarent.commons.dataaccess.query.impl.ManagmentQueryParser;
import junit.framework.TestCase;

public class SqlQueryBuilderTest extends TestCase {
	public void testManagmentQueryParserAndSqlFilter() throws Exception {
		// JMX query
		QueryExp queryExp1 = Query.match(new AttributeValueExp("lastname"), new StringValueExp("Müller"));
		QueryExp queryExp2 = Query.initialSubString(new AttributeValueExp("lastname"), new StringValueExp("Muster"));
		QueryExp queryExp3 = Query.finalSubString(new AttributeValueExp("lastname"), new StringValueExp("mann"));
		QueryExp queryExp4 = Query.finalSubString(new AttributeValueExp("lastname"), new StringValueExp("frau"));
		QueryExp queryExp5 = Query.match(new AttributeValueExp("firstname"), new StringValueExp("Detlef"));
		QueryExp managmentQuery = Query.and(Query.or(queryExp1, Query.or(queryExp2, Query.or(queryExp3, queryExp4))), queryExp5);
		
		ManagmentQueryParser managmentQueryParser = new ManagmentQueryParser();
		TraversingVisitor traversingVisitor = new TraversingVisitor();
		SqlQueryBuilder sqlQueryBuilder = new SqlQueryBuilder();
		QueryProcessor.bind(null, managmentQueryParser, traversingVisitor, sqlQueryBuilder);
		managmentQueryParser.parse(managmentQuery);
		
		assertEquals(
				"((lastname=\'Müller\' OR (lastname LIKE \'Muster%\'" +
				" OR (lastname LIKE \'%mann\' OR lastname LIKE \'%frau\')))" +
				" AND firstname=\'Detlef\')",
				sqlQueryBuilder.getExpression());
	}

	public void testLuceneQueryParserAndSqlFilter() throws Exception {
		// Lucene query
		Object luceneQuery = Q.lucene(
				"(lastname:Müller" +
				" OR lastname:Muster*" +
				" OR lastname:*mann" +
				" OR lastname:*frau)" +
				" AND firstname:Detlef");
		
		LuceneQueryParser luceneQueryParser = new LuceneQueryParser();
		TraversingVisitor traversingVisitor = new TraversingVisitor();
		SqlQueryBuilder sqlQueryBuilder = new SqlQueryBuilder();
		QueryProcessor.bind(null, luceneQueryParser, traversingVisitor, sqlQueryBuilder);
		luceneQueryParser.parse(luceneQuery);
		
		assertEquals(
				"((lastname=\'Müller\'" +
				" OR lastname LIKE \'Muster%\'" +
				" OR lastname LIKE \'%mann\'" +
				" OR lastname LIKE \'%frau\')" +
				" AND firstname=\'Detlef\')",
				sqlQueryBuilder.getExpression());
	}
}
