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

package de.tarent.commons.dataaccess;

import javax.management.AttributeValueExp;
import javax.management.Query;
import javax.management.StringValueExp;

import de.tarent.commons.dataaccess.query.QueryParser;
import de.tarent.commons.dataaccess.query.impl.LdapQueryParser;
import de.tarent.commons.dataaccess.query.impl.LuceneQueryParser;
import de.tarent.commons.dataaccess.query.impl.ManagmentQueryParser;
import de.tarent.commons.dataaccess.query.impl.NullQueryParser;
import junit.framework.TestCase;

/**
 * Tests the class {@link QueryProcessor}. Has many methods copied.
 * 
 * @author Christoph Jerolimov, tarent GmbH
 */
public class QueryProcessorTest extends TestCase {
	/**
	 * Internal class of <code>org.apache.lucene.search.Query</code>. Only if
	 * lucene is in the class path we will support also support lucene as an
	 * query dialect.
	 */
	private static final Class LUCENE_QUERY = getQueryClassOrNull("org.apache.lucene.search.Query");

	/**
	 * Internal class of <code>javax.managment.QueryEval</code>.
	 */
	private static final Class MANAGMENT_QUERY = getQueryClassOrNull("javax.management.QueryEval");

	public void testCreateQueryParser() {
		QueryParser queryParser = createQueryParser(Query.anySubString(
				new AttributeValueExp(""),
				new StringValueExp("")));
		
		assertEquals(queryParser.getClass(), ManagmentQueryParser.class);
	}

	protected QueryParser createQueryParser(Object baseExpr) {
		if (baseExpr == null)
			return new NullQueryParser();
		else if (baseExpr instanceof Q.LuceneQuery)
			return new LuceneQueryParser();
		else if (baseExpr instanceof Q.LdapQuery)
			return new LdapQueryParser();
		else if (LUCENE_QUERY != null && LUCENE_QUERY.isInstance(baseExpr))
			return new LuceneQueryParser();
		else if (MANAGMENT_QUERY != null && MANAGMENT_QUERY.isInstance(baseExpr))
			return new ManagmentQueryParser();
		else
			throw new DataAccessException("Unknown query type " + baseExpr.getClass().getName() + " (" + baseExpr + ")");
	}

	/**
	 * Return a {@link Class} instance for the given <code>className</code> or
	 * null if any exeption is happend.
	 * 
	 * @param className Class name for loading.
	 * @return Class instance or null.
	 */
	private static Class getQueryClassOrNull(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		} catch (RuntimeException t) {
			return null;
		}
	}
}
