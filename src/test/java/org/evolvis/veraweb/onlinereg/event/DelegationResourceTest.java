package org.evolvis.veraweb.onlinereg.event;

import org.evolvis.veraweb.onlinereg.TestSuite;
import org.evolvis.veraweb.onlinereg.Main;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class DelegationResourceTest {

    private DelegationResource delegationResource;

    public DelegationResourceTest() {
        Main main = TestSuite.DROPWIZARD.getApplication();
        delegationResource = main.getDelegationResource();
    }

    @Test
    public void testGetDelegates() throws Exception {
        String uuid = "uuid";
        List<Person> delegates = delegationResource.getDelegates(uuid);
        assertEquals(3, delegates.size());
    }
}