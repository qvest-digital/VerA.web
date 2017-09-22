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
import java.util.Date;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@RunWith(MockitoJUnitRunner.class)
public class OsiamUserActivationResourceTest {


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
    public void testGetOsiamUserActivationByUsername() throws Exception {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);

        when(session.getNamedQuery("OsiamUserActivation.getOsiamUserActivationEntryByUsername")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        // WHEN
        final OsiamUserActivation osiamUserActivation = osiamUserActivationResource.getOsiamUserActivationByUsername("username");

        // THEN
        assertNull(osiamUserActivation.getUsername());
        assertNull(osiamUserActivation.getActivation_token());
        assertNull(osiamUserActivation.getExpiration_date());
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
        verify(query, times(2)).setString(any(String.class), any(String.class));
        verify(query, times(1)).setDate(any(String.class), (Date) anyObject());
        verify(query, times(1)).executeUpdate();
    }

    private void prepareSession() {
        when(osiamUserActivationResource.context.getAttribute("SessionFactory")).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
    }
}
