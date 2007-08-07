/*
 * $Id: BeanException.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * Created on 08.02.2005
 */
package de.tarent.octopus.custom.beans;

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
    // geschützte Variablen
    //
    /** Serialisierungs-Versions-UID */
    private static final long serialVersionUID = 3978986574434481456L;
}
