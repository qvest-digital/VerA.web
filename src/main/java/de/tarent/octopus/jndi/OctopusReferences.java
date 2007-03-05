/*
 * $Id: OctopusReferences.java,v 1.2 2007/03/05 10:53:37 christoph Exp $
 * 
 * Created on 14.06.2005
 */
package de.tarent.octopus.jndi;

import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Schnittstelle dient der abstrahierten Abfrage Octopus-spezifischer
 * Daten, zum Beispiel via JNDI mit der Klasse {@link OctopusJndiFactory}.
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
