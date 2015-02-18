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
package org.evolvis.veraweb.onlinereg.event;

import org.classpath.icedtea.Config;
import org.evolvis.veraweb.onlinereg.TestSuite;
import org.evolvis.veraweb.onlinereg.Main;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.jersey.api.client.UniformInterfaceException;

import sun.security.action.GetLongAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by mley on 28.08.14.
 */
public class EventResourceTest {

    private EventResource er;
    private List<Event> event;
    private Config config;

    public EventResourceTest() throws IOException {
    	Main main = TestSuite.DROPWIZARD.getApplication();

        er = main.getEventResource();
    }

    @Test
    public void testListEvents() throws IOException {
        List<Event> events = new ArrayList<Event>(er.getEvents());
        assertEquals(3, events.size());
        assertEquals("Woche der Brüderlichkeit", events.get(1).getShortname());
        //TODO verbessern
        assertEquals("Kamin", (events.get(2).getLocation().toString().replaceAll(".*locationname=", "").replace(")", "")));
    }

    @Test
    public void testGetEvent() throws IOException {
        Event e = er.getEvent(1);
        assertEquals("Tag der deutschen Einheit", e.getShortname());
        assertEquals(new Date(1406930430000l), e.getDatebegin());
    }

    @Test
    public void testGetRegistration() throws IOException {
        Guest g = er.getRegistration(1, 2);
        assertEquals(1, g.getInvitationstatus());
        assertEquals("Notiz", g.getNotehost());
    }

    @Test@Ignore
    public void testSaveRegistration() throws IOException {
        String returnedValue = er.register("1", "2", "note");
        assertEquals("OK", returnedValue);
    }
}
