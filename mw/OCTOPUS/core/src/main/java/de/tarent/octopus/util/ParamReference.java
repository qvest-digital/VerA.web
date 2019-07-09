package de.tarent.octopus.util;
/**
 * Klasse zum Kapseln eines Params mit einem refvalue
 *
 * ERWEITERUNG: Besorgen des Value aus einem Kontext, in diese Klasse integrieren.
 */
public class ParamReference {
    String refvalue;

    public ParamReference(String refvalue) {
        this.refvalue = refvalue;
    }

    public String getRefvalue() {
        return refvalue;
    }

    public String toString() {
        return "ParamReference: " + getRefvalue();
    }
}
