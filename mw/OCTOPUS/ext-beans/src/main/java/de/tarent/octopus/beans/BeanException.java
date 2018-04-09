/*
 * tarent-octopus bean extension,
 * an opensource webservice and webapplication framework (bean extension)
 * Copyright (c) 2006-2007 tarent GmbH
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
 * interest in the program 'tarent-octopus bean extension'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: BeanException.java,v 1.3 2007/06/11 13:24:36 christoph Exp $
 * Created on 08.02.2005
 */
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
    /** Serialisierungs-Versions-UID */
    private static final long serialVersionUID = 3978986574434481456L;
}
