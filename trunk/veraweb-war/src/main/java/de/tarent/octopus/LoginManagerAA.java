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
 * Created on 29.07.2005
 */
package de.tarent.octopus;

import java.util.Set;

import de.tarent.aa.veraweb.beans.Proxy;
import de.tarent.octopus.security.TcSecurityException;
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
     * @throws TcSecurityException Wenn keine authentisierte persönliche Konfiguration
     *  vorliegt oder schon als Vertreter agiert wird.
     */
    public void setProxy(OctopusContext octx, Proxy proxyDescription) throws TcSecurityException;

    /**
     * Diese Methode liefert eine Auflistung verfügbarer AA-Rollen, aus denen
     * VerA.web-Benutzer ausgewählt werden können. 
     * 
     * @return Liste verfügbarer AA-Rollen.
     */
    public Set getAARoles() throws TcSecurityException;
}