package de.tarent.veraweb.pages

import de.tarent.veraweb.modules.NavigationBar
import de.tarent.veraweb.modules.PersonForm
import geb.Page

class PersonEditPage extends Page {

    static at = {
        pageTitle.text().startsWith('Person bearbeiten')
        waitFor {
            navigationBar.isDisplayed()
            personForm.isDisplayed()
        }
    }

    static content = {
        pageTitle { $('h1') }
        personForm { module PersonForm }
        navigationBar { module NavigationBar }
    }

    def deletePerson() {
        personForm.deletePerson()
    }
}
