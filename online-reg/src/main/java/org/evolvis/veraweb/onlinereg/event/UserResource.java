package org.evolvis.veraweb.onlinereg.event;

import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class UserResource {
    private Config config;

    public UserResource(Config config) {
        this.config = config;
    }


    @POST
    @Path("/register/{osiam_username}")
    public String registerUser(@PathParam("osiam_username") String osiam_username,
                               @QueryParam("osiam_firstname") String osiam_firstname,
                               @QueryParam("osiam_secondname") String osiam_secondname,
                               @QueryParam("osiam_password1") String osiam_password1) {
        if(osiam_username == "existinguser") {
            return "USER_EXISTS";
        }

        return "OK";
  }
  

}