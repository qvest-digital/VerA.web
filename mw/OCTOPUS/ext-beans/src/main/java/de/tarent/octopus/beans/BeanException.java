package de.tarent.octopus.beans;
/**
 * Exception-Klasse des Bean-Frameworks
 *
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
public class BeanException extends Exception {
    //
    // Konstruktoren
    //

    /**
     * Konstruktor mit Hinweistext
     */
    public BeanException(String msg) {
        super(msg);
    }

    /**
     * Konstruktor mit Hinweistext und innerem <code>Throwable</code>.
     */
    public BeanException(String msg, Throwable e) {
        super(msg, e);
    }

    //
    // geschätzte Variablen
    //
    /**
     * Serialisierungs-Versions-UID
     */
    private static final long serialVersionUID = 3978986574434481456L;
}
