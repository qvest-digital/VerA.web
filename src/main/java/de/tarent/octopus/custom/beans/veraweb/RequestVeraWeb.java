/* $Id: RequestVeraWeb.java,v 1.1 2007/06/20 11:56:52 christoph Exp $ */
package de.tarent.octopus.custom.beans.veraweb;

import de.tarent.octopus.custom.beans.BeanFactory;
import de.tarent.octopus.custom.beans.Request;
import de.tarent.octopus.server.OctopusContext;

/**
 * Konkrete {@link BeanFactory}, die Beans aus den Request-Parametern
 * im Kontext des Octopus-Moduls veraweb ausliest. 
 * 
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
public class RequestVeraWeb extends Request {
	//
    // Konstruktor
    //
    /**
     * Dieser Konstruktor initialisiert die {@link Request}-{@link BeanFactory}
     * mit dem übergebenen {@link OctopusContext} und dem VerA.web-Bean-Package
     * {@link #BEANPACKAGE "de.tarent.aa.veraweb.beans"}.
     */
	public RequestVeraWeb(OctopusContext cntx) {
		super(cntx, BEANPACKAGE);
	}

    //
    // Konstanten
    //
    /** VerA.web-Bean-Package */
    public static final String BEANPACKAGE = "de.tarent.aa.veraweb.beans";
}
