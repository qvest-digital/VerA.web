package de.tarent.veraweb.pages

import de.tarent.veraweb.modules.NavigationBar
import de.tarent.veraweb.modules.PersonForm
import geb.Page
import org.openqa.selenium.By

class PersonCreatePage extends Page {

    static at = {
        pageTitle.text() == 'Neue Person anlegen'
    }

    static content = {
        pageTitle { $('h1') }
        personForm { module PersonForm }
        navigationBar { module NavigationBar }
    }

    def savePerson() {
        personForm.submit()
        personForm.submit()
    }

    def toPrivateAddressData() {
        personForm.toPrivateAddressData()
    }

    def toCompanyAddressData() {
        personForm.toCompanyAddressData()
    }

    def toPersonData() {
        personForm.toPersonData()
    }
}
