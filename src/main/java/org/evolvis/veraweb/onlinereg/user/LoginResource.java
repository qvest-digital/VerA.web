package org.evolvis.veraweb.onlinereg.user;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.ClientResponse;
import org.evolvis.veraweb.onlinereg.Config;

import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by mley on 26.08.14.
 */
@Path("/idm")
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    /**
     * key name for access tokens
     */
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    /**
     * configuration
     */
    private final Config config;
    private Client client;

    /**
     * Servlet context
     */
    @javax.ws.rs.core.Context
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
    public boolean login(@PathParam("username") String userName, @QueryParam("password") String password)  {

        try {
        	if(userName == null || password == null) {
                return false;
            }
            String accessToken = config.getOsiam().getClient(client).getAccessToken(userName, password, "POST");
            context.setAttribute(ACCESS_TOKEN, accessToken);
            return true;
        } catch (IOException ue) {
            return false;
        }catch (UniformInterfaceException uie) {
            ClientResponse response = uie.getResponse();
            response.getStatus();
            return false;
        }

    }

    /**
     * Logs the current logged in user out
     */
    @POST
    @Path("/logout")
    public void logout() {
        context.removeAttribute(ACCESS_TOKEN);
    }
}
