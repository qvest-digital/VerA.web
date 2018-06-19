package de.tarent.veraweb

import de.tarent.veraweb.pages.LoginPage
import de.tarent.veraweb.pages.MainPage

class LoginLogoutTest extends AbstractUITest {

    def 'should login and logout'() {

        when: 'we navigate to the login page'
        def loginPage = to LoginPage

        then:
        at LoginPage

        when: 'we log in as admin'
        loginPage.loginAsAdmin()

        then: 'we are at the main page'
        MainPage mainPage = at MainPage

        when: 'we log out'
        mainPage.logout()

        then: 'we are at the login page'
        at LoginPage
    }
}
