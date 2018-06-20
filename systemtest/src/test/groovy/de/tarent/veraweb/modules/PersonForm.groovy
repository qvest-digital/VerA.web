package de.tarent.veraweb.modules

import geb.Module
import org.openqa.selenium.By

class PersonForm extends Module {

    static content = {
        mainContent { $('form', name: 'PersonForm') }

        // tabs
        personData { mainContent.find(By.id('tabsMain')).$('ul').$('li')[0] }
        addressData { mainContent.find(By.id('tabsMain')).$('ul').$('li')[1] }
        addressDataCompany { mainContent.$('div.tabsSub')[1].$('ul').$('li')[0].$('div') }
        addressDataPrivate { mainContent.$('div.tabsSub')[1].$('ul').$('li')[1].$('div') }

        // person input fields
        isCompany { mainContent.find(By.id('person-iscompany')) }
        firstName { mainContent.find(By.id('person-firstname_a_e1')) }
        lastName { mainContent.find(By.id('person-lastname_a_e1')) }

        // address input fields
        companyName { mainContent.find(By.id('person-company_a_e1')) }
        companyStreet { mainContent.find(By.id('person-street_a_e1')) }
        companyZipCode { mainContent.find(By.id('person-zipcode_a_e1')) }
        companyCity { mainContent.find(By.id('person-city_a_e1')) }
        companyState { mainContent.find(By.id('person-state_a_e1')) }
        companyCountry { mainContent.find(By.id('person-country_a_e1')) }
        companyPhone { mainContent.find(By.id('person-fon_a_e1')) }
        companyFax { mainContent.find(By.id('person-fax_a_e1')) }
        companyMobile { mainContent.find(By.id('person-mobil_a_e1')) }
        companyEmail { mainContent.find(By.id('person-mail_a_e1')) }
        companyUrl { mainContent.find(By.id('person-url_a_e1')) }

        privateCompanyName  { mainContent.find(By.id('person-company_b_e1')) }
        privateStreet { mainContent.find(By.id('person-street_b_e1')) }
        privateZipCode { mainContent.find(By.id('person-zipcode_b_e1')) }
        privateCity { mainContent.find(By.id('person-city_b_e1')) }
        privateState { mainContent.find(By.id('person-state_b_e1')) }
        privateCountry { mainContent.find(By.id('person-country_b_e1')) }
        privatePhone { mainContent.find(By.id('person-fon_b_e1')) }
        privateFax { mainContent.find(By.id('person-fax_b_e1')) }
        privateMobile { mainContent.find(By.id('person-mobil_b_e1')) }
        privateEmail { mainContent.find(By.id('person-mail_b_e1')) }
        privateUrl { mainContent.find(By.id('person-url_b_e1')) }

        // top buttons
        deleteButton { mainContent.find(By.id('buttonPanel-top')).$('input', name: 'remove')}
        reallyDeleteButton { mainContent.$('div.msg.errormsg.errormsgButton').$('div.floatRight').$('input')[0]}

        // bottom buttons
        submitButton { mainContent.find(By.id('buttonPanel-bottom')).$('input.mainButton')[0] }
    }

    def toPersonData() {
        personData.click()
    }

    def fillPersonData(String firstName, String lastName, boolean isCompany) {
        this.firstName = firstName
        this.lastName = lastName
        if (isCompany) {
            this.isCompany.click()
        }
    }

    def toPrivateAddressData() {
        addressData.click()
        addressDataPrivate.click()
    }

    def fillPrivateAddressData(String company, String street, String zipCode, String city, String state,
                               String country, String phone, String fax, String mobile, String email) {
        toPrivateAddressData()
        privateCompanyName = company
        privateStreet = street
        privateZipCode = zipCode
        privateCity = city
        privateState = state
        privateCountry = country
        privatePhone = phone
        privateFax = fax
        privateMobile = mobile
        privateEmail = email
    }

    def toCompanyAddressData() {
        addressData.click()
        addressDataCompany.click()
    }

    def fillCompanyAddressData(String company, String street, String zipCode, String city, String state,
                               String country, String phone, String fax, String mobile, String email) {
        toCompanyAddressData()
        companyName = company
        companyStreet = street
        companyZipCode = zipCode
        companyCity = city
        companyState = state
        companyCountry = country
        companyPhone = phone
        companyFax = fax
        companyMobile = mobile
        companyEmail = email
    }

    def submit() {
        submitButton.click()
    }
}
