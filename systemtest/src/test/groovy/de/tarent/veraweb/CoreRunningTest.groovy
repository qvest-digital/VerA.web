package de.tarent.veraweb

class CoreRunningTest extends AbstractSystemTest {

    def 'application is up'() {
        when:
        def isUp = isCoreRunning()

        then:
        isUp
    }
}
