package org.evolvis.veraweb.onlinereg.rest

import org.hibernate.Query
import org.hibernate.Session
import org.hibernate.SessionFactory
import spock.lang.Specification

import javax.servlet.ServletContext

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class SalutationAlternativeResourceTest extends Specification {
    def resource
    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    Query query = Mock(Query)

    def setup() {
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
        session.getNamedQuery(_) >> query

        resource = new SalutationAlternativeResource(context: context);
    }

    void testGetSalutations() {

        given:
            List dummyResults = [
                [1,1,1,"Herrn","Herr"],
                [2,2,1,"Fau","Frau"],
                [3,3,1,"Sir",""],
                [4,4,1,"Madam",""],
            ]
            query.list() >> dummyResults

        when:
            List salutations = resource.getSalutations(1)

        then:
            assert session != null
            1 * session.close()
            assert salutations.size() == 4
    }

    void testGetUnusedDefaultSalutations() {
        when:
            resource.getSalutationsWithoutAlternativeContent(1)

        then:
            assert session != null
            1 * session.close()
    }

    void testSaveAlternativeSalutation() {
        when:
            def response = resource.saveAlternativeSalutation(1,1,"content")

        then:
            assert session != null
            1 * session.flush()
            1 * session.close()
            assert response.entity.content == "content"
    }

    void testSaveAlternativeSalutationEmptyString() {
        when:
            def response = resource.saveAlternativeSalutation(1,1,"")

        then:
            assert session != null
            0 * session.flush()
            0 * session.close()
            assert response.status == 400
    }

    void testSaveAlternativeSalutationNull() {
        when:
            def response = resource.saveAlternativeSalutation(null,1,"content")

        then:
            assert session != null
            0 * session.flush()
            0 * session.close()
            assert response.status == 400
    }

    void testDeleteAlternativeSalutationNull() {
        when:
            def response = resource.deleteAlternativeSalutation(1)

        then:
            assert session != null
            1 * session.flush()
            1 * session.close()
            assert response.entity == 1
    }
}
