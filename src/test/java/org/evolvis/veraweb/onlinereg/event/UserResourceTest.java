package org.evolvis.veraweb.onlinereg.event;

/*-
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
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.Main;
import org.evolvis.veraweb.onlinereg.TestSuite;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mley on 28.08.14.
 */
public class UserResourceTest {

    private final UserResource ur;
    private Client client;
    private Config config;

    public UserResourceTest() {
        Main main = TestSuite.DROPWIZARD.getApplication();
        ur = main.getUserResource();
        client=ur.getClient();
        config=ur.getConfig();
    }

    @Test@Ignore
    public void testRegisterUser() throws IOException {
    	//Benutzer wird durch Test angelegt, aber nicht mehr gelöscht -> Erneutes Ausführen des Tests schlägt fehl
    	long zeit = Calendar.getInstance().getTimeInMillis();

        String result = ur.registerUser("newusertest" + zeit, "firstnametest" + zeit, "secondnametest" + zeit, "passwordtest" + zeit, "email" + zeit, "language");
        assertEquals(StatusConverter.convertStatus("OK"), result);
    }

    //testet, ob post() nach Veraweb eine Person-Instanz zurückliefert
    @Test@Ignore
    public void testVerawebPerson() throws IOException {

        WebResource r = client.resource(config.getVerawebEndpoint() + "/rest/person/");
        r = r.queryParam("username", "newuserTwo").queryParam("firstname", "firstnameTwo").queryParam("lastname", "lastnameTwo");

       try {
        	Person person = r.post(Person.class);
        	assertTrue(person instanceof Person);

	    } catch (UniformInterfaceException uie) {
	        ClientResponse response = uie.getResponse();
	        if (response.getStatus() == 404) {
	        	System.out.println("404");
	        } else {
	            throw uie;
	        }
	    }
    }

    @Test
    public void testRegisterUserInvaldUsername() throws IOException {
        String result = ur.registerUser("invalid_username%&/", "firstname", "secondname", "password", "email", "language");
        assertEquals(StatusConverter.convertStatus("INVALID_USERNAME"), result);
    }

    @Test@Ignore
    public void testRegisterExistingUser() throws IOException {
        String result = ur.registerUser("existing", "firstname", "secondname", "password", "email", "language");
        assertEquals(StatusConverter.convertStatus("USER_EXISTS"), result);
    }

}
