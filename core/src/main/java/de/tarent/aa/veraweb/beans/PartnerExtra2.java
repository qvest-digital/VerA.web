package de.tarent.aa.veraweb.beans;
import de.tarent.aa.veraweb.beans.facade.AbstractMember;

/**
 * Diese Klasse liefert eine Facade für die Partner-Daten im Zusatzzeichensatz 2.
 */
class PartnerExtra2 extends AbstractMember {
    private Person person;

    public PartnerExtra2(Person person) {
        this.person = person;
    }

    public String getSalutation() {
        return person.salutation_b_e3;
    }

    public Integer getSalutationFK() {
        return person.fk_salutation_b_e3;
    }

    @Override
    public String getBirthplace() {
        throw new RuntimeException("Not implemented.");
        //return birthplace_b_e3;
    }

    public String getTitle() {
        return person.title_b_e3;
    }

    public String getFirstname() {
        return person.firstname_b_e3;
    }

    public String getLastname() {
        return person.lastname_b_e3;
    }

    public void setSalutation(String value) {
        person.salutation_b_e3 = value;
    }

    public void setSalutationFK(Integer value) {
        person.fk_salutation_b_e3 = value;
    }

    @Override
    public void setBirthplace(String value) {
        throw new RuntimeException("Not implemented.");
        //birthplace_b_e3 = value;
    }

    public void setTitle(String value) {
        person.title_b_e3 = value;
    }

    public void setFirstname(String value) {
        person.firstname_b_e3 = value;
    }

    public void setLastname(String value) {
        person.lastname_b_e3 = value;
    }

    // Für ist Firma
    public String getCompany() {
        return person.company_b_e3;
    }
}
