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

import junit.framework.TestCase;
import de.tarent.commons.dataaccess.query.FilterSummery;

public class FilterSummeryTest extends TestCase {
	public void testAnd() {
		FilterSummery filterSummery = new FilterSummery.AND();
		
		assertTrue(filterSummery.getMatchResult());
		assertTrue(filterSummery.addMatchResult(true));
		assertTrue(filterSummery.addMatchResult(true));
		assertFalse(filterSummery.addMatchResult(false));
		assertFalse(filterSummery.addMatchResult(false));
		assertFalse(filterSummery.addMatchResult(true));
		assertFalse(filterSummery.addMatchResult(true));
	}

	public void testOr() {
		FilterSummery filterSummery = new FilterSummery.OR();
		
		assertFalse(filterSummery.getMatchResult());
		assertTrue(filterSummery.addMatchResult(true));
		assertTrue(filterSummery.addMatchResult(true));
		assertTrue(filterSummery.addMatchResult(false));
		assertTrue(filterSummery.addMatchResult(false));
		assertTrue(filterSummery.addMatchResult(true));
		assertTrue(filterSummery.addMatchResult(true));
		
		FilterSummery filterSummery2 = new FilterSummery.OR();
		
		assertFalse(filterSummery2.getMatchResult());
		assertFalse(filterSummery2.addMatchResult(false));
		assertFalse(filterSummery2.addMatchResult(false));
		assertTrue(filterSummery2.addMatchResult(true));
		assertTrue(filterSummery2.addMatchResult(true));
		assertTrue(filterSummery2.addMatchResult(false));
		assertTrue(filterSummery2.addMatchResult(false));
	}

	public void testNot() {
		FilterSummery filterSummery = new FilterSummery.NOT();
		
		assertTrue(filterSummery.getMatchResult());
		assertTrue(filterSummery.addMatchResult(false));
		assertTrue(filterSummery.addMatchResult(false));
		assertFalse(filterSummery.addMatchResult(true));
		assertFalse(filterSummery.addMatchResult(true));
		assertFalse(filterSummery.addMatchResult(false));
		assertFalse(filterSummery.addMatchResult(false));
		
		FilterSummery filterSummery2 = new FilterSummery.NOT();
		
		assertTrue(filterSummery2.getMatchResult());
		assertTrue(filterSummery2.addMatchResult(false));
		assertTrue(filterSummery2.addMatchResult(false));
		assertFalse(filterSummery2.addMatchResult(true));
		assertFalse(filterSummery2.addMatchResult(true));
		assertFalse(filterSummery2.addMatchResult(false));
		assertFalse(filterSummery2.addMatchResult(false));
	}

	public void testXor() {
		FilterSummery filterSummery = new FilterSummery.XOR();
		
		assertTrue(filterSummery.getMatchResult());
		assertTrue(filterSummery.addMatchResult(false));
		assertTrue(filterSummery.addMatchResult(false));
		assertFalse(filterSummery.addMatchResult(true));
		assertTrue(filterSummery.addMatchResult(true));
		assertTrue(filterSummery.addMatchResult(false));
		assertTrue(filterSummery.addMatchResult(false));
		
		FilterSummery filterSummery2 = new FilterSummery.XOR();
		
		assertTrue(filterSummery2.getMatchResult());
		assertTrue(filterSummery2.addMatchResult(false));
		assertTrue(filterSummery2.addMatchResult(false));
		assertFalse(filterSummery2.addMatchResult(true));
		assertTrue(filterSummery2.addMatchResult(true));
		assertTrue(filterSummery2.addMatchResult(false));
		assertTrue(filterSummery2.addMatchResult(false));
	}
}
