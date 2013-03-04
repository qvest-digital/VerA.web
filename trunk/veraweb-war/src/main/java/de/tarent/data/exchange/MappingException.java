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
     * Dieser Konstruktor erh�lt einen Fehlertext.
     * 
     * @param message Fehlertext
     */
    public MappingException(String message) {
        super(message);
    }

    /**
     * Dieser Konstruktor erh�lt einen geschachtelten {@link Throwable}. 
     * 
     * @param cause geschachtelter {@link Throwable}
     */
    public MappingException(Throwable cause) {
        super(cause);
    }

    /**
     * Dieser Konstruktor erh�lt einen Fehlertext und einen geschachtelten {@link Throwable}.
     * 
     * @param message Fehlertext
     * @param cause geschachtelter {@link Throwable}
     */
    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    //
    // gesch�tzt Member
    //
    /** Serialisierungs-ID (um Eclipse gl�cklich zu machen) */
    private static final long serialVersionUID = 974345013719043419L;
}
