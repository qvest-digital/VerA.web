package de.tarent.aa.veraweb.beans;
import de.tarent.aa.veraweb.beans.facade.AbstractMember;

/**
 * Diese Klasse liefert eine Facade für die Hauptperson-Daten im Zusatzzeichensatz 1.
 */
class MainExtra1 extends AbstractMember {
    private Person person;

    public MainExtra1(Person person) {
        this.person = person;
    }

    public String getSalutation() {
        return person.salutation_a_e2;
    }

    public Integer getSalutationFK() {
        return person.fk_salutation_a_e2;
    }

    @Override
    public String getBirthplace() {
        return person.birthplace_a_e2;
    }

    public String getTitle() {
        return person.title_a_e2;
    }

    public String getFirstname() {
        return person.firstname_a_e2;
    }

    public String getLastname() {
        return person.lastname_a_e2;
    }

    public void setSalutation(String value) {
        person.salutation_a_e2 = value;
    }

    public void setSalutationFK(Integer value) {
        person.fk_salutation_a_e2 = value;
    }

    @Override
    public void setBirthplace(String value) {
        person.birthplace_a_e2 = value;
    }

    public void setTitle(String value) {
        person.title_a_e2 = value;
    }

    public void setFirstname(String value) {
        person.firstname_a_e2 = value;
    }

    public void setLastname(String value) {
        person.lastname_a_e2 = value;
    }

    // Für ist Firma
    public String getCompany() {
        return person.company_a_e2;
    }
}
