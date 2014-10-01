package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Event;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by mley on 03.08.14.
 */
@Path("/event")
@Produces(MediaType.APPLICATION_JSON)
public class EventResource extends AbstractResource {

    @Path("/")
    @GET
    public List<Event> listEvents() {
        Session session = openSession();
        try {
            Query query = session.getNamedQuery("Event.list");
            return (List<Event>) query.list();

        } finally {
            session.close();
        }

    }

    @Path("/")
    @GET
    public List<Event> listAllEvents() {
        Session session = openSession();
        try {
            Query query = session.getNamedQuery("AllEvents.list");
            return (List<Event>) query.list();

        } finally {
            session.close();
        }

    }

    @Path("/{eventId}")
    @GET
    public Event getEvent(@PathParam("eventId") int eventId) {
        Session session = openSession();
        try {

            Query query = session.getNamedQuery("Event.getEvent");
            query.setInteger("pk", eventId);
            return (Event) query.uniqueResult();

        } finally {
            session.close();
        }

    }
}
