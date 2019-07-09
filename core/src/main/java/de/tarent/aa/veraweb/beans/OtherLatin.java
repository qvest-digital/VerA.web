package de.tarent.aa.veraweb.beans;
import de.tarent.aa.veraweb.beans.facade.PersonAddressFacade;

/**
 * Diese Klasse liefert eine Facade für die Zusatz-Adresse im Latin-Zeichensatz.
 */
public class OtherLatin implements PersonAddressFacade {
    private Person person;

    public OtherLatin(Person person) {
        this.person = person;
    }

    public String getFunction() {
        return person.function_c_e1;
    }

    public String getCompany() {
        return person.company_c_e1;
    }

    public String getStreet() {
        return person.street_c_e1;
    }

    public String getZipCode() {
        return person.zipcode_c_e1;
    }

    public String getState() {
        return person.state_c_e1;
    }

    public String getCity() {
        return person.city_c_e1;
    }

    public String getCountry() {
        return person.country_c_e1;
    }

    public String getPOBox() {
        return person.pobox_c_e1;
    }

    public String getPOBoxZipCode() {
        return person.poboxzipcode_c_e1;
    }

    public String getSuffix1() {
        return person.suffix1_c_e1;
    }

    public String getSuffix2() {
        return person.suffix2_c_e1;
    }

    public String getPhone() {
        return person.fon_c_e1;
    }

    public String getFax() {
        return person.fax_c_e1;
    }

    public String getMobile() {
        return person.mobil_c_e1;
    }

    public String getEMail() {
        return person.mail_c_e1;
    }

    public String getUrl() {
        return person.url_c_e1;
    }

    public void setFunction(String value) {
        person.function_c_e1 = value;
    }

    public void setCompany(String value) {
        person.company_c_e1 = value;
    }

    public void setStreet(String value) {
        person.street_c_e1 = value;
    }

    public void setZipCode(String value) {
        person.zipcode_c_e1 = value;
    }

    public void setState(String state) {
        person.state_c_e1 = state;
    }

    public void setCity(String value) {
        person.city_c_e1 = value;
    }

    public void setCountry(String value) {
        person.country_c_e1 = value;
    }

    public void setPOBox(String value) {
        person.pobox_c_e1 = value;
    }

    public void setPOBoxZipCode(String value) {
        person.poboxzipcode_c_e1 = value;
    }

    public void setSuffix1(String value) {
        person.suffix1_c_e1 = value;
    }

    public void setSuffix2(String value) {
        person.suffix2_c_e1 = value;
    }

    public void setPhone(String value) {
        person.fon_c_e1 = value;
    }

    public void setFax(String value) {
        person.fax_c_e1 = value;
    }

    public void setMobile(String value) {
        person.mobil_c_e1 = value;
    }

    public void setEMail(String value) {
        person.mail_c_e1 = value;
    }

    public void setUrl(String value) {
        person.url_c_e1 = value;
    }
}
