package de.tarent.octopus.content;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
