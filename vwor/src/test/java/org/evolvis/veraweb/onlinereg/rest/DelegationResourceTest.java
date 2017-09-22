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
import org.evolvis.veraweb.onlinereg.entities.Delegation;
import org.evolvis.veraweb.onlinereg.entities.OptionalField;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldTypeContent;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.ws.rs.container.ResourceContext;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by aalexa on 20.01.15.
 *
 * @author aalexa
 * @author jnunez
 */
@RunWith(MockitoJUnitRunner.class)
public class DelegationResourceTest {

    @Mock
    private static SessionFactory mockitoSessionFactory;
    @Mock
    private static Session mockitoSession;
    @Mock
    ResourceContext resourceContext;

    DelegationResource delegationResource;

    public DelegationResourceTest() {
        delegationResource = new DelegationResource();
        delegationResource.context = mock(ServletContext.class);
    }

    @AfterClass
    public static void tearDown() {
        mockitoSessionFactory.close();
        mockitoSession.disconnect();
        mockitoSession.close();
    }

    private List<OptionalField> getDummyOptionalFields() {
        List<OptionalField> optionalFields = new ArrayList<OptionalField>();

        // -------------------------------------------------
            OptionalField inputField = new OptionalField();
            inputField.setFk_type(1);
            inputField.setFk_event(1);
            inputField.setLabel("My input field");
            inputField.setPk(11);

            OptionalField singleComboField = new OptionalField();
            singleComboField.setFk_type(2);
            singleComboField.setFk_event(1);
            singleComboField.setLabel("My combobox field");
            singleComboField.setPk(12);

            OptionalField multipleComboField = new OptionalField();
            multipleComboField.setFk_type(3);
            multipleComboField.setFk_event(1);
            multipleComboField.setLabel("My multiple combobox field");
            multipleComboField.setPk(13);
        // -------------------------------------------------

        optionalFields.add(inputField);
        optionalFields.add(singleComboField);
        optionalFields.add(multipleComboField);

        return optionalFields;
    }

    private List<OptionalFieldTypeContent> getDummyOptionalFieldTypeContents() {
        List<OptionalFieldTypeContent> optionalFieldTypeContents = new ArrayList<OptionalFieldTypeContent>();
        // -------------------------------------------------
            OptionalFieldTypeContent optionalFieldTypeContent1 = new OptionalFieldTypeContent();
            optionalFieldTypeContent1.setContent("Option single combo 1");
            optionalFieldTypeContent1.setFk_optional_field(12);
            optionalFieldTypeContent1.setPk(1);

            OptionalFieldTypeContent optionalFieldTypeContent2 = new OptionalFieldTypeContent();
            optionalFieldTypeContent2.setContent("Option single combo 2");
            optionalFieldTypeContent2.setFk_optional_field(12);
            optionalFieldTypeContent2.setPk(2);

            OptionalFieldTypeContent optionalFieldTypeContent3 = new OptionalFieldTypeContent();
            optionalFieldTypeContent3.setContent("Option multiple combo 1");
            optionalFieldTypeContent3.setFk_optional_field(13);
            optionalFieldTypeContent3.setPk(3);


            OptionalFieldTypeContent optionalFieldTypeContent4 = new OptionalFieldTypeContent();
            optionalFieldTypeContent3.setContent("Option multiple combo 2");
            optionalFieldTypeContent3.setFk_optional_field(13);
            optionalFieldTypeContent3.setPk(4);
        // -------------------------------------------------

        optionalFieldTypeContents.add(optionalFieldTypeContent1);
        optionalFieldTypeContents.add(optionalFieldTypeContent2);
        optionalFieldTypeContents.add(optionalFieldTypeContent3);
        optionalFieldTypeContents.add(optionalFieldTypeContent4);

        return optionalFieldTypeContents;
    }

    @Test
    public void testGetFields() {
        prepareSession();
        Query findByEventIdQuery = mock(Query.class);
        ResourceContext resourceContext = mock(ResourceContext.class);
        OptionalFieldResource optionalFieldResource = mock(OptionalFieldResource.class);
        delegationResource.setResourceContext(resourceContext);
        when(mockitoSession.getNamedQuery(OptionalField.OPTIONAL_FIELD_FIND_BY_EVENT_ID)).thenReturn(findByEventIdQuery);
        when(findByEventIdQuery.list()).thenReturn(getDummyOptionalFields());

        Query findByGuestIdQuery = mock(Query.class);
        when(mockitoSession.getNamedQuery("Delegation.findByGuestId")).thenReturn(findByGuestIdQuery);
        when(findByGuestIdQuery.list()).thenReturn(new ArrayList<Delegation>());

        Query findTypeContentsByOptionalFieldQuery = mock(Query.class);
        when(mockitoSession.getNamedQuery("OptionalFieldTypeContent.findTypeContentsByOptionalField"))
                .thenReturn(findTypeContentsByOptionalFieldQuery);
        when(findTypeContentsByOptionalFieldQuery.list()).thenReturn(getDummyOptionalFieldTypeContents());
        when(resourceContext.getResource(any(Class.class))).thenReturn(optionalFieldResource);
        when(optionalFieldResource.getOptionalFields(any(Integer.class))).thenReturn(getDummyOptionalFields());


        List<OptionalFieldValue> fields = delegationResource.getFieldsFromEvent(1, 1);
        assertEquals(3, fields.size());
        assertEquals("My input field", fields.get(0).getLabel());
        assertEquals(1, fields.get(1).getFk_event().intValue());
    }

    @Test
    public void testGetLabelId() {
        prepareSession();
        Query findByEventAndLabelQuery = mock(Query.class);
        when(mockitoSession.getNamedQuery(OptionalField.OPTIONAL_FIELD_FIND_BY_EVENT_ID_AND_LABEL)).thenReturn(findByEventAndLabelQuery);
        when(findByEventAndLabelQuery.uniqueResult()).thenReturn(1);

        Integer labelId = delegationResource.getLabelIdfromEventAndLabel(1, "Label 1");

        assertEquals(new Integer(1), labelId);
    }

    @Test
    public void testSaveField() {
        prepareSession();
        Delegation delegation = delegationResource.saveOptionalField(1, 1, "Value Field 2");
        assertNotNull(delegation);
    }

    private void prepareSession() {
        when(delegationResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
    }

}
