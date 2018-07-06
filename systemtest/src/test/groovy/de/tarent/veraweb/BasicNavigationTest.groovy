package de.tarent.veraweb

import de.tarent.veraweb.pages.PersonOverviewPage
import de.tarent.veraweb.pages.PersonSearchPage

class BasicNavigationTest extends AbstractUITest {

    def setup() {
        loginAsAdmin()
    }

    def cleanup() {
        logout()
    }

    def 'navigate to person overview'() {

        when: 'navigate to person overview'
        mainPage.navigationBar.toPersonOverview(driver)

        then:
        at PersonOverviewPage
    }

    def 'navigate to person search'() {

        when: 'navigate to person search'
        mainPage.navigationBar.toPersonSearch(driver)

        then:
        at PersonSearchPage
    }
}
