package org.evolvis.veraweb.onlinereg.auth;

import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * HTTP Basic Auth Logic class. Relationship between REST-API and Online-Reg app
 * 
 * @author jnunez
 */
@Getter
public class AuthConfig {

	@JsonProperty
	private String username;
	@JsonProperty
	private String password;
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
}
