package org.evolvis.veraweb.onlinereg;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.client.JerseyClientBuilder;
import com.yammer.dropwizard.client.JerseyClientConfiguration;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import org.evolvis.veraweb.onlinereg.event.EventResource;
import org.evolvis.veraweb.onlinereg.event.UserResource;


public class Main extends Service<Config> {

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
        bootstrap.setName("verA.web Self-Registration server");

        // serve all files in src/main/resources/webroot on /
        bootstrap.addBundle(new AssetsBundle("/webroot/", "/"));

    }

    @Override
    public void run(final Config configuration, final Environment environment) {

        JerseyClientConfiguration jcc = configuration.getJerseyClientConfiguration();


        final Client client = new JerseyClientBuilder()
                .using(environment)
                .using(jcc)
                .withProperty(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 500)
                .withProperty(ClientConfig.PROPERTY_READ_TIMEOUT, 2000)
                .build();
        environment.addHealthCheck(new Health(client, configuration.getVerawebEndpoint()));
        environment.addResource(new EventResource(client, configuration));
        environment.addResource(new UserResource(client, configuration));

    }

}
