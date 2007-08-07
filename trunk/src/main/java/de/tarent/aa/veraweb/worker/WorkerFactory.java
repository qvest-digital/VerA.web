/*
 * $Id: WorkerFactory.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 17.05.2005
 */
package de.tarent.aa.veraweb.worker;

import de.tarent.octopus.content.ContentWorker;
import de.tarent.octopus.content.ContentWorkerFactory;
import de.tarent.octopus.content.ReflectedWorkerWrapper;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.WorkerCreationException;

/**
 * Diese Klasse gibt Instanzen anderer Worker und direktem Arbeiten mit
 * diesen zurück. Dadurch verlaggert sich die Verwendung von statischen
 * Methoden ausschließlich in diese Klasse.
 * 
 * @author Christoph
 */
public class WorkerFactory {
	private WorkerFactory() {
	}

    /**
     * Diese Methode liefert den Worker namens "PersonDetailWorker".
     * 
     * @param cntx Octopus-Kontext
     * @return {@link PersonDetailWorker}-Instanz des Moduls
     */
	static public PersonDetailWorker getPersonDetailWorker(OctopusContext cntx) {
		return (PersonDetailWorker)getWorker(cntx, "PersonDetailWorker");
	}

    /**
     * Diese Methode liefert den Worker namens "PersonDoctypeWorker".
     * 
     * @param cntx Octopus-Kontext
     * @return {@link PersonDetailWorker}-Instanz des Moduls
     */
	static public PersonDoctypeWorker getPersonDoctypeWorker(OctopusContext cntx) {
		return (PersonDoctypeWorker)getWorker(cntx, "PersonDoctypeWorker");
	}

    /**
     * Diese Methode liefert den Worker namens "PersonListWorker".
     * 
     * @param cntx Octopus-Kontext
     * @return {@link PersonListWorker}-Instanz des Moduls
     */
	static public PersonListWorker getPersonListWorker(OctopusContext cntx) {
		return (PersonListWorker)getWorker(cntx, "PersonListWorker");
	}

    /**
     * Diese Methode liefert den Worker namens "PersonExportWorker".
     * 
     * @param cntx Octopus-Kontext
     * @return {@link PersonExportWorker}-Instanz des Moduls
     */
    static public PersonExportWorker getPersonExportWorker(OctopusContext cntx) {
        return (PersonExportWorker)getWorker(cntx, "PersonExportWorker");
    }

    /**
     * Diese Methode liefert den Worker namens "ImportPersonsWorker".
     * 
     * @param cntx Octopus-Kontext
     * @return {@link ImportPersonsWorker}-Instanz des Moduls
     */
    static public ImportPersonsWorker getImportPersonsWorker(OctopusContext cntx) {
        return (ImportPersonsWorker)getWorker(cntx, "ImportPersonsWorker");
    }

    /**
     * Diese Methode liefert den Worker namens "DataExchangeWorker".
     * 
     * @param cntx Octopus-Kontext
     * @return {@link DataExchangeWorker}-Instanz des Moduls
     */
    static public DataExchangeWorker getDataExchangeWorker(OctopusContext cntx) {
        return (DataExchangeWorker)getWorker(cntx, "DataExchangeWorker");
    }

	/**
	 * Diese Methode liefert den Worker namens "ConfigWorker".
	 * 
	 * @param cntx Octopus-Kontext
	 * @return {@link ConfigWorker}-Instanz des Moduls
	 */
	static public ConfigWorker getConfigWorker(OctopusContext cntx) {
		return (ConfigWorker)getWorker(cntx, "ConfigWorker");
	}

	/**
	 * Diese Methode liefert den Worker namens "VerifyWorker".
	 * 
	 * @param cntx Octopus-Kontext
	 * @return {@link VerifyWorker}-Instanz des Moduls
	 */
	static public VerifyWorker getVerifyWorker(OctopusContext cntx) {
		return (VerifyWorker)getWorker(cntx, "VerifyWorker");
	}

	/**
	 * Diese Methode liefert den Worker namens "GuestListWorker".
	 * 
	 * @param cntx Octopus-Kontext
	 * @return {@link GuestListWorker}-Instanz des Moduls
	 */
	static public GuestListWorker getGuestListWorker(OctopusContext cntx) {
		return (GuestListWorker)getWorker(cntx, "GuestListWorker");
	}

	/**
	 * Diese Methode liefert den Worker namens "GuestWorker".
	 * 
	 * @param cntx Octopus-Kontext
	 * @return {@link GuestWorker}-Instanz des Moduls
	 */
	static public GuestWorker getGuestWorker(OctopusContext cntx) {
		return (GuestWorker)getWorker(cntx, "GuestWorker");
	}

	/**
	 * Diese Methode liefert den Worker namens "LocationWorker".
	 * 
	 * @param cntx Octopus-Kontext
	 * @return {@link LocationWorker}-Instanz des Moduls
	 */
	static public LocationWorker getLocationWorker(OctopusContext cntx) {
		return (LocationWorker)getWorker(cntx, "LocationWorker");
	}

    /**
     * Diese statische Methode holt mittels des Octopus-Kontexts einen Worker. 
     * 
     * @param cntx Octopus-Kontext
     * @param name Worker-Name
     * @return Worker-Instanz
     */
	static private Object getWorker(OctopusContext cntx, String name) {
		try {
            ContentWorker worker = ContentWorkerFactory.getContentWorker(
                    cntx.moduleConfig(), name,
                    cntx.getRequestObject().getRequestID());
            return (worker instanceof ReflectedWorkerWrapper) ?
                 ((ReflectedWorkerWrapper)worker).getWorkerDelegate() : worker;
		} catch (ClassCastException e) {
		} catch (WorkerCreationException e) {
		}
        return null;
	}
}
