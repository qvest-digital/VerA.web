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
package de.tarent.aa.veraweb.beans;

/**
 * Diese Schnittstelle markiert Beans, die Erg�nzungen zu ImportPersons darstellen. 
 * 
 * @author mikel
 */
public interface ImportPersonExtra {
    /**
     * Diese Methode assoziiert diese Importpersonenerg�nzung mit ihrer Importperson;
     * dies bedeutet insbesondere die �bernahme deren ID als Fremdschl�ssel auf sie. 
     * 
     * @param person Importperson, mit der dieses Extra assoziiert werden soll.
     */
    void associateWith(ImportPerson person);
}
