package org.evolvis.veraweb.onlinereg.mail;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mweier on 21.01.16.
 */
public class EmailValidatorTest {

    @Test
    public void testIsValidEmailAddress() {
        assertTrue(EmailValidator.isValidEmailAddress("valid@email.de"));
        assertTrue(EmailValidator.isValidEmailAddress("valid@e.mail.de"));
        assertTrue(EmailValidator.isValidEmailAddress("valid@127.0.0.1"));
        assertTrue(EmailValidator.isValidEmailAddress("valid@[127.0.0.1]"));
        assertTrue(EmailValidator.isValidEmailAddress("valid@[IPv6:2001:db8::1]"));
        assertTrue(EmailValidator.isValidEmailAddress("valid!#$%&'*+-/=?^_`{|}~@email.de"));


        assertFalse(EmailValidator.isValidEmailAddress("not-valid<>@email.de"));
        assertFalse(EmailValidator.isValidEmailAddress("not-valid@e@mail.de"));
        assertFalse(EmailValidator.isValidEmailAddress("not-valid@email:de"));
    }
}