package org.evolvis.veraweb.onlinereg.mail;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;

public class EmailValidator {

    /**
     * checks if an String represents a valid e-mail address
     * return true if e-mail address is valid, false if not
     *
     * @param email String with possible e-mail address
     * @return      boolean if e-mail address is valid or not
     */
    public static boolean isValidEmailAddress(String email){

        try {
            new InternetAddress(email).validate();
        } catch (AddressException e) {
            return false;
        }
        return true;
    }
}