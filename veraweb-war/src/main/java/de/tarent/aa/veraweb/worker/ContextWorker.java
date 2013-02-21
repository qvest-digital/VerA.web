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
 * Created on 07.03.2005
 */
package de.tarent.aa.veraweb.worker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.octopus.content.TcContentProzessException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Klasse stellt einen Octopus-Worker dar, der Anwendungskontexte verwalten kann.
 * Anwendungskontexte bestehen aus einem Task und einer Menge Bezeichner mit zugeordneten
 * Objekten. Solche Kontexte können auf einen Stapel ge-PUSH-t und von ihm ge-POP-t werden.<br>
 * TODO: Fertig umsetzen
 * 
 * @author mikel
 */
public class ContextWorker {
    //
    // Octopus-Aktionen
    //
    /** Eingabe-Parameter für die Octopus-Aktion {@link #pop(OctopusContext, String)} */
    public static final String[] INPUT_pop = {"contexts"};
    /** Eingabe-Parameterzwang für die Octopus-Aktion {@link #pop(OctopusContext, String)} */
	public static final boolean[] MANDATORY_pop = {false};
    /** Ausgabe-Parameter für die Octopus-Aktion {@link #pop(OctopusContext, String)} */
	public static final String OUTPUT_pop = "contexts";
    /**
     * Diese Octopus-Action holt den obersten Kontext aus der Serialisierung eines
     * Kontext-Stacks und setzt passend Werte im aktuellen Octopus-Content; dies
     * betrifft einerseits die enthaltenen Werte, die unter dem assoziierten Bezeichner
     * gesetzt werden, und andererseits den Tasknamen, der unter dem Bezeichner
     * "context" dort abgelegt wird, so dass ein im laufenden Task nachfolgender
     * Aufruf <code>&lt;doTask name="{context}"/&gt;</code> in den Kontext-Pfad
     * springt.
     * 
     * @param oc aktueller Octopus-Kontext
     * @param contexts Serialisierung eines Kontext-Stacks
     * @return Serialisierung des Kontext-Stacks ohne den bisherig obersten Kontext
     * @throws TcContentProzessException
     */
	public String pop(OctopusContext oc, String contexts) {
	    StringBuffer contextsBuffer = new StringBuffer(contexts == null ? "" : contexts);
	    Context context = Context.pop(contextsBuffer);
	    if (context != null)
	        context.set(oc);
	    return contextsBuffer.toString();
	}
	
    /** Eingabe-Parameter für die Octopus-Aktion {@link #push(OctopusContext, String, List)} */
    public static final String[] INPUT_push = {"contexts", "push"};
    /** Eingabe-Parameterzwang für die Octopus-Aktion {@link #push(OctopusContext, String, List)} */
	public static final boolean[] MANDATORY_push = {false, false};
    /** Ausgabe-Parameter für die Octopus-Aktion {@link #push(OctopusContext, String, List)} */
	public static final String OUTPUT_push = "contexts";
    /**
     * Diese Octopus-Aktion legt einen neuen Kontext-Eintrag auf den übergebenen
     * serialisierten Kontext-Stack. In diesem befinden sich der übergebene Taskname
     * und die aktuellen Werte der Octopus-Content-Variablen unter den übergebenen
     * Bezeichnern. 
     * 
     * @param oc Octopus-Kontext
     * @param contexts Serialisierung eines Kontext-Stacks
     * @param data Liste, deren erster Eintrag als Taskname und deren weitere Einträge
     *  als Bezeichner von Octopus-Content-Variablen interpretiert werden, deren Werte
     *  als Teil des Kontexts gesichert werden sollen.
     * @return Serialisierung des Kontext-Stacks erweitert um den übergebenen Kontext.
     * @throws TcContentProzessException
     */
	public String push(OctopusContext oc, String contexts, List data) {
	    if (data == null || data.isEmpty())
	        return contexts;
	    StringBuffer contextsBuffer = new StringBuffer(contexts == null ? "" : contexts);
	    Context context = new Context(oc, data);
	    context.push(contextsBuffer);
	    return contextsBuffer.toString();
	}
	
	//
	// interne Methoden
	//
	
