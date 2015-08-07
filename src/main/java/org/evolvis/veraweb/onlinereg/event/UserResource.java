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
package org.evolvis.veraweb.onlinereg.event;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.osiam.OsiamClient;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.User;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.math.BigInteger;

import lombok.Getter;
import lombok.extern.java.Log;

/**
 * Resource to register new users in OSIAM backend
 */
@Getter
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class UserResource {
    private static final String VERAWEB_SCHEME = "urn:scim:schemas:veraweb:1.5:Person";
	private Config config;
    private Client client;

    /**
     * Creates new UserResource
     *
     * @param config configuration
     * @param client jersey client
     */
    public UserResource(Config config, Client client) {
        this.config = config;
        this.client = client;
    }

    /**
     * Creates a new user
     *
     * @param osiam_username   user name
     * @param osiam_firstname  first name
     * @param osiam_secondname family name
     * @param osiam_password1  password
     * @return result of creation. Values can be "OK", "INVALID_USERNAME" or "USER_EXISTS"
     * @throws IOException
     */
    @POST
    @Path("/register/{osiam_username}")
    public String registerUser(@PathParam("osiam_username") String osiam_username,
                               @FormParam("osiam_firstname") String osiam_firstname,
                               @FormParam("osiam_secondname") String osiam_secondname,
                               @FormParam("osiam_password1") String osiam_password1) throws IOException {

        if (!osiam_username.matches("\\w+")) {
            return StatusConverter.convertStatus("INVALID_USERNAME");
        }

        OsiamClient osiamClient = config.getOsiam().getClient(client);
        String accessToken = osiamClient.getAccessTokenClientCred("GET", "POST");
log.warning("OSIAM getAccessTokenClientCred token '" + accessToken + "'");//…

        User user = osiamClient.getUser(accessToken, osiam_username);
        if (user != null) {
            return StatusConverter.convertStatus("USER_EXISTS");
        }
        
        WebResource r = client.resource(config.getVerawebEndpoint() + "/rest/person/");
        Form postBody = new Form();
        
        postBody.add("username", osiam_username);
        postBody.add("firstname", osiam_firstname);
        postBody.add("lastname", osiam_secondname);
        
        Person person = new Person();
        try {
             person = r.post(Person.class, postBody);
        } catch (UniformInterfaceException e) {
            int statusCode = e.getResponse().getStatus();
            if(statusCode == 204) {
            	return StatusConverter.convertStatus("USER_EXISTS");
            }

            return StatusConverter.convertStatus("USER_NOT_CREATED");
        }

		user = new User.Builder(osiam_username)
                .setName(new Name.Builder().setGivenName(osiam_firstname).setFamilyName(osiam_secondname).build())
                .setPassword(osiam_password1)
                .setActive(true)
                .addExtension(new Extension.Builder(VERAWEB_SCHEME).setField("tpersonid", BigInteger.valueOf(person.getPk())).build())
                .build();              
        

        osiamClient.createUser(accessToken, user);      
       

        return StatusConverter.convertStatus("OK");
    }
}