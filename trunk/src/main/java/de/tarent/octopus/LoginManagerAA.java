/*
 * $Id: LoginManagerAA.java,v 1.1 2007/06/20 11:56:53 christoph Exp $
 * 
 * Created on 29.07.2005
 */
package de.tarent.octopus;

import java.util.Set;

import de.tarent.aa.veraweb.beans.Proxy;
import de.tarent.octopus.security.TcSecurityException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Schnittstelle stellt die Methoden dar, die im Kontext des VerA.web-Projekts
 * vom LoginManager �ber die vom Octopus geforderten hinaus ben�tigt werden. 
 * 
 * @author mikel
 */
public interface LoginManagerAA {
    /**
     * Diese Methode �ndert die pers�nliche Konfiguration so ab, dass sie
     * in Vertretung der angegebenen Rolle handelt.
     * 
     * @param octx anzupassender Octopus-Kontext der Sitzung des Vertreters
     * @param proxyDescription Beschreibungs-Bean der Vertretung
     * @throws TcSecurityException Wenn keine authentisierte pers�nliche Konfiguration
     *  vorliegt oder schon als Vertreter agiert wird.
     */
    public void setProxy(OctopusContext octx, Proxy proxyDescription) throws TcSecurityException;

    /**
     * Diese Methode liefert eine Auflistung verf�gbarer AA-Rollen, aus denen
     * VerA.web-Benutzer ausgew�hlt werden k�nnen. 
     * 
     * @return Liste verf�gbarer AA-Rollen.
     */
    public Set getAARoles() throws TcSecurityException;
}