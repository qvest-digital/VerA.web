package org.evolvis.veraweb.onlinereg.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import lombok.Getter;
import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.osiam.OsiamClient;
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
import java.net.SocketTimeoutException;

/**
 * This class is used for password reset actions (for example: reset password).
 *
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/reset/password")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class ResetPasswordResource {

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
     */
    @POST
    @Path("/{uuid}")
    public String getEvenByUUId(@PathParam("uuid") Integer uuid, @FormParam("password") String password) throws IOException {
        final Integer userId = readResource(path("links", uuid), INTEGER);
        final Person person = readResource(path("person", "list", userId), PERSON);
        final String username = person.getUsername();

        deleteOsiamUser(username);
        createOsiamUser(username, password);

        return StatusConverter.convertStatus("OK");
    }

    private void createOsiamUser(String username, String password) throws IOException {
        final OsiamClient osiamClient = config.getOsiam().getClient(client);
        final String accessTokenAsString = osiamClient.getAccessToken("GET POST");

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

    /**
     * Constructs a path from vera.web endpint, BASE_RESOURCE and given path fragmensts.
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
//                log.warning("Retrying request to " + path + " once because of SocketTimeoutException");
                resource = client.resource(path);
                String json = resource.get(String.class);
                return mapper.readValue(json, type);
            } else {
                throw che;
            }

        } catch (UniformInterfaceException uie) {
//            log.warning(uie.getResponse().getEntity(String.class));
            throw uie;
        }
    }
}
