package de.tarent.veraweb

import de.tarent.veraweb.pages.LoginPage
import de.tarent.veraweb.pages.MainPage
import de.tarent.veraweb.util.SystemtestHelper
import geb.navigator.Navigator
import geb.spock.GebReportingSpec
import spock.lang.Shared

abstract class AbstractUITest extends GebReportingSpec {

    @Shared
    def ENDPOINT = 'https://localhost'

    @Shared
    def PATH = '/veraweb/do/Main'

    def mainPage

    def navigationBar

    def setupSpec() {
        SystemtestHelper.waitForContainers(ENDPOINT, PATH)
    }

    def loginAsAdmin() {
        LoginPage loginPage = to LoginPage
        loginPage.loginAsAdmin()
        mainPage = at MainPage
        navigationBar = mainPage.navigationBar
        mainPage
    }

    def navigateTo(Navigator menu, Navigator menuItem) {
        navigationBar.navigateTo(menu, menuItem, driver)
    }

    def logout() {
        mainPage.logout()
    }
}
