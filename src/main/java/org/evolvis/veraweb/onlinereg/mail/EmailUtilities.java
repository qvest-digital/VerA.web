package org.evolvis.veraweb.onlinereg.mail;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import org.evolvis.veraweb.onlinereg.Config;
import com.sun.jersey.api.client.Client;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class EmailUtilities {

    private Config config;
    private Client client;

    public EmailUtilities(Config config, Client client) {
        this.config = config;
        this.client = client;
    }

    public void sendEmailVerification(String email, String activationToken, String currentLanguageKey) {
        final Form postBody = new Form();
        postBody.add("email", email);
        postBody.add("endpoint", config.getOnlineRegistrationEndpoint());
        postBody.add("activation_token", activationToken);
        postBody.add("language", currentLanguageKey);
        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/email/confirmation/send");
        resource.post(postBody);
    }
}
