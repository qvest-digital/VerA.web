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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import lombok.Getter;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.OsiamUserActivation;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.osiam.OsiamClient;
import org.evolvis.veraweb.onlinereg.utils.ResourceReader;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;
import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.User;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.UUID;

/**
 * Resource to register new users in OSIAM backend
 */
@Getter
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private static final String VERAWEB_SCHEME = "urn:scim:schemas:veraweb:1.5:Person";
	private Config config;
    private Client client;
    private ObjectMapper mapper = new ObjectMapper();
    private static final String BASE_RESOURCE = "/rest";
    private static final TypeReference<OsiamUserActivation> OSIAM_USER_ACTIVATION = new TypeReference<OsiamUserActivation>() {};
    private OsiamClient osiamClient;

    /**
     * Creates new UserResource
     *
     * @param config configuration
     * @param client jersey client
     */
    public UserResource(Config config, Client client) {
        this.config = config;
        this.client = client;
        osiamClient = config.getOsiam().getClient(client);
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
                               @FormParam("osiam_password1") String osiam_password1,
                               @FormParam("osiam_email") String email) throws IOException {

        if (!osiam_username.matches("\\w+")) {
            return StatusConverter.convertStatus("INVALID_USERNAME");
        }

        final String accessToken = osiamClient.getAccessTokenClientCred("GET", "POST");

        User user = osiamClient.getUser(accessToken, osiam_username);
        if (user != null) {
            return StatusConverter.convertStatus("USER_EXISTS");
        }

        final Form postBody = createPersonPostBody(osiam_username, osiam_firstname, osiam_secondname);

        final Person person;
        try {
            final WebResource r = client.resource(config.getVerawebEndpoint() + "/rest/person/");
            person = r.post(Person.class, postBody);
        } catch (UniformInterfaceException e) {
            int statusCode = e.getResponse().getStatus();
            if (statusCode == 204) {
                return StatusConverter.convertStatus("USER_EXISTS");
            }

            return StatusConverter.convertStatus("USER_NOT_CREATED");
        }

        user = initUser(osiam_username, osiam_firstname, osiam_secondname, osiam_password1, email, person.getPk());
        osiamClient.createUser(accessToken, user);

        final String activationToken = UUID.randomUUID().toString();
        sendEmailVerification(email, activationToken);
        addOsiamUserActivationEntry(osiam_username, activationToken);

        return StatusConverter.convertStatus("OK");
    }

    @GET
    @Path("/activate/{activationToken}")
    public String activateUser(@PathParam("activationToken") String activationToken) throws IOException {
        final OsiamUserActivation osiamUserActivation = getOsiamUserActivationByToken(activationToken);
        if (osiamUserActivation == null) {
            return StatusConverter.convertStatus("LINK_INVALID");
        }
        removeOsiamUserActivationEntry(activationToken);
        setOsiamUserAsActive(osiamUserActivation.getUsername());
        return StatusConverter.convertStatus("OK");
    }

    private void setOsiamUserAsActive(String username) throws IOException {
        final String accessTokenAsString = osiamClient.getAccessTokenClientCred("GET", "POST");
        final User user = osiamClient.getUser(accessTokenAsString, username);
        final User updatedUser = new User.Builder(user).setActive(true).build();
        osiamClient.createUser(accessTokenAsString, updatedUser);
    }

    private void removeOsiamUserActivationEntry(String activationToken) throws IOException {
        final Form postBody = new Form();
        postBody.add("osiam_user_activation", activationToken);
        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/osiam/user/activate");
        resource.post(postBody);
    }

    private OsiamUserActivation getOsiamUserActivationByToken(String activationToken) throws IOException {
        final ResourceReader resourceReader = new ResourceReader(client, mapper, config);
        final String osiamUserActivationPath = resourceReader.constructPath(BASE_RESOURCE, "osiam", "user", "get", "activation", activationToken);
        return resourceReader.readStringResource(osiamUserActivationPath, OSIAM_USER_ACTIVATION);
    }


    private void addOsiamUserActivationEntry(String osiamUsername, String activationToken) {
        final Form postBody = new Form();
        postBody.add("activation_token", activationToken);
        postBody.add("username", osiamUsername);
        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/osiam/user/create");
        resource.post(postBody);
    }

    private void sendEmailVerification(String email, String activationToken) {
        final Form postBody = new Form();
        postBody.add("email", email);
        postBody.add("endpoint", config.getOnlineRegistrationEndpoint());
        postBody.add("activation_token", activationToken);
        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/email/confirmation/send");
        resource.post(postBody);
    }

    private Form createPersonPostBody(String osiam_username, String osiam_firstname, String osiam_secondname) {
        final Form postBody = new Form();
        postBody.add("username", osiam_username);
        postBody.add("firstname", osiam_firstname);
        postBody.add("lastname", osiam_secondname);
        return postBody;
    }

    private User initUser(@PathParam("osiam_username") String osiam_username,
                          @FormParam("osiam_firstname") String osiam_firstname,
                          @FormParam("osiam_secondname") String osiam_secondname,
                          @FormParam("osiam_password1") String osiam_password1,
                          @FormParam("email") String email,
                          Integer personId) {
        User user;
        final Email userEmail = buildUserEmail(email);
        user = new User.Builder(osiam_username)
                .setName(new Name.Builder().setGivenName(osiam_firstname).setFamilyName(osiam_secondname).build())
                .setPassword(osiam_password1)
                .setActive(false)
                .addEmail(userEmail)
                .addExtension(
                    new Extension.Builder(VERAWEB_SCHEME).setField("tpersonid", BigInteger.valueOf(personId)).build()
                )
                .build();
        return user;
    }

    private Email buildUserEmail(@FormParam("email") String email) {
        return new Email.Builder()
            .setType(Email.Type.HOME)
            .setValue(email).build();
    }
}