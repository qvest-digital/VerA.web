package org.evolvis.veraweb.onlinereg.event;

import com.sun.jersey.api.client.Client;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.osiam.OsiamClient;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.User;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private Config config;
    private Client client;

    public UserResource(Config config, Client client) {
        this.config = config;
        this.client = client;
    }


    @POST
    @Path("/register/{osiam_username}")
    public String registerUser(@PathParam("osiam_username") String osiam_username,
                               @QueryParam("osiam_firstname") String osiam_firstname,
                               @QueryParam("osiam_secondname") String osiam_secondname,
                               @QueryParam("osiam_password1") String osiam_password1) throws IOException {

        if(!osiam_username.matches("\\w+")) {
            return "INVALID_USERNAME";
        }

        OsiamClient osiamClient = config.getOsiam().getClient(client);
        String accessToken = osiamClient.getAccessToken("GET POST");

        User user = osiamClient.getUser(accessToken, osiam_username);
        if(user != null) {
            return "USER_EXISTS";
        }

        user = new User.Builder(osiam_username)
                .setName(new Name.Builder().setGivenName(osiam_firstname).setFamilyName(osiam_secondname).build())
                .setPassword(osiam_password1)
                .setActive(true)
                .build();

        osiamClient.createUser(accessToken, user);

        return "OK";
    }


}