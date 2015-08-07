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
import lombok.Getter;
import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.osiam.OsiamClient;
import org.evolvis.veraweb.onlinereg.utils.ResourceReader;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;
import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.User;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * This class is used for password reset actions (for example: reset password).
 *
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/reset/password")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class ResetPasswordResource {

	/** Returned types from REST */
    private static final TypeReference<Integer> INTEGER = new TypeReference<Integer>() {};
    private static final TypeReference<Person> PERSON = new TypeReference<Person>() {};

    private static final String BASE_RESOURCE = "/rest";

    /** Jersey client */
    private Client client;

    /** Configuration */
    private Config config;

    /** Jackson Object Mapper */
    private ObjectMapper mapper = new ObjectMapper();

    /** Servlet context */
    @javax.ws.rs.core.Context
    @Getter
    private ServletContext context;

    /**
     * Creates a new EventResource
     *
     * @param client jersey client
     * @param config configuration
     */
    public ResetPasswordResource(Config config, Client client) {
        this.client = client;
        this.config = config;
    }

    /**
     * Show view for password reset.
     *
     * @param uuid The UUID of the person for which the password will be reset.
     * @param password The password
     *
     * @throws IOException If one of the database actions fails
     *
     * @return OK if successfull.
     */
    @POST
    @Path("/{uuid}")
    public String getEventByUUId(@PathParam("uuid") String uuid, @FormParam("password") String password)
            throws IOException {
        final ResourceReader resourceReader = new ResourceReader(client, mapper, config);
        final Integer userId = getUserId(uuid, resourceReader);
        final Person person = getPerson(resourceReader, userId);
        final String username = person.getUsername();

        deleteOsiamUser(username);
        createOsiamUser(username, password);

        return StatusConverter.convertStatus("OK");
    }

    private Person getPerson(ResourceReader resourceReader, Integer userId) throws IOException {
        final String pathForPerson = resourceReader.constructPath(BASE_RESOURCE, "person", "list", userId);
        return resourceReader.readStringResource(pathForPerson, PERSON);
    }

    private Integer getUserId(String uuid, ResourceReader resourceReader) throws IOException {
        final String pathForUserId = resourceReader.constructPath(BASE_RESOURCE, "links", uuid);
        return resourceReader.readStringResource(pathForUserId, INTEGER);
    }

    private void createOsiamUser(String username, String password) throws IOException {
        final OsiamClient osiamClient = config.getOsiam().getClient(client);
        final String accessTokenAsString = osiamClient.getAccessTokenClientCred("GET", "POST");

        final User user = new User.Builder(username)
                .setName(new Name.Builder().build())
                .setPassword(password)
                .setActive(true)
                .build();

        osiamClient.createUser(accessTokenAsString, user);
    }

    private void deleteOsiamUser(String username) throws IOException {
        final OsiamClient osiamClient = config.getOsiam().getClient(client);
        final OsiamConnector osiamConnector = osiamClient.getConnector();
        final AccessToken accessToken = osiamConnector.retrieveAccessToken(Scope.ALL);
        final String accessTokenAsString = accessToken.getToken();
        final User user = osiamClient.getUser(accessTokenAsString, username);
        osiamConnector.deleteUser(user.getId(), accessToken);
    }
}
