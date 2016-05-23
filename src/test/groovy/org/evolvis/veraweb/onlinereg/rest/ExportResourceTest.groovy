package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.utils.VworConstants
import spock.lang.Specification

import javax.naming.Context
import javax.naming.InitialContext
import javax.sql.DataSource
import javax.ws.rs.core.Response

/**
 * Created by mweier on 26.04.16.
 */
class ExportResourceTest extends Specification {

    private ExportResource exportResource
    private InitialContext initContext = Mock(InitialContext)
    private Context namingContext = Mock(Context)
    private DataSource dataSource = Mock(DataSource)

    public void setup() {
        exportResource = new ExportResource(initContext: initContext)
        initContext.lookup("java:comp/env") >> namingContext
        namingContext.lookup("jdbc/vwonlinereg") >> dataSource
    }

    void testGetGuestList() {
        when:
            Response response = exportResource.getGuestList(1)

        then:
            assert response.status == VworConstants.HTTP_OK
    }
}