	//
	// interne Klassen
	//
    /**
     * Diese Klasse stellt einen Kontext mit Tasknamen und Sammlung von Bezeichnern
     * mit assoziierten Werten dar.
     */
	public static class Context {
        //
        // Konstruktoren
        //
        /**
         * Dieser Konstruktor führt keine weiteren Initialisierungen durch; ein hiermit
         * erstellter {@link ContextWorker.Context Kontext} muss noch befüllt werden,
         * zum Beispiel mittels eines {@link #pop(StringBuffer)}-Aufrufs.
         */
	    public Context() {
	    }
        /**
         * Dieser Konstruktor legt den Kontext-Task fest, speichert jedoch keine Werte
         * aus dem Octopus-Content. 
         * 
         * @param context Name des Kontext-Tasks
         */
        public Context(String context) {
            this();
            this.context = context;
        }
        /**
         * Dieser Konstruktor legt Kontext-Task und -Werte fest. 
         * 
         * @param oc Octopus-Kontext, aus dem Octopus-Content-Werte gelesen werden sollen.
         * @param data Liste, deren erster Eintrag als Taskname und deren weitere Einträge
         *  als Bezeichner von Octopus-Content-Variablen interpretiert werden, deren Werte
         *  in den Kontext übernommen werden sollen.
         */
	    public Context(OctopusContext oc, List data) {
	        this();
	        if (data != null && data.size() > 0) {
	            Iterator it = data.iterator();
	            context = safeToString(it.next());
	            if (it.hasNext()) {
	                values = new HashMap();
	                do {
	                    String key = safeToString(it.next());
	                    if (key != null)
	                        values.put(key, oc.contentAsObject(key));
	                } while (it.hasNext());
	            }
	        }
	    }
	    //
        // öffentliche Methoden
        //
        /** Name des Kontext-Tasks  */
        public String getContextTask() {
            return context;
        }
        /**
         * Diese Methode setzt im Content des übergebenen Octopus-Kontexts diesen
         * Kontext ein; dies betrifft einerseits die enthaltenen Werte, die unter
         * dem assoziierten Bezeichner gesetzt werden, und andererseits den Tasknamen,
         * der unter dem Bezeichner "context" dort abgelegt wird, so dass ein im
         * laufenden Task nachfolgender Aufruf <code>&lt;doTask name="{context}"/&gt;</code>
         * in den Kontext-Pfad springt.<br>
         * TODO: Setzen der Variablen 
         * 
         * @param oc Octopus-Kontext, in dem dieser {@link ContextWorker.Context Kontext}
         *  gesetzt werden soll
         */
	    public void set(OctopusContext oc) {
	        oc.setContent("context", getContextTask());
	    }
	    /**
         * Diese Methode holt den obersten Kontext aus der Serialisierung eines
         * Kontext-Stacks und entfernt diesen aus der Serialisierung.<br>
         * TODO: Werte übernehmen
         * 
         * @param contexts Serialisierung eines Kontext-Stacks, wird um den obersten
         *  Eintrag verringert
         * @return vom Stack geholter {@link ContextWorker.Context Kontext}.
	     */
	    public static Context pop(StringBuffer contexts) {
	        if (contexts == null || contexts.length() == 0)
	            return null;
	        int colon = contexts.lastIndexOf(":");
	        String context;
	        if (colon < 0) {
	            context = contexts.toString();
	            contexts.setLength(0);
	        } else {
	            context = contexts.substring(colon + 1);
	            contexts.setLength(colon);
	        }
	        return new Context(context);
	    }
        /**
         * Diese Methode ergänzt die übergebene Serialisierung eines Kontext-Stacks
         * um diesen {@link ContextWorker.Context Kontext}.<br>
         * TODO: Werte einfügen
         * 
         * @param contexts Serialisierung eines Kontext-Stacks, wird um den diesen
         *  Eintrag erweitert
         */
	    public void push(StringBuffer contexts) {
	        if (contexts == null)
	            return;
	        if (contexts.length() != 0)
	            contexts.append(':');
	        contexts.append(context);
	    }
	    String context = null;
	    Map values = null;
	    final static String safeToString(Object orig) {
	        return orig == null ? null : orig.toString(); 
	    }
	}
}
