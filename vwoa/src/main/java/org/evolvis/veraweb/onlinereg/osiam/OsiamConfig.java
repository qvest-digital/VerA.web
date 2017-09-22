package org.evolvis.veraweb.onlinereg.osiam;

/*-
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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.jersey.api.client.Client;
import lombok.Getter;


/**
 * Created by mley on 26.08.14.
 */
@Getter
public class OsiamConfig {

    @JsonProperty
    private String endpoint;

    @JsonProperty
    private String clientId;

    @JsonProperty
    private String clientSecret;

    /**
     * Creates a new OSIAM client
     * @param client jersey client
     * @return OsiamClient object
     */
    public OsiamClient getClient(Client client) {
        return new OsiamClient(this, client);
    }
    
    public String getEndpoint() {
    	return this.endpoint;
    }

	public String getClientId() {
		return this.clientId;
	}

	public String getClientSecret() {
		return this.clientSecret;
	}
    
    

}
