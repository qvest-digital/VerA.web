package org.evolvis.veraweb.onlinereg;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.osiam.client.connector.OsiamConnector;
import org.osiam.client.oauth.GrantType;
import org.osiam.client.oauth.Scope;


/**
 * Created by mley on 26.08.14.
 */
public class OsiamConfig {

    @JsonProperty
    private String endpoint;

    @JsonProperty
    private String clientId;

    @JsonProperty
    private String clientSecret;

    public OsiamConnector getConnector() {
        return new OsiamConnector.Builder()
                .setEndpoint(endpoint)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
    }

    public OsiamConnector getConnector(String user, String password) {
        return new OsiamConnector.Builder()
                .setEndpoint(endpoint)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setGrantType(GrantType.RESOURCE_OWNER_PASSWORD_CREDENTIALS)
                .setUserName(user)
                .setPassword(password)
                .setScope(Scope.GET)
                .build();
    }

}
