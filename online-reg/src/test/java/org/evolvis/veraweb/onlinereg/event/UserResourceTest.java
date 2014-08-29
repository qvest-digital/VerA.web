package org.evolvis.veraweb.onlinereg.event;

import org.evolvis.veraweb.onlinereg.TestSuite;
import org.evolvis.veraweb.onlinereg.Main;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by mley on 28.08.14.
 */
public class UserResourceTest {

    private final UserResource ur;

    public UserResourceTest() {
        Main main = TestSuite.DROPWIZARD.getApplication();
        ur = main.getUserResource();
    }

    @Test
    public void testRegisterUser() throws IOException {
        String result = ur.registerUser("newuser", "firstname", "secondname", "password");
        assertEquals("OK", result);
    }

    @Test
    public void testRegisterUserInvaldUsername() throws IOException {
        String result = ur.registerUser("invalid_username%&/", "firstname", "secondname", "password");
        assertEquals("INVALID_USERNAME", result);
    }

    @Test
    public void testRegisterExistingUser() throws IOException {
        String result = ur.registerUser("existing", "firstname", "secondname", "password");
        assertEquals("USER_EXISTS", result);
    }

}
