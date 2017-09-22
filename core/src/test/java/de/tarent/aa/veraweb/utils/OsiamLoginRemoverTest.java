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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OsiamLoginRemoverTest {

    private OsiamLoginRemover osiamLoginRemover;

    @Mock
    private OsiamConnector connector;

    @Before
    public void setUp() {
        osiamLoginRemover = new OsiamLoginRemover(connector);
    }

    @Test
    public void testRemoveOsiamUser() {
        // GIVEN
        AccessToken accessToken = mock(AccessToken.class);
        SCIMSearchResult<User> result = mock(SCIMSearchResult.class);
        when(connector.searchUsers(any(Query.class), any(AccessToken.class))).thenReturn(result);
        when(result.getTotalResults()).thenReturn(1l);
        List<User> users = getDummyUsers();
        when(result.getResources()).thenReturn(users);

        // WHEN
        osiamLoginRemover.deleteOsiamUser(accessToken, "mmuster");

        // THEN
        verify(connector, times(1)).deleteUser("id", accessToken);
    }

    @Test
    public void testRemoveUserButNotFound() {
        // GIVEN
        AccessToken accessToken = mock(AccessToken.class);
        SCIMSearchResult<User> result = mock(SCIMSearchResult.class);
        when(connector.searchUsers(any(Query.class), any(AccessToken.class))).thenReturn(result);
        when(result.getTotalResults()).thenReturn(0l);

        // WHEN
        osiamLoginRemover.deleteOsiamUser(accessToken, "mmuster");

        // THEN
        verify(connector, times(0)).deleteUser(any(String.class), any(AccessToken.class));
    }

    private List<User> getDummyUsers() {
        User user = mock(User.class);

        when(user.getId()).thenReturn("id");

        List<User> users = new ArrayList<User>();
        users.add(user);

        return  users;
    }

}