package org.evolvis.veraweb.util;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class DelegationPasswordGeneratorTest {
    @Test
    public void test() {
        final String companyName = "tarent solutions GmbH";
        final String eventName = "3. Treffen der anonymen Scrum-Leugner";
        final Calendar cal = Calendar.getInstance();
        cal.set(1978, 0, 9);
        final Date eventBegin = cal.getTime();
        final String password = new DelegationPasswordGenerator().generatePassword(eventName, eventBegin, companyName);
        assertEquals("3. tar1978-01-09", password);
    }
}
