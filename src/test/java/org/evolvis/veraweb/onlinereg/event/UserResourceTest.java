package org.evolvis.veraweb.onlinereg.event;

import org.evolvis.veraweb.onlinereg.TestSuite;
import org.evolvis.veraweb.onlinereg.Main;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.junit.Test;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

import org.evolvis.veraweb.onlinereg.Config;

import com.sun.jersey.api.client.WebResource;

import java.io.IOException;

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

    @Test
    public void testRegisterUser() throws IOException {
        String result = ur.registerUser("newuser", "firstname", "secondname", "password");
        assertEquals("OK", result);
        Person person = new Person();
        person.setFirstName("firstname2");
        person.setLastName("lastname2");
        person.setUsername("newuser2");

		WebResource r = client.resource(config.getVerawebEndpoint() + "/rest/person/");
        r = r.queryParam("username", "newuser2").queryParam("firstname", "firstname2").queryParam("lastname", "lastname2");
        boolean isPerson=false;
        try {
        	Person person2 = r.post(Person.class);        
        	if (person2 instanceof Person) {
        	  isPerson = true;
        	}
        	assertTrue(isPerson);
        	assertEquals(person, person2); 
	    } catch (UniformInterfaceException uie) {
	        ClientResponse response = uie.getResponse();
	        if (response.getStatus() == 404) {
	            // status 400 indicates user error: user does not exist, password wrong, user deactivated, etc...
	        	System.out.println("404");
	        } else {
	            throw uie;
	        }
	    }              
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
