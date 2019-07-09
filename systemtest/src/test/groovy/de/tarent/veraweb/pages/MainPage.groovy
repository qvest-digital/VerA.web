package de.tarent.veraweb.pages

import de.tarent.veraweb.modules.NavigationBar
import geb.Page
import org.openqa.selenium.By

class MainPage extends Page {

    static at = {
        pageTitle.text() == 'Willkommen bei VerA.web'
    }

    static content = {
        navigationBar { module NavigationBar }
        container { $('div#container') }
        pageTitle { container.$('h1')}
        logoutButton { container.find(By.id('menu.logout')) }
    }

    def logout() {
        logoutButton.click()
    }
}
