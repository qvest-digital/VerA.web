package de.tarent.veraweb.modules.event

import geb.Module
import geb.navigator.EmptyNavigator

class EventTable extends Module {
    static content = {
        form {$('form#formlist')}

        table {form.find('table')}
        tableRows {table.$('tbody > tr').moduleList(EventTableRow)}
    }

    def selectRowByName(String eventName) {
        waitFor {
            table.displayed
        }
        EventTableRow row = tableRows.findResult {
            !EmptyNavigator.isInstance(it.cell) &&
                    !EmptyNavigator.isInstance(it.name) &&
                    it.name.text() == eventName ? it : null
        }
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
