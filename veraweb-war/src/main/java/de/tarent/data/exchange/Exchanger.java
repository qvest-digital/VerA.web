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

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Diese Schnittstelle beschreibt Grundfunktionen einer Datenaustauschklasse,
 * also eines {@link de.tarent.aa.veraweb.utils.Exporter Exporters} oder
 * {@link de.tarent.aa.veraweb.utils.Importer Importers}. Diese dienen im
 * Wesentlichen dazu, diesem Informationen zum Datenaustauschformat und zu
 * den beteiligten Datenstr�men zu geben. 
 * 
 * @author mikel
 */
public interface Exchanger {
    //
    // Getter und Setter
    //
    /** Das zu verwendende Austauschformat */
    ExchangeFormat getExchangeFormat();
    /** Das zu verwendende Austauschformat */
    void setExchangeFormat(ExchangeFormat format);
    
    /** Der zu verwendende Eingabedatenstrom */
    InputStream getInputStream();
    /** Der zu verwendende Eingabedatenstrom */
    void setInputStream(InputStream stream);
    
    /** Der zu verwendende Ausgabedatenstrom */
    OutputStream getOutputStream();
    /** Der zu verwendende Ausgabedatenstrom */
    void setOutputStream(OutputStream stream);
}
