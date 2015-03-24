package de.tarent.aa.veraweb.beans;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public enum LinkType {

    FREEVISITORS("freevisitors"), DELEGATION("delegation"), MEDIA("media"), PERSON("person");

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
