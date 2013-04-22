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
package de.tarent.aa.veraweb.utils;

import java.io.IOException;

/**
 * Diese Schnittstelle ist f�r jede grunds�tzliche Importvariante umzusetzen. 
 * 
 * @author mikel
 */
public interface Importer {
    /**
     * Diese Methode f�hrt einen Import aus. Hierbei werden alle erkannten zu
     * importierenden Personendatens�tze und Zus�tze nacheinander dem �bergebenen 
     * {@link ImportDigester} �bergeben.
     * 
     * @param digester der {@link ImportDigester}, der die Datens�tze weiter
     *  verarbeitet.
     * @throws IOException 
     */
    public void importAll(ImportDigester digester) throws IOException;
}
