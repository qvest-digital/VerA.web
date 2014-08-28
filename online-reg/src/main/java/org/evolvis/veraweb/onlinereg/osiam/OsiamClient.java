package org.evolvis.veraweb.onlinereg.osiam;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
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


        String result = resource.post(String.class);
        try {
            String accessToken = (String) mapper.readValue(result, Map.class).get("access_token");

            return accessToken;
        } catch (UniformInterfaceException uie) {
            log.warning(uie.getResponse().getEntity(String.class));
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
