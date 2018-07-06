package de.tarent.veraweb.util

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import static java.util.concurrent.TimeUnit.MILLISECONDS
import static java.util.concurrent.TimeUnit.MINUTES
import static org.awaitility.Awaitility.await

class SystemtestHelper {

    static waitForContainers(String endpoint, String path) {
        HTTPBuilder httpBuilder = new HTTPBuilder(endpoint)

        await().pollInterval(500, MILLISECONDS).atMost(2, MINUTES).until {
            isCoreRunning(httpBuilder, endpoint, path)
        }
    }

    static isCoreRunning(HTTPBuilder httpBuilder, String endpoint, String path) {

        def response = httpBuilder.request(endpoint, Method.GET, ContentType.TEXT) { req ->
            uri.path = path
        }

        response.str.contains('<title>VerA.web</title>')
    }
}
