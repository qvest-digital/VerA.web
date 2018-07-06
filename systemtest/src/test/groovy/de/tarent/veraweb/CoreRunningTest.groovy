package de.tarent.veraweb

import de.tarent.veraweb.util.SystemtestHelper
import groovyx.net.http.HTTPBuilder

class CoreRunningTest extends AbstractSystemTest {

    def 'application is up'() {
        given:
        HTTPBuilder httpBuilder = new HTTPBuilder(ENDPOINT)
        httpBuilder.ignoreSSLIssues()

        when:
        def isUp = SystemtestHelper.isCoreRunning(httpBuilder, ENDPOINT, PATH)

        then:
        isUp
    }
}
