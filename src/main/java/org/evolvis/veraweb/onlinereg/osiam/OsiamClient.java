/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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
package org.evolvis.veraweb.onlinereg.osiam;

import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryBuilder;

import com.sun.jersey.api.client.Client;

import lombok.extern.java.Log;

import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;

import java.io.IOException;


/**
 * Created by mley on 27.08.14.
 * <p/>
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
     * @param scopes    scopes
     * @return accesstoken as String
     * @throws IOException if an error occurs, e.g. service is not available, user does not exists, password wrong
     */
    public String getAccessTokenUserPass(String username, String password, String... scopes) throws IOException {
	Scope[] scopeObjects = new Scope[scopes.length];

	for (int i = 0; i < scopes.length; ++i) {
		scopeObjects[i] = new Scope(scopes[i]);
	}

    	return this.connector.retrieveAccessToken(username, password, scopeObjects).getToken();
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

    	return this.connector.retrieveAccessToken(scopeObjects).getToken();
    }

    /**
     * Gets the user with the specified username
     *
     * @param accessTokenAsString access token
     * @param userName    username
     * @return User object of null, if user does not exist
     * @throws IOException
     */
    public User getUser(String accessTokenAsString, String userName) throws IOException {
log.warning("OSIAM getUser token '" + accessTokenAsString + "'");//…
    	Query query = new QueryBuilder().filter("userName eq \"" + userName + "\"").build();
    	AccessToken accessToken = new AccessToken.Builder(accessTokenAsString).build();
    	SCIMSearchResult<User> users = this.connector.searchUsers(query, accessToken);
    	
    	if(users.getResources().size() == 0) {
    		return null;
    	}
    	return users.getResources().get(0);
    }

    /**
     * Creates a new user
     *
     * @param accessTokenAsString access token
     * @param user        User object to create
     * @return newly created user with meta information
     * @throws IOException
     */
    public User createUser(String accessTokenAsString, User user) throws IOException {
log.warning("OSIAM createUser token '" + accessTokenAsString + "'");//…
    	AccessToken accessToken = new AccessToken.Builder(accessTokenAsString).build();
    	return this.connector.createUser(user, accessToken);
	}

	/**
	 * Delete osiam user
	 *

	 */
	public void deleteUser(String id, AccessToken accessToken) {
		this.connector.deleteUser(id, accessToken);
	}

	public OsiamConnector getConnector() {
		return connector;
	}

	public void setConnector(OsiamConnector connector) {
		this.connector = connector;
	}
}
