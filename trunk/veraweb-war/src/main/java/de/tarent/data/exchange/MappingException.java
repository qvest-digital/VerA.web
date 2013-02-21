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

/*
 * $Id$
 * 
 * Created on 23.08.2005
 */
package de.tarent.data.exchange;

/**
 * Diese Klasse stellt Ausnahmen beim Mapping von Feldern dar.
 * 
 * @author mikel
 */
public class MappingException extends Exception {
    //
    // Konstruktoren
    //
    /**
     * Der leere Konstruktor
     */
    public MappingException() {
        super();
    }

    /**
     * Dieser Konstruktor erhält einen Fehlertext.
     * 
     * @param message Fehlertext
     */
    public MappingException(String message) {
        super(message);
    }

    /**
     * Dieser Konstruktor erhält einen geschachtelten {@link Throwable}. 
     * 
     * @param cause geschachtelter {@link Throwable}
     */
    public MappingException(Throwable cause) {
        super(cause);
    }

    /**
     * Dieser Konstruktor erhält einen Fehlertext und einen geschachtelten {@link Throwable}.
     * 
     * @param message Fehlertext
     * @param cause geschachtelter {@link Throwable}
     */
    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    //
    // geschützt Member
    //
    /** Serialisierungs-ID (um Eclipse glücklich zu machen) */
    private static final long serialVersionUID = 974345013719043419L;
}
