package de.tarent.octopus.custom.beans;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.BeanFactory;
import junit.framework.TestCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Diese Klasse testet die Funktionalität der {@link BeanFactory}.
 *
 * @author Christoph Jerolimov
 */
public class BeanFactoryTest extends TestCase {
    public void testGetDateSimpleCorrect() throws Exception {
        assertDate("01.01.2000", "1.1.00");
        assertDate("01.01.2000", "1.1.2000");
    }

    public void testGetDateSimpleInvalid() throws Exception {
        try {
            assertDate("32.01.2000", "32.1.00");
            assertTrue(false);
        } catch (BeanException e) {
            assertTrue(true);
        }
        try {
            assertDate("32.01.2000", "32.1.2000");
            assertTrue(false);
        } catch (BeanException e) {
            assertTrue(true);
        }
    }

    public void testGetDateTwoDigitsGerman() throws Exception {
        for (int i = 0; i <= 99; i++) {
            assertDate(
              "01.01." + (i < 10 ? "0" + i : "" + i),
              "01.01." + (i < 10 ? "0" + i : "" + i));
        }
    }

    public void testGetDateTwoDigitsEnglish() throws Exception {
        for (int i = 0; i <= 99; i++) {
            assertDate(
              "01.01." + (i < 10 ? "0" + i : "" + i),
              "01-01-" + (i < 10 ? "0" + i : "" + i));
        }
    }

    public void testGetDateFourDigitsGerman() throws Exception {
        for (int i = 1500; i <= 2500; i++) {
            assertDate("01.01." + i, "01.01." + i);
        }
    }

    public void testGetDateFourDigitsEnglish() throws Exception {
        for (int i = 1500; i <= 2500; i++) {
            assertDate("01.01." + i, "01-01-" + i);
        }
    }

    private void assertDate(String expectedInput, String actualInput) throws BeanException, ParseException {
        Date expected = new SimpleDateFormat("d.M.y").parse(expectedInput);
        Date actual = (Date) BeanFactory.transform(actualInput, Date.class);
        assertEquals(expected, actual);
    }
}
