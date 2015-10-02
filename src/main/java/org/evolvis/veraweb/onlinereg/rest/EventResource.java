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

	/**
	 * Getting the list of openned Events
	 * 
	 * @return List<Event> List of events
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
     * @return List<Event> List of events
     */
    @Path("/userevents/{username}")
    @GET
    public List<Event> listUsersEvents(@PathParam("username") String username) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Person.findPersonIdByUsername");
            query.setString("username", username);
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
    
    @Path("/userid/{username}")
    @GET
    public Integer getPersonIdByUsername(@PathParam("username") String username) {
        final Session session = openSession();

        try {
            final Query query = session.getNamedQuery("Person.findPersonIdByUsername");
            query.setString("username", username);
            if (query.list().isEmpty()) {
                // user does not exists
                // MUST NOT HAPPEN, BUT IT IS A PREVENTION
                return null;
            } else {
                final Integer personId = (Integer) query.uniqueResult();
                return personId;
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
    public Event getEvent(@PathParam("eventId") int eventId) {
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
    public Boolean existEventIdByDelegation(@PathParam("uuid") String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Event.guestByUUID");
            query.setString("uuid", uuid);
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
    public Integer getEventIdByUUID(@PathParam("uuid") String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Event.getEventByUUID");
            query.setString("uuid", uuid);

            final Integer eventId = (Integer) query.uniqueResult();
            return eventId;

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
    public Boolean isOpenEvent(@PathParam("eventId") Integer eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Event.isOpen");
            query.setInteger("eventId", eventId);

            final BigInteger counter = (BigInteger) query.uniqueResult();

            if (counter.longValue() > 0) {
                return true;
            }
            return false;

        } finally {
            session.close();
        }
    }

    @GET
    @Path("/guestlist/status/{eventId}")
    public Boolean isGuestListFull(@PathParam("eventId") Integer eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Event.checkMaxGuestLimit");
            query.setInteger("eventId", eventId);

            final BigInteger counter = (BigInteger) query.uniqueResult();

            if (counter.longValue() > 0) {
                return true;
            }
            return false;

        } finally {
            session.close();
        }
    }

    @GET
    @Path("/reservelist/status/{eventId}")
    public Boolean isReserveListFull(@PathParam("eventId") Integer eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Event.checkMaxReserveLimit");
            query.setInteger("eventId", eventId);

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
}
