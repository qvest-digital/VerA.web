/* $Id: TcContentWorker.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.content;

import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.request.TcRequest;

/** 
 * Jeglicher Zugriff auf Daten erfolgt über diese TcContentWorker.
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public interface TcContentWorker {
    /**
     * Diese Methode wird nach Erzeugung des Workers aufgerufen, so dass dieser
     * sich im Kontext seines Moduls konfigurieren kann.
     * 
     * @param config Modulkonfiguration.
     */
    public void init(TcModuleConfig config);

    /**
     * Abarbeiten einer Action mit diesem ContentWorker
     * Die Ergebnisse werden in dem tcContent-Kontainer abgelegt.
     * Ein ContentWorker kann für mehrere Actions zuständig sein.
     *
     * @param tcConfig Konfiguration
     * @param actionName Name der Aktion, die von diesem Worker ausgeführt werden soll.
     * @param tcRequest Die Anfragedaten
     * @param tcContent Der Content-Kontainer, in dem die Daten abgelegt werden können.
     * @return String mit einem Statuscode z.B. ok oder error
     */
    public String doAction(TcConfig tcConfig, String actionName, TcRequest tcRequest, TcContent tcContent)
        throws TcContentProzessException;

    /**
     * Liefert eine Beschreibgung der Actions und deren Eingabeparameter,
     * die von diesem Worker bereit gestellt werden.
     * 
     * @return Eine Abstrakte Beschreibung der Methoden und Parameter 
     */
    public TcPortDefinition getWorkerDefinition();

    /**
     * Diese Methode liefert einen Versionseintrag.
     * 
     * @return Version des Workers.
     */
    public String getVersion();

    /**
     * Standard-Ergebnis für den Erfolgsfall.
     */
    public final static String RESULT_ok = "ok";

    /**
     * Standard-Ergebnis für den Fehlerfall.
     */
    public final static String RESULT_error = "error";
}
