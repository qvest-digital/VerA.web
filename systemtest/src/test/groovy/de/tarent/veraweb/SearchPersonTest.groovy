package de.tarent.veraweb

import de.tarent.veraweb.pages.PersonOverviewPage
import de.tarent.veraweb.pages.PersonSearchPage

class SearchPersonTest extends AbstractUITest {

    def setup() {
        loginAsAdmin()
    }

    def cleanup() {
        logout()
    }

    def 'search for previously created person'() {

        given:
        def lastName = 'Mustermann'

        when: 'navigate to search page'
        mainPage.navigationBar.toPersonSearch(driver)

        then:
        PersonSearchPage searchPage = at PersonSearchPage

        when: 'search for person'
        searchPage.searchPerson(lastName)

        then:
        PersonOverviewPage overviewPage = at PersonOverviewPage
        overviewPage.selectRowByLastName(lastName) == true
    }
}
