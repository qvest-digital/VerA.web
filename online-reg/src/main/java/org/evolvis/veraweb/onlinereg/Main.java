package org.evolvis.veraweb.onlinereg;


import com.sun.jersey.api.client.Client;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;
import org.evolvis.veraweb.onlinereg.event.EventResource;
import org.evolvis.veraweb.onlinereg.event.UserResource;
import org.evolvis.veraweb.onlinereg.user.LoginResource;


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



    @Override
    public void initialize(final Bootstrap<Config> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/webroot/", "/", "index.html", "static"));

    }

    @Override
    public void run(final Config configuration, final Environment environment) {


        JerseyClientConfiguration jcc = configuration.getJerseyClientConfiguration();
        jcc.setConnectionTimeout(Duration.milliseconds(1000));
        jcc.setTimeout(Duration.milliseconds(5000));
        jcc.setGzipEnabled(false);



        final Client client = new JerseyClientBuilder(environment)
                .using(jcc)
                .build("jerseyClient");

        environment.jersey().setUrlPattern("/api/*");

        environment.healthChecks().register("veraweb availability", new Health(client, configuration.getVerawebEndpoint()));
        environment.jersey().register(new EventResource(client, configuration));
        environment.jersey().register(new UserResource(configuration, client));
        environment.jersey().register(new LoginResource(configuration, client));



    }



}
