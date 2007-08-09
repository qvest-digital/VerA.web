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

public class LdapQueryBuilderTest extends TestCase {
	public void testManagmentQueryParserAndLdapFilter() throws Exception {
		// JMX query
		QueryExp queryExp1 = Query.initialSubString(new AttributeValueExp("mail"), new StringValueExp("joe"));
		QueryExp queryExp2 = Query.match(new AttributeValueExp("c"), new StringValueExp("germany"));
		QueryExp managmentQuery = Query.and(queryExp1, queryExp2);
		
		ManagmentQueryParser managmentQueryParser = new ManagmentQueryParser();
		TraversingVisitor traversingVisitor = new TraversingVisitor();
		LdapQueryBuilder ldapQueryBuilder = new LdapQueryBuilder();
		QueryProcessor.bind(null, managmentQueryParser, traversingVisitor, ldapQueryBuilder);
		
		managmentQueryParser.parse(managmentQuery);
		
		assertEquals("(&(mail=joe*)(c=germany))", ldapQueryBuilder.getExpression());
	}

	public void testLuceneQueryParserAndLdapFilter() {
		// Lucene query
		Object luceneQuery = Q.lucene("mail:joe* AND c:germany");
		
		LuceneQueryParser luceneQueryParser = new LuceneQueryParser();
		TraversingVisitor traversingVisitor = new TraversingVisitor();
		LdapQueryBuilder ldapQueryBuilder = new LdapQueryBuilder();
		QueryProcessor.bind(null, luceneQueryParser, traversingVisitor, ldapQueryBuilder);
		
		luceneQueryParser.parse(luceneQuery);
		
		assertEquals("(&(mail=joe*)(c=germany))", ldapQueryBuilder.getExpression());
	}
}
