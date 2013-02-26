/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
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
