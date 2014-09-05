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
    }
    
    //testet, ob post() nach Veraweb eine Person-Instanz zurückliefert
    @Test
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
        String result = ur.registerUser("invalid_username%&/", "firstname", "secondname", "password");
        assertEquals("INVALID_USERNAME", result);
    }

    @Test
    public void testRegisterExistingUser() throws IOException {
        String result = ur.registerUser("existing", "firstname", "secondname", "password");
        assertEquals("USER_EXISTS", result);
    }

}
