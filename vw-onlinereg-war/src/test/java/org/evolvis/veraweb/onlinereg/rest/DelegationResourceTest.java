package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.AbstractResourceTest;
import org.evolvis.veraweb.onlinereg.entities.Delegation;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.OptionalField;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue;
import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        session.persist(optionalField);

        OptionalField optionalField1 = new OptionalField();
        optionalField1.setFk_event(1);
        optionalField1.setLabel("Label 2");
        session.persist(optionalField1);

        OptionalField optionalField3 = new OptionalField();
        optionalField3.setFk_event(1);
        optionalField3.setLabel("Label 3");
        session.persist(optionalField3);

        session.flush();
        session.close();
    }

    @Test
    public void testGetFields() {
        List<OptionalFieldValue> fields = resource.getFieldsFromEvent(1, 1);
        assertEquals(3, fields.size());
        assertEquals("Label 1", fields.get(0).getLabel());
        assertEquals(1, fields.get(1).getFk_event());
    }

    @Test
    public void testGetLabelId() {
        Integer labelId = resource.getLabelIdfromEventAndLabel(1, "Label 1");
        assertEquals(new Integer(1), labelId);
    }

    @Test
    public void testSaveField() {
        Delegation delegation = resource.saveOptionalField(1, 1, "Value Field 2");
        assertNotNull(delegation);
    }
}
