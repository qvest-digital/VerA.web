package de.tarent.veraweb

import de.tarent.veraweb.util.SystemtestHelper
import spock.lang.Shared
import spock.lang.Specification

abstract class AbstractSystemTest extends Specification {

    @Shared
    def ENDPOINT = 'https://localhost'

    @Shared
    def PATH = '/veraweb/do/Main'

    def setupSpec() {
        SystemtestHelper.waitForContainers(ENDPOINT, PATH)
    }
}