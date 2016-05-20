package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.utils.VworConstants
import org.hibernate.Session
import org.hibernate.SessionFactory
import spock.lang.Specification

import javax.naming.Context
import javax.naming.InitialContext
import javax.servlet.ServletContext
import javax.sql.DataSource
import javax.ws.rs.core.Response

/**
 * Created by mweier on 26.04.16.
 */
class ExportResourceTest extends Specification {

    private exportResource
    private ServletContext context = Mock(ServletContext)
    private SessionFactory sessionFactory = Mock(SessionFactory)
    private Session session = Mock(Session)
    private InitialContext initContext = Mock(InitialContext)
    private Context namingContext = Mock(Context)
    private DataSource datasource = Mock(DataSource)

    public void setup() {
        exportResource = new ExportResource(context: context, initContext: initContext)
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
        initContext.lookup("java:comp/env") >> namingContext
        context.lookup("jdbc/vwonlinereg") >> datasource
    }

    void testGetGuestList() {
        when:
            Response response = exportResource.getGuestList(1)

        then:
            assert response.status == VworConstants.HTTP_OK
    }
}
