package org.evolvis.veraweb.onlinereg.mail;

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
