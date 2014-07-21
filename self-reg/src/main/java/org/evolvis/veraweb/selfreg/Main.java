package org.evolvis.veraweb.selfreg;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;


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

    /**
     * hibernate bundle creation. All entity classes must be referenced here.
     */
    /*private final HibernateBundle<Config> hibernate = new HibernateBundle<Config>(
            // List all persisted entities' classes here
            ) {

        @Override
        public DatabaseConfiguration getDatabaseConfiguration(Config configuration) {
            return configuration.getDatabase();
        }
    };
*/


    @Override
    public void initialize(final Bootstrap<Config> bootstrap) {
        bootstrap.setName("verA.web Self-Registration server");

        // serve all files in src/main/resources/webroot on /
        bootstrap.addBundle(new AssetsBundle("/webroot/", "/"));

        // ViewBundle for BookViews templating
        bootstrap.addBundle(new ViewBundle());

        //bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(final Config configuration, final Environment environment) {
        environment.addHealthCheck(new Health());
        //BookResource br = new BookResource(hibernate.getSessionFactory());
        //environment.addResource(br);

    }

}
