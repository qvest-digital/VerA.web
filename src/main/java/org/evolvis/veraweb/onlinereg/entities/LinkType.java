package org.evolvis.veraweb.onlinereg.entities;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public enum LinkType {

    FREEVISITORS("freevisitors"), DELEGATION("delegation"), MEDIA("media"), PASSWORDRESET("passwordreset");

    private String text;

    private LinkType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public String getText() {
        return text;
    }

}
