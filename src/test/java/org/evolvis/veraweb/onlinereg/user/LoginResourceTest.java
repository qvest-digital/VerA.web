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
        assertFalse(lr.loggedIn());
        assertTrue(lr.login("test", "password"));
        assertTrue(lr.loggedIn());
    }

    @Test
    public void testLoginWrongPassword() throws IOException {
        assertFalse(lr.login("test", "wrong"));
    }

    @Test
    public void testLoginUnknownUser() throws IOException {
        assertFalse(lr.login("unknown", "wrong"));
    }

    @Test
    public void testLoginNoUserPassword() throws IOException {
        assertFalse(lr.login(null, "wrong"));
        assertFalse(lr.login("user", null));
        assertFalse(lr.login(null, null));
    }

    @Test
    public void testLogout() throws IOException {
        lr.login("test", "password");
        lr.logout();
        assertFalse(lr.loggedIn());
    }


}
