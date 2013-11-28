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
import java.io.OutputStream;

/**
 * Diese Schnittstelle erlaubt {@link de.tarent.aa.veraweb.utils.Exporter}-Instanzen,
 * alternative Exportziele anzugeben, zum Beispiel im lokalen Dateisystem des Servers.
 * 
 * @author mikel
 */
public interface AlternativeDestination {
    /**
     * Diese Methode liefert ein alternatives Ziel in Form eines {@link OutputStream}s. 
     * 
     * @return ein {@link OutputStream} oder <code>null</code>.
     * @throws IOException
     */
    public OutputStream getAlternativeOutputStream() throws IOException;

    /**
     * Diese Methode f�hrt einen Rollback auf das alternative Ziel durch,
     * l�scht dabei z.B. neu erstellte Dateien wieder.
     */
    public void rollback();
}
