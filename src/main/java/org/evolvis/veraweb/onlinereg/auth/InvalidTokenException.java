package org.evolvis.veraweb.onlinereg.auth;

public class InvalidTokenException extends Exception {
    
    private static final long serialVersionUID = 1124072645499948366L;

    public InvalidTokenException() {
        super("The given token is not authentic.");
    }
}
