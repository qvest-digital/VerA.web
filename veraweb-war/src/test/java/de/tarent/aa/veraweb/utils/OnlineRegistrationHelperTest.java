package de.tarent.aa.veraweb.utils;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

public class OnlineRegistrationHelperTest extends TestCase {

    private OnlineRegistrationHelper helper;

    /**
     * At least one digit
     * At leas one upper case letter
     * At least one special character
     */
    private static final String CONDITIONS = ".*(?=.*\\d)(?=.*[A-Z])(?=.*[-_$!#<>@&()+=}]).*";

    @Before
    public void setUp() {
        helper = new OnlineRegistrationHelper();
    }

    @Test
    public void testGeneratePassword() throws Exception {
        for (int i = 0; i < 1000; i++) {
            String password = helper.generatePassword();
            assertEquals(8, password.length());

            assertTrue(password.matches(CONDITIONS));
        }
    }
}