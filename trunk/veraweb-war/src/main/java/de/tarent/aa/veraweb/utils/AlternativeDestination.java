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
 * Created on 08.11.2005
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
     * Diese Methode führt einen Rollback auf das alternative Ziel durch,
     * löscht dabei z.B. neu erstellte Dateien wieder.
     */
    public void rollback();
}
