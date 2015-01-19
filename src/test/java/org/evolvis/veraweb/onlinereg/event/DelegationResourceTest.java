package org.evolvis.veraweb.onlinereg.event;

import junit.framework.Assert;
import org.evolvis.veraweb.onlinereg.TestSuite;
import org.evolvis.veraweb.onlinereg.Main;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

public class DelegationResourceTest {

    private DelegationResource delegationResource;

    private final String uuid = "242eb535-ef8d-40a1-b27b-086f7eb58bd5";

    public DelegationResourceTest() {
        Main main = TestSuite.DROPWIZARD.getApplication();
        delegationResource = main.getDelegationResource();
    }

    @Test
    public void testGetDelegates() throws Exception {
        List<Person> delegates = delegationResource.getDelegates(uuid);
        assertEquals(3, delegates.size());
        assertEquals("Max", delegates.get(0).getFirstname_a_e1());
        assertEquals("MyCompany GmbH", delegates.get(2).getCompany_a_e1());
    }

    @Test
    public void testGetExtraFields() throws IOException {
        Integer personId = 42;
        List<OptionalFieldValue> extraDataFields = delegationResource.getExtraDataFields(uuid, personId);
        assertEquals(15, extraDataFields.size());
    }
}