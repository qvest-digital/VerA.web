/*
 * $Id: ActionWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
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
