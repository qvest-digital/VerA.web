package org.evolvis.veraweb.onlinereg;

import io.dropwizard.testing.junit.DropwizardAppRule;
import org.evolvis.veraweb.onlinereg.event.EventResourceTest;
import org.evolvis.veraweb.onlinereg.event.UserResourceTest;
import org.evolvis.veraweb.onlinereg.user.LoginResourceTest;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by mley on 28.08.14.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({EventResourceTest.class, UserResourceTest.class, LoginResourceTest.class})
public class TestSuite{

    @ClassRule
    public static final DropwizardAppRule<Config> DROPWIZARD =
            new DropwizardAppRule<Config>(Main.class, TestSuite.class.getResource("/test_config.yaml").getPath());

    @ClassRule
    public static final WiremockRule WIREMOCK = new WiremockRule();





}
