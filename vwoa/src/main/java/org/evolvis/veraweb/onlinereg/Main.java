package org.evolvis.veraweb.onlinereg;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017, 2018 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
     * Startup with the parameter java DropwizardDemo server config.jsn
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
            log.warning("REST-Auth Konfiguration in config.jsn prüfen!");
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
        environment.servlets().addFilter("AuthorizationRequestFilter", new AuthenticationFilter(configuration, client))
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
