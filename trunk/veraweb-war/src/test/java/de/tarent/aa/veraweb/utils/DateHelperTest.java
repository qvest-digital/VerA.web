/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
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
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.aa.veraweb.utils;

import java.sql.Timestamp;
import java.util.Calendar;

import junit.framework.TestCase;

public class DateHelperTest extends TestCase {
	public void testEmptyDate() {
		/* invalid test case here: result returns current date and not what is defined as parameters
		assertTime(null, 0, 0, 30);
		assertTime("", 0, 0, 30);
		*/
	}

	public void testDotDate() {
		for (int i = 0; i < 24; i++) {
			assertTime(i + ".00", i, 0, 0);
		}
		for (int i = 0; i < 60; i++) {
			assertTime("14." + i, 14, i, 0);
		}
	}

	public void testColonDate() {
		for (int i = 0; i < 24; i++) {
			assertTime(i + ":00", i, 0, 0);
		}
		for (int i = 0; i < 60; i++) {
			assertTime("14:" + i, 14, i, 0);
		}
	}

	public void testOnlyHourDate() {
		for (int i = 0; i < 24; i++) {
			assertTime(i + "", i, 0, 0);
		}
		assertTime("24", 0, 0, 30);
	}

	private void assertTime(String input, int h, int m, int s) {
		Timestamp date = new Timestamp(Calendar.getInstance().getTimeInMillis());
		DateHelper.addTimeToDate(date, input, null);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		assertEquals(h, calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(m, calendar.get(Calendar.MINUTE));
		assertEquals(s, calendar.get(Calendar.SECOND));
	}
}
