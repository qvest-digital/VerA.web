package org.evolvis.veraweb.onlinereg.user;

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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.osiam.OsiamClient;
import org.evolvis.veraweb.onlinereg.utils.ResourceReader;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;
import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;

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

    

    private final ResourceReader resourceReader;

    /**
     * Creates a new EventResource
     *
     * @param client jersey client
     * @param config configuration
     */
    public ResetPasswordResource(Config config, Client client) {
        resourceReader = new ResourceReader(client, mapper, config);
        this.client = client;
        this.config = config;
        System.out.println();
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
    public String resetPassword(@PathParam("uuid") String uuid, @FormParam("password") String password)
            throws IOException {

        final Integer userId = getUserId(uuid);
        if(userId == null) {
            return StatusConverter.convertStatus("WRONG_LINK");
        }
        final Person person = getPerson(userId);
        final String username = person.getUsername();

        return updateOsiamUser(username, password, userId);
    }

    private String updateOsiamUser(String username, String password, Integer personId){
        final OsiamClient osiamClient = config.getOsiam().getClient(client);
        final OsiamConnector osiamConnector = osiamClient.getConnector();
        final AccessToken accessToken = osiamConnector.retrieveAccessToken(Scope.ALL);
        final String accessTokenAsString = accessToken.getToken();
        try {
            final User user = osiamClient.getUser(accessTokenAsString, username);
            final UpdateUser updateUser = new UpdateUser.Builder().updatePassword(password).build();
            osiamConnector.updateUser(user.getId(), updateUser, accessToken);
            deleteResetPasswordUUID(personId);
            return StatusConverter.convertStatus("OK");
        } catch (IOException e) {
            return StatusConverter.convertStatus("GETTING_USER_FAILED");
        }
    }

    private void deleteResetPasswordUUID(Integer personId) {
        final WebResource deleteEntryLinkUUID = client.resource(config.getVerawebEndpoint() + "/rest/links/delete");
        final Form postBody = new Form();
        postBody.add("personId", personId);
        deleteEntryLinkUUID.post(postBody);
    }

    private Person getPerson(Integer userId) throws IOException {
        final String pathForPerson = resourceReader.constructPath(BASE_RESOURCE, "person", "list", userId);
        return resourceReader.readStringResource(pathForPerson, PERSON);
    }

    private Integer getUserId(String uuid) throws IOException {
        final String pathForUserId = resourceReader.constructPath(BASE_RESOURCE, "links", uuid);
        return resourceReader.readStringResource(pathForUserId, INTEGER);
    }
}
