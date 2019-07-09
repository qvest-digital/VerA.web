package de.tarent.octopus;
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
     * @param octx             anzupassender Octopus-Kontext der Sitzung des Vertreters
     * @param proxyDescription Beschreibungs-Bean der Vertretung
     * @throws TcSecurityException Wenn keine authentisierte persönliche Konfiguration
     *                             vorliegt oder schon als Vertreter agiert wird.
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
