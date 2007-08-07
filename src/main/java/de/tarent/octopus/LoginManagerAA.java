/*
 * $Id: LoginManagerAA.java,v 1.1 2007/06/20 11:56:53 christoph Exp $
 * 
 * Created on 29.07.2005
 */
package de.tarent.octopus;

import java.util.Set;

import de.tarent.aa.veraweb.beans.Proxy;
import de.tarent.octopus.security.OctopusSecurityException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Schnittstelle stellt die Methoden dar, die im Kontext des VerA.web-Projekts
 * vom LoginManager über die vom Octopus geforderten hinaus benötigt werden. 
 * 
 * @author mikel
 */
public interface LoginManagerAA {
    /**
     * Diese Methode ändert die persönliche Konfiguration so ab, dass sie
     * in Vertretung der angegebenen Rolle handelt.
     * 
     * @param octx anzupassender Octopus-Kontext der Sitzung des Vertreters
     * @param proxyDescription Beschreibungs-Bean der Vertretung
     * @throws OctopusSecurityException Wenn keine authentisierte persönliche Konfiguration
     *  vorliegt oder schon als Vertreter agiert wird.
     */
    public void setProxy(OctopusContext octx, Proxy proxyDescription) throws OctopusSecurityException;

    /**
     * Diese Methode liefert eine Auflistung verfügbarer AA-Rollen, aus denen
     * VerA.web-Benutzer ausgewählt werden können. 
     * 
     * @return Liste verfügbarer AA-Rollen.
     */
    public Set getAARoles() throws OctopusSecurityException;
}