package de.tarent.veraweb

import de.tarent.veraweb.util.SystemtestHelper
import spock.lang.Specification

abstract class AbstractSystemTest extends Specification {

    static def ENDPOINT = 'http://localhost:18080'

    static def PATH = '/veraweb/do/Main'

    def setupSpec() {
        SystemtestHelper.waitForContainers(ENDPOINT, PATH)
    }
}
