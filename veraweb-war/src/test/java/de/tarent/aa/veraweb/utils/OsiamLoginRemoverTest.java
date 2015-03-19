package de.tarent.aa.veraweb.utils;

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