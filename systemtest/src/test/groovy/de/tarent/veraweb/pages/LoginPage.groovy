package de.tarent.veraweb.pages

import geb.Page

class LoginPage extends Page {

    static at = {
        pageTitle.text() == 'Anmelden'
    }

    static content = {
        loginForm { $('div#german').$('form') }
        pageTitle { loginForm.$('h1')}
        username { loginForm.find("input", name:"username") }
        password { loginForm.find("input", name:"password") }
        loginButton (to: MainPage){ loginForm.find('input', title: 'Anmelden') }
    }

    def loginAsAdmin() {
        waitFor {
            loginForm.isDisplayed()
        }
        username = 'admin'
        password = 'admin'
        loginButton.click()
    }
}
