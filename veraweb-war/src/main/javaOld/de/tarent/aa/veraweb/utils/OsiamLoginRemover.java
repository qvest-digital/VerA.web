package de.tarent.aa.veraweb.utils;

import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;

/**
 * Created by aalexa on 18.03.15.
 */
public class OsiamLoginRemover {

    /**
     * Delete given user in OSIAM
     *
     * @param accessToken
     * @param id
     * @param connector
     */
    public void deleteOsiamUser(AccessToken accessToken,
                                       String id,
                                       OsiamConnector connector) {
        // delete User in osiam
        connector.deleteUser(id, accessToken);
    }
}
