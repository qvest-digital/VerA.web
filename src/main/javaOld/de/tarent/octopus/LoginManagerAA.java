package de.tarent.octopus;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
import de.tarent.aa.veraweb.beans.Proxy;
import de.tarent.octopus.security.TcSecurityException;
import de.tarent.octopus.server.OctopusContext;

import java.util.Set;

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
     * @throws TcSecurityException Wenn keine authentisierte persönliche Konfiguration
     *  vorliegt oder schon als Vertreter agiert wird.
     */
    void setProxy(OctopusContext octx, Proxy proxyDescription) throws TcSecurityException;

    /**
     * Diese Methode liefert eine Auflistung verfügbarer AA-Rollen, aus denen
     * VerA.web-Benutzer ausgewählt werden können.
     *
     * @return Liste verfügbarer AA-Rollen.
     */
    Set getAARoles() throws TcSecurityException;
}
