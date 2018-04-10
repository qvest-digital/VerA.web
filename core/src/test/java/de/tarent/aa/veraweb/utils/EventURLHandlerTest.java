package de.tarent.aa.veraweb.utils;

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
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2007 Jan Meyer <jan@evolvis.org>
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

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.octopus.server.OctopusContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
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
        // GIVEN
        Properties properties = mock(Properties.class);
        when(propertiesReader.propertiesAreAvailable()).thenReturn(true);
        when(propertiesReader.getProperties()).thenReturn(properties);
        when(properties.getProperty(URLGenerator.VERAWEB_ONLINEREG_PROTOCOL)).thenReturn("https");
        when(properties.getProperty(URLGenerator.VERAWEB_ONLINEREG_PORT)).thenReturn("8443");
        when(properties.getProperty(URLGenerator.VERAWEB_ONLINEREG_HOST)).thenReturn("localhost");

        // WHEN
        eventURLHandler.setEventUrl(octopusContext, "44cf712d-90f5-4958-ae49-a2155b63e1c1");

        // THEN
        verify(octopusContext, times(1))
                .setContent("eventUrl", "https://localhost:8443/#/freevisitors/44cf712d-90f5-4958-ae49-a2155b63e1c1");
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
