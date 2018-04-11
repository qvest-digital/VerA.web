package de.tarent.octopus.request.directcall;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
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

import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.Octopus;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcSession;
import de.tarent.octopus.request.internal.OctopusStarter;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.soap.TcSOAPEngine;
import org.apache.commons.logging.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.*;

/**
 * Ermöglicht das einfache Starten des Octopus
 * aus einer Anwendung heraus, oder als neuen Prozess.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class OctopusDirectCallStarter implements OctopusStarter {
    private static final Log logger = LogFactory.getLog(OctopusStarter.class);

    private Octopus octopus = null;
    private TcEnv env = null;

    private static Logger baseLogger = null;

    private File logFile = null;
    private Handler logHandler = null;

    private TcSOAPEngine soapEngine = null;

    // Derzeit hat ein OctopusStarter genau eine Session
    TcSession tcSession = new TcDirectCallSession();

    /**
     * Startet den Octopus in der Komandozeile
     */
    public static void main(String[] args) {
        try {
            if (args.length > 0 && ("-h".equals(args[0]) || "--help".equals(args[0]))) {
                dieError();
            }

            Map cfg = new HashMap();
            int i = 0;
            while (args.length > i && args[i].startsWith("-P")) {
                int pos = args[i].indexOf("=");
                if (pos == -1) {
                    dieError();
                }

                cfg.put(args[i].substring(2, pos), args[i].substring(pos + 1));
                i++;
            }
            OctopusDirectCallStarter starter = new OctopusDirectCallStarter(cfg);

            Map params = new HashMap();
            for (; i + 1 < args.length; i += 2) {
                params.put(args[i], args[i + 1]);
            }
            starter.request(params);
        } catch (TcDirectCallException re) {
            Throwable rc = re.getRootCause();
            System.err.println("Fehler während der Octopus-Anfragebearbeitung: " + rc);
            rc.printStackTrace();
        } catch (Exception e) {
            System.err.println("Fehler während der Octopus-Anfragebearbeitung: " + e);
            e.printStackTrace();
        }
    }

    protected static void dieError() {
        System.err.println("Usage: octopusStarter [-Ppropname=value ..] [key value ..]");
        System.exit(1);
    }

    public OctopusDirectCallStarter() {
    }

    /**
     * Inititalisiert die Komponenten, die für alle Aufrufe gleich sind.
     *
     * @param configParams Parameter, die die Konfigurationen überschreiben, darf null sein
     */
    public OctopusDirectCallStarter(Map configParams) {
        boolean loggerWasNull = false;
        try {
            // Statische Loggerinstanz erstellen
            if (baseLogger == null) {
                loggerWasNull = true;
                baseLogger = Logger.getLogger("de.tarent");
                baseLogger.setLevel(Level.ALL);

                baseLogger.getHandlers();

                //Keinen Consolen-Logger wenn der Octopus direkt gestartet wird.
                baseLogger.setUseParentHandlers(false);
            }

            createEnvObject(configParams);

            //TODO: Warum wird das als System Property gesetzt?
            System.setProperty(TcEnv.KEY_PATHS_ROOT, env.getValueAsString(TcEnv.KEY_PATHS_ROOT));

            // Konfigurieren der Logging Api, wenn der
            // statische looger nicht schon existierte
            if (loggerWasNull) {
                File logDir = new File(System.getProperty("user.home") + "/.tarent/octopuslog/");
                if (!logDir.exists()) {
                    logDir.mkdirs();
                }
                logFile = new File(logDir, "current.log");
                ////Verzeichnis anlegen, wenn es nicht bereits existiert
                //(new File(env.getValueAsString(TcEnv.KEY_PATHS_ROOT) + "log")).mkdirs();
                logHandler = new FileHandler(logFile.getAbsolutePath());
                logHandler.setFormatter(new SimpleFormatter());
                baseLogger.addHandler(logHandler);
                baseLogger.info(Resources.getInstance().get("REQUESTPROXY_LOG_START_LOGGING"));
                String logLevel = env.getValueAsString(TcEnv.KEY_LOGGING_LEVEL);
                if (logLevel != null && logLevel.length() > 0) {
                    try {
                        Level level = Level.parse(logLevel);
                        baseLogger.config(Resources.getInstance()
                                .get("REQUESTPROXY_LOG_NEW_LOG_LEVEL", level));
                        baseLogger.setLevel(level);
                    } catch (IllegalArgumentException iae) {
                        baseLogger.log(
                                Level.WARNING,
                                Resources.getInstance()
                                        .get("REQUESTPROXY_LOG_INVALID_LOG_LEVEL", logLevel),
                                iae);
                    }
                }
            }

            octopus = new Octopus();
            octopus.init(env);
            octopus.init(new DirectCallModuleLookup(this));

            soapEngine = new TcSOAPEngine(env);
        } catch (Exception e) {
            logger.error(Resources.getInstance().get("REQUESTPROXY_LOG_INIT_EXCEPTION"), e);
            throw new RuntimeException("Fehler beim Initialisieren des lokalen Octopus", e);
        }

        logger.trace("TcRequextProxy init");
    }

    public void destroy() {
        if (octopus != null) {
            try {
                octopus.deInit();
            } catch (Exception e) {
                logger.warn(Resources.getInstance().get("REQUESTPROXY_LOG_CLEANUP_EXCEPTION"), e);
            }
        }

        // TODO: Sollte der logHandler wirklich geschlossen werden?
        // Der baseLogger list ja Statisch und somit in
        // anderen Instanzen evt. noch aktiv
        baseLogger.removeHandler(logHandler);
        logHandler.close();
    }

    /**
     * Startet die Abarbeitung einer Anfrage
     */
    public OctopusDirectCallResult request(Map requestParams)
            throws TcDirectCallException {

        logger.debug(Resources.getInstance().get("REQUESTPROXY_LOG_REQUEST_PROCESSING_START"));

        try {
            TcDirectCallResponse response = new TcDirectCallResponse();
            logger.trace(Resources.getInstance().get("REQUESTPROXY_LOG_RESPONSE_OBJECT_CREATED"));
            response.setSoapEngine(soapEngine);

            logger.trace(Resources.getInstance().get("REQUESTPROXY_LOG_SESSION_OBJECT_CREATED"));

            TcRequest request = new TcRequest();
            request.setRequestParameters(requestParams);
            request.setParam(TcRequest.PARAM_SESSION_ID, tcSession.getId());
            logger.debug(Resources.getInstance().get("REQUESTPROXY_LOG_REQUEST_OBJECT_CREATED"));

            octopus.dispatch(request, response, tcSession);

            response.flush();
            return new OctopusDirectCallResult(response);
        } catch (Exception e) {
            logger.error(Resources.getInstance().get("REQUESTPROXY_LOG_PROCESSING_EXCEPTION"), e);
            throw new TcDirectCallException(
                    Resources.getInstance().get("REQUESTPROXY_LOG_PROCESSING_EXCEPTION"), e);
        }

    }

    /**
     * Erstellt eine default-Konfiguration im TcEnv
     *
     * @param overrideSettings Parameter, die die Konfigurationen überschreiben,
     *                         darf null sein
     */
    protected void createEnvObject(Map overrideSettings) {
        // Env Objekt mit Umgebungsvariablen und Einstellungsparametern
        env = new TcEnv();

        //env.setValue(TcEnv.KEY_PATHS_ROOT, "." + System.getProperty("file.separator"));
        env.setValue(TcEnv.KEY_PATHS_ROOT, System.getProperty("user.dir") + "/OCTOPUS/");
        env.setValue(TcEnv.KEY_LOGGING_LEVEL, "CONFIG");
        env.setValue(TcEnv.KEY_PATHS_CONFIG_ROOT, "config/");
        env.setValue(TcEnv.KEY_PATHS_CONFIG_FILE, "config.xml");
        //env.setValue(TcEnv.loggerConfigFile, "logger.conf");
        //env.setValue(TcEnv.paths.pageDescriptionRoot, );
        env.setValue(TcEnv.KEY_PATHS_TEMPLATE_ROOT, "templates/");

        if (null != overrideSettings) {
            for (Iterator iter = overrideSettings.keySet().iterator(); iter.hasNext(); ) {
                String key = "" + iter.next();
                env.setValue(key, "" + overrideSettings.get(key));
            }
        }
    }

    public TcEnv getEnv() {
        return env;
    }
}
