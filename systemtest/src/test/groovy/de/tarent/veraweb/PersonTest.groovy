package de.tarent.veraweb

import de.tarent.veraweb.pages.person.DoubletSearchPage
import de.tarent.veraweb.pages.person.PersonCreatePage
import de.tarent.veraweb.pages.person.PersonEditPage
import de.tarent.veraweb.pages.person.PersonOverviewPage
import de.tarent.veraweb.pages.person.PersonSearchSimplePage

class PersonTest extends AbstractUITest {
    def setup() {
        loginAsAdmin()
    }

    def cleanup() {
        logout()
    }

    def 'search for person'() {
        given:
        def lastName = 'Mustermann'

        when: 'navigate to search page'
        mainPage.navigationBar.toPersonSearch(driver)

        then:
        PersonSearchSimplePage searchPage = at PersonSearchSimplePage

        when: 'search for person'
        searchPage.searchPerson(lastName)

        then:
        PersonOverviewPage overviewPage = at PersonOverviewPage
        overviewPage.selectRowByLastName(lastName) == true
    }

    def 'create a person and delete it afterwards'() {

        when: 'navigate to new person page'
        mainPage.navigationBar.toPersonCreation(driver)

        then:
        PersonCreatePage page = at PersonCreatePage

        when: 'fill in data and save person'
        String lastName = "Utzer-${UUID.randomUUID()}"
        page.personForm.fillPersonData('Ben', lastName, false)
        page.personForm.fillPrivateAddressData('Brot für die Welt', 'Testgasse 2', '12345',
                'Testfurt', 'NRW', 'Deutschland', '012345', '234567', '345678', 'ben.utzer@test.de')
        page.personForm.fillCompanyAddressData('tarent AG', 'Rochusstr. 2', '53123',
                'Bonn', 'NRW', 'Deutschland', '012345', '234567', '345678', 'ben.utzer@tarent.de')
        page.savePerson()

        then:
        PersonEditPage editPage = at PersonEditPage

        when: 'navigate to search page'
        editPage.navigationBar.toPersonSearch(driver)

        then:
        PersonSearchSimplePage searchPage = at PersonSearchSimplePage

        when: 'search for created person'
        searchPage.searchPerson(lastName)

        then:
        PersonOverviewPage overviewPage = at PersonOverviewPage

        when: 'delete created person'
        overviewPage.selectRowByLastName(lastName)
        overviewPage.performDeletion()

        then:
        at PersonOverviewPage
        overviewPage.successMessage() == 'Es wurde eine Person gelöscht..'
    }

    def 'duplicate search does work'() {
        given:
        def lastname = "Doublet"

        when: 'navigate to doublet search'
        mainPage.navigationBar.toPersonDoublet(driver)

        then:
        DoubletSearchPage page = at DoubletSearchPage
        page.countAllPersonsByLastName(lastname) == 2
    }
}
