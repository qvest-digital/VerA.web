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
