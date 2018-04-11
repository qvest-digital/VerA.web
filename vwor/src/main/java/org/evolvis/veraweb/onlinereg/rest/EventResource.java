package org.evolvis.veraweb.onlinereg.rest;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nunez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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

import org.evolvis.veraweb.onlinereg.entities.Event;
import org.hibernate.query.Query;
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
     * @param session  Session
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
     * @param session  Session
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
