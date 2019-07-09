package de.tarent.veraweb.pages.person

import de.tarent.veraweb.modules.NavigationBar
import de.tarent.veraweb.modules.person.PersonForm
import geb.Page

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
