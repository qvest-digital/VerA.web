package org.evolvis.veraweb.onlinereg.osiam;

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

}
