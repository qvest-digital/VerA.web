package de.tarent.veraweb.modules

import geb.Module
import geb.navigator.Navigator
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions

class NavigationBar extends Module {

    static content = {
        nav { $('nav#nav')}

        // menus
        // persons
        personsMenu { nav.find('span')[0] }
        personsOverview { nav.find(By.id('menu.overviewPerson')) }
        personsSearch { nav.find(By.id('menu.searchPerson')) }

        // events
        eventsMenu { nav.find('span')[1] }

        // management
        managementMenu { nav.find('span')[2] }

        // administration
        administrationMenu { nav.find('span')[3] }
    }

    def navigateTo(Navigator menu, Navigator menuItem, WebDriver driver) {
        openMenu(menu, driver)
        clickMenuItem(menuItem, driver)
    }

    def openMenu(Navigator menu, WebDriver driver) {
        Actions actions = new Actions(driver)
        actions.moveToElement(menu.firstElement())
        actions.perform()
    }

    def clickMenuItem(Navigator menuItem, WebDriver driver) {
        Actions actions = new Actions(driver)
        actions.click(menuItem.firstElement())
        actions.perform()
    }
}
