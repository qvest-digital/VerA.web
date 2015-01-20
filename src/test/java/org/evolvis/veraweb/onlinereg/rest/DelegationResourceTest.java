package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.AbstractResourceTest;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.OptionalField;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue;
import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by aalexa on 20.01.15.
 */
public class DelegationResourceTest extends AbstractResourceTest<DelegationResource> {
    public DelegationResourceTest() {
        super(DelegationResource.class);
    }

    @BeforeClass
    public static void init() {
        setDummyOptionalFields();
    }

    private static void setDummyOptionalFields() {
        Session session = sessionFactory.openSession();

        OptionalField optionalField = new OptionalField();
        optionalField.setFk_event(1);
        optionalField.setLabel("Label 1");
        session.saveOrUpdate(optionalField);

        OptionalField optionalField1 = new OptionalField();
        optionalField1.setFk_event(1);
        optionalField1.setLabel("Label 2");
        session.saveOrUpdate(optionalField1);

        OptionalField optionalField3 = new OptionalField();
        optionalField3.setFk_event(1);
        optionalField3.setLabel("Label 3");
        session.saveOrUpdate(optionalField3);

        session.flush();
        session.close();
    }

    @Test
    public void testGetFields() {
        List<OptionalFieldValue> fields = resource.getFieldsFromEvent(1, 1);
        assertEquals(3, fields.size());
    }
}
