package de.tarent.veraweb

import groovyx.net.http.ContentType
import groovyx.net.http.Method
import spock.lang.Shared
import spock.lang.Specification

import static java.util.concurrent.TimeUnit.MILLISECONDS
import static java.util.concurrent.TimeUnit.MINUTES
import static org.awaitility.Awaitility.await
import groovyx.net.http.HTTPBuilder

abstract class AbstractSystemTest extends Specification {

    @Shared
    def ENDPOINT = 'https://localhost'

    @Shared
    def PATH = '/veraweb/do/Main'

    @Shared
    HTTPBuilder httpBuilder = new HTTPBuilder(ENDPOINT)

    def setupSpec() {
        httpBuilder.ignoreSSLIssues()
        waitForContainers()
    }

    def waitForContainers() {
        await().pollInterval(500, MILLISECONDS).atMost(2, MINUTES).until { isCoreRunning() }
    }

    def isCoreRunning() {
        def response = httpBuilder.request( ENDPOINT, Method.GET, ContentType.TEXT ) { req ->
            uri.path = PATH
        }

        response.str.contains('<title>VerA.web</title>')
    }
}