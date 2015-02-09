/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package org.evolvis.veraweb.onlinereg.user;

import com.sun.jersey.api.client.UniformInterfaceException;
import org.evolvis.veraweb.onlinereg.Main;
import org.evolvis.veraweb.onlinereg.TestSuite;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;
/**
 * Created by mley on 29.08.14.
 */
public class LoginResourceTest {

    private LoginResource lr = ((Main)TestSuite.DROPWIZARD.getApplication()).getLoginResource();

    @Test
    public void testLogin() throws IOException {
        assertFalse(lr.loggedIn());
        assertNotNull(lr.login("test", "password"));
        assertTrue(lr.loggedIn());
    }

    @Test
    public void testLoginWrongPassword() throws IOException {
        assertNull(lr.login("test", "wrong"));
    }

    @Test
    public void testLoginUnknownUser() throws IOException {
    	assertNull(lr.login("unknown", "wrong"));
    }

    @Test
    public void testLoginNoUserPassword() throws IOException {
    	assertNull(lr.login(null, "wrong"));
}

    @Test
    public void testLogout() throws IOException {
        lr.login("test", "password");
        lr.logout();
        assertFalse(lr.loggedIn());
    }

    @Test(expected=UniformInterfaceException.class)
    public void testLoginServerError() throws IOException {
        lr.login("fail", "password");
    }

    @Test
    public void testNotLoggedIn() throws IOException {
        lr.getContext().setAttribute(LoginResource.USERNAME, "notloggedin");
        lr.getContext().setAttribute(LoginResource.ACCESS_TOKEN, "notloggedin");

        lr.loggedIn();

        lr.getContext().setAttribute(LoginResource.USERNAME, null);
        lr.getContext().setAttribute(LoginResource.ACCESS_TOKEN, "accestoken");

        assertFalse(lr.loggedIn());

        lr.getContext().setAttribute(LoginResource.USERNAME, "user");
        lr.getContext().setAttribute(LoginResource.ACCESS_TOKEN, null);

        assertFalse(lr.loggedIn());

    }


    @Test(expected=UniformInterfaceException.class)
    public void testLoggedInServerError() throws IOException {
        lr.getContext().setAttribute(LoginResource.USERNAME, "illegal");
        lr.getContext().setAttribute(LoginResource.ACCESS_TOKEN, "illegal");

        lr.loggedIn();
    }

    @Test
    public void testLoggedInDeletedUser() throws IOException {
        lr.getContext().setAttribute(LoginResource.USERNAME, "newuser");
        lr.getContext().setAttribute(LoginResource.ACCESS_TOKEN, "ddf31856-ebbc-4087-8d85-093fa36044a7");

        assertFalse(lr.loggedIn());
    }


}
