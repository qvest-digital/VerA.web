package org.evolvis.veraweb.onlinereg;

import org.evolvis.veraweb.onlinereg.rest.EventResourceTest;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by mley on 02.09.14.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({EventResourceTest.class})
public class WarTestSuite {


    @ClassRule
    public static final H2HibernateRule H2 = new H2HibernateRule();

}
