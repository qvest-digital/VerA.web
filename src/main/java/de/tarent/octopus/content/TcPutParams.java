/* $Id: TcPutParams.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
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
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.content;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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

    private static Logger logger = Logger.getLogger(TcPutParams.class.getName());
    private TcConfig tcConfig;

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
        return CVS.getContent("$Revision: 1.1.1.1 $") + " (" + CVS.getContent("$Date: 2005/11/21 13:33:37 $") + ')';
    }
}
