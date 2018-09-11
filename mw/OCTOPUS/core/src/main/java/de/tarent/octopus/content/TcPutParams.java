package de.tarent.octopus.content;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2018 Dominik George (d.george@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.resource.Resources;

import java.util.HashMap;
import java.util.Map;

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
        if (commonConfig.getDefaultModuleName() != null) {
            paths.put("defaultStaticWeb",
              commonConfig.getRelativeWebRootPath(commonConfig.getDefaultModuleName()));
        }
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

        TcOperationDefinition putMinimal = port.addOperation("putMinimal",
          "Setzen weniger notwendiger Felder.");

        putMinimal.setInputMessage();
        putMinimal
          .setOutputMessage()
          .addPart("url", TcMessageDefinition.TYPE_SCALAR, "Url des Systems mit Sessioninformationen.")
          .addPart("jsessionid", TcMessageDefinition.TYPE_SCALAR, "Die Session Id.")
          .addPart("paths", TcMessageDefinition.TYPE_STRUCT, "Pfade z.B. zu den Templateverzeichnissen.");

        TcOperationDefinition putAll = port.addOperation("putAll",
          "Setzen aller Felder des TcRequest Objektes.");

        putAll.setInputMessage();
        putAll.setOutputMessage().addPart(CONTENT_PREFIX, TcMessageDefinition.TYPE_STRUCT,
          "Parameter des Request.");

        return port;
    }

    /**
     * Diese Methode liefert einen Versionseintrag.
     *
     * @return Version des Workers.
     * @see de.tarent.octopus.content.TcContentWorker#getVersion()
     */
    public String getVersion() {
        return Resources.getInstance().get("OCTOPUS_CORE_VERSION");
    }
}
