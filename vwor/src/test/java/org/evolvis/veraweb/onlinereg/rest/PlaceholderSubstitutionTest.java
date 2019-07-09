package org.evolvis.veraweb.onlinereg.rest;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PlaceholderSubstitutionTest {
    @Test
    public void testPersonBasicFieldSubstitutions() throws IllegalArgumentException, IllegalAccessException {
        //GIVEN
        Person person = createPerson();
        PlaceholderSubstitution subst = new PlaceholderSubstitution(person);
        final List<String> words =
          Arrays.asList("firstname", "lastname", "salutation", "title", "function", "company", "street", "zipcode", "city",
            "country", "poboxzipcode", "pobox", "suffix1", "suffix2", "phone", "fax", "mobile", "email", "url");

        final String template = createTemplate(words);
        final String expectedOutput = createExpectedOutputByTableColumnNames(words);

        // WHEN
        String actualOutput = subst.apply(template);

        // THEN
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testHintFieldPlaceholderSubstition() throws IllegalArgumentException, IllegalAccessException {
        final List<String> words = Arrays.asList("remark", "hintfororga", "hintforhost");
        final List<String> expectedSubstitutions = Arrays.asList("note_a_e1", "noteForOrga", "notehost_a_e1");
        //GIVEN
        Person person = createPerson();
        PlaceholderSubstitution subst = new PlaceholderSubstitution(person);

        final String template = createTemplate(words);
        final String expectedOutput = createExpectedSubstitute(words, expectedSubstitutions);

        // WHEN
        String actualOutput = subst.apply(template);

        // THEN
        assertEquals(expectedOutput, actualOutput);
    }

    private String createExpectedOutputByTableColumnNames(final List<String> words) {
        final List<String> expectedSubstitutes = new LinkedList<>();
        for (String word : words) {
            String expectedValue = word;
            if (word.equals("phone")) {
                expectedValue = "fon";
            }
            if (word.equals("email")) {
                expectedValue = "mail";
            }
            if (word.equals("mobile")) {
                expectedValue = "mobil";
            }
            expectedSubstitutes.add(expectedValue + "_a_e1");
        }

        return createExpectedSubstitute(words, expectedSubstitutes);
    }

    private String createTemplate(final List<String> words) {
        final StringBuilder sb1 = new StringBuilder();
        for (String word : words) {
            sb1.append(word + ": '&lt;" + word + "&gt;'\n");
        }
        final String template = sb1.toString();
        return template;
    }

    private String createExpectedSubstitute(final List<String> words, final List<String> expectedSubstitutes) {
        final StringBuilder sb1 = new StringBuilder();
        for (int i = 0; i < words.size(); i++) {
            sb1.append(words.get(i) + ": '" + expectedSubstitutes.get(i) + "'\n");
        }
        final String template = sb1.toString();
        return template;
    }

    private Person createPerson() throws IllegalAccessException {

        final Person person = new Person();
        final Field[] fields = Person.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType().isAssignableFrom(String.class)) {
                //for now, all the fields that we care about *are* strings.
                //We put the field name in there, just to have some values we can
                //examine.
                field.set(person, field.getName());
            }
        }
        return person;
    }
}
