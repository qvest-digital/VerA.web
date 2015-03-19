package de.tarent.aa.veraweb.utils;

import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryBuilder;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;

/**
 * This class handles the osiam user deletion.
 *
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class OsiamLoginRemover {

    private OsiamConnector connector;

    /**
     * Custom constructor.
     *
     * @param connector The {@link org.osiam.client.OsiamConnector}
     */
    public OsiamLoginRemover(OsiamConnector connector) {
        this.connector = connector;
    }

    /**
     * Delete osiam user by username.
     *
     * @param accessToken The {@link org.osiam.client.oauth.AccessToken}
     * @param username String the username
     */
    public void deleteOsiamUser(AccessToken accessToken, String username) {
        final String id = getUserId(accessToken, username);
        if (id != null) {
            connector.deleteUser(id, accessToken);
        }
    }

    /**
     * Get OSIAM-ID by Username
     * 
     * @param accessToken token
     * @param username String the username
     * @return String user id
     */
    private String getUserId(AccessToken accessToken, String username) {
        final Query query = buildQuery(username.toLowerCase());
        final SCIMSearchResult<User> result = connector.searchUsers(query, accessToken);

        String id = null;
        if (result.getTotalResults() > 0) {
            id = result.getResources().get(0).getId();
        }
        return id;
    }

    /**
     * OSIAM GetId query builder
     * @param username String the username
     * @return Query the query
     */
    private Query buildQuery(String username) {
        return new QueryBuilder().filter("userName eq \"" + username + "\"").attributes("id").build();
    }
}
