package org.evolvis.veraweb.onlinereg.utils

import org.evolvis.veraweb.onlinereg.entities.Person
import spock.lang.Specification

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class PdfTemplateUtilitiesTest extends Specification {

    PdfTemplateUtilities pdfTemplateUtilities

    void setup() {
        Person person = initDummyPerson()
        pdfTemplateUtilities = new PdfTemplateUtilities(person)
    }

    void testGetSalutationCompleteOne() {
        when:
            def salutationCompleteTwo = pdfTemplateUtilities.getSalutationCompleteOne()

        then:
            assert salutationCompleteTwo.equals("Herr Prof. Dr. Max Mustermann")
    }

    void testGetSalutationCompleteTwo() {
        when:
            def salutationCompleteTwo = pdfTemplateUtilities.getSalutationCompleteTwo()

        then:
            assert salutationCompleteTwo.equals("Herr Prof. Dr. Max Mustermann, Sekräter")
    }

    void testGetEnvelopeOne() {
        when:
            def envelopeOne = pdfTemplateUtilities.getEnvelopeOne()

        then:
            assert envelopeOne.equals("Vorsitzender\n" +
                "Herr Prof. Dr. Max Mustermann\n" +
                "Sekräter\n" +
                "Hauptstrsasse 1\n" +
                "04103 Leipzig\n" +
                "Deutschland")
    }

    private Person initDummyPerson() {
        def person = new Person(
                firstname_a_e1: "Max",
                lastname_a_e1: "Mustermann",
                salutation_a_e1: "Herr",
                title_a_e1: "Prof. Dr.",
                suffix1_a_e1: "Sekräter",
                street_a_e1: "Hauptstrsasse 1",
                city_a_e1: "Leipzig",
                zipcode_a_e1: "04103",
                country_a_e1: "Deutschland",
                function_a_e1: "Vorsitzender"
        )
        person
    }
}
