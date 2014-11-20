package org.evolvis.veraweb.onlinereg.event;

import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.entities.Guest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/delegation")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class DelegationResource {

    @GET
    @Path("/{uuid}")
    public List<Guest> showRegisterView(@PathParam("uuid") String uuid) throws IOException {
        return null;
    }

    @GET
    @Path("/{uuid}/register")
    public List<Guest> registerDelegateForEvent(@PathParam("uuid") String uuid) throws IOException {
        return null;
    }

    @GET
    @Path("/{uuid}/remove/{userid}")
    public List<Guest> removeDelegateFromEvent(@PathParam("uuid") String uuid, @PathParam("userid") Long userid) throws IOException {
        return null;
    }
}
