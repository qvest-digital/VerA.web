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

            Guest guest = (Guest) query.uniqueResult();
            guest.setInvitationstatus(invitationstatus);
            guest.setNotehost(notehost);

            session.update(guest);
            session.flush();
            return guest;
        } finally {
            session.close();
        }
    }

    @GET
    @Path("/{uuid}")
    public Guest findEventIdByDelegation(@PathParam("uuid") String uuid) {
    	Session session = openSession();
        try {
            Query query = session.getNamedQuery("Guest.findEventIdByDelegationUUID");
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
            Query query = session.getNamedQuery("Guest.guestByUUID");
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
    @Path("/{uuid}/register")
    public Guest addGuestToEvent(@PathParam("uuid") String uuid, @QueryParam("eventId") String eventId, @QueryParam("userId") String userId,
    		@QueryParam("invitationstatus") String invitationstatus, @QueryParam("gender") String gender) {
		Session session = openSession();
		try { 
			Guest guest = initGuest(uuid,eventId, userId, invitationstatus, gender);
            session.save(guest);
			session.flush();
			     
			return guest;
		} finally {
			session.close();
		}
    }
    
    
    /**
     * Initialize guest with event information 
     */
    private Guest initGuest(String uuid, String eventId, String userId, String invitationstatus, String gender) {
        Guest guest = new Guest();
        guest.setDelegation(uuid);
        guest.setFk_person(Integer.parseInt(userId));
        guest.setFk_event(Integer.parseInt(eventId));
        guest.setInvitationstatus(Integer.parseInt(invitationstatus));
        guest.setNotehost("");
        setGender(gender, guest);

        return guest;
    }

    private void setGender(String gender, Guest guest) {
        if (gender.equalsIgnoreCase("Herr")) {
        	guest.setGender("m");
        	guest.setGender_p("m");
        }
        else {
        	guest.setGender("w");
        	guest.setGender_p("w");
        }
    }
}
