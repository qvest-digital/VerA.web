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
        personsSearch { $('nav#nav').find(By.id('menu.searchPerson')) }
        personsCreate { nav.find(By.id('menu.newPerson')) }

        // events
        eventsMenu { nav.find('span')[1] }
        eventsSearch { $('nav#nav').find(By.id('menu.searchEvent')) }

        // management
        managementMenu { nav.find('span')[2] }

        // administration
        administrationMenu { nav.find('span')[3] }
    }

    def toPersonOverview(WebDriver driver) {
        navigateTo(personsMenu, personsOverview, driver)
    }

    def toPersonSearch(WebDriver driver) {
        navigateTo(personsMenu, personsSearch, driver)
    }

    def toPersonCreation(WebDriver driver) {
        navigateTo(personsMenu, personsCreate, driver)
    }

    def toEventSearch(WebDriver driver) {
        navigateTo(eventsMenu, eventsSearch, driver)
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
