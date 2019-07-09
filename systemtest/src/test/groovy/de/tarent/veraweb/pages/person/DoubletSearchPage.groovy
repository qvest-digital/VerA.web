package de.tarent.veraweb.pages.person

import de.tarent.veraweb.modules.person.DoubletPersonTableRow
import geb.Page
import geb.navigator.EmptyNavigator

class DoubletSearchPage extends Page {
    static at = {
        pageTitle.text() == "Dublettensuche"
    }

    static content = {
        pageTitle { $('h1') }

        form {$('form#formlist')}

        table {form.find('table')}
        tableRows { table.$('tbody > tr').moduleList(DoubletPersonTableRow) }
    }

    def countAllPersonsByLastName(String lastName) {
        waitFor {
            table.displayed
        }
        List<DoubletPersonTableRow> personList = tableRows.findResults {
            !EmptyNavigator.isInstance(it.cell) &&
                    !EmptyNavigator.isInstance(it.lastName) ? it : null
                    it.lastName.text() == lastName ? it : null
        }

        return personList.size()
    }
}
