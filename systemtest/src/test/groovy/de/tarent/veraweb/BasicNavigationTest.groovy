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
        navigateTo(navigationBar.personsMenu, navigationBar.personsOverview)

        then:
        at PersonOverviewPage
    }

    def 'navigate to person search'() {

        when: 'navigate to person search'
        navigateTo(navigationBar.personsMenu, navigationBar.personsSearch)

        then:
        at PersonSearchPage
    }
}
