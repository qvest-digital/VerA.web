package org.evolvis.veraweb.onlinereg.mail;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;

/**
 * Created by mweier on 14.01.16.
 */
public class EmailValidator {

    public boolean isValidEmailAddress(String email){
        boolean isValid = false;

        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            isValid = true;
        } catch (AddressException e) {
            e.printStackTrace();
        }

        return isValid;
    }
}