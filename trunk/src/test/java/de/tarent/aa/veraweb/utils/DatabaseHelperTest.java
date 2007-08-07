/*
 * VerA.web,
 * Veranstaltungsmanagment VerA.web
 * Copyright (c) 2005-2007 tarent GmbH
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
 * interest in the program 'VerA.web'
 * Signature of Elmar Geese, 7 August 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.aa.veraweb.utils;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class DatabaseHelperTest extends TestCase {
	public void testEmptyOrder() {
		assertNull(DatabaseHelper.getOrder(null));
		assertNull(DatabaseHelper.getOrder(new ArrayList()));
	}

	public void testSingleOrder() {
		List order = new ArrayList();
		order.add("column1");
		assertEquals(" ORDER BY column1 ASC", DatabaseHelper.getOrder(order).clauseToString());
		
		order.add("DESC");
		assertEquals(" ORDER BY column1 DESC", DatabaseHelper.getOrder(order).clauseToString());
		
		order.set(1, "ASC");
		assertEquals(" ORDER BY column1 ASC", DatabaseHelper.getOrder(order).clauseToString());
		
		order.remove(1);
		assertEquals(" ORDER BY column1 ASC", DatabaseHelper.getOrder(order).clauseToString());
	}

	public void testMultiOrder() {
		List order = new ArrayList();
		order.add("column1");
		order.add("column2");
		order.add("DESC");
		
		assertEquals(" ORDER BY column1 ASC, column2 DESC", DatabaseHelper.getOrder(order).clauseToString());
	}
}
