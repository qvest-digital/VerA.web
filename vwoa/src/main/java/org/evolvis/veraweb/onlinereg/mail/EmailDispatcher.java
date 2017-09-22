package org.evolvis.veraweb.onlinereg.mail;

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
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import org.evolvis.veraweb.onlinereg.Config;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class EmailDispatcher {

    private Config config;
    private Client client;

    public EmailDispatcher(Config config, Client client) {
        this.config = config;
        this.client = client;
    }

    public void sendEmailVerification(String email, String activationToken, String currentLanguageKey, Boolean isPressUser) {
        final Form postBody = new Form();
        postBody.add("email", email);
        postBody.add("endpoint", config.getOnlineRegistrationEndpoint());
        postBody.add("activation_token", activationToken);
        postBody.add("language", currentLanguageKey);
        postBody.add("usertype", isPressUser);
        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/email/confirmation/send");
        resource.post(postBody);
    }
}
