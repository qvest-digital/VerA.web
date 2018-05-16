package de.tarent.aa.veraweb.beans

import de.tarent.aa.veraweb.worker.ConfigWorker
import de.tarent.octopus.server.OctopusContext
import spock.lang.Specification

class ConfigWorkerTest extends Specification {

    ConfigWorker testObject = new ConfigWorker()
    ViewConfig viewConfig = testObject.verawebViewConfig
    OctopusContext octopusContextMock = Mock(OctopusContext)

    def setup() {
        testObject.loaded = true
    }

    def 'if load is called view view config will be put as content into context'() {
        when:
        testObject.load(octopusContextMock)

        then:
        viewConfig != null
        1 * octopusContextMock.setContent('viewConfig', viewConfig)
    }
}
