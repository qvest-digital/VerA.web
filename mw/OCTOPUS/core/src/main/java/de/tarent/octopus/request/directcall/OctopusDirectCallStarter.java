package de.tarent.octopus.request.directcall;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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
