package org.evolvis.veraweb.onlinereg.rest

import org.hibernate.Query
import org.hibernate.Session
import org.hibernate.SessionFactory
import spock.lang.Specification

import javax.servlet.ServletContext

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class SalutationResourceTest extends Specification {

    private SalutationResource resource
    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)

    void setup() {
        resource = new SalutationResource(context: context)
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
    }

    void testGetSalutations() {

        given:
            Query query = Mock(Query)
            session.getNamedQuery("Salutation.getAllSalutations") >> query
            query.list() >> ['Herr', 'Frau', 'Sir', 'Madam']

        when:
            def salutations = resource.getSalutations()

        then:
            salutations.get(0) == 'Herr'
            salutations.get(1) == 'Frau'
            salutations.get(2) == 'Sir'
            salutations.get(3) == 'Madam'
            session != null
            1 * session.close()
    }
}
