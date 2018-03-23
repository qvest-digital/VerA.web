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
import java.util.HashMap;
import java.util.Map;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.util.CVS;

/**
 * Worker, der die Felder des Requests in den Content schieben kann
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcPutParams implements TcContentWorker {
    public static String CONTENT_PREFIX = "request";

    /**
     * Diese Methode wird nach Erzeugung des Workers aufgerufen, so dass dieser
     * sich im Kontext seines Moduls konfigurieren kann.
     *
     * @param config Modulkonfiguration.
     * @see de.tarent.octopus.content.TcContentWorker#init(de.tarent.octopus.config.TcModuleConfig)
     */
    public void init(TcModuleConfig config) {
    }

    public String doAction(TcConfig tcConfig, String actionName, TcRequest tcRequest, TcContent tcContent)
        throws TcContentProzessException {

        String returnStatus = RESULT_error;

        if ("putMinimal".equals(actionName)) {
            returnStatus = putMinimal(tcConfig, tcRequest, tcContent);
        } else if ("putAll".equals(actionName)) {
            returnStatus = putAll(tcConfig, tcRequest, tcContent);
        } else {
            throw new TcContentProzessException(
                "Nicht unterstützte action im Worker 'TcPutParams': " + actionName);
        }
        return returnStatus;
    }

    public String putMinimal(TcConfig tcConfig, TcRequest tcRequest, TcContent tcContent) {
        TcCommonConfig commonConfig = tcConfig.getCommonConfig();

        tcContent.setField("url", tcRequest.get("encodedUrl"));
        tcContent.setField("jsessionid", tcRequest.get("jsessionid"));

        Map paths = new HashMap();
        paths.put("templatesRelative", "");
        paths.put("staticWeb", tcConfig.getRelativeWebRootPath());
        if (commonConfig.getDefaultModuleName() != null)
            paths.put("defaultStaticWeb", commonConfig.getRelativeWebRootPath(commonConfig.getDefaultModuleName()));
        tcContent.setField("paths", paths);

        return RESULT_ok;
    }

    public String putAll(TcConfig tcConfig, TcRequest tcRequest, TcContent tcContent) {
        tcContent.setField(CONTENT_PREFIX, tcRequest.getRequestParameters());
        return RESULT_ok;
    }

    public TcPortDefinition getWorkerDefinition() {
        TcPortDefinition port =
            new TcPortDefinition(
                "de.tarent.octopus.content.TcPutRequestParams",
                "Worker, der die Felder des TcRequests in den Content schieben kann.");

        TcOperationDefinition putMinimal = port.addOperation("putMinimal", "Setzen weniger notwendiger Felder.");

        putMinimal.setInputMessage();
        putMinimal
            .setOutputMessage()
            .addPart("url", TcMessageDefinition.TYPE_SCALAR, "Url des Systems mit Sessioninformationen.")
            .addPart("jsessionid", TcMessageDefinition.TYPE_SCALAR, "Die Session Id.")
            .addPart("paths", TcMessageDefinition.TYPE_STRUCT, "Pfade z.B. zu den Templateverzeichnissen.");

        TcOperationDefinition putAll = port.addOperation("putAll", "Setzen aller Felder des TcRequest Objektes.");

        putAll.setInputMessage();
        putAll.setOutputMessage().addPart(CONTENT_PREFIX, TcMessageDefinition.TYPE_STRUCT, "Parameter des Request.");

        return port;
    }

    /**
     * Diese Methode liefert einen Versionseintrag.
     *
     * @return Version des Workers.
     * @see de.tarent.octopus.content.TcContentWorker#getVersion()
     */
    public String getVersion() {
        return CVS.getContent("$Revision: 1.3 $") + " (" + CVS.getContent("$Date: 2006/02/23 15:07:57 $") + ')';
    }
}
