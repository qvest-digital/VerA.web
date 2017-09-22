package org.evolvis.veraweb.onlinereg.rest;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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

        Config c = new Config();
        c.setPk(1);
        c.setCname("SCHEMA_VERSION");
        c.setCvalue("1.5.0");

        s.save(c);
        s.flush();
        s.close();
        assertEquals("OK", resource.health());

        s = sessionFactory.openSession();
        s.delete(c);
        s.flush();
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
