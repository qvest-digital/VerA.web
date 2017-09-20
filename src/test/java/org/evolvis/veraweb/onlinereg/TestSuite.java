package org.evolvis.veraweb.onlinereg;

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
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.evolvis.veraweb.onlinereg.event.DelegationResourceTest;
import org.evolvis.veraweb.onlinereg.event.EventResourceTest;
import org.evolvis.veraweb.onlinereg.event.UserResourceTest;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by mley on 28.08.14.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventResourceTest.class,
        UserResourceTest.class,
        HealthTest.class,
        DelegationResourceTest.class
})
public class TestSuite{

    @ClassRule
    public static final DropwizardAppRule<Config> DROPWIZARD =
            new DropwizardAppRule<Config>(Main.class, TestSuite.class.getResource("/test_config.yaml").getPath());

    @ClassRule
    public static final WiremockRule WIREMOCK = new WiremockRule();





}
