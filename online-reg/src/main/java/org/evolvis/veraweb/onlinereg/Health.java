package org.evolvis.veraweb.onlinereg;

import com.codahale.metrics.health.HealthCheck;
import com.sun.jersey.api.client.Client;


public class Health extends HealthCheck {


    private final Client client;
    private final String resource;
    public Health(Client client, String verawebEndpoint) {
        this.resource = verawebEndpoint + "/rest/available";
        this.client = client;
    }

    @Override
    protected Result check() {
        String result = null;
        try {
            result = client.resource(resource).get(String.class);
        } catch(Exception e) {
            return Result.unhealthy("Error checking vera.web availability: "+e.getMessage());
        }

        return "OK".equals(result) ? Result.healthy() : Result.unhealthy("vera.web has problems: "+result);
    }

}
