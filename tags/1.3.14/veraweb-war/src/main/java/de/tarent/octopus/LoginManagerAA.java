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
