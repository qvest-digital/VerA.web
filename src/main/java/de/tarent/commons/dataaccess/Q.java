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

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;

import de.tarent.commons.dataaccess.query.impl.LdapQueryBuilder;
import de.tarent.commons.dataaccess.query.impl.LdapQueryParser;
import de.tarent.commons.dataaccess.query.impl.LuceneQueryBuilder;
import de.tarent.commons.dataaccess.query.impl.LuceneQueryParser;
import de.tarent.commons.dataaccess.query.impl.ManagmentQueryBuilder;
import de.tarent.commons.dataaccess.query.impl.ManagmentQueryParser;

/**
 * <p>Q is the seventeenth letter of the modern Latin alphabet and the name of
 * this class which support you to build queries.</p>
 * 
 * <p>The main functions of this class are {@link #lucene(String)} and
 * {@link #ldap(String)} which generates small boxing instances around your
 * query string. Without this boxing it would not be possible to indicate which
 * query type (lucene, ldap, ...) u use.</p>
 * 
 * <p>See {@link LuceneQueryParser} and {@link LuceneQueryBuilder} for more
 * informations about the lucene integration.</p>
 * 
 * <p>See {@link LdapQueryParser} and {@link LdapQueryBuilder} for more
 * informations about the LDAP integration.</p>
 * 
 * <p>Note that not all query types require an boxing class, for example the
 * query classes of the java managment extension (package javax.management).
 * See {@link ManagmentQueryParser} and {@link ManagmentQueryBuilder} for
 * more information about these integration.</p>
 * 
 * @author Christoph Jerolimov, tarent GmbH
 * @author Timo Pick, tarent GmbH
 */
public class Q {
	/**
	 * Return an new {@link LuceneQuery} boxing instance which contains your
	 * lucene <code>queryString</code>.
	 * 
	 * @param queryString Lucene query.
	 * @return Boxed query.
	 */
	public static LuceneQuery lucene(String queryString) {
		return new LuceneQuery(queryString);
	}

	/**
	 * Return an new {@link LdapQuery} boxing instance which contains your
	 * LDAP <code>queryString</code>.
	 * 
	 * @param queryString Lucene query.
	 * @return Boxed query.
	 */
	public static LdapQuery ldap(String queryString) {
		return new LdapQuery(queryString);
	}

	/**
	 * Lucene query boxing class.
	 * 
	 * @see #getQueryString()
	 * @see #getLuceneQuery()
	 */
	public static class LuceneQuery extends Q {
		/** The query string */
		private final String queryString;
		
		/** Create an new lucene query boxing instance. */
		private LuceneQuery(String queryString) {
			this.queryString = queryString;
		}
		
		/** Returns the query string. */
		public String getQueryString() {
			return queryString;
		}
		
		/**
		 * <p>Return an new lucene {@link Query} instance with the following
		 * creation process:</p>
		 * 
		 * <pre><code>
		 *   QueryParser queryParser = new QueryParser("default", new KeywordAnalyzer());
		 *   queryParser.setAllowLeadingWildcard(true);
		 *   queryParser.setLowercaseExpandedTerms(false);
		 *   return queryParser.parse(getQueryString());
		 * </code></pre>
		 * 
		 * <p>See lucene documentation or the {@link QTest} case for more
		 * information about these parameters.</p>
		 * 
		 * @return
		 */
		public Query getLuceneQuery() {
			try {
				QueryParser queryParser = new QueryParser("default", new KeywordAnalyzer());
				queryParser.setAllowLeadingWildcard(true);
				queryParser.setLowercaseExpandedTerms(false);
				return queryParser.parse(getQueryString());
			} catch (ParseException e) {
				throw new DataAccessException(e);
			}
		}
	}

	/**
	 * LDAP query boxing class.
	 * 
	 * @see #getQueryString()
	 */
	public static class LdapQuery extends Q {
		/** The query string */
		private final String queryString;
		
		/** Create an new LDAP query boxing instance. */
		private LdapQuery(String queryString) {
			this.queryString = queryString;
		}
		
		/** Returns the query string. */
		public String getQueryString() {
			return queryString;
		}
	}
}
