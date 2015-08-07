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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import lombok.Getter;
import lombok.extern.java.Log;

import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.resources.scim.User;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by mley on 26.08.14.
 * Edited by Max Marche <m.marche@tarent.de>
 */
@Path("/idm")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class LoginResource {

    public static final String USERNAME = "USERNAME";

    /**
     * base path of all resource
     */
    public static final String BASE_RESOURCE = "/rest";

    // TYPE REFERENCES
    private static final TypeReference<Boolean> BOOLEAN = new TypeReference<Boolean>() {};

    /**
     * String
     */
    private static final TypeReference<String> STRING = new TypeReference<String>() {
    };

    /**
     * key name for access tokens
     */
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";


    /**
     * Jackson Object Mapper
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * configuration
     */
    private final Config config;
    private Client client;

    /**
     * Servlet context
     */
    @javax.ws.rs.core.Context
    @Getter
    private ServletContext context;

    /**
     * Creates a new LoginResource
     *
     * @param config configuration
     */
    public LoginResource(Config config, Client client) {
        this.config = config;
        this.client = client;
    }



    /**
     * Logs a user in.
     *
     * @param userName user name
     * @param password password
     * @return true if login was successful, fals if username or password are not vaild
     */
	@POST
	@Path("/login/{username}")
	public String login(@PathParam("username") String userName,
			@FormParam("password") String password, 
			@FormParam("delegation") String delegation) throws IOException {
		if (userName == null || password == null) {
			return null;
		}
		Boolean isRegisterdForDelegationEvent = true;
		if (delegation != null && !"".equals(delegation)) {
			isRegisterdForDelegationEvent = 
					readResource(path("guest" , "registered", "delegation", userName, delegation), BOOLEAN);
		}
		
		if (isRegisterdForDelegationEvent) {
		
			try {
				String accessToken = config.getOsiam().getClient(client)
						.getAccessTokenUserPass(userName, password, "POST");
				context.setAttribute(USERNAME, userName);
				context.setAttribute(ACCESS_TOKEN, accessToken);
	
				WebResource resource;
	
	            resource = client.resource(path("person", "userinfo", userName));
	            String returnedValue;
	
	            try {
	            	returnedValue = resource.get(String.class);
	            } catch (UniformInterfaceException e) {
	               int statusCode = e.getResponse().getStatus();
	               if(statusCode == 204) {
	               		return StatusConverter.convertStatus(userName);
	               }
	
	               return null;
	            }
	
				return StatusConverter.convertStatus(returnedValue);
			} catch (ConnectionInitializationException cie) {
				return null;
			}
		} else {
			return null;
		}
	}

    /**
     * Test if user is logged in.
     *
     * @return true if user is logged in, which means username and valid access token are stored in the session context.
     */
    @GET
    @Path("/login")
    public boolean loggedIn() throws IOException {

        String accessToken = (String) context.getAttribute(ACCESS_TOKEN);
        String username = (String) context.getAttribute(USERNAME);

        if (accessToken == null || username == null) {
            return false;
        }
        try {
            User user = config.getOsiam().getClient(client).getUser(accessToken, username);
            if (user != null) {
                return true;
            }
        } catch (UniformInterfaceException uie) {
            ClientResponse response = uie.getResponse();
            if (response.getStatus() == 400) {
                // status 400 indicates user error: user does not exist, password wrong, user deactivated, etc...
                return false;
            } else {
                throw uie;
            }
        }

        return false;
    }

    /**
     * Logs the current logged in user out
     */
    @POST
    @Path("/logout")
    public void logout() {
        context.removeAttribute(USERNAME);
        context.removeAttribute(ACCESS_TOKEN);
    }


    /**
     * Constructs a path from VerA.web endpint, BASE_RESOURCE and given path fragmensts.
     *
     * @param path path fragments
     * @return complete path as string
     */
    private String path(Object... path) {
        String r = config.getVerawebEndpoint() + BASE_RESOURCE;
        for (Object p : path) {
            r += "/" + p;
        }
        return r;
    }


    /**
     * Reads the resource at given path and returns the entity.
     *
     * @param path path
     * @param type TypeReference of requested entity
     * @param <T>  Type of requested entity
     * @return requested resource
     * @throws IOException
     */
    private <T> T readResource(String path, TypeReference<T> type) throws IOException {
        WebResource resource;
        try {
            resource = client.resource(path);
            String json = resource.get(String.class);
            return mapper.readValue(json, type);
        } catch (ClientHandlerException che) {
            if (che.getCause() instanceof SocketTimeoutException) {
                //FIXME some times open, pooled connections time out and generate errors
                log.warning("Retrying request to " + path + " once because of SocketTimeoutException");
                resource = client.resource(path);
                String json = resource.get(String.class);
                return mapper.readValue(json, type);
            } else {
                throw che;
            }

        } catch (UniformInterfaceException uie) {
            log.warning(uie.getResponse().getEntity(String.class));
            throw uie;
        }
    }

    public ServletContext getContext() {
		return context;
	}
}
