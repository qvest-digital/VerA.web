package org.evolvis.veraweb.onlinereg.user;

import org.evolvis.veraweb.onlinereg.Main;
import org.evolvis.veraweb.onlinereg.TestSuite;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by mley on 29.08.14.
 */
public class LoginResourceTest {

    private LoginResource lr = ((Main)TestSuite.DROPWIZARD.getApplication()).getLoginResource();

    @Test
    public void testLogin() throws IOException {
        assertTrue(lr.login("test", "password"));
    }

    @Test
    public void testLoginWrongPassword() throws IOException {
        assertFalse(lr.login("test", "wrong"));
    }

    @Test
    public void testLoginUnknownUser() throws IOException {
        assertFalse(lr.login("unknown", "wrong"));
    }
}
