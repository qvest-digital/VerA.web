package de.tarent.aa.veraweb.utils;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.TransactionContext;

import java.io.IOException;

/**
 * Diese Schnittstelle ist für jede grundsätzliche Importvariante umzusetzen.
 *
 * @author mikel
 */
public interface Importer {
    /**
     * Diese Methode führt einen Import aus. Hierbei werden alle erkannten zu
     * importierenden Personendatensätze und Zusätze nacheinander dem übergebenen
     * {@link ImportDigester} übergeben.
     *
     * @param digester der {@link ImportDigester}, der die Datensätze weiter
     *  verarbeitet.
     * @param transactionContext FIXME
     * @throws IOException FIXME
     * @throws BeanException FIXME
     */
    void importAll(ImportDigester digester, TransactionContext transactionContext) throws IOException, BeanException;
}
