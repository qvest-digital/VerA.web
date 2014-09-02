package org.evolvis.veraweb.onlinereg.event;

import org.evolvis.veraweb.onlinereg.TestSuite;
import org.evolvis.veraweb.onlinereg.Main;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by mley on 28.08.14.
 */
public class EventResourceTest {

    private EventResource er;

    public EventResourceTest() {
        Main main = TestSuite.DROPWIZARD.getApplication();
        er = main.getEventResource();

    }

    @Test
    public void testListEvents() throws IOException {

        List<Event> events = er.getEvents();
        assertEquals(3, events.size());
        assertEquals("Woche der Brüderlichkeit", events.get(1).getShortname());
        assertEquals("Kamin", events.get(2).getLocation().getLocationname());

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
        assertEquals("Zusage", g.getNotehost());
    }

    @Test
    public void testSaveRegistration() throws IOException {
        Guest g = er.register(1, 2, "2", "note");
    }
}
