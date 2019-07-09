package de.tarent.octopus.request.directcall;
import de.tarent.octopus.request.Octopus;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcSession;
import de.tarent.octopus.request.internal.OctopusStarter;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.soap.TcSOAPEngine;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Ermöglicht das einfache Starten des Octopus
 * aus einer Anwendung heraus, oder als neuen Prozess.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
@Log4j2
public class OctopusDirectCallStarter implements OctopusStarter {
    private Octopus octopus = null;
    private TcEnv env = null;

    private File logFile = null;

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
        try {
            createEnvObject(configParams);

            //TODO: Warum wird das als System Property gesetzt?
            System.setProperty(TcEnv.KEY_PATHS_ROOT, env.getValueAsString(TcEnv.KEY_PATHS_ROOT));

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
    }

    /**
     * Startet die Abarbeitung einer Anfrage
     */
    public OctopusDirectCallResult request(Map requestParams) throws TcDirectCallException {
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
        env.setValue(TcEnv.KEY_PATHS_CONFIG_ROOT, "config/");
        env.setValue(TcEnv.KEY_PATHS_CONFIG_FILE, "config.xml");
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
