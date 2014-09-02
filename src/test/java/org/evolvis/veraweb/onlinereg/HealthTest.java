package org.evolvis.veraweb.onlinereg;

import com.codahale.metrics.health.HealthCheck;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by mley on 02.09.14.
 */
public class HealthTest {

    @Test
    public void testHealth() {
        Main main = TestSuite.DROPWIZARD.getApplication();
        Health h = main.getHealth();

        assertEquals(HealthCheck.Result.healthy(), h.check());
    }
}
