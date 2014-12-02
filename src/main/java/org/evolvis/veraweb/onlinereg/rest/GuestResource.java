package org.evolvis.veraweb.onlinereg.rest;

import java.math.BigInteger;
import java.util.List;

import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.Person;
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

    @GET
    @Path("/{uuid}")
    public Guest findEventIdByDelegation(@PathParam("uuid") String uuid) {
    	Session session = openSession();
        try {
            Query query = session.getNamedQuery("Guest.findEventIdByDelegation");
            query.setString("uuid", uuid);
            Guest guest = (Guest) query.uniqueResult();
            return guest;
        } finally {
            session.close();
        }
    }
    @GET
    @Path("/exist/{uuid}")
    public Boolean existEventIdByDelegation(@PathParam("uuid") String uuid) {
    	Session session = openSession();
        try {
            Query query = session.getNamedQuery("Guest.guestByUuid");
            query.setString("uuid", uuid);
            BigInteger numberFoundDelegations = (BigInteger) query.uniqueResult();
            if(numberFoundDelegations.intValue() == 1) {
            	return true;
            }
            return false;
        } finally {
            session.close();
        }
    }
    
    @POST
    @Path("/{uuid}/einladen")
    public Guest addGuestToEvent(@PathParam("uuid") String uuid, @QueryParam("eventId") String eventId, @QueryParam("userId") String userId, @QueryParam("invitationstatus") String invitationstatus) {
		Session session = openSession();
		try { 
			Guest g = initGuest(uuid,eventId, userId, invitationstatus);
			session.save(g);
			session.flush();
			     
			return g;
		} finally {
			session.close();
		}
    }
    
    /**
     * Initialize guest with event information 
     */
    private Guest initGuest(String uuid, String eventId, String userId, String invitationstatus) {
        Guest g = new Guest();
        g.setDelegation(uuid);
        g.setFk_person(Integer.parseInt(userId));
        g.setFk_event(Integer.parseInt(eventId));
        g.setInvitationstatus(Integer.parseInt(invitationstatus));
        g.setNotehost("");
    	g.setGender("m");
    	g.setGender_p("m");
    	
    	return g;
    }
    
    
}
