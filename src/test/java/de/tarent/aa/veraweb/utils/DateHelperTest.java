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

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

public class DateHelperTest extends TestCase {
	public void testEmptyDate() {
		assertTime(null, 0, 0, 30);
		assertTime("", 0, 0, 30);
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
		Date date = new Date();
		DateHelper.addTimeToDate(date, input, null);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		assertEquals(h, calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(m, calendar.get(Calendar.MINUTE));
		assertEquals(s, calendar.get(Calendar.SECOND));
	}
}
