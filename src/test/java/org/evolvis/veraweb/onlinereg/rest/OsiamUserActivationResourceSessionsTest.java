package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.OsiamUserActivation;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@RunWith(MockitoJUnitRunner.class)
public class OsiamUserActivationResourceSessionsTest {

    private OsiamUserActivationResource osiamUserActivationResource;
    @Mock
    private static SessionFactory sessionFactory;
    @Mock
    private static Session session;

    @Before
    public void setUp() throws Exception {
        osiamUserActivationResource = new OsiamUserActivationResource();
        osiamUserActivationResource.context = mock(ServletContext.class);
    }

    @AfterClass
    public static void tearDown() {
        sessionFactory.close();

        session.disconnect();
        session.close();
    }

    @Test
    public void testAddOsiamUserActivationEntry() throws Exception {
        // GIVEN
        prepareSession();

        // WHEN
        osiamUserActivationResource.addOsiamUserActivationEntry("username", "token");

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testRemoveOsiamUserActivationEntryTheFirst() throws Exception {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        OsiamUserActivation osiamUserActivation = mock(OsiamUserActivation.class);
        when(session.getNamedQuery("OsiamUserActivation.getOsiamUserActivationEntryByToken")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(osiamUserActivation);

        // WHEN
        osiamUserActivationResource.removeOsiamUserActivationEntry("token");

        // THEN
        verify(sessionFactory, times(2)).openSession();
        verify(session, times(2)).close();
    }

    @Test
    public void testRemoveOsiamUserActivationEntryTheSecond() throws Exception {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("OsiamUserActivation.getOsiamUserActivationEntryByToken")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        // WHEN
        osiamUserActivationResource.removeOsiamUserActivationEntry("token");

        // THEN
        verify(sessionFactory, times(2)).openSession();
        verify(session, times(2)).close();
    }

    @Test
    public void testGetOsiamUserActivationByUsername() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        OsiamUserActivation osiamUserActivation = mock(OsiamUserActivation.class);
        when(session.getNamedQuery("OsiamUserActivation.getOsiamUserActivationEntryByUsername")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(osiamUserActivation);

        // WHEN
        osiamUserActivationResource.getOsiamUserActivationByUsername("username");

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testRefreshActivationdataByUsername() throws MessagingException {
        // GIVEN
        prepareSession();
        final EmailResource emailResource = mock(EmailResource.class);
        osiamUserActivationResource.setEmailResource(emailResource);
        Query query = mock(Query.class);
        when(session.getNamedQuery("OsiamUserActivation.refreshOsiamUserActivationByUsername")).thenReturn(query);
        doNothing().when(emailResource).sendEmailVerification(any(String.class), any(String.class), any(String.class), any(String.class), any(Boolean.class));

        // WHEN
        osiamUserActivationResource.refreshActivationdataByUsername("email", "username", "token", "endpoint", "de_DE");

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    private void prepareSession() {
        when(osiamUserActivationResource.context.getAttribute("SessionFactory")).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
    }
}