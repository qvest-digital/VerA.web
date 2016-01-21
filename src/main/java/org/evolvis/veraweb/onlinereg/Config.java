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
package org.evolvis.veraweb.onlinereg;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import lombok.Getter;

import org.evolvis.veraweb.onlinereg.auth.AuthConfig;
import org.evolvis.veraweb.onlinereg.osiam.OsiamConfig;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Configuration
 */
@Getter
public class Config extends Configuration {

    @JsonProperty
    private String verawebEndpoint;

	@JsonProperty
	private String onlineRegistrationEndpoint;

    @JsonProperty
    private OsiamConfig osiam;
    
    @JsonProperty
    private AuthConfig restauth;
    
    @Valid
    @NotNull
    @JsonProperty
    private JerseyClientConfiguration jerseyClientConfiguration = new JerseyClientConfiguration();
    
    public String getVerawebEndpoint() {
    	return this.verawebEndpoint;
    }
    
    public OsiamConfig getOsiam() {
		return osiam;
	}

	public JerseyClientConfiguration getJerseyClientConfiguration() {
		return jerseyClientConfiguration;
	}

	public void setJerseyClientConfiguration( // TODO remove?
			JerseyClientConfiguration jerseyClientConfiguration) {
		this.jerseyClientConfiguration = jerseyClientConfiguration;
	}

	public void setVerawebEndpoint(String verawebEndpoint) {// TODO remove?
		this.verawebEndpoint = verawebEndpoint;
	}

	public AuthConfig getRestauth() {
		return restauth;
	}

	public String getOnlineRegistrationEndpoint() {
		return onlineRegistrationEndpoint;
	}

	public void setOnlineRegistrationEndpoint(String onlineRegistrationEndpoint) {
		this.onlineRegistrationEndpoint = onlineRegistrationEndpoint;
	}
}
