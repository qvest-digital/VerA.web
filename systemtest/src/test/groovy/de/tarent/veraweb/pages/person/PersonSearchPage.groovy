package de.tarent.veraweb.pages.person

import geb.Page
import org.openqa.selenium.By

class PersonSearchPage extends Page {
    static at = {
        pageTitle.text().startsWith("Personensuche")
    }

    static content = {
        pageTitle {$('h1')}

        form {$('form#peopleSearch_form')}

        submitButton {$('input', type: 'submit', name: 'search')}

        firstname {form.find(By.id('firstname'))}
        lastname {form.find(By.id('lastname'))}
    }

    def searchPersonByName(String firstnameString, String lastnameString) {
        firstname = firstnameString
        lastname = lastnameString
        search()
    }

    def search() {
        submitButton.click()
    }
}
