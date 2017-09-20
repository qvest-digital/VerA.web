package org.evolvis.veraweb.onlinereg.event;

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
import org.evolvis.veraweb.onlinereg.Main;
import org.evolvis.veraweb.onlinereg.TestSuite;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class DelegationResourceTest {

    private DelegationResource delegationResource;

    private final String uuid = "d2d29cb5-c8b7-45dd-9411-6799a6e9f240";

    public DelegationResourceTest() {
        Main main = TestSuite.DROPWIZARD.getApplication();
        delegationResource = main.getDelegationResource();
    }

    @Test@Ignore
    public void testGetDelegates() throws Exception {
        List<Person> delegates = delegationResource.getDelegates(uuid);
        assertEquals(3, delegates.size());
        assertEquals("Max", delegates.get(0).getFirstname_a_e1());
        assertEquals("MyCompany GmbH", delegates.get(2).getCompany_a_e1());
    }

    @Test@Ignore
    public void testGetExtraFields() throws IOException {
        List<OptionalFieldValue> extraDataFields = delegationResource.getExtraDataFields(uuid);
        assertEquals(9, extraDataFields.size());
    }
}