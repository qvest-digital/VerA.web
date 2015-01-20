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

}
