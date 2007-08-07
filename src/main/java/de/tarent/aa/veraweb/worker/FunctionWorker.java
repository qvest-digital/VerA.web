/*
 * $Id: FunctionWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 */
package de.tarent.aa.veraweb.worker;

import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Liste zeigt eine Liste von Funktionen an.
 * 
 * @author Christoph
 * @version $Revision: 1.1 $
 */
public class FunctionWorker extends StammdatenWorker {
    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public FunctionWorker() {
		super("Function");
	}

    //
    // Oberklasse BeanListWorker
    //
	protected Integer getLimit(OctopusContext cntx) {
		return new Integer(5);
	}
}
