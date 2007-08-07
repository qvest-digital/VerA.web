/*
 * VerA.web,
 * Veranstaltungsmanagment VerA.web
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'VerA.web'
 * Signature of Elmar Geese, 7 August 2007
 * Elmar Geese, CEO tarent GmbH.
 */

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
