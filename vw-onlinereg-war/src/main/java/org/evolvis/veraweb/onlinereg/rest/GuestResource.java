package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by mley on 03.08.14.
 */
@Path("/guest")
@Produces(MediaType.APPLICATION_JSON)
public class GuestResource extends AbstractResource{


    @GET
    @Path("/{eventId}/{userId}")
    public Guest getGuest(@PathParam("eventId") int eventId, @PathParam("userId") int userId) {
        Session session = openSession();
        try {
            Query query = session.getNamedQuery("Guest.findByEventAndUser");
            query.setInteger("eventId", eventId);
            query.setInteger("userId", userId);
            return (Guest) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    @POST
    @Path("/{eventId}/{userId}")
    public Guest saveGuest(@PathParam("eventId") int eventId, @PathParam("userId") int userId, @QueryParam("invitationstatus") int invitationstatus, @QueryParam("notehost") String notehost) {
        Session session = openSession();
        try {
            Query query = session.getNamedQuery("Guest.findByEventAndUser");
            query.setInteger("eventId", eventId);
            query.setInteger("userId", userId);

            Guest g = (Guest) query.uniqueResult();

            g.setInvitationstatus(invitationstatus);
            g.setNotehost(notehost);
            session.update(g);
            session.flush();
            return g;
        } finally {
            session.close();
        }

    }

}
