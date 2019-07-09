package de.tarent.veraweb.pages.person

import de.tarent.veraweb.modules.person.PersonTableRow
import geb.Page
import geb.navigator.EmptyNavigator
import org.openqa.selenium.By

class PersonOverviewPage extends Page {

    static at = {
        pageTitle.text() == 'Personenübersicht'
    }

    static content = {
        pageTitle { $('h1') }
        form { $('form#formlist') }

        table { form.find('table')}
        tableRows { table.$('tbody > tr').moduleList(PersonTableRow) }

        executeButton { form.find(By.id('button.execute')) }
        actionSelection { form.find(By.id('actionSelection'))}

        reallyDeleteButton { browser.$('div.msg.errormsg.errormsgButton').$('div.floatRight').$('input')[0]}
        successMessage { browser.$('div.msg.successmsg').$('span') }
    }

    def selectRowByLastName(String lastName) {
        waitFor {
            table.displayed
        }
        PersonTableRow row = tableRows.findResult {
            !EmptyNavigator.isInstance(it.cell) &&
                    !EmptyNavigator.isInstance(it.lastName) &&
                    it.lastName.text() == lastName ? it : null
        }
        if (row != null) {
            row.checkbox.click()
            return true
        }
        return false
    }

    def performDeletion() {
        performAction('Löschen')
        waitFor {
            reallyDeleteButton.displayed
        }
        reallyDeleteButton.click()
    }

    def performAction(String action) {
        actionSelection = action
        executeButton.click()
    }

    def successMessage() {
        waitFor {
            successMessage.displayed
        }
        successMessage.text()
    }
}
