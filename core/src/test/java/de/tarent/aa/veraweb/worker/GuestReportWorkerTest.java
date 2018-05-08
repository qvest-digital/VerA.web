package de.tarent.aa.veraweb.worker;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.GuestSearch;
import de.tarent.aa.veraweb.beans.Location;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class GuestReportWorkerTest {

    @Test
    public void testCreateReport() throws BeanException {

        //given:
        GuestReportWorker worker = new GuestReportWorker();
        GuestListWorker guestListWorker = new GuestListWorker();

        OctopusContext octopusContext = mock(OctopusContext.class);

        //Event
        Event event = mock(Event.class);
        doReturn(event).when(octopusContext).contentAsObject("event");
        doReturn("Kat02").when(octopusContext).requestAsString("type");

        //Search
        GuestSearch search = mock(GuestSearch.class);
        search.invitationstatus = 2;
        doReturn(search).when(octopusContext).contentAsObject("search");

        //Location
        Location location = mock(Location.class);
        doReturn(location).when(octopusContext).contentAsObject("location");

        //WorkerFactory
        when(WorkerFactory.getGuestListWorker(octopusContext)).thenReturn(guestListWorker);

        //when:
        worker.createReport(octopusContext);

        //then:
        //TODO: check if title == title and so one
        octopusContext.getContentObject();

    }
}
