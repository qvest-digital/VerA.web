package de.tarent.aa.veraweb.utils;

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
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
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

import de.tarent.octopus.beans.Database;
import de.tarent.octopus.server.OctopusContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryBuilder;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OsiamLoginCreatorTest {

    OsiamLoginCreator osiamLoginCreator;

    @Mock
    private Database database;

    @Mock
    private OsiamConnector connector;

    @Mock
    private QueryBuilder queryBuilder;

    @Before
    public void setUp() throws Exception {
        osiamLoginCreator = new OsiamLoginCreator(database);
        osiamLoginCreator.setDatabase(database);
        osiamLoginCreator.setQueryBuilder(queryBuilder);
    }

    @Test
    public void testGeneratePersonUsernameNoExisting() throws Exception {
        // GIVEN
        OctopusContext oc = mock(OctopusContext.class);
        AccessToken accessToken = mock(AccessToken.class);

        // FIRST LOOP
        SCIMSearchResult<User> scimUsersResults = mock(SCIMSearchResult.class);
        Query query = new QueryBuilder().filter("userName eq \"mmuste\"").attributes("id").build();
        when(queryBuilder.filter("userName eq \"mmuste\"")).thenReturn(queryBuilder);
        when(queryBuilder.attributes("id")).thenReturn(queryBuilder);
        when(queryBuilder.build()).thenReturn(query);
        when(connector.searchUsers(query, accessToken)).thenReturn(scimUsersResults);
        when(scimUsersResults.getTotalResults()).thenReturn(1l);

        // SECOND LOOP
        SCIMSearchResult<User> scimUsersResults1 = mock(SCIMSearchResult.class);
        Query query1 = new QueryBuilder().filter("userName eq \"mmuste1\"").attributes("id").build();
        QueryBuilder queryBuilder1 = mock(QueryBuilder.class);
        when(queryBuilder.filter("userName eq \"mmuste1\"")).thenReturn(queryBuilder1);
        when(queryBuilder1.attributes("id")).thenReturn(queryBuilder1);
        when(queryBuilder1.build()).thenReturn(query1);
        when(connector.searchUsers(query1, accessToken)).thenReturn(scimUsersResults1);
        when(scimUsersResults1.getTotalResults()).thenReturn(0l);

        when(connector.retrieveAccessToken(Scope.ALL)).thenReturn(accessToken);

        // WHEN
        String username = osiamLoginCreator.generatePersonUsername("Max", "Mustermann", connector);

        // THEN
        assertEquals("mmuste1", username);
    }

    @Test
    public void testGeneratePersonUsernameNoWhitespace() throws Exception {
        // GIVEN
        OctopusContext oc = mock(OctopusContext.class);
        AccessToken accessToken = mock(AccessToken.class);

        // FIRST LOOP
        SCIMSearchResult<User> scimUsersResults = mock(SCIMSearchResult.class);
        Query query = new QueryBuilder().filter("userName eq \"hvande\"").attributes("id").build();
        when(queryBuilder.filter("userName eq \"hvande\"")).thenReturn(queryBuilder);
        when(queryBuilder.attributes("id")).thenReturn(queryBuilder);
        when(queryBuilder.build()).thenReturn(query);
        when(connector.searchUsers(query, accessToken)).thenReturn(scimUsersResults);
        when(scimUsersResults.getTotalResults()).thenReturn(0l);

        when(connector.retrieveAccessToken(Scope.ALL)).thenReturn(accessToken);

        // WHEN
        String username = osiamLoginCreator.generatePersonUsername("Henry", "Van de Velde", connector);

        // THEN
        assertEquals("hvande", username);
    }

    @Test
    public void testGeneratePersonUsernameNotLongerFive() throws Exception {
        // GIVEN
        OctopusContext oc = mock(OctopusContext.class);
        AccessToken accessToken = mock(AccessToken.class);

        // FIRST LOOP
        SCIMSearchResult<User> scimUsersResults = mock(SCIMSearchResult.class);
        Query query = new QueryBuilder().filter("userName eq \"hvan\"").attributes("id").build();
        when(queryBuilder.filter("userName eq \"hvan\"")).thenReturn(queryBuilder);
        when(queryBuilder.attributes("id")).thenReturn(queryBuilder);
        when(queryBuilder.build()).thenReturn(query);
        when(connector.searchUsers(query, accessToken)).thenReturn(scimUsersResults);
        when(scimUsersResults.getTotalResults()).thenReturn(0l);

        when(connector.retrieveAccessToken(Scope.ALL)).thenReturn(accessToken);

        // WHEN
        String username = osiamLoginCreator.generatePersonUsername("Henry", "V An", connector);

        // THEN
        assertEquals("hvan", username);
    }

    @Test
    public void testGenerateCompanyUsernameNoWhitespace() throws Exception {
        // GIVEN
        OctopusContext oc = mock(OctopusContext.class);
        AccessToken accessToken = mock(AccessToken.class);

        // FIRST LOOP
        SCIMSearchResult<User> scimUsersResults = mock(SCIMSearchResult.class);
        Query query = new QueryBuilder().filter("userName eq \"tarentsolu\"").attributes("id").build();
        when(queryBuilder.filter("userName eq \"tarentsolu\"")).thenReturn(queryBuilder);
        when(queryBuilder.attributes("id")).thenReturn(queryBuilder);
        when(queryBuilder.build()).thenReturn(query);
        when(connector.searchUsers(query, accessToken)).thenReturn(scimUsersResults);
        when(scimUsersResults.getTotalResults()).thenReturn(0l);

        when(connector.retrieveAccessToken(Scope.ALL)).thenReturn(accessToken);

        // WHEN
        String username = osiamLoginCreator.generateCompanyUsername("tarent solutions GmbH", connector);

        // THEN
        assertEquals("tarentsolu", username);
    }

    @Test
    public void testGenerateCompanyUsernameNotLongerTen() throws Exception {
        // GIVEN
        OctopusContext oc = mock(OctopusContext.class);
        AccessToken accessToken = mock(AccessToken.class);

        // FIRST LOOP
        SCIMSearchResult<User> scimUsersResults = mock(SCIMSearchResult.class);
        Query query = new QueryBuilder().filter("userName eq \"targmbh\"").attributes("id").build();
        when(queryBuilder.filter("userName eq \"targmbh\"")).thenReturn(queryBuilder);
        when(queryBuilder.attributes("id")).thenReturn(queryBuilder);
        when(queryBuilder.build()).thenReturn(query);
        when(connector.searchUsers(query, accessToken)).thenReturn(scimUsersResults);
        when(scimUsersResults.getTotalResults()).thenReturn(0l);

        when(connector.retrieveAccessToken(Scope.ALL)).thenReturn(accessToken);

        // WHEN
        String username = osiamLoginCreator.generateCompanyUsername("tar GmbH", connector);

        // THEN
        assertEquals("targmbh", username);
    }

    @Test
    public void testGeneratePassword() throws Exception {
        for (int i = 0; i < 1000; i++) {
            String password = osiamLoginCreator.generatePassword();
            assertEquals(8, password.length());
            assertTrue(password.matches(osiamLoginCreator.CONDITIONS));
        }
    }

    @Test
    public void testCreateOsiamUser() throws Exception {
        OsiamConnector osiamConnector = mock(OsiamConnector.class);
        osiamLoginCreator.createOsiamUser(mock(AccessToken.class), "Max", "Mustermann", osiamConnector);

        verify(osiamConnector, times(1)).createUser(any(User.class), any(AccessToken.class));
    }
}
