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
     * @param username The username
     */
    public void deleteOsiamUser(AccessToken accessToken, String username) {
        if (username != null) {
            final String id = getUserId(accessToken, username);
            if (id != null) {
                connector.deleteUser(id, accessToken);
            }
        }
    }

    /**
     * Get id for osiam user by Username.
     *
     * @param accessToken The {@link org.osiam.client.oauth.AccessToken}
     * @param username The username
     *
     * @return User id
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
     * Build query which returns the user id by given username.
     *
     * @param username The username
     *
     * @return The {@link org.osiam.client.query.Query}
     */
    private Query buildQuery(String username) {
        return new QueryBuilder().filter("userName eq \"" + username + "\"").attributes("id").build();
    }
}
