/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.math.BigInteger;

/**
 * Created by mley on 03.08.14.
 */
@Path("/guest")
@Produces(MediaType.APPLICATION_JSON)
public class GuestResource extends AbstractResource{

    /**
     * Get guest.
     *
     * @param eventId Event id
     * @param userId User id
     *
     * @return Guest
     */
    @GET
    @Path("/{eventId}/{userId}")
    public Guest getGuest(@PathParam("eventId") int eventId, @PathParam("userId") int userId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.findByEventAndUser");
            query.setInteger("eventId", eventId);
            query.setInteger("userId", userId);
            return (Guest) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Get guest id by event id and user id.
     *
     * @param eventId Event id
     * @param userId User id
     *
     * @return Guest
     */
    @GET
    @Path("/concrete/{eventId}/{userId}")
    public Integer getGuestId(@PathParam("eventId") int eventId, @PathParam("userId") int userId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.findIdByEventAndUser");
            query.setInteger("eventId", eventId);
            query.setInteger("userId", userId);
            return (Integer) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Save guest.
     *
     * @param eventId Event id
     * @param userId User id
     * @param invitationstatus invitation status
     * @param notehost TODO
     *
     * @return The guest
     */
    @POST
    @Path("/{eventId}/{userId}")
    public Guest saveGuest(@PathParam("eventId") int eventId,
                           @PathParam("userId") int userId,
                           @QueryParam("invitationstatus") int invitationstatus,
                           @QueryParam("notehost") String notehost) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.findByEventAndUser");
            query.setInteger("eventId", eventId);
            query.setInteger("userId", userId);

            final Guest guest = (Guest) query.uniqueResult();
            guest.setInvitationstatus(invitationstatus);
            guest.setNotehost(notehost);

            session.update(guest);
            session.flush();
            return guest;
        } finally {
            session.close();
        }
    }
    /**
     * Save guest.
     *
     * @param eventId Event id
     * @param userId User id
     * @param invitationstatus invitation status
     * @param notehost TODO
     */
    @POST
    @Path("/update/{eventId}/{userId}")
    public void updateGuest(@PathParam("eventId") int eventId,
                           @PathParam("userId") int userId,
                           @FormParam("invitationstatus") int invitationstatus,
                           @FormParam("notehost") String notehost) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.findByEventAndUser");
            query.setInteger("eventId", eventId);
            query.setInteger("userId", userId);

            final Guest guest = (Guest) query.uniqueResult();
            guest.setInvitationstatus(invitationstatus);
            guest.setNotehost(notehost);

            session.update(guest);
            session.flush();
        } finally {
            session.close();
        }
    }

    /**
     * Get guest using the UUID of a delegation
     * TODO Rename method. It is not clear.
     *
     * @param uuid Delegation uuid
     * @return Guest
     */
    @GET
    @Path("/{uuid}")
    public Guest findEventIdByDelegation(@PathParam("uuid") String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.findEventIdByDelegationUUID");
            query.setString("uuid", uuid);

            final Guest guest = (Guest) query.uniqueResult();
            return guest;
        } finally {
            session.close();
        }
    }

    /**
     * Find guest by delegation and person id.
     *
     * @param uuid Delegation UUID
     * @param userId Person id
     *
     * @return Guest
     */
    @GET
    @Path("/delegation/{uuid}/{userId}")
    public Guest findGuestByDelegationAndPerson(@PathParam("uuid") String uuid, @PathParam("userId") int userId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.findByDelegationAndUser");
            query.setString("delegation", uuid);
            query.setInteger("userId", userId);

            return (Guest) query.uniqueResult();
        } finally {
            session.close();
        }
    }
    /**
     * Find guest by delegation and person id.
     *
     * @param uuid Delegation UUID
     * @param userId Person id
     *
     * @return Guest
     */
    @GET
    @Path("/delegation/{uuid}")
    public Guest findGuestByDelegationUUID(@PathParam("uuid") String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.findByDelegationUUID");
            query.setString("delegation", uuid);

            return (Guest) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Check for existing delegation by delegation uuid.
     *
     * @param uuid Delegation UUID
     *
     * @return True if exists only one delegation, otherwise false
     */
    @GET
    @Path("/exist/{uuid}")
    public Boolean existEventIdByDelegation(@PathParam("uuid") String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.guestByUUID");
            query.setString("uuid", uuid);

            final BigInteger numberFoundDelegations = (BigInteger) query.uniqueResult();
            if(numberFoundDelegations.intValue() == 1) {
            	return true;
            }
            return false;
        } finally {
            session.close();
        }
    }

    @GET
    @Path("/registered/{username}/{eventId}")
    public Boolean isUserRegisteredintoEvent(@PathParam("username") String username, @PathParam("eventId") Integer eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.checkUserRegistration");
            query.setString("username", username);
            query.setInteger("eventId", eventId);

            final BigInteger numberOfGuestsFound = (BigInteger) query.uniqueResult();
            if(numberOfGuestsFound.intValue() > 0) {
            	return true;
            }
            return false;
        } finally {
            session.close();
        }
    }

    /**
     * Add guest to event.
     *
     * @param uuid Uuid
     * @param eventId Event id
     * @param userId User id
     * @param invitationstatus Invitation status
     * @param invitationtype Invitationtype
     * @param gender Gender
     * @param category Category
     *
     * @return Guest
     */
    @POST
    @Path("/{uuid}/register")
    public Guest addGuestToEvent(@PathParam("uuid") String uuid,
                                @FormParam("eventId") Integer eventId,
                                @FormParam("userId") Integer userId,
                                @FormParam("invitationstatus") Integer invitationstatus,
                                @FormParam("invitationtype") Integer invitationtype,
                                @FormParam("gender") String gender,
                                @FormParam("category") Integer category,
                                @FormParam("hostNode") String hostNode,
                                @FormParam("username") String username) {
        final Session session = openSession();
		try {
            final Guest guest = initGuest(uuid,eventId, userId, invitationstatus, invitationtype, gender, category, username, hostNode);

            session.save(guest);
			session.flush();

			return guest;
		} finally {
			session.close();
		}
    }

    /**
     * Add guest to event.
     *
     * @param eventId Event id
     * @param userId User id
     * @param invitationstatus Invitation status
     * @param invitationtype Invitationtype
     * @param gender Gender
     * @param category Category
     *
     * @return Guest
     */
    @POST
    @Path("/register")
    public Guest addGuestToEvent(
                                @FormParam("eventId") Integer eventId,
                                @FormParam("userId") Integer userId,
                                @FormParam("invitationstatus") Integer invitationstatus,
                                @FormParam("invitationtype") Integer invitationtype,
                                @FormParam("gender") String gender,
                                @FormParam("category") Integer category,
                                @FormParam("username") String username, 
                                @FormParam("hostNode") String nodeHost) {
        final Session session = openSession();
        
		try {
            final Guest guest = initGuest(null,eventId, userId, invitationstatus, invitationtype, gender, category, username, nodeHost);

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
    private Guest initGuest(String uuid, Integer eventId, Integer userId, Integer invitationstatus,
                            Integer invitationtype, String gender, Integer category, String username, String hostNode) {
        final Guest guest = new Guest();
        guest.setDelegation(uuid);
        guest.setFk_person(userId);
        guest.setFk_event(eventId);
        guest.setInvitationstatus(invitationstatus);
        guest.setNotehost("");
        guest.setInvitationtype(invitationtype);
        guest.setOsiam_login(username);
        guest.setGender(gender);
        guest.setGender_p(gender);
        guest.setFk_category(category);
        guest.setNotehost(hostNode);

        return guest;
    }
}
