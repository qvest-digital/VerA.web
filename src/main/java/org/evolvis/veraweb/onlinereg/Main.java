/**
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
package org.evolvis.veraweb.onlinereg;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.jersey.sessions.HttpSessionProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;
import lombok.Getter;
import lombok.extern.java.Log;
import org.eclipse.jetty.server.session.SessionHandler;
import org.evolvis.veraweb.onlinereg.auth.AuthenticationFilter;
import org.evolvis.veraweb.onlinereg.event.DelegationResource;
import org.evolvis.veraweb.onlinereg.event.EventResource;
import org.evolvis.veraweb.onlinereg.event.FreeVisitorsResource;
import org.evolvis.veraweb.onlinereg.event.MediaResource;
import org.evolvis.veraweb.onlinereg.event.UpdateResource;
import org.evolvis.veraweb.onlinereg.event.UserResource;
import org.evolvis.veraweb.onlinereg.fileupload.FileUploadResource;
import org.evolvis.veraweb.onlinereg.imprint.ImprintResource;
import org.evolvis.veraweb.onlinereg.user.KontaktdatenResource;
import org.evolvis.veraweb.onlinereg.user.LoginResource;
import org.evolvis.veraweb.onlinereg.user.ResetPasswordResource;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

@Getter
@Log
public class Main extends Application<Config> {

	
	/* RESOURCES */
		private EventResource eventResource;
		private UserResource userResource;
		private LoginResource loginResource;
		private DelegationResource delegationResource;
        private KontaktdatenResource kontaktdatenResource;
		private MediaResource mediaResource;
		private Health health;
		private FreeVisitorsResource freeVisitorsResource;
		private UpdateResource updateResource;
        private ResetPasswordResource resetPasswordResource;
        private FileUploadResource fileUploadResource;
        private ImprintResource imprintResource;
	/* ********* */
		
    /**
     * Startup with the parameter java DropwizardDemo server config.json
     *
     * @param args the commandline args
     */
    public static void main(final String[] args) {
        try {
            new Main().run(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void initialize(final Bootstrap<Config> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/webroot/", "/", "index.html", "static"));
    }

    @Override
    public void run(final Config configuration, final Environment environment) {

        JerseyClientConfiguration jcc = configuration.getJerseyClientConfiguration();
        // timeouts must be increased, because OSIAM is kind of slow.
        jcc.setConnectionTimeout(Duration.milliseconds(1000));
        jcc.setTimeout(Duration.milliseconds(5000));
        jcc.setGzipEnabled(false);

        final Client client = new JerseyClientBuilder(environment)
                .using(jcc)
                .build("jerseyClient");

        try {
            client.addFilter(new HTTPBasicAuthFilter(configuration.getRestauth().getUsername(),
                                                     configuration.getRestauth().getPassword()));
        } catch (NullPointerException e) {
            e.printStackTrace();
            log.warning("REST-Auth Konfiguration in config.json prüfen!");
        }

        environment.jersey().setUrlPattern("/api/*");

        environment.healthChecks().register("veraweb availability",
                health = new Health(client, configuration.getVerawebEndpoint()));

//        environment.jersey().register(new OsiamAuthProvider("OSIAM protected"));

        initAPIResources(configuration, environment, client);

    }

    /**
     * Initializing every resources needed from Online-Anmeldung
     * MUST: When a new Resource is created, it is need to be initialized here
     * 
     * @param configuration
     * @param environment
     * @param client
     */
	private void initAPIResources(final Config configuration, final Environment environment, final Client client) {
	    environment.servlets().addFilter("AuthorizationRequestFilter", new AuthenticationFilter(configuration,client))
        .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
	    environment.jersey().register(HttpSessionProvider.class);
	    environment.servlets().setSessionHandler(new SessionHandler());

		environment.jersey().register(setEventResource(new EventResource(configuration, client)));
        environment.jersey().register(userResource = new UserResource(configuration, client));
        environment.jersey().register(loginResource = new LoginResource(configuration, client));
        environment.jersey().register(delegationResource = new DelegationResource(configuration, client));
        environment.jersey().register(kontaktdatenResource = new KontaktdatenResource(configuration, client));
        environment.jersey().register(mediaResource = new MediaResource(configuration, client));
        environment.jersey().register(freeVisitorsResource = new FreeVisitorsResource(configuration, client));
        environment.jersey().register(updateResource = new UpdateResource(configuration, client));
        environment.jersey().register(resetPasswordResource = new ResetPasswordResource(configuration, client));
        environment.jersey().register(fileUploadResource = new FileUploadResource(configuration, client));
        environment.jersey().register(imprintResource = new ImprintResource(configuration, client));
	}

	public EventResource getEventResource() {
		return eventResource;
	}

	public EventResource setEventResource(EventResource eventResource) {
		this.eventResource = eventResource;
		return eventResource;
	}
}
