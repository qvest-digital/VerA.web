/**
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
package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Event;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by mley on 03.08.14.
 */
@Path("/event")
@Produces(MediaType.APPLICATION_JSON)
public class EventResource extends AbstractResource {
    private static final String QUERY_FIND_PERSON_ID_BY_USERNAME = "Person.findPersonIdByUsername";
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_UUID = "uuid";
    private static final String PARAM_EVENT_ID = "eventId";

    /**
     * Getting the list of openned Events
     * 
     * @return List of events
     */
    @Path("/")
    @GET
    public List<Event> listEvents() {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Event.list");
            return (List<Event>) query.list();

        } finally {
            session.close();
        }

    }

    /**
     * Getting the list of events of a person using the username (by previous getting of the id)
     *
     * @param username String
     * @return List of events
     */
    @Path("/userevents/{username}")
    @GET
    public List<Event> listUsersEvents(@PathParam(PARAM_USERNAME) String username) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery(QUERY_FIND_PERSON_ID_BY_USERNAME);
            query.setString(PARAM_USERNAME, username);
            if (query.list().isEmpty()) {
                // user does not exists
                return null;
            } else {
                final int personId = (int) query.uniqueResult();
                return getUsersEvents(session, personId);
            }
        } finally {
            session.close();
        }
    }

    /**
     * Checks if a person is registered to at least one event using the username
     *
     * @param username String
     * @return Boolean true, if user is registered to at least one event
     */
    @Path("/userevents/existing/{username}")
    @GET
    public Boolean checkUserRegistrationToEvents(@PathParam(PARAM_USERNAME) String username) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery(QUERY_FIND_PERSON_ID_BY_USERNAME);
            query.setString(PARAM_USERNAME, username);
            if (query.list().isEmpty()) {
                // user does not exists
                return false;
            } else {
                final int personId = (int) query.uniqueResult();
                return hasUserRegistrationToEvents(session, personId);
            }
        } finally {
            session.close();
        }
    }
    
    @Path("/userid/{username}")
    @GET
    public Integer getPersonIdByUsername(@PathParam(PARAM_USERNAME) String username) {
        final Session session = openSession();

        try {
            final Query query = session.getNamedQuery(QUERY_FIND_PERSON_ID_BY_USERNAME);
            query.setString(PARAM_USERNAME, username);
            if (query.list().isEmpty()) {
                // user does not exists
                // MUST NOT HAPPEN, BUT IT IS A PREVENTION
                return null;
            } else {
                return (Integer) query.uniqueResult();
            }
        } finally {
            session.close();
        }

    }

    /**
     * Get event using the event id
     * 
     * @param eventId int
     * @return Event the event
     */
    @Path("/{eventId}")
    @GET
    public Event getEvent(@PathParam(PARAM_EVENT_ID) int eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Event.getEvent");
            query.setInteger("pk", eventId);
            return (Event) query.uniqueResult();
        } finally {
            session.close();
        }

    }

    /**
     * Checks if a delegation exists according to the relationship between delegation and event
     * 
     * @param uuid String the uuid
     * @return the checking
     */
    @GET
    @Path("/exist/{uuid}")
    public Boolean existEventIdByDelegation(@PathParam(PARAM_UUID) String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Event.guestByUUID");
            query.setString(PARAM_UUID, uuid);
            final BigInteger numberFoundDelegations = (BigInteger) query.uniqueResult();
            if (numberFoundDelegations.intValue() >= 1) {
                return true;
            }
            return false;
        } finally {
            session.close();
        }
    }
    
    /**
     * Get event id using the uuid from a Press Event
     * 
     * @param uuid press uuid
     * @return Integer the event id
     */
    @GET
    @Path("/require/{uuid}")
    public Integer getEventIdByUUID(@PathParam(PARAM_UUID) String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Event.getEventByUUID");
            query.setString(PARAM_UUID, uuid);

            return (Integer) query.uniqueResult();

        } finally {
            session.close();
        }
    }
    
    /**
     * Checking if one event (eventId) is open or not
     * 
     * @param eventId Integer pk
     * @return Boolean check
     */
    @GET
    @Path("/isopen/{eventId}")
    public Boolean isOpenEvent(@PathParam(PARAM_EVENT_ID) Integer eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Event.isOpen");
            query.setInteger(PARAM_EVENT_ID, eventId);

            final BigInteger counter = (BigInteger) query.uniqueResult();

            return counter.longValue() > 0;

        } finally {
            session.close();
        }
    }

    @GET
    @Path("/guestlist/status/{eventId}")
    public Boolean isGuestListFull(@PathParam(PARAM_EVENT_ID) Integer eventId) {
        return isListFull(eventId, "Event.checkMaxGuestLimit");
    }

    @GET
    @Path("/reservelist/status/{eventId}")
    public Boolean isReserveListFull(@PathParam(PARAM_EVENT_ID) Integer eventId) {
        return isListFull(eventId, "Event.checkMaxReserveLimit");
    }

    /**
     * Get event using the uuid
     *
     * @param uuid String
     * @return Event the event
     */
    @Path("/uuid/{uuid}")
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
    /**
     * Generalized method for isGuestListFull and isReserveListFull.
     * 
     * @param eventId
     * @param namedQuery
     * @return
     */
    private Boolean isListFull(Integer eventId, String namedQuery) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery(namedQuery);
            query.setInteger(PARAM_EVENT_ID, eventId);

            final BigInteger counter = (BigInteger) query.uniqueResult();

            if (counter.longValue() > 0) {
                return true;
            }
            return false;

        } finally {
            session.close();
        }
    }
    
    /**
     * Get the events associated to a person
     *
     * @param session Session
     * @param personId ID
     * @return List<Event> List of events
     */
    private List<Event> getUsersEvents(Session session, int personId) {
        final Query query = session.getNamedQuery("Event.list.userevents");
        query.setInteger("fk_person", personId);

        return query.list();
    }

    /**
     * Checks the quantity of events associated to a person
     *
     * @param session Session
     * @param personId ID
     * @return Boolean true, if user is registered to minimum one event
     */
    private Boolean hasUserRegistrationToEvents(Session session, int personId) {
        final Query query = session.getNamedQuery("Event.count.userevents");
        query.setInteger("fk_person", personId);
        final BigInteger isUserRegisteredToEvent = (BigInteger) query.uniqueResult();

        return isUserRegisteredToEvent.signum() == 1;
    }
}
