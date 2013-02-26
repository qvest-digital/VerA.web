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

import java.util.Locale;
import java.util.ResourceBundle;

import de.tarent.aa.veraweb.utils.LocaleMessage;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Octopus-Worker-Klasse stellt Helferobjekte in den Kontext, die Mitteilungstexte
 * und Build-Informationen liefern.
 * 
 * @author Christoph
 * @author mikel
 */
public class MessageWorker {
    //
    // Octopus-Aktionen
    //
    /** Octopus-Eingabeparameter f�r die Aktion {@link #init()} */
	public final static String INPUT_init[] = {};
    /**
     * Diese Octopus-Aktion initialisiert die Member dieses Workers.
     */
	public void init() {
		message = new LocaleMessage(Locale.getDefault());
        properties = ResourceBundle.getBundle("de.tarent.aa.veraweb.veraweb", Locale.getDefault());
	}

    /** Octopus-Eingabeparameter f�r die Aktion {@link #load(OctopusContext)} */
	public final static String INPUT_load[] = {};
    /**
     * Diese Octopus-Aktion legt geb�ndelte lokalisierte Mitteilungen als "message"
     * und geb�ndelte Versions- und Buildinformationen als "properties" in den
     * Octopus-Content.
     * 
     * @param cntx Octopus-Kontext
     */
	public void load(OctopusContext cntx) {
		cntx.setContent("message", message);
        cntx.setContent("properties", properties);
	}

	//
    // Member-Variablen
    //
    LocaleMessage message;
    ResourceBundle properties;
}
