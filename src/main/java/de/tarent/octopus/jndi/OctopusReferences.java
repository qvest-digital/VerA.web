/*
 * $Id: OctopusReferences.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
 * 
 * Created on 14.06.2005
 */
package de.tarent.octopus.jndi;

import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Schnittstelle dient der abstrahierten Abfrage Octopus-spezifischer
 * Daten, zum Beispiel via JNDI mit der Klasse {@link OctopusFactory}.
 * 
 * @author mikel
 */
public interface OctopusReferences {
    /**
     * Diese Methode liefert den aktuellen {@link OctopusContext}. 
     * 
     * @return aktueller Octopus-Kontext
     */
    public OctopusContext getContext();
}
