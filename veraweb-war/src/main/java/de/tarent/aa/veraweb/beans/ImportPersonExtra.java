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
 * Created on 15.09.2005
 */
package de.tarent.aa.veraweb.beans;

/**
 * Diese Schnittstelle markiert Beans, die Ergänzungen zu ImportPersons darstellen. 
 * 
 * @author mikel
 */
public interface ImportPersonExtra {
    /**
     * Diese Methode assoziiert diese Importpersonenergänzung mit ihrer Importperson;
     * dies bedeutet insbesondere die Übernahme deren ID als Fremdschlüssel auf sie. 
     * 
     * @param person Importperson, mit der dieses Extra assoziiert werden soll.
     */
    void associateWith(ImportPerson person);
}
