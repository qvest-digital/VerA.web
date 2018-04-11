package org.evolvis.veraweb.onlinereg.osiam;

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
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nunez Alvarez (j.nunez-alvarez@tarent.de)
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

import com.sun.jersey.api.client.Client;
import lombok.extern.java.Log;
import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryBuilder;
import org.osiam.client.user.BasicUser;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;

import java.io.IOException;
import java.util.List;

/**
 * Created by mley on 27.08.14.
 *
 * OSIAM client
 */
@Log
public class OsiamClient {
    private OsiamConnector connector;

    /**
     * Creates a new OSIAM client
     *
     * @param config OSIAM config object
     * @param client jersey HTTP client
     */
    public OsiamClient(OsiamConfig config, Client client) {
        this.connector = new OsiamConnector.Builder()
          .setEndpoint(config.getEndpoint())
          .setClientId(config.getClientId())
          .setClientSecret(config.getClientSecret())
          .build();
    }

    /**
     * Gets an access token with username and password with the resource owner password grant
     *
     * @param username username
     * @param password password
     * @param scopes   scopes
     * @return accesstoken as String
     * @throws IOException if an error occurs, e.g. service is not available, user does not exists, password wrong
     */
    public String getAccessTokenUserPass(String username, String password, String... scopes) throws IOException {
        Scope[] scopeObjects = new Scope[scopes.length];

        for (int i = 0; i < scopes.length; ++i) {
            scopeObjects[i] = new Scope(scopes[i]);
        }

        AccessToken at = this.connector.retrieveAccessToken(username, password, scopeObjects);
        return at.getToken();
    }

    /**
     * Gets an access token with the client credentials
     *
     * @param scopes scopes
     * @return access token
     * @throws IOException if an error occurs, e.g. service is not available, client credentials wrong
     */
    public String getAccessTokenClientCred(String... scopes) throws IOException {
        Scope[] scopeObjects = new Scope[scopes.length];

        for (int i = 0; i < scopes.length; ++i) {
            scopeObjects[i] = new Scope(scopes[i]);
        }

        AccessToken at = this.connector.retrieveAccessToken(scopeObjects);
        return at.getToken();
    }

    /**
     * Gets the user with the specified username
     *
     * @param accessTokenAsString access token
     * @param userName            username
     * @return User object of null, if user does not exist
     * @throws IOException FIXME
     */
    public User getUser(String accessTokenAsString, String userName) throws IOException {
        Query query = new QueryBuilder().filter("userName eq \"" + userName + "\"").build();
        AccessToken accessToken = new AccessToken.Builder(accessTokenAsString).build();
        SCIMSearchResult<User> users = this.connector.searchUsers(query, accessToken);

        if (users.getResources().size() == 0) {
            return null;
        }
        return users.getResources().get(0);
    }

    /**
     * Creates a new user
     *
     * @param accessTokenAsString access token
     * @param user                User object to create
     * @return newly created user with meta information
     * @throws IOException FIXME
     */
    public User createUser(String accessTokenAsString, User user) throws IOException {
        AccessToken accessToken = new AccessToken.Builder(accessTokenAsString).build();
        return this.connector.createUser(user, accessToken);
    }

    public BasicUser getUserBasic(String accessToken) {
        return this.connector.getCurrentUserBasic(new AccessToken.Builder(accessToken).build());
    }

    /**
     * Get all users.
     *
     * @param accessTokenAsString Access token
     * @return List with users
     */
    public List<User> getAllUsers(String accessTokenAsString) {
        AccessToken accessToken = new AccessToken.Builder(accessTokenAsString).build();
        return this.connector.getAllUsers(accessToken);
    }

    /**
     * Get user by email
     *
     * @param email Email of the user
     * @return {@link User}
     * @throws IOException FIXME
     */
    public User getUserByEmail(String email) throws IOException {
        final String accessTokenAsString = getAccessTokenClientCred("GET", "POST");
        final AccessToken accessToken = new AccessToken.Builder(accessTokenAsString).build();
        final Query query = new QueryBuilder().filter("emails.value eq \"" + email + "\"").build();
        final List<User> resources = connector.searchUsers(query, accessToken).getResources();
        if (resources.size() == 1) {
            return resources.get(0);
        }
        return null;
    }

    /**
     * Delete osiam user
     *
     * @param accessToken FIXME
     * @param id          FIXME
     */
    public void deleteUser(String id, AccessToken accessToken) {
        this.connector.deleteUser(id, accessToken);
    }

    public void updateUser(String id, UpdateUser updateUser, AccessToken accessToken) {
        this.connector.updateUser(id, updateUser, accessToken);
    }

    public OsiamConnector getConnector() {
        return connector;
    }

    public void setConnector(OsiamConnector connector) {
        this.connector = connector;
    }
}
