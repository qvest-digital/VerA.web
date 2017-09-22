package org.evolvis.veraweb.onlinereg;

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
            return Result.unhealthy("Error checking VerA.web availability: "+e.getMessage());
        }

        return "OK".equals(result) ? Result.healthy() : Result.unhealthy("VerA.web has problems: "+result);
    }

}
