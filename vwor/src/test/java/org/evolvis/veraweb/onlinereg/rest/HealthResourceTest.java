package org.evolvis.veraweb.onlinereg.rest;
import org.evolvis.veraweb.onlinereg.AbstractResourceTest;
import org.evolvis.veraweb.onlinereg.entities.Config;
import org.hibernate.Session;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by mley on 02.09.14.
 */
public class HealthResourceTest extends AbstractResourceTest<HealthResource> {
    public HealthResourceTest() {
        super(HealthResource.class);
    }

    @Test
    public void testOk() {
        Session s = sessionFactory.openSession();
        s.beginTransaction();

        Config c = new Config();
        c.setPk(1);
        c.setCname("SCHEMA_VERSION");
        c.setCvalue("1.5.0");

        s.save(c);
        s.flush();
        s.getTransaction().commit();
        s.close();
        assertEquals("OK", resource.health());

        s = sessionFactory.openSession();
        s.beginTransaction();
        s.delete(c);
        s.flush();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testFail() {
        assertEquals("NO SCHEMA_VERSION", resource.health());
    }

    @Test(expected = Exception.class)
    public void testDBError() {
        stopH2();

        try {
            resource.health();
        } finally {
            startH2();
        }
    }
}
