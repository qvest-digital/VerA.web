package org.evolvis.veraweb.onlinereg.event;

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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.Main;
import org.evolvis.veraweb.onlinereg.TestSuite;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mley on 28.08.14.
 */
public class UserResourceTest {

    private final UserResource ur;
    private Client client;
    private Config config;

    public UserResourceTest() {
        Main main = TestSuite.DROPWIZARD.getApplication();
        ur = main.getUserResource();
        client = ur.getClient();
        config = ur.getConfig();
    }

    @Test
    @Ignore
    public void testRegisterUser() throws IOException {
        //Benutzer wird durch Test angelegt, aber nicht mehr gelöscht -> Erneutes Ausführen des Tests schlägt fehl
        long zeit = Calendar.getInstance().getTimeInMillis();

        String result = ur.registerUser("newusertest" + zeit, "firstnametest" + zeit, "secondnametest" + zeit, "passwordtest" + zeit, "email" + zeit,
                "language");
        assertEquals(StatusConverter.convertStatus("OK"), result);
    }

    //testet, ob post() nach Veraweb eine Person-Instanz zurückliefert
    @Test
    @Ignore
    public void testVerawebPerson() throws IOException {

        WebResource r = client.resource(config.getVerawebEndpoint() + "/rest/person/");
        r = r.queryParam("username", "newuserTwo").queryParam("firstname", "firstnameTwo").queryParam("lastname", "lastnameTwo");

        try {
            Person person = r.post(Person.class);
            assertTrue(person instanceof Person);

        } catch (UniformInterfaceException uie) {
            ClientResponse response = uie.getResponse();
            if (response.getStatus() == 404) {
                System.out.println("404");
            } else {
                throw uie;
            }
        }
    }

    @Test
    public void testRegisterUserInvaldUsername() throws IOException {
        String result = ur.registerUser("invalid_username%&/", "firstname", "secondname", "password", "email", "language");
        assertEquals(StatusConverter.convertStatus("INVALID_USERNAME"), result);
    }

    @Test
    @Ignore
    public void testRegisterExistingUser() throws IOException {
        String result = ur.registerUser("existing", "firstname", "secondname", "password", "email", "language");
        assertEquals(StatusConverter.convertStatus("USER_EXISTS"), result);
    }

}
