package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.entities.OptionalFieldTypeContent
import org.hibernate.Query
import org.hibernate.Session
import org.hibernate.SessionFactory
import spock.lang.Specification

import javax.servlet.ServletContext

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class OptionalFieldTypeContentResourceTest extends Specification {

    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    def resource

    void setup() {
        resource = new OptionalFieldTypeContentResource(context: context)
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
    }

    void testGetTypeContentsByOptionalField() {
        given:
            def query = Mock(Query)
            session.getNamedQuery("OptionalFieldTypeContent.findTypeContentsByOptionalField") >> query
            query.list() >> [Mock(OptionalFieldTypeContent),Mock(OptionalFieldTypeContent)]

        when:
            def fields = resource.getTypeContentsByOptionalField(1);

        then:
            fields.size() == 2
            session != null
            1 * session.close()
    }

}
