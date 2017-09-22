package org.evolvis.veraweb.onlinereg.rest

import org.hibernate.Query
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.Before
import spock.lang.Specification

import javax.servlet.ServletContext

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class MediaRepresentativeActivationResourceTest extends Specification {

    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    private resource

    @Before
    void setuo() {
        resource = new MediaRepresentativeActivationResource(context: context)
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
    }

    void testAddMediaRepresentativeActivationEntry() {
        when:
            resource.addMediaRepresentativeActivationEntry("token", "email", 1, "herr", "address", "city", "country", "vorname", "nachname", 22222);

        then:
            session != null
            1 * session.close()
    }

    void testExistEventIdByDelegationTheFirst() {

        given:
            def query = Mock(Query)
            session.getNamedQuery("MediaRepresentativeActivation.getEntryByEmailAndEventId") >> query
            query.uniqueResult() >> (BigInteger) 1

        when:
            def result = resource.existEventIdByDelegation("email", "1")

        then:
            assert result
            session != null
            1 * session.close()
    }

    void testExistEventIdByDelegationTheSecond() {

        given:
            def query = Mock(Query)
            session.getNamedQuery("MediaRepresentativeActivation.getEntryByEmailAndEventId") >> query
            query.uniqueResult() >> (BigInteger) 0

        when:
            def result = resource.existEventIdByDelegation("email", "1")

        then:
            assert !result
            session != null
            1 * session.close()
    }

    void testExistEventIdByDelegationTheThird() {

        given:
            def query = Mock(Query)
            session.getNamedQuery("MediaRepresentativeActivation.getEntryByEmailAndEventId") >> query
            query.uniqueResult() >> null

        when:
            def result = resource.existEventIdByDelegation("email", "1")

        then:
            assert !result
            session != null
            1 * session.close()
    }

    void testGetMediaRepresentativeActivationByToken() {
        given:
            def query = Mock(Query)
            session.getNamedQuery("MediaRepresentativeActivation.getActivationByActivationToken") >> query

        when:
            resource.getMediaRepresentativeActivationByToken("token");

        then:
            session != null
            1 * session.close()
    }

    void testActivatePressUser() {
        given:
            def query = Mock(Query)
            session.getNamedQuery("MediaRepresentativeActivation.activate") >> query

        when:
            resource.activatePressUser("email", 1);

        then:
            1 * query.executeUpdate()
            session != null
            1 * session.close()
    }
}
