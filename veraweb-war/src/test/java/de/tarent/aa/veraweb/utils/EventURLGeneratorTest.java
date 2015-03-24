package de.tarent.aa.veraweb.utils;

import de.tarent.octopus.server.OctopusContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EventURLGeneratorTest {

    private EventURLGenerator eventURLGenerator;

    @Mock
    private OctopusContext octopusContext;

    @Before
    public void setUp() {
        eventURLGenerator = new EventURLGenerator();
    }

    @Test
    public void testSetEventUrl() {
        eventURLGenerator.setEventUrl(octopusContext, "44cf712d-90f5-4958-ae49-a2155b63e1c1");

        verify(octopusContext, times(1)).setContent(any(String.class), any(String.class));
    }

    @Test
    public void testSetEventUrlFailed() {
        eventURLGenerator.setEventUrl(octopusContext, null);

        verify(octopusContext, times(1)).setContent("eventUrl", "Nicht verf&uuml;gbar");
    }

}