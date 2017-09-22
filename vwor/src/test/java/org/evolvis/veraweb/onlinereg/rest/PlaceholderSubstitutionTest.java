package org.evolvis.veraweb.onlinereg.rest;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PlaceholderSubstitutionTest {

    @Test
    public void test() throws IllegalArgumentException, IllegalAccessException {
        //GIVEN
        Person person = createPerson();
        PlaceholderSubstitution subst = new PlaceholderSubstitution(person);
        final List<String> words = Arrays.asList("firstname", "lastname", "salutation", "title", "function", "company", "street", "zipcode", "city",
                "country", "poboxzipcode", "pobox", "suffix1", "suffix2", "phone", "fax", "mobile", "email", "url");
        
        final String template = createTemplate(words);
        final String expectedOutput = createExpectedOutput(words);
        
        // WHEN
        String actualOutput = subst.apply(template);
        
        // THEN
        assertEquals(expectedOutput, actualOutput);
    }


    private String createExpectedOutput(final List<String> words) {
        final StringBuilder sb2 = new StringBuilder();
        for (String word : words) {
            String expectedValue = word;
            if(word.equals("phone") ){
                expectedValue = "fon";
            }
            if(word.equals("email") ){
                expectedValue = "mail";
            }
            if(word.equals("mobile") ){
                expectedValue = "mobil";
            }
            sb2.append(word+": '"+expectedValue+"_a_e1'\n");
        }
        
        final String expectedOutput = sb2.toString();
        return expectedOutput;
    }


    private String createTemplate(final List<String> words) {
        final StringBuilder sb1 = new StringBuilder();
        for (String word : words) {
            sb1.append(word+": '&lt;"+word+"&gt;'\n");
        }
        final String template = sb1.toString();
        return template;
    }

    
    private Person createPerson() throws IllegalAccessException {
        
        final Person person = new Person();
        final Field[] fields = Person.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if(field.getType().isAssignableFrom(String.class)){
                //for now, all the fields that we care about *are* strings.
                //We put the field name in there, just to have some values we can
                //examine.
                field.set(person,field.getName());
            }
        }
        return person;
    }

}