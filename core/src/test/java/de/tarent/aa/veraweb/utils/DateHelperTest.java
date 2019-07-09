package de.tarent.aa.veraweb.utils;
import junit.framework.TestCase;

import java.sql.Timestamp;
import java.util.Calendar;

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
