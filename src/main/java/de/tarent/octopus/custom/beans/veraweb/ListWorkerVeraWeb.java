/* $Id: ListWorkerVeraWeb.java,v 1.1 2007/06/20 11:56:52 christoph Exp $ */
package de.tarent.octopus.custom.beans.veraweb;

import de.tarent.octopus.custom.beans.BeanListWorker;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.Request;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Klasse konkretisiert den abstrakten Basis-Worker {@link BeanListWorker}
 * auf die Benutzung der VerA.web-spezifischen Implementierungen von
 * {@link Database} und {@link Request} hin.
 */
public class ListWorkerVeraWeb extends BeanListWorker {
    //
    // Konstruktoren
    //
    /**
     * Dieser Konstruktor gibt den Namen der zugrunde liegenden Bean weiter.
     */
    protected ListWorkerVeraWeb(String beanName) {
        super(beanName);
    }

    //
    // Oberklasse BeanListWorker
    //
	/**
     * Diese Methode liefert eine {@link DatabaseVeraWeb}-Instanz
     * 
	 * @see BeanListWorker#getDatabase(OctopusContext)
	 */
	protected Database getDatabase(OctopusContext cntx) {
		return new DatabaseVeraWeb(cntx);
	}

	/**
     * Diese Methode liefert eine {@link RequestVeraWeb}-Instanz.
	 * @see BeanListWorker#getRequest(OctopusContext)
	 */
	protected Request getRequest(OctopusContext cntx) {
		return new RequestVeraWeb(cntx);
	}
}
