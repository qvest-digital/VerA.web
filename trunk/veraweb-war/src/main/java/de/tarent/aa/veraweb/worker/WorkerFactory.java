/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.worker;

import de.tarent.octopus.content.TcContentWorker;
import de.tarent.octopus.content.TcContentWorkerFactory;
import de.tarent.octopus.content.TcReflectedWorkerWrapper;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.WorkerCreationException;

/**
 * Diese Klasse gibt Instanzen anderer Worker und direktem Arbeiten mit
 * diesen zur�ck. Dadurch verlaggert sich die Verwendung von statischen
 * Methoden ausschlie�lich in diese Klasse.
 * 
 * @author Christoph
 */
public class WorkerFactory {
	private WorkerFactory() {
	}
	
    /**
     * Diese Methode liefert den Worker namens "PersonCategorieWorker".
     * 
     * @param cntx Octopus-Kontext
     * @return {@link PersonCategorieWorker}-Instanz des Moduls
     */
	static public PersonCategorieWorker getPersonCategorieWorker(OctopusContext cntx) {
		return (PersonCategorieWorker)getWorker(cntx, "PersonCategorieWorker");
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
     * Diese Methode liefert den Worker namens "PersonDuplicateSearchWorker".
     * 
     * @param cntx Octopus-Kontext
     * @return {@link PersonDuplicateSearchWorker}-Instanz des Moduls
     */
	static public PersonDuplicateSearchWorker getPersonDuplicateSearchWorker(OctopusContext cntx) {
		return (PersonDuplicateSearchWorker)getWorker(cntx, "PersonDuplicateSearchWorker");
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
	 * Gets a new instance of the {@link GuestDetailWorker}.
	 *  
	 * @param cntx the current octopus context
	 * @return instance of {@link GuestDetailWorker}
	 * @since 1.2.0
	 */
	static public GuestDetailWorker getGuestDetailWorker( OctopusContext cntx )
	{
		return ( GuestDetailWorker ) getWorker( cntx, "GuestDetailWorker" );
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
	 * Diese Methode liefert den Worker namens "LocationWorker".
	 * 
	 * @param cntx Octopus-Kontext
	 * @return {@link LocationWorker}-Instanz des Moduls
	 */
	static public WorkAreaWorker getWorkAreaWorker( OctopusContext cntx )
	{
		return ( WorkAreaWorker ) getWorker( cntx, "WorkAreaWorker" );
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
            TcContentWorker worker = TcContentWorkerFactory.getContentWorker(
                    cntx.moduleConfig(), name,
                    cntx.getRequestObject().getRequestID());
            return (worker instanceof TcReflectedWorkerWrapper) ?
                 ((TcReflectedWorkerWrapper)worker).getWorkerDelegate() : worker;
		} catch (WorkerCreationException e) {
		}
        return null;
	}
}
