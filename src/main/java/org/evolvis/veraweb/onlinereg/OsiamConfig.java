package org.evolvis.veraweb.onlinereg;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.osiam.client.OsiamConnector;

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
}
