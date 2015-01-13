package org.evolvis.veraweb.onlinereg;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;
import lombok.Getter;

import org.evolvis.veraweb.onlinereg.event.DelegationResource;
import org.evolvis.veraweb.onlinereg.event.EventResource;
import org.evolvis.veraweb.onlinereg.event.MediaResource;
import org.evolvis.veraweb.onlinereg.event.UserResource;
import org.evolvis.veraweb.onlinereg.user.LoginResource;

@Getter
public class Main extends Application<Config> {

    /**
     * startup with the parameter java DropwizardDemo server config.yaml.
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

    private EventResource eventResource;
    private UserResource userResource;
    private LoginResource loginResource;
    private DelegationResource delegationResource;
    private MediaResource mediaResource;
    private Health health;

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
        
        client.addFilter(new HTTPBasicAuthFilter("hallo","hallo"));

        environment.jersey().setUrlPattern("/api/*");

        environment.healthChecks().register("veraweb availability", health = new Health(client, configuration.getVerawebEndpoint()));

//        environment.jersey().register(new OsiamAuthProvider("OSIAM protected"));

        environment.jersey().register(eventResource = new EventResource(configuration, client));
        environment.jersey().register(userResource = new UserResource(configuration, client));
        environment.jersey().register(loginResource = new LoginResource(configuration, client)); 
        environment.jersey().register(delegationResource = new DelegationResource(configuration, client)); 
        environment.jersey().register(mediaResource = new MediaResource(configuration, client)); 

    }



}
