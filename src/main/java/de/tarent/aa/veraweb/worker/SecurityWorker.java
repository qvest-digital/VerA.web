/* $Id: SecurityWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 *
 * Created on 08.03.2005
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
