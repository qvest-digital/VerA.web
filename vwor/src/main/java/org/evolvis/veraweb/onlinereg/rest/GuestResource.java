package org.evolvis.veraweb.onlinereg.rest;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
 * @author mley on 03.08.14.
 * @author sweiz
 * @author jnunez
 * @author jobere
 */
@Path("/guest")
@Produces(MediaType.APPLICATION_JSON)
public class GuestResource extends AbstractResource{
    private static final String PARAM_DELEGATION_UUID = "delegationUUID";
    private static final String PARAM_GUEST_ID = "guestId";
    private static final String PARAM_NO_LOGIN_REQUIRED_UUID = "noLoginRequiredUUID";
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_UUID = "uuid";
    private static final String PARAM_DELEGATION = "delegation";
    private static final String PARAM_USER_ID = "userId";
    private static final String PARAM_EVENT_ID = "eventId";
    private static final String QUERY_FIND_BY_EVENT_AND_USER = "Guest.findByEventAndUser";

    /**
     * Get guest
     *
     * @param eventId Event id
     * @param userId User id
     * @return Guest
     */
    @GET
    @Path("/{eventId}/{userId}")
    public Guest getGuest(@PathParam(PARAM_EVENT_ID) int eventId, @PathParam(PARAM_USER_ID) int userId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery(QUERY_FIND_BY_EVENT_AND_USER);
            query.setInteger(PARAM_EVENT_ID, eventId);
            query.setInteger(PARAM_USER_ID, userId);

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
     * @return Guest
     */
    @GET
    @Path("/concrete/{eventId}/{userId}")
    public Integer getGuestId(@PathParam(PARAM_EVENT_ID) int eventId, @PathParam(PARAM_USER_ID) int userId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.findIdByEventAndUser");
            query.setInteger(PARAM_EVENT_ID, eventId);
            query.setInteger(PARAM_USER_ID, userId);
            return (Integer) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Get the image UUID of a guest
     *
     * @param delegationUUID UUID of delegation
     * @param userId ID of user
     * @return image UUID
     */
    @GET
    @Path("/image/{delegationUUID}/{personId}")
    public String getGuestImageUUID(@PathParam(PARAM_DELEGATION_UUID) String delegationUUID,
                                    @PathParam("personId") int userId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.findImageByDelegationAndUser");
            query.setString(PARAM_DELEGATION_UUID, delegationUUID);
            query.setInteger(PARAM_USER_ID, userId);

            return (String) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * TODO We dont need this method. Next method with the same behaviour...
     * Save guest.
     *
     * @param eventId Event id
     * @param userId User id
     * @param invitationstatus invitation status
     * @param notehost note text for host
     * @param reserve 0 = not on reserve, 1 = on reserve
     * @return The guest
     */
    @POST
    @Path("/{eventId}/{userId}")
    public Guest saveGuest(@PathParam(PARAM_EVENT_ID) int eventId,
                           @PathParam(PARAM_USER_ID) int userId,
                           @QueryParam("invitationstatus") int invitationstatus,
                           @QueryParam("notehost") String notehost,
                           @QueryParam("reserve") int reserve) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery(QUERY_FIND_BY_EVENT_AND_USER);
            query.setInteger(PARAM_EVENT_ID, eventId);
            query.setInteger(PARAM_USER_ID, userId);

            final Guest guest = (Guest) query.uniqueResult();
            guest.setNotehost(notehost);
            guest.setReserve(reserve);
            if (reserve == 1 ) {
                guest.setInvitationstatus(0);
            }
            else {
                guest.setInvitationstatus(invitationstatus);
            }

            session.update(guest);
            session.flush();
            return guest;
        } finally {
            session.close();
        }
    }

    /**
     *
     * @param guestId ID of guest
     * @param imgUUID UUID of image
     */
    @POST
    @Path("/update/entity")
    public void updateGuestEntity(@FormParam(PARAM_GUEST_ID) Integer guestId, @FormParam("imgUUID") String imgUUID) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.getGuestById");
            query.setInteger(PARAM_GUEST_ID, guestId);
            final Guest guest = (Guest) query.uniqueResult();
            guest.setImage_uuid(imgUUID);

            session.update(guest);
            session.flush();
        } finally {
            session.close();
        }
    }

    /**
     * Save guest.
     *
     * @param eventId ID of event
     * @param userId ID of user
     * @param invitationstatus 0 = Offen, 1 = Zusage, 2 = Absage
     * @param notehost note text for host
     */
    @POST
    @Path("/update/{eventId}/{userId}")
    public void updateGuest(@PathParam(PARAM_EVENT_ID) int eventId,
                           @PathParam(PARAM_USER_ID) int userId,
                           @FormParam("invitationstatus") int invitationstatus,
                           @FormParam("notehost") String notehost) {
        final Session session = openSession();
        try {
            final Query checkMaxGuestLimit = session.getNamedQuery("Event.checkMaxGuestLimit");
            checkMaxGuestLimit.setInteger(PARAM_EVENT_ID, eventId);

            BigInteger maxGuestLimit = (BigInteger) checkMaxGuestLimit.uniqueResult();

            final Query getGuest = session.getNamedQuery(QUERY_FIND_BY_EVENT_AND_USER);
            getGuest.setInteger(PARAM_EVENT_ID, eventId);
            getGuest.setInteger(PARAM_USER_ID, userId);

            final Guest guest = (Guest) getGuest.uniqueResult();
            guest.setInvitationstatus(invitationstatus);
            guest.setNotehost(notehost);

            //Remove guest from reserve, if maxguests are not greater as guest count
            if(maxGuestLimit.longValue() == 0) {
                guest.setReserve(0);
            }

            session.update(guest);
            session.flush();
        } finally {
            session.close();
        }
    }

    /**
     *
     * @param noLoginRequiredUUID UUID of users without logindata
     * @param invitationstatus 0 = Offen, 1 = Zusage, 2 = Absage
     * @param notehost note text for host
     */
    @POST
    @Path("/update/nologin/{noLoginRequiredUUID}")
    public void updateGuestWithoutLogin(
                            @PathParam(PARAM_NO_LOGIN_REQUIRED_UUID) String noLoginRequiredUUID,
                            @FormParam("invitationstatus") int invitationstatus,
                            @FormParam("notehost") String notehost) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.findByNoLoginUUID");
            query.setString(PARAM_NO_LOGIN_REQUIRED_UUID, noLoginRequiredUUID);

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
     *
     * @param uuid UUID of delegation
     * @return Guest
     */
    @GET
    @Path("/{uuid}")
    public Guest findGuestByEventWithDelegationUUID(@PathParam(PARAM_UUID) String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.findEventIdByDelegationUUID");
            query.setString(PARAM_UUID, uuid);

            return (Guest) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Find guest by delegation and person id.
     *
     * @param uuid UUID of delegation
     * @param userId ID of person
     *
     * @return Guest
     */
    @GET
    @Path("/delegation/{uuid}/{userId}")
    public Guest findGuestByDelegationAndPerson(@PathParam(PARAM_UUID) String uuid, @PathParam(PARAM_USER_ID) int userId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.findByDelegationAndUser");
            query.setString(PARAM_DELEGATION, uuid);
            query.setInteger(PARAM_USER_ID, userId);

            return (Guest) query.uniqueResult();
        } finally {
            session.close();
        }
    }
    /**
     * Find guest by delegation and person id.
     *
     * @param uuid UUID of delegation
     *
     * @return Guest
     */
    @GET
    @Path("/delegation/{uuid}")
    public Guest findGuestByDelegationUUID(@PathParam(PARAM_UUID) String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.findByDelegationUUID");
            query.setString(PARAM_DELEGATION, uuid);

            return (Guest) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Check for existing delegation by delegation uuid.
     *
     * @param uuid UUID of delegation
     * @return True if exists only one delegation, otherwise false
     */
    @GET
    @Path("/exist/{uuid}")
    public Boolean existEventIdByDelegation(@PathParam(PARAM_UUID) String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.guestByUUID");
            query.setString(PARAM_UUID, uuid);

            final BigInteger numberFoundDelegations = (BigInteger) query.uniqueResult();
            if(numberFoundDelegations.intValue() == 1) {
                return true;
            }
            return false;
        } finally {
            session.close();
        }
    }
    
    /**
     * Find whether guest is registered for the event delegation
     * 
     * @param username OSIAM username
     * @param delegation UUID of delegation
     *
     * @return true if guest count over 0
     */
    @GET
    @Path("/registered/delegation/{username}/{delegation}")
    public Boolean isUserRegisteredintoEventByDelegation(@PathParam(PARAM_USERNAME) String username,
                                                         @PathParam(PARAM_DELEGATION) String delegation) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.isGuestForEvent");
            query.setString("osiam_login", username);
            query.setString(PARAM_DELEGATION, delegation);

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
     * Checking if the current user is already registered for the current event
     *
     * @param username OSIAM username
     * @param eventId ID of event
     * @return true if guest count over 0
     */
    @GET
    @Path("/registered/{username}/{eventId}")
    public Boolean isUserRegisteredintoEvent(@PathParam(PARAM_USERNAME) String username,
                                             @PathParam(PARAM_EVENT_ID) Integer eventId) {
        return isUserRegistered(username, eventId, "Guest.checkUserRegistration");
    }

    /**
     * Checking if the current user is already registered for the current event
     *
     * @param username OSIAM username
     * @param eventId ID of event
     * @return true if guest count over 0
     */
    @GET
    @Path("/registered/accept/{username}/{eventId}")
    public Boolean isUserRegisteredintoEventToAccept(@PathParam(PARAM_USERNAME) String username,
                                                     @PathParam(PARAM_EVENT_ID) Integer eventId) {
        return isUserRegistered(username, eventId, "Guest.checkUserRegistrationToAccept");
    }

    /**
     * Generalized method for isUserRegisteredintoEvent and isUserRegisteredintoEventToAccept.
     * 
     * @param username Username
     * @param eventId Event id
     * @param namedQuery Query
     * @return FIXME
     */
    private Boolean isUserRegistered(String username, Integer eventId, String namedQuery) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery(namedQuery);
            query.setString(PARAM_USERNAME, username);
            query.setInteger(PARAM_EVENT_ID, eventId);

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
     * Checking if the current user is registered into the event
     *
     * @param noLoginRequiredUUID UUID of users without logindata
     * @param eventId ID of event
     * @return the person id to allow updating
     */
    @GET
    @Path("/registered/nologin/{noLoginRequiredUUID}/{eventId}")
    public Boolean isUserRegisteredintoEventByUUID(@PathParam(PARAM_NO_LOGIN_REQUIRED_UUID) String noLoginRequiredUUID,
                                                   @PathParam(PARAM_EVENT_ID) Integer eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Guest.checkUserRegistrationWithoutLogin");
            query.setString(PARAM_NO_LOGIN_REQUIRED_UUID, noLoginRequiredUUID);
            query.setInteger(PARAM_EVENT_ID, eventId);

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
     * @param uuid UUID of guest
     * @param eventId ID of event
     * @param userId ID of user
     * @param invitationstatus
     *      0 = Offen,
     *      1 = Zusage,
     *      2 = Absage
     * @param invitationtype
     *      0 = main person and partner invited,
     *      1 = main person and partner invited,
     *      2 = only main person invited,
     *      3 = only partner invited
     * @param gender m = male, f = female
     * @param category Category
     * @param hostNode Note text for host
     * @param username Username
     *
     * @return Guest
     */
    @POST
    @Path("/{uuid}/register")
    public Guest addGuestToEvent(@PathParam(PARAM_UUID) String uuid,
                                @FormParam(PARAM_EVENT_ID) Integer eventId,
                                @FormParam(PARAM_USER_ID) Integer userId,
                                @FormParam("invitationstatus") Integer invitationstatus,
                                @FormParam("invitationtype") Integer invitationtype,
                                @FormParam("gender") String gender,
                                @FormParam("category") Integer category,
                                @FormParam("hostNode") String hostNode,
                                @FormParam(PARAM_USERNAME) String username) {
        final Session session = openSession();
        try {
            //0 = not in reserve list
            final Guest guest = initGuest(uuid, eventId, userId, invitationstatus, invitationtype, gender, category,
                    username, hostNode, 0);

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
     * @param eventId ID of event
     * @param userId ID of user
     * @param invitationstatus
     *      0 = Offen,
     *      1 = Zusage,
     *      2 = Absage
     * @param invitationtype
     *      0 = main person and partner invited,
     *      1 = main person and partner invited,
     *      2 = only main person invited,
     *      3 = only partner invited
     * @param gender m = male, f = female
     * @param category Category
     * @param username Username
     * @param reserve 0 = not on reserve, 1 = on reserve
     * @param nodeHost Node text for host
     *
     * @return Guest
     */
    @POST
    @Path("/register")
    public Guest addGuestToEvent(
                                @FormParam(PARAM_EVENT_ID) Integer eventId,
                                @FormParam(PARAM_USER_ID) Integer userId,
                                @FormParam("invitationstatus") Integer invitationstatus,
                                @FormParam("invitationtype") Integer invitationtype,
                                @FormParam("gender") String gender,
                                @FormParam("category") Integer category,
                                @FormParam(PARAM_USERNAME) String username, 
                                @FormParam("hostNode") String nodeHost,
                                @FormParam("reserve") Integer reserve) {
        final Session session = openSession();

        try {
            final Guest guest = initGuest(null, eventId, userId, invitationstatus, invitationtype, gender, category,
                    username, nodeHost, reserve);

            final Query query = session.getNamedQuery("Guest.findIdByEventAndUser");
            query.setInteger(PARAM_EVENT_ID, eventId);
            query.setInteger(PARAM_USER_ID, userId);
            Integer guestId = (Integer) query.uniqueResult();
            if (guestId != null) {
                guest.setPk(guestId);
            }

            session.saveOrUpdate(guest);
            session.flush();

            return guest;
        } finally {
            session.close();
        }
    }

    /**
     * Checking if the guest is a reserve or not
     *
     * @param eventId the event ID
     * @param username the username - osiam_login
     *
     * @return FIXME
     */
    @GET
    @Path("/isreserve/{eventId}/{username}")
    public Boolean isReserve(@PathParam(PARAM_EVENT_ID) final Integer eventId,
                             @PathParam(PARAM_USERNAME) final String username) {

        final Session session = openSession();
        try {

            final Query query = session.getNamedQuery("Guest.isReserve");
            query.setInteger(PARAM_EVENT_ID, eventId);
            query.setString(PARAM_USERNAME, username);
            Integer reserve = (Integer) query.uniqueResult();

            if (reserve != null && reserve.equals(1)) {
                return true;
            }
            return false;
        } finally {
            session.close();
        }
    }

    /**
     * Initialize guest with event information
     */
    private Guest initGuest(String uuid, Integer eventId, Integer userId, Integer invitationstatus,
                            Integer invitationtype, String gender, Integer category, String username,
                            String hostNode, Integer reserve) {
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
        guest.setReserve(reserve);

        return guest;
    }
}
