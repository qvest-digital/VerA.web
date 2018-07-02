package de.tarent.veraweb

import de.tarent.veraweb.pages.PersonCreatePage
import de.tarent.veraweb.pages.PersonEditPage
import de.tarent.veraweb.pages.PersonOverviewPage
import de.tarent.veraweb.pages.PersonSearchPage

class CreateAndDeletePersonTest extends AbstractUITest {

    def setup() {
        loginAsAdmin()
    }

    def cleanup() {
        logout()
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
        PersonSearchPage searchPage = at PersonSearchPage

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
}
