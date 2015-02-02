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
package org.evolvis.veraweb.onlinereg.osiam;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import lombok.extern.java.Log;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;


/**
 * Created by mley on 27.08.14.
 * <p/>
 * OSIAM client
 */
@Log
public class OsiamClient {

    private OsiamConfig config;
    private Client client;
    private ObjectMapper mapper;

    /**
     * Creates a new OSIAM client
     *
     * @param config OSIAM config object
     * @param client jersey HTTP client
     */
    public OsiamClient(OsiamConfig config, Client client) {
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.config = config;
        this.client = client;
    }

    /**
     * Gets an access token with username and password with the resource owner password grant
     *
     * @param username username
     * @param password password
     * @param scope    scope
     * @return accesstoken as String
     * @throws IOException if an error occurs, e.g. service is not available, user does not exists, password wrong
     */
    public String getAccessToken(String username, String password, String scope) throws IOException {
        return getAccessToken(scope, "password", new String[]{"username", username}, new String[]{"password", password});
    }

    /**
     * Gets an access token with the client credentials
     *
     * @param scope scope
     * @return access token
     * @throws IOException if an error occurs, e.g. service is not available, client credentials wrong
     */
    public String getAccessToken(String scope) throws IOException {
        return getAccessToken(scope, "client_credentials");
    }

    /**
     * Gets an access token.
     *
     * @param scope     scope
     * @param grantType grant type
     * @param params    additional query parameters
     * @return access token
     * @throws IOException
     */
    public String getAccessToken(String scope, String grantType, String[]... params) throws IOException {
        WebResource resource = client.resource(config.getEndpoint() + "/osiam-auth-server/oauth/token");
        resource.addFilter(new HTTPBasicAuthFilter(config.getClientId(), config.getClientSecret()));
        resource = resource.queryParam("grant_type", grantType);
        resource = resource.queryParam("scope", scope);
        for (String[] param : params) {
            resource = resource.queryParam(param[0], param[1]);
        }


        try {
            String result = resource.post(String.class);
            String accessToken = (String) mapper.readValue(result, Map.class).get("access_token");

            return accessToken;
        } catch (UniformInterfaceException uie) {
            ClientResponse response = uie.getResponse();
            String entity = response.getEntity(String.class);
            log.warning("Error getting accesstoken: " + resource.toString() + " : " + entity);
            throw uie;
        }
    }

    /**
     * Gets the user with the specified username
     *
     * @param accessToken access token
     * @param userName    username
     * @return User object of null, if user does not exist
     * @throws IOException
     */
    public User getUser(String accessToken, String userName) throws IOException {
        WebResource resource = client.resource(config.getEndpoint() + "/osiam-resource-server/Users");
        resource = resource.queryParam("access_token", accessToken)
                .queryParam("filter", "userName eq \"" + userName + "\"");

        SCIMSearchResult<User> result = mapper.readValue(resource.get(String.class), new TypeReference<SCIMSearchResult<User>>() {
        });


        if (result.getResources().size() == 1) {
            return result.getResources().get(0);
        }

        return null;
    }

    /**
     * Creates a new user
     *
     * @param accessToken access token
     * @param user        User object to create
     * @return newly created user with meta information
     * @throws IOException
     */
    public User createUser(String accessToken, User user) throws IOException {
        WebResource resource = client.resource(config.getEndpoint() + "/osiam-resource-server/Users");

        User createdUser = mapper.readValue(resource.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .post(String.class, user), User.class);

        return createdUser;

    }


}
