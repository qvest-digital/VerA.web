package org.evolvis.veraweb.onlinereg.user;

import org.evolvis.veraweb.onlinereg.Config;
import org.osiam.client.OsiamConnector;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;

import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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
    public LoginResource(Config config) {
        this.config = config;
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
    public boolean login(@PathParam("username") String userName, @QueryParam("password") String password) {
        OsiamConnector oc = config.getOsiam().getConnector();
        try {
            AccessToken accessToken = oc.retrieveAccessToken(userName, password, Scope.GET);
            context.setAttribute(ACCESS_TOKEN, accessToken);
            return true;
        } catch (UnauthorizedException ue) {
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
