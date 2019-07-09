package de.tarent.aa.veraweb.utils;
/**
 * Types for an optional field.
 *
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public enum OptionalFieldTypeFacade {
    inputfield("Eingabefeld", 1),
    simple_combobox("Einfaches Auswahlfeld", 2),
    multiple_combobox("Mehrfaches Auswahlfeld", 3);

    private String text;

    private Integer value;

    /**
     * TODO
     *
     * @param text Field type content
     */
    OptionalFieldTypeFacade(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    @Override
    public String toString() {
        return text;
    }

    public String getText() {
        return text;
    }

    public Integer getValue() {
        return value;
    }
}
