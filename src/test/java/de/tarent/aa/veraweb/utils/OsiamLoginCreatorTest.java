package de.tarent.aa.veraweb.utils;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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