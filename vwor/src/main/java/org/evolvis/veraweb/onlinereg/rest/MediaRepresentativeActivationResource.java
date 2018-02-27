package org.evolvis.veraweb.onlinereg.rest;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
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
import org.evolvis.veraweb.onlinereg.entities.MediaRepresentativeActivation;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/press/activation")
@Produces(MediaType.APPLICATION_JSON)
public class MediaRepresentativeActivationResource extends AbstractResource {

    @POST
    @Path("/create")
    public MediaRepresentativeActivation addMediaRepresentativeActivationEntry(
        @FormParam("activationToken") String activationToken,
        @FormParam("email") String email,
        @FormParam("eventId") Integer eventId,
        @FormParam("gender") String gender,
        @FormParam("address") String address,
        @FormParam("city") String city,
        @FormParam("country") String country,
        @FormParam("firstname") String firstname,
        @FormParam("lastname") String lastname,
        @FormParam("zip") Integer zip
    ) {

        final Session session = openSession();
        try {

            final MediaRepresentativeActivation mediaRepresentativeActivation = initMediaRepresentativeActivation(
                    activationToken, email, eventId, gender, address, city, country, firstname, lastname, zip
            );
            session.persist(mediaRepresentativeActivation);
            session.flush();
            return mediaRepresentativeActivation;
        } finally {
            session.close();
        }
    }

    @GET
    @Path("/exists/{encodedAddress}/{eventId}")
    public Boolean existEventIdByDelegation(@PathParam("encodedAddress") String encodedAddress, @PathParam("eventId") String eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("MediaRepresentativeActivation.getEntryByEmailAndEventId");
            query.setInteger("fk_event", Integer.parseInt(eventId));
            query.setString("email", encodedAddress);
            final BigInteger activations = (BigInteger) query.uniqueResult();
            if (activations != null && activations.intValue() >= 1) {
                return true;
            }
            return false;
        } finally {
            session.close();
        }
    }

    @GET
    @Path("/exists/{mediaRepresentativeActivationToken}")
    public MediaRepresentativeActivation getMediaRepresentativeActivationByToken(
            @PathParam("mediaRepresentativeActivationToken") String mediaRepresentativeActivationToken) {

        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("MediaRepresentativeActivation.getActivationByActivationToken");
            query.setString("activation_token", mediaRepresentativeActivationToken);
            return (MediaRepresentativeActivation) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    @POST
    @Path("/update")
    public void activatePressUser(@FormParam("email") String email, @FormParam("eventId") Integer eventId) {

        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("MediaRepresentativeActivation.activate");
            query.setString("email", email);
            query.setInteger("fk_event", eventId);
            query.executeUpdate();
        } finally {
            session.close();
        }
    }

    private MediaRepresentativeActivation initMediaRepresentativeActivation(
            String activationToken,
            String email,
            Integer eventId,
            String gender,
            String address,
            String city,
            String country,
            String firstname,
            String lastname,
            Integer zip) {
        final MediaRepresentativeActivation mediaRepresentativeActivation = new MediaRepresentativeActivation();
        mediaRepresentativeActivation.setActivation_token(activationToken);
        mediaRepresentativeActivation.setEmail(email);
        mediaRepresentativeActivation.setFk_event(eventId);
        mediaRepresentativeActivation.setGender(gender);
        mediaRepresentativeActivation.setAddress(address);
        mediaRepresentativeActivation.setCity(city);
        mediaRepresentativeActivation.setCountry(country);
        mediaRepresentativeActivation.setFirstname(firstname);
        mediaRepresentativeActivation.setLastname(lastname);
        mediaRepresentativeActivation.setZip(zip);
        mediaRepresentativeActivation.setActivated(0);
        
        return mediaRepresentativeActivation;
    }
}
