package org.evolvis.veraweb.onlinereg.rest

import org.hibernate.Session
import org.hibernate.SessionFactory
import spock.lang.Specification

import javax.naming.InitialContext
import javax.servlet.ServletContext

/**
 * Created by mweier on 26.04.16.
 */
class ExportResourceTest extends Specification {

    private exportResource
    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    InitialContext ic = Mock(InitialContext)

    public void setup() {
        exportResource = new ExportResource(context: context)
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
        //TODO insert ic as InitialContext in ExportResource
    }

    void testGetGuestList() {
        when:
        def dataSource = exportResource.getGuestList()

        then:
        assert dataSource.getServerName() == "localhost"
        assert dataSource.getPortNumber() == 5432
        assert dataSource.getDatabaseName() == "veraweb"
        assert dataSource.getUser() == "veraweb"
        assert dataSource.getPassword() == "veraweb"
    }
}
