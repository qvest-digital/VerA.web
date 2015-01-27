package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Event;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by Max Marche, <m.marche@tarent.de> on 26.01.2015
 */
@Path("/freevisitors")
@Produces(MediaType.APPLICATION_JSON)
public class FreeVisitorsResource extends AbstractResource {




    /**
     * Get event using the uuid
     * 
     * @param uuid String
     * @return Event the event
     */
    @Path("/{uuid}")
    @GET
    public Event getEventByUUId(@PathParam("uuid") String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Event.getEventByHash");
            query.setString("hash", uuid);
            return (Event) query.uniqueResult();
        } finally {
            session.close();
        }

    }
}
