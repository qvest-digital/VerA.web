/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
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
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/* $Id$ */
package de.tarent.octopus.beans.veraweb;

import de.tarent.octopus.beans.BeanFactory;
import de.tarent.octopus.beans.Request;
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
