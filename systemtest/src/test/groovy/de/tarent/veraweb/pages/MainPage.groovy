package de.tarent.veraweb.pages

import de.tarent.veraweb.modules.NavigationBar
import geb.Page
import org.openqa.selenium.By

class MainPage extends Page {

    static url = '/veraweb/do/default'

    static at = {
        driver.currentUrl.startsWith("${browser.baseUrl}${url}")
    }

    static content = {
        navigationBar { module NavigationBar }
        container { $('div#container') }
        logoutButton { container.find(By.id('menu.logout')) }
    }

    def logout() {
        logoutButton.click()
    }
}
