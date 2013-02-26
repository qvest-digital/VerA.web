/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.worker;

import de.tarent.aa.veraweb.beans.Grants;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;

/**
 * Setzt Grant Bohne in den Content.
 * Wodr�ber dann in Velocity entschieden wird
 * welche Men�punkte ein/ausgeblendet werden sollen.
 */
public class SecurityWorker {
    //
    // Octopus-Aktionen
    //
    /** Octopus-Input-Parameter von {@link #load(OctopusContext)} */
	public static final String INPUT_load[] = {};
    /** Octopus-Output-Parameter von {@link #load(OctopusContext)} */
	public static final String OUTPUT_load = "grants";
    /**
     * Diese Octopus-Aktion liefert ein {@link Grants}-Objekt, das die globale
     * Benutzerrolle darstellt 
     * 
     * @param cntx der aktuelle {@link OctopusContext}.
     * @return {@link Grants} des eingeloggten Benutzers
     */
	public Grants load(OctopusContext cntx) {
		return getGrants(cntx);
	}
    
    //
    // gesch�tzte Hilfsmethoden
    //
    /**
     * Diese Methode holt die Grants aus der Session oder der pers�nlichen
     * Konfiguration.
     * 
     * @param cntx Octopus-Kontext
     * @return {@link Grants}-Instanz zum angemeldeten Benutzer
     */
	protected Grants getGrants(OctopusContext cntx) {
		Grants grants = null;
        PersonalConfig config = cntx.personalConfig();
        if (config instanceof PersonalConfigAA) {
            grants = ((PersonalConfigAA)config).getGrants();
            cntx.setSession("grants", grants);
        }
		return grants;
	}
}
