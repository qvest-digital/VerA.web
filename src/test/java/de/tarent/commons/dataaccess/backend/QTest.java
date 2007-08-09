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

package de.tarent.commons.dataaccess.backend;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;

import de.tarent.commons.dataaccess.Q;
import junit.framework.TestCase;

/**
 * Testcase for the class {@link Q}.
 * 
 * @author Christoph Jerolimov, tarent GmbH
 */
public class QTest extends TestCase {
	/**
	 * Tests the {@link Q#query(String)} method with different
	 * query strings. Shows additional the results of different lucene queries.
	 * 
	 * @throws ParseException
	 */
	public void testLuceneString() throws ParseException {
		String queryA = "key01:value01";
		String expectedFieldA = "key01";
		String expectedTextA = "value01";
		
		String queryB = "firstname:Jim";
		String expectedFieldB = "firstname";
		String expectedTextB = "Jim";
		
		String queryC = "lastname:Muster*";
		String expectedFieldC = "lastname";
		String expectedTextC = "Muster*";
		
		String queryD = "lastname:*mann-O-mann";
		String expectedFieldD = "lastname";
		String expectedTextD = "*mann-O-mann";
		
		
		TermQuery query1a = (TermQuery) getQueryParser(new KeywordAnalyzer()).parse(queryA);
		TermQuery query2a = (TermQuery) getQueryParser(new SimpleAnalyzer()).parse(queryA);
		TermQuery query3a = (TermQuery) getQueryParser(new StandardAnalyzer()).parse(queryA);
		TermQuery query4a = (TermQuery) getQueryParser(new StopAnalyzer()).parse(queryA);
		TermQuery query5a = (TermQuery) getQueryParser(new WhitespaceAnalyzer()).parse(queryA);
		TermQuery query6a = (TermQuery) Q.lucene(queryA).getLuceneQuery();
		
		assertEquals("key01", query1a.getTerm().field());
		assertEquals("value01", query1a.getTerm().text());
		
		assertEquals("key01", query2a.getTerm().field());
		assertEquals("value", query2a.getTerm().text());
		
		assertEquals("key01", query3a.getTerm().field());
		assertEquals("value01", query3a.getTerm().text());
		
		assertEquals("key01", query4a.getTerm().field());
		assertEquals("value", query4a.getTerm().text());
		
		assertEquals("key01", query5a.getTerm().field());
		assertEquals("value01", query5a.getTerm().text());
		
		assertEquals(expectedFieldA, query6a.getTerm().field());
		assertEquals(expectedTextA, query6a.getTerm().text());
		
		
		TermQuery query1b = (TermQuery) getQueryParser(new KeywordAnalyzer()).parse(queryB);
		TermQuery query2b = (TermQuery) getQueryParser(new SimpleAnalyzer()).parse(queryB);
		TermQuery query3b = (TermQuery) getQueryParser(new StandardAnalyzer()).parse(queryB);
		TermQuery query4b = (TermQuery) getQueryParser(new StopAnalyzer()).parse(queryB);
		TermQuery query5b = (TermQuery) getQueryParser(new WhitespaceAnalyzer()).parse(queryB);
		TermQuery query6b = (TermQuery) Q.lucene(queryB).getLuceneQuery();
		
		assertEquals("firstname", query1b.getTerm().field());
		assertEquals("Jim", query1b.getTerm().text());
		
		assertEquals("firstname", query2b.getTerm().field());
		assertEquals("jim", query2b.getTerm().text());
		
		assertEquals("firstname", query3b.getTerm().field());
		assertEquals("jim", query3b.getTerm().text());
		
		assertEquals("firstname", query4b.getTerm().field());
		assertEquals("jim", query4b.getTerm().text());
		
		assertEquals("firstname", query5b.getTerm().field());
		assertEquals("Jim", query5b.getTerm().text());
		
		assertEquals(expectedFieldB, query6b.getTerm().field());
		assertEquals(expectedTextB, query6b.getTerm().text());

		
		PrefixQuery query1c = (PrefixQuery) getQueryParser(new KeywordAnalyzer()).parse(queryC);
		PrefixQuery query2c = (PrefixQuery) getQueryParser(new SimpleAnalyzer()).parse(queryC);
		PrefixQuery query3c = (PrefixQuery) getQueryParser(new StandardAnalyzer()).parse(queryC);
		PrefixQuery query4c = (PrefixQuery) getQueryParser(new StopAnalyzer()).parse(queryC);
		PrefixQuery query5c = (PrefixQuery) getQueryParser(new WhitespaceAnalyzer()).parse(queryC);
		PrefixQuery query6c = (PrefixQuery) Q.lucene(queryC).getLuceneQuery();
		
		assertEquals("lastname", query1c.getPrefix().field());
		assertEquals("Muster", query1c.getPrefix().text());
		
		assertEquals("lastname", query2c.getPrefix().field());
		assertEquals("Muster", query2c.getPrefix().text());
		
		assertEquals("lastname", query3c.getPrefix().field());
		assertEquals("Muster", query3c.getPrefix().text());
		
		assertEquals("lastname", query4c.getPrefix().field());
		assertEquals("Muster", query4c.getPrefix().text());
		
		assertEquals("lastname", query5c.getPrefix().field());
		assertEquals("Muster", query5c.getPrefix().text());
		
		assertEquals(expectedFieldC, query6c.getPrefix().field());
		assertEquals(expectedTextC, query6c.getPrefix().text() + "*");
		
		
		WildcardQuery query1d = (WildcardQuery) getQueryParser(new KeywordAnalyzer()).parse(queryD);
		WildcardQuery query2d = (WildcardQuery) getQueryParser(new SimpleAnalyzer()).parse(queryD);
		WildcardQuery query3d = (WildcardQuery) getQueryParser(new StandardAnalyzer()).parse(queryD);
		WildcardQuery query4d = (WildcardQuery) getQueryParser(new StopAnalyzer()).parse(queryD);
		WildcardQuery query5d = (WildcardQuery) getQueryParser(new WhitespaceAnalyzer()).parse(queryD);
		WildcardQuery query6d = (WildcardQuery) Q.lucene(queryD).getLuceneQuery();
		
		assertEquals("lastname", query1d.getTerm().field());
		assertEquals("*mann-O-mann", query1d.getTerm().text());
		
		assertEquals("lastname", query2d.getTerm().field());
		assertEquals("*mann-O-mann", query2d.getTerm().text());
		
		assertEquals("lastname", query3d.getTerm().field());
		assertEquals("*mann-O-mann", query3d.getTerm().text());
		
		assertEquals("lastname", query4d.getTerm().field());
		assertEquals("*mann-O-mann", query4d.getTerm().text());
		
		assertEquals("lastname", query5d.getTerm().field());
		assertEquals("*mann-O-mann", query5d.getTerm().text());
		
		assertEquals(expectedFieldD, query6d.getTerm().field());
		assertEquals(expectedTextD, query6d.getTerm().text());
	}

	private QueryParser getQueryParser(Analyzer analyzer) {
		QueryParser queryParser = new QueryParser("default", analyzer);
		queryParser.setAllowLeadingWildcard(true);
		queryParser.setLowercaseExpandedTerms(false);
		return queryParser;
	}
}
