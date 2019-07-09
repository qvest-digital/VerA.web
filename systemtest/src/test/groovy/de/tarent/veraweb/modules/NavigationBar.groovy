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
        personsCreate { nav.find(By.id('menu.newPerson')) }
        personsSearchReplace {nav.find(By.id('menu.searchReplace'))}
        personsDoublet {nav.find(By.id('menu.searchDuplicate'))}
        personsExport {nav.find(By.id('menu.exportPerson'))}
        personsImport {nav.find(By.id('menu.importPerson'))}

        // events
        eventsMenu { nav.find('span')[1] }
        eventsOverview {nav.find(By.id('menu.overviewEvent'))}
        eventsSearch {nav.find(By.id('menu.searchEvent'))}
        eventsCreate {nav.find(By.id('menu.newEvent'))}

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

    def toPersonSearchReplace(WebDriver driver) {
        navigateTo(personsMenu, personsSearchReplace, driver)
    }

    def toPersonDoublet(WebDriver driver) {
        navigateTo(personsMenu, personsDoublet, driver)
    }

    def toPersonExport(WebDriver driver) {
        navigateTo(personsMenu, personsExport, driver)
    }

    def toPersonImport(WebDriver driver) {
        navigateTo(personsMenu, personsImport, driver)
    }

    def toEventOverview(WebDriver driver) {
        navigateTo(eventsMenu, eventsOverview, driver)
    }

    def toEventSearch(WebDriver driver) {
        navigateTo(eventsMenu, eventsSearch, driver)
    }

    def toEventCreation(WebDriver driver) {
        navigateTo(eventsMenu, eventsCreate, driver)
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
