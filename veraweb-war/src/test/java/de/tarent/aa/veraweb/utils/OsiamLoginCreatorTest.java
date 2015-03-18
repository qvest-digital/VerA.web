package de.tarent.aa.veraweb.utils;

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.server.OctopusContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osiam.client.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.resources.scim.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OsiamLoginCreatorTest {

    OsiamLoginCreator osiamLoginCreator;

    @Mock
    private Database database;

    @Before
    public void setUp() throws Exception {
        osiamLoginCreator = new OsiamLoginCreator(database);
        osiamLoginCreator.setDatabase(database);
    }

    @Test
    public void testGenerateUsernameNoExisting() throws Exception {
        // GIVEN
        OctopusContext oc = mock(OctopusContext.class);
        Select selectStatement = mock(Select.class);
        List resultList = new ArrayList();
        when(database.getSelect("Person")).thenReturn(selectStatement);
        when(selectStatement.where(any(Clause.class))).thenReturn(selectStatement);
        when(database.getBeanList("Person", selectStatement)).thenReturn(resultList);

        // WHEN
        String username = osiamLoginCreator.generateUsername("Max", "Mustermann", oc);

        // THEN
        assertEquals("mmuste", username);
    }

    @Test
    public void testGenerateUsernameExisting() throws Exception {
        // GIVEN
        OctopusContext oc = mock(OctopusContext.class);
        Select selectStatement = mock(Select.class);
        List resultList = getDummyResultList();
        when(database.getSelect("Person")).thenReturn(selectStatement);
        when(selectStatement.where(any(Clause.class))).thenReturn(selectStatement);
        when(database.getBeanList("Person", selectStatement)).thenReturn(resultList);

        // WHEN
        String username = osiamLoginCreator.generateUsername("Max", "Mustermann", oc);

        // THEN
        assertEquals("mmuste2", username);
    }

    @Test
    public void testGeneratePassword() throws Exception {
        for (int i = 0; i < 1000; i++) {
            String password = osiamLoginCreator.generatePassword();
            assertEquals(8, password.length());
            assertTrue(password.matches(osiamLoginCreator.CONDITIONS));
        }
    }

    @Test
    public void testCreateOsiamUser() throws Exception {
        OsiamConnector osiamConnector = mock(OsiamConnector.class);
        osiamLoginCreator.createOsiamUser(mock(AccessToken.class), "Max", "Mustermann", osiamConnector);

        verify(osiamConnector, times(1)).createUser(any(User.class), any(AccessToken.class));
    }

    private List getDummyResultList() {
        List resultList = new ArrayList();
        Person person1 = mock(Person.class);
        person1.username = "mmuste1";
        resultList.add(person1);
        return resultList;
    }
}