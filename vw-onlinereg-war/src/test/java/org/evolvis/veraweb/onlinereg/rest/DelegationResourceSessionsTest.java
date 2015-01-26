package org.evolvis.veraweb.onlinereg.rest;

import junit.framework.TestCase;
import org.evolvis.veraweb.onlinereg.entities.OptionalField;
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

import java.math.BigInteger;
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

    @Test
    public void testGetFieldsFromEvent() {
        // GIVEN
        prepareSession();
        List<OptionalFieldValue> fields = getDummyOptionalFieldValues();
        Query query1 = mock(Query.class);
        Query query2 = mock(Query.class);
        when(mockitoSession.getNamedQuery("OptionalField.findByEventId")).thenReturn(query1);
        when(mockitoSession.getNamedQuery("Delegation.findByGuestId")).thenReturn(query2);
        when(query1.list()).thenReturn(fields);

        // WHEN
        delegationResource.getFieldsFromEvent(1,1);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
        assertEquals(3, query1.list().size());
    }

    @Test
    public void testGetLabelIdfromEventAndLabel() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("OptionalField.findByEventIdAndLabel")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(1);

        // WHEN
        delegationResource.getLabelIdfromEventAndLabel(1, "test");

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
        OptionalFieldValue ofv1 = new OptionalFieldValue();
        OptionalFieldValue ofv2 = new OptionalFieldValue();
        OptionalFieldValue ofv3 = new OptionalFieldValue();
        ofv1.setPk(1);
        ofv2.setPk(2);
        ofv3.setPk(3);

        List<OptionalFieldValue> fields = new ArrayList();
        fields.add(ofv1);
        fields.add(ofv2);
        fields.add(ofv3);

        return fields;
    }

    private void prepareSession() {
        when(delegationResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
    }
}