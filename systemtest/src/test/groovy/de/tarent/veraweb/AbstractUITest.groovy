package de.tarent.veraweb

import de.tarent.veraweb.pages.LoginPage
import de.tarent.veraweb.pages.MainPage
import de.tarent.veraweb.util.SystemtestHelper
import geb.spock.GebReportingSpec

abstract class AbstractUITest extends GebReportingSpec {

    def mainPage

    def setupSpec() {
        SystemtestHelper.waitForContainers(AbstractSystemTest.ENDPOINT, AbstractSystemTest.PATH)
    }

    def loginAsAdmin() {
        LoginPage loginPage = to LoginPage
        loginPage.loginAsAdmin()
        mainPage = at MainPage
        mainPage
    }

    def logout() {
        mainPage.logout()
    }
}
