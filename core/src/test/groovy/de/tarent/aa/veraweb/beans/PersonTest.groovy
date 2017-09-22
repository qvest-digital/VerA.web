package de.tarent.aa.veraweb.beans

import spock.lang.Specification

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class PersonTest extends Specification {

    private Validator validator
    private java.util.Set<javax.validation.ConstraintViolation<org.evolvis.veraweb.onlinereg.entities.Person>> violations

    public void setup() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    public void testFirstnameSuccess() {
        given:
            Person person = new Person(firstname_a_e1: "Max_Latin");

        when:
            violations = this.validator.validate(person);

        then:
            assert this.violations.isEmpty() == true
            person.firstname_a_e1 == "Max_Latin"
    }

    public void testValidation() {
        def tooLongString = "maxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "xxxxxxxxxxxxx"
        given:
            Person person = new Person(
                birthplace_a_e1: tooLongString,
                birthplace_a_e2: tooLongString,
                birthplace_a_e3: tooLongString,
                city_a_e1: tooLongString,
                city_a_e2: tooLongString,
                city_a_e3: tooLongString,
                city_b_e1: tooLongString,
                city_b_e2: tooLongString,
                city_b_e3: tooLongString,
                city_c_e1: tooLongString,
                city_c_e2: tooLongString,
                city_c_e3: tooLongString,
                company_a_e1: tooLongString,
                company_a_e2: tooLongString,
                company_a_e3: tooLongString,
                company_b_e1: tooLongString,
                company_b_e2: tooLongString,
                company_b_e3: tooLongString,
                company_c_e1: tooLongString,
                company_c_e2: tooLongString,
                company_c_e3: tooLongString,
                country_a_e1: tooLongString,
                country_a_e2: tooLongString,
                country_a_e3: tooLongString,
                country_b_e1: tooLongString,
                country_b_e2: tooLongString,
                country_b_e3: tooLongString,
                country_c_e1: tooLongString,
                country_c_e2: tooLongString,
                country_c_e3: tooLongString,
                fax_a_e1: tooLongString,
                fax_a_e2: tooLongString,
                fax_a_e3: tooLongString,
                fax_b_e1: tooLongString,
                fax_b_e2: tooLongString,
                fax_b_e3: tooLongString,
                fax_c_e1: tooLongString,
                fax_c_e2: tooLongString,
                fax_c_e3: tooLongString,
                firstname_a_e1: tooLongString,
                firstname_a_e2: tooLongString,
                firstname_a_e3: tooLongString,
                firstname_b_e1: tooLongString,
                firstname_b_e2: tooLongString,
                firstname_b_e3: tooLongString,
                fon_a_e1: tooLongString,
                fon_a_e2: tooLongString,
                fon_a_e3: tooLongString,
                fon_b_e1: tooLongString,
                fon_b_e2: tooLongString,
                fon_b_e3: tooLongString,
                fon_c_e1: tooLongString,
                fon_c_e2: tooLongString,
                fon_c_e3: tooLongString,
                function_a_e1: tooLongString,
                function_a_e2: tooLongString,
                function_a_e3: tooLongString,
                function_b_e1: tooLongString,
                function_b_e2: tooLongString,
                function_b_e3: tooLongString,
                function_c_e1: tooLongString,
                function_c_e2: tooLongString,
                function_c_e3: tooLongString,
                languages_a_e1: tooLongString,
                languages_b_e1: tooLongString,
                lastname_a_e1: tooLongString,
                lastname_a_e2: tooLongString,
                lastname_a_e3: tooLongString,
                lastname_b_e1: tooLongString,
                lastname_b_e2: tooLongString,
                lastname_b_e3: tooLongString,
                mail_a_e1: tooLongString,
                mail_a_e2: tooLongString,
                mail_a_e3: tooLongString,
                mail_b_e1: tooLongString,
                mail_b_e2: tooLongString,
                mail_b_e3: tooLongString,
                mail_c_e1: tooLongString,
                mail_c_e2: tooLongString,
                mail_c_e3: tooLongString,
                mobil_a_e1: tooLongString,
                mobil_a_e2: tooLongString,
                mobil_a_e3: tooLongString,
                mobil_b_e1: tooLongString,
                mobil_b_e2: tooLongString,
                mobil_b_e3: tooLongString,
                mobil_c_e1: tooLongString,
                mobil_c_e2: tooLongString,
                mobil_c_e3: tooLongString,
                nationality_a_e1: tooLongString,
                nationality_b_e1: tooLongString,
                note_a_e1: tooLongString,
                note_b_e1: tooLongString,
                notehost_a_e1: tooLongString,
                notehost_b_e1: tooLongString,
                noteorga_a_e1: tooLongString,
                noteorga_b_e1: tooLongString,
                pobox_a_e1: tooLongString,
                pobox_a_e2: tooLongString,
                pobox_a_e3: tooLongString,
                pobox_b_e1: tooLongString,
                pobox_b_e2: tooLongString,
                pobox_b_e3: tooLongString,
                pobox_c_e1: tooLongString,
                pobox_c_e2: tooLongString,
                pobox_c_e3: tooLongString,
                poboxzipcode_a_e1: tooLongString,
                poboxzipcode_a_e2: tooLongString,
                poboxzipcode_a_e3: tooLongString,
                poboxzipcode_b_e1: tooLongString,
                poboxzipcode_b_e2: tooLongString,
                poboxzipcode_b_e3: tooLongString,
                poboxzipcode_c_e1: tooLongString,
                poboxzipcode_c_e2: tooLongString,
                poboxzipcode_c_e3: tooLongString,
                state_a_e1: tooLongString,
                state_a_e2: tooLongString,
                state_a_e3: tooLongString,
                state_b_e1: tooLongString,
                state_b_e2: tooLongString,
                state_b_e3: tooLongString,
                state_c_e1: tooLongString,
                state_c_e2: tooLongString,
                state_c_e3: tooLongString,
                street_a_e1: tooLongString,
                street_a_e2: tooLongString,
                street_a_e3: tooLongString,
                street_b_e1: tooLongString,
                street_b_e2: tooLongString,
                street_b_e3: tooLongString,
                street_c_e1: tooLongString,
                street_c_e2: tooLongString,
                street_c_e3: tooLongString,
                suffix1_a_e1: tooLongString,
                suffix1_a_e2: tooLongString,
                suffix1_a_e3: tooLongString,
                suffix1_b_e1: tooLongString,
                suffix1_b_e2: tooLongString,
                suffix1_b_e3: tooLongString,
                suffix1_c_e1: tooLongString,
                suffix1_c_e2: tooLongString,
                suffix1_c_e3: tooLongString,
                suffix2_a_e1: tooLongString,
                suffix2_a_e2: tooLongString,
                suffix2_a_e3: tooLongString,
                suffix2_b_e1: tooLongString,
                suffix2_b_e2: tooLongString,
                suffix2_b_e3: tooLongString,
                suffix2_c_e1: tooLongString,
                suffix2_c_e2: tooLongString,
                suffix2_c_e3: tooLongString,
                title_a_e1: tooLongString,
                title_a_e2: tooLongString,
                title_a_e3: tooLongString,
                title_b_e1: tooLongString,
                title_b_e2: tooLongString,
                title_b_e3: tooLongString,
                url_a_e1: tooLongString,
                url_a_e2: tooLongString,
                url_a_e3: tooLongString,
                url_b_e1: tooLongString,
                url_b_e2: tooLongString,
                url_b_e3: tooLongString,
                url_c_e1: tooLongString,
                url_c_e2: tooLongString,
                url_c_e3: tooLongString,
                zipcode_a_e1: tooLongString,
                zipcode_a_e2: tooLongString,
                zipcode_a_e3: tooLongString,
                zipcode_b_e1: tooLongString,
                zipcode_b_e2: tooLongString,
                zipcode_b_e3: tooLongString,
                zipcode_c_e1: tooLongString,
                zipcode_c_e2: tooLongString,
                zipcode_c_e3: tooLongString,
            );

        when:
            violations = this.validator.validate(person);

        then:
            assert violations.isEmpty() == false
            assert violations.size() == 175
    }

}
