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
 */
package de.tarent.aa.veraweb.worker;

import de.tarent.octopus.server.OctopusContext;

/**
 * Einfacher Worker, der User-Aktionen verwaltet und dazu den
 * Parameter 'action' aus dem Request in der Session verwaltet
 * und in den Content stellt.
 * 
 * @author christoph
 */
public class ActionWorker {
    /** Octopus-Eingabe-Parameter für {@link #load(OctopusContext, String)} */
	public static final String[] INPUT_load = { "action" };
    /** Octopus-Eingabepflicht-Parameter für {@link #load(OctopusContext, String)} */
	public static final boolean[] MANDATORY_load = { false };
    /** Octopus-Ausgabe-Parameter für {@link #load(OctopusContext, String)} */
	public static final String OUTPUT_load = "action";
    /**
     * Diese Worker-Aktion ermittelt aus Request und Session die aktuelle
     * Aktion, legt sie in der Session ab und gibt sie zurück.<br>
     * TODO: action Parameter Hack entfernen, siehe eventDetail.vm,
     *       dort kann man sonst die form-action nicht setzten!!
     * 
     * @param cntx Octopus-Kontext
     * @param action optionaler Parameter "action"
     * @return aktuelle Aktion
     */
	public String load(OctopusContext cntx, String action) {
		String a = cntx.requestAsString("a");
		if (a != null && a.length() != 0)
			action = a;
		if (action == null)
			action = cntx.sessionAsString("action");
		if (action == null)
			action = "";
		cntx.setSession("action", action);
		return action;
	}

    /** Octopus-Eingabe-Parameter für {@link #remove(OctopusContext, String)} */
	public static final String[] INPUT_remove = { "action" };
    /** Octopus-Eingabepflicht-Parameter für {@link #remove(OctopusContext, String)} */
	public static final boolean[] MANDATORY_remove = { false };
    /** Octopus-Ausgabe-Parameter für {@link #remove(OctopusContext, String)} */
	public static final String OUTPUT_remove = "action";
    /**
     * Diese Methode löscht die aktuelle Aktion und ersetzt sie gegebenenfalls
     * durch die übergebene. 
     * 
     * @param cntx Octopus-Kontext
     * @param action optionaler Parameter "action"
     * @return aktuelle Aktion
     */
	public String remove(OctopusContext cntx, String action) {
		cntx.setSession("action", action);
		return action;
	}
}
