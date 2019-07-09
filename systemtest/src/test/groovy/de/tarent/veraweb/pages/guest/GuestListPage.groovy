package de.tarent.veraweb.pages.guest

import de.tarent.veraweb.modules.guest.GuestTableRow
import geb.Page
import geb.navigator.EmptyNavigator

class GuestListPage extends Page {

    static at = {
        pageTitle.text().startsWith('Gästeverwaltung der Veranstaltung')
    }

    static content = {
        pageTitle { $('h1') }
        form { $('form#formlist') }

        table { form.find('table')}
        tableRows { table.$('tbody > tr').moduleList(GuestTableRow) }

        addGuestButton {form.$('input', type: 'button', name: 'add')}
    }

    def addGuest() {
        addGuestButton.click()
    }

    def selectRowByName(String name, String firstName) {
        GuestTableRow row = findRowByName(name, firstName)
        if (row != null) {
            row.checkbox.click()
            return true
        }
        return false
    }

    def clickRowByName(String name, String firstName) {
        GuestTableRow row = findRowByName(name, firstName)
        if (row != null) {
            row.name.click()
        }
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
