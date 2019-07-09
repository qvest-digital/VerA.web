package de.tarent.veraweb.pages.guest

import de.tarent.veraweb.modules.guest.AddGuestTableRow
import geb.Page
import geb.navigator.EmptyNavigator

class AddGuestPage extends Page {
    static at = {
        pageTitle.text().startsWith('Gast hinzufügen zu')
    }

    static content = {
        pageTitle {$('h1')}

        form {$('form#formlist')}

        table {form.find('table')}
        tableRows {table.$('tbody > tr').moduleList(AddGuestTableRow)}

        addPersonButton {form.$('input', type: 'button', value: 'Hinzufügen')}
    }

    def addSelectedPerson() {
        addPersonButton.click()
    }

    def selectRowByName(String firstname, String lastname) {
        AddGuestTableRow row = findRowByName(firstname, lastname)
        if(row != null) {
            row.mainPersonCheckbox.click()
            return true
        }
        return false
    }

    def findRowByName(String name, String firstName) {
        waitFor {
            table.displayed
        }

        tableRows.findResult {
            !EmptyNavigator.isInstance(it.cell) &&
                    !EmptyNavigator.isInstance(it.name) &&
                    !EmptyNavigator.isInstance(it.firstName) &&
                    (it.name.text() == name && it.firstName.text() == firstName)? it : null }
    }
}
