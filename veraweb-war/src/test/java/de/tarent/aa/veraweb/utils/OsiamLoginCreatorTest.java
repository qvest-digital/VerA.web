package de.tarent.aa.veraweb.utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class OsiamLoginCreatorTest {

    OsiamLoginCreator osiamLoginCreator;

    @Before
    public void setUp() throws Exception {
        osiamLoginCreator = new OsiamLoginCreator();
    }

    @Test
    public void testGenerateUsername() throws Exception {

    }

    @Test
    public void testGeneratePassword() throws Exception {
        for (int i = 0; i < 1000; i++) {
            String password = osiamLoginCreator.generatePassword();
            assertEquals(8, password.length());
            assertTrue(password.matches(osiamLoginCreator.CONDITIONS));
        }
    }

    @Test
    public void testCreateOsiamUser() throws Exception {

    }
}