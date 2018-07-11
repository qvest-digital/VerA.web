package de.tarent.veraweb.pages

import de.tarent.veraweb.modules.EventTableRow
import geb.Page
import geb.navigator.EmptyNavigator

class EventListPage extends Page {

    static at = {
        pageTitle.text() == 'Veranstalungsübersicht' || pageTitle.text() == 'Suchergebnis: Veranstaltung(en)'
    }

    static content = {
        pageTitle { $('h1') }
        form { $('form#formlist') }

        table { form.find('table')}
        tableRows { table.$('tbody > tr').moduleList(EventTableRow) }
    }

    def selectRowByName(String name) {
        EventTableRow row = findRowByName(name)
        if (row != null) {
            row.checkbox.click()
            return true
        }
        return false
    }

    def clickRowByName(String name) {
        EventTableRow row = findRowByName(name)
        if (row != null) {
            row.name.click()
        }
    }

    def findRowByName(String name) {
        waitFor {
            table.displayed
        }

        tableRows.findResult {
            !EmptyNavigator.isInstance(it.cell) &&
                    !EmptyNavigator.isInstance(it.name) &&
                    it.name.text() == name ? it : null }
    }
}
