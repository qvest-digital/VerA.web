/**
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
package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Delegation;
import org.evolvis.veraweb.onlinereg.entities.OptionalField;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldTypeContent;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldTypeContentFacade;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by aalexa on 26.01.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class DelegationResourceSessionsTest {

    @Mock
    private static SessionFactory mockitoSessionFactory;
    @Mock
    private static Session mockitoSession;

    DelegationResource delegationResource;

    public DelegationResourceSessionsTest() {
        delegationResource = new DelegationResource();
        delegationResource.context = mock(ServletContext.class);
    }

    @AfterClass
    public static void tearDown() {
        mockitoSessionFactory.close();

        mockitoSession.disconnect();
        mockitoSession.close();
    }

    @Test@Ignore
    public void testGetFieldsFromEvent() {
        // GIVEN
        prepareSession();
        List<OptionalFieldValue> fields = getDummyOptionalFieldValues();
        List delegationFields = getDummyOptionalFields();
        List typeContents = getDummyTypeContents();

        Query query1 = mock(Query.class);
        Query query2 = mock(Query.class);
        Query query3 = mock(Query.class);
        when(mockitoSession.getNamedQuery(OptionalField.OPTIONAL_FIELD_FIND_BY_EVENT_ID)).thenReturn(query1);
        when(mockitoSession.getNamedQuery("Delegation.findByGuestId")).thenReturn(query2);
        when(mockitoSession.getNamedQuery("OptionalFieldTypeContent.findTypeContentsByOptionalField")).thenReturn(query3);

        when(query1.list()).thenReturn(fields);
        when(query2.list()).thenReturn(delegationFields);
        when(query3.list()).thenReturn(typeContents);

        // WHEN
        delegationResource.getFieldsFromEvent(1,1);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
        assertEquals(4, query1.list().size());
    }

    @Test
    public void testGetLabelIdfromEventAndLabel() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery(OptionalField.OPTIONAL_FIELD_FIND_BY_EVENT_ID_AND_LABEL)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(1);

        // WHEN
        delegationResource.getLabelIdfromEventAndLabel(1, "test");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testRemoveFieldsForGuest() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Delegation.deleteOptionalFieldsByGuestId")).thenReturn(query);

        // WHEN
        delegationResource.removeFieldsForGuest(1, 2);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testSaveOptionalField() {
        // GIVEN
        prepareSession();

        // WHEN
        delegationResource.saveOptionalField(1,1,"fieldContent");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    private List<OptionalFieldValue> getDummyOptionalFieldValues() {
        OptionalFieldTypeContent typeContent = new OptionalFieldTypeContent();
        typeContent.setContent("typeContent 1");
        typeContent.setFk_optional_field(1);
        typeContent.setPk(1);

        OptionalFieldTypeContent typeContent2 = new OptionalFieldTypeContent();
        typeContent2.setContent("typeContent 2");
        typeContent2.setFk_optional_field(1);
        typeContent2.setPk(2);

        OptionalFieldTypeContent typeContent3 = new OptionalFieldTypeContent();
        typeContent3.setContent("typeContent 3");
        typeContent3.setFk_optional_field(2);
        typeContent3.setPk(3);

        OptionalFieldTypeContent typeContent4 = new OptionalFieldTypeContent();
        typeContent4.setContent("typeContent 4");
        typeContent4.setFk_optional_field(2);
        typeContent4.setPk(4);

        OptionalFieldTypeContent typeContent5 = new OptionalFieldTypeContent();
        typeContent5.setContent("typeContent 5");
        typeContent5.setFk_optional_field(3);
        typeContent5.setPk(5);

        OptionalFieldTypeContentFacade facadede1 = new OptionalFieldTypeContentFacade(typeContent);
        OptionalFieldTypeContentFacade facadede2 = new OptionalFieldTypeContentFacade(typeContent2);
        OptionalFieldTypeContentFacade facadede3 = new OptionalFieldTypeContentFacade(typeContent3);
        OptionalFieldTypeContentFacade facadede4 = new OptionalFieldTypeContentFacade(typeContent4);
        OptionalFieldTypeContentFacade facadede5 = new OptionalFieldTypeContentFacade(typeContent5);

        List<OptionalFieldTypeContentFacade> facadeElements1 = new ArrayList<>();;
        facadeElements1.add(facadede1);
        facadeElements1.add(facadede2);

        List<OptionalFieldTypeContentFacade> facadeElements2 = new ArrayList<>();;
        facadeElements2.add(facadede3);
        facadeElements2.add(facadede4);

        List<OptionalFieldTypeContentFacade> facadeElements3 = new ArrayList<>();;
        facadeElements3.add(facadede5);

        OptionalFieldValue optionalFieldValue1 = new OptionalFieldValue();
        optionalFieldValue1.setPk(1);
        optionalFieldValue1.setValue("Value 1");
        optionalFieldValue1.setLabel("Label 1");
        optionalFieldValue1.setOptionalFieldTypeContentsFacade(facadeElements1);
        optionalFieldValue1.setFk_type(3);

        OptionalFieldValue optionalFieldValue2 = new OptionalFieldValue();
        optionalFieldValue2.setPk(2);
        optionalFieldValue2.setValue("Value 2");
        optionalFieldValue2.setLabel("Label 2");
        optionalFieldValue2.setOptionalFieldTypeContentsFacade(facadeElements2);
        optionalFieldValue2.setFk_type(2);

        OptionalFieldValue optionalFieldValue3 = new OptionalFieldValue();
        optionalFieldValue3.setPk(3);
        optionalFieldValue3.setValue("Value 3");
        optionalFieldValue3.setLabel("Label 3");
        optionalFieldValue3.setOptionalFieldTypeContentsFacade(facadeElements3);
        optionalFieldValue3.setFk_type(1);


        OptionalFieldValue optionalFieldValue4 = new OptionalFieldValue();
        optionalFieldValue3.setPk(4);
        optionalFieldValue3.setValue("Value 4");
        optionalFieldValue3.setLabel("Label 4");
        optionalFieldValue3.setOptionalFieldTypeContentsFacade(new ArrayList<OptionalFieldTypeContentFacade>());
        optionalFieldValue3.setFk_type(1);

        List<OptionalFieldValue> fields = new ArrayList();
        fields.add(optionalFieldValue1);
        fields.add(optionalFieldValue2);
        fields.add(optionalFieldValue3);
        fields.add(optionalFieldValue4);

        return fields;
    }

    private List<Delegation> getDummyOptionalFields() {
        final Delegation optionalField1 = new Delegation();
        optionalField1.setPk(1);
        optionalField1.setFk_delegation_field(1);
        optionalField1.setFk_guest(1);
        optionalField1.setValue("Value 1");

        final Delegation optionalField2 = new Delegation();
        optionalField2.setPk(2);
        optionalField2.setFk_delegation_field(1);
        optionalField2.setFk_guest(1);
        optionalField2.setValue("Value 2");

        final Delegation optionalField3 = new Delegation();
        optionalField3.setPk(3);
        optionalField3.setFk_delegation_field(1);
        optionalField3.setFk_guest(1);
        optionalField3.setValue("Value 3");

        final List<Delegation> optionalFields = new ArrayList<>();
        optionalFields.add(optionalField1);
        optionalFields.add(optionalField2);
        optionalFields.add(optionalField3);

        return optionalFields;
    }

    private List getDummyTypeContents() {
        OptionalFieldTypeContent typeContent1 = new OptionalFieldTypeContent();
        typeContent1.setPk(1);
        typeContent1.setFk_optional_field(1);
        typeContent1.setContent("Value 1");

        OptionalFieldTypeContent typeContent2 = new OptionalFieldTypeContent();
        typeContent2.setPk(2);
        typeContent2.setFk_optional_field(1);
        typeContent2.setContent("Value 2");

        OptionalFieldTypeContent typeContent3 = new OptionalFieldTypeContent();
        typeContent3.setPk(3);
        typeContent3.setFk_optional_field(1);
        typeContent3.setContent("Value 3");

        OptionalFieldTypeContent typeContent4 = new OptionalFieldTypeContent();
        typeContent4.setPk(4);
        typeContent4.setFk_optional_field(2);
        typeContent4.setContent("Value 4");


        OptionalFieldTypeContent typeContent5 = new OptionalFieldTypeContent();
        typeContent5.setPk(5);
        typeContent5.setFk_optional_field(2);
        typeContent5.setContent("Value 5");

        OptionalFieldTypeContent typeContent6 = new OptionalFieldTypeContent();
        typeContent6.setPk(6);
        typeContent6.setFk_optional_field(3);
        typeContent6.setContent("Value 6");

        List<OptionalFieldTypeContent> typeContents = new ArrayList<>();
        typeContents.add(typeContent1);
        typeContents.add(typeContent2);
        typeContents.add(typeContent3);
        typeContents.add(typeContent4);
        typeContents.add(typeContent5);
        typeContents.add(typeContent6);

        return typeContents;
    }

    private void prepareSession() {
        when(delegationResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
    }
}