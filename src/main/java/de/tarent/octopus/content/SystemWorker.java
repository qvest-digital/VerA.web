/* $Id: SystemWorker.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
 * 
 * Created on 25.06.2004
 * 
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
 * by Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */
package de.tarent.octopus.content;

import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.util.CVS;

/**
 * Dieser Worker stellt einige Systemfunktionen zur Verfügung, etwa das Neuladen eines Moduls,
 * was dem Neueinlesen der Konfiguration entspricht. 
 * 
 * @author mikel
 */
public class SystemWorker implements TcContentWorker {
    //
    // Schnittstelle TcContentWorker
    //
    /**
     * Diese Methode wird nach Erzeugung des Workers aufgerufen, so dass dieser
     * sich im Kontext seines Moduls konfigurieren kann.
     * 
     * @param config Modulkonfiguration.
     * @see de.tarent.octopus.content.TcContentWorker#init(de.tarent.octopus.config.TcModuleConfig)
     */
    public void init(TcModuleConfig config) {
    }

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
     * @see de.tarent.octopus.content.TcContentWorker#doAction(de.tarent.octopus.config.TcConfig, java.lang.String, de.tarent.octopus.request.TcRequest, de.tarent.octopus.content.TcContent)
     */
    public String doAction(TcConfig tcConfig, String actionName, TcRequest tcRequest, TcContent tcContent)
            throws TcContentProzessException {
        // ACTION_RELOAD_MODULE
        if (ACTION_RELOAD_MODULE.equals(actionName)) {
            String moduleName = tcConfig.getModuleConfig().getName();
            tcConfig.getCommonConfig().deregisterModule(moduleName);
            
            TcModuleConfig moduleConfig = tcConfig.getCommonConfig().getModuleConfig(moduleName);
            return moduleConfig != null ? RESULT_ok : RESULT_error;
        } else {
            throw new TcContentProzessException(
                    "Nicht unterstützte Aktion im SystemWorker: " + actionName);
        }
    }

    /**
     * Liefert eine Beschreibgung der Actions und deren Eingabeparameter,
     * die von diesem Worker bereit gestellt werden.
     * 
     * @return Eine Abstrakte Beschreibung der Methoden und Parameter 
     * @see de.tarent.octopus.content.TcContentWorker#getWorkerDefinition()
     */
    public TcPortDefinition getWorkerDefinition() {
        TcPortDefinition port =
            new TcPortDefinition(
                SystemWorker.class.getName(),
                "Worker für Systemfunktionen");

        TcOperationDefinition operation = port.addOperation(ACTION_RELOAD_MODULE, "Neuladen des aufrufenden Moduls");
        operation.setInputMessage();
        operation.setOutputMessage();
//            .addPart("url", TcMessageDefinition.TYPE_SCALAR, "Url des Systems mit Sessioninformationen.")
//            .addPart("jsessionid", TcMessageDefinition.TYPE_SCALAR, "Die Session Id.")
//            .addPart("paths", TcMessageDefinition.TYPE_STRUCT, "Pfade z.B. zu den Templateverzeichnissen.");

        return port;
    }

    /**
     * Diese Methode liefert einen Versionseintrag.
     * 
     * @return Version des Workers.
     * @see de.tarent.octopus.content.TcContentWorker#getVersion()
     */
    public String getVersion() {
        return CVS.getContent("$Revision: 1.1.1.1 $") + " (" + CVS.getContent("$Date: 2005/11/21 13:33:37 $") + ')';
    }

    //
    // Konstanten
    //
    final static String ACTION_RELOAD_MODULE = "reloadModule";
}
