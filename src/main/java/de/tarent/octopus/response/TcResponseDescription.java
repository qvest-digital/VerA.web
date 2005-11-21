/* $Id: TcResponseDescription.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.response;

/** Objekt zur Beschreibung der Ausgabeseite für verscheidenen Ausgabeformate.
 *  @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 *  @author <a href="mailto:H.Helwich@tarent.de">Hendrik Helwich</a>, <b>tarent GmbH</b>
 */
public class TcResponseDescription {
    private String descName;
    private String responseType;

    /** Inititalisiert die Antwortbeschreibung
     *  @param descName Name der Response
     *  @param responseType Typ, von der die Response sein soll (z.B. velocityPage oder simplePage)
     */
    public TcResponseDescription(String descName, String responseType) {
        this.descName = descName;
        this.responseType = responseType;
    }

    /** Gibt den Typ zurück, von dem die Ausgabeseite sein soll.
     * Damit kann das Modul für die Ausgabegenerierung bestimmt werden.
     */
    public String getDescName() {
        return descName;
    }

    /** Gibt den Namen zurück, den das Modul für die Ausgabegenerierung benutzt.
     */
    public String getResponseType() {
        return responseType;
    }
}
