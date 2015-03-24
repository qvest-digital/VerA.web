package de.tarent.aa.veraweb.utils;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.octopus.server.OctopusContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventURLHandlerTest {

    private EventURLHandler eventURLHandler;

    @Mock
    private OctopusContext octopusContext;

    @Mock
    private Event event;

    @Mock
    private PropertiesReader propertiesReader;

    @Before
    public void setUp() {
        eventURLHandler = new EventURLHandler();
        eventURLHandler.setPropertiesReader(propertiesReader);
    }

    @Test
    public void testSetEventUrl() {
        // WHEN
        eventURLHandler.setEventUrl(octopusContext, "44cf712d-90f5-4958-ae49-a2155b63e1c1");

        // THEN
        verify(octopusContext, times(1)).setContent(any(String.class), any(String.class));
    }

    @Test
    public void testSetEventUrlFailed() {
        // WHEN
        eventURLHandler.setEventUrl(octopusContext, null);

        // THEN
        verify(octopusContext, times(1)).setContent("eventUrl", "Nicht verf&uuml;gbar");
    }

    @Test
    public void testGetEventUrl() {
        // GIVEN

        Properties properties = mock(Properties.class);
        when(propertiesReader.propertiesAreAvailable()).thenReturn(true);
        when(propertiesReader.getProperties()).thenReturn(properties);
        when(properties.getProperty(URLGenerator.VERAWEB_ONLINEREG_PROTOCOL)).thenReturn("https");
        when(properties.getProperty(URLGenerator.VERAWEB_ONLINEREG_PORT)).thenReturn("8443");
        when(properties.getProperty(URLGenerator.VERAWEB_ONLINEREG_HOST)).thenReturn("localhost");
        when(event.getHash()).thenReturn("44cf712d-90f5-4958-ae49-a2155b63e1c1");

        // WHEN
        String url = eventURLHandler.generateEventUrl(event);

        // THEN
        assertEquals("https://localhost:8443/#/freevisitors/44cf712d-90f5-4958-ae49-a2155b63e1c1", url);
    }

    @Test
    public void testGetEventUrlFailed() {
        // WHEN
        String url = eventURLHandler.generateEventUrl(null);

        // THEN
        assertEquals("", url);
    }



}