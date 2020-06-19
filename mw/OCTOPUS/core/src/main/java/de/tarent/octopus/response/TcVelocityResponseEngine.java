package de.tarent.octopus.response;

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

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.CookieMap;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcResponse;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.server.OctopusContext;
import lombok.extern.log4j.Log4j2;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * This class merge the octopus content with a velocity script.
 *
 * @author <a href="mailto:h.helwich@tarent.de">Hendrik Helwich</a>, <b>tarent GmbH</b>
 */
@Log4j2
public class TcVelocityResponseEngine implements TcResponseEngine {
    /**
     * Filename suffix
     */
    public static final String FILE_SUFFIX = ".vm";
    /**
     * Velocity content-key for the {@link TcConfig}.
     */
    public static final String PARAM_NAME_CONFIG = "octopusConfig";
    /**
     * Velocity content-key for the {@link TcRequest}.
     */
    public static final String PARAM_NAME_REQUEST = "octopusRequest";
    /**
     * Velocity content-key for the {@link TcResponse}.
     */
    public static final String PARAM_NAME_RESPONSE = "octopusResponse";
    /**
     * Octopus content-key for the {@link Writer}.
     */
    private static final String OCTOPUS_RESPONSEENGINE = "octopusResponseEngine";
    /**
     * Octopus content-key for the {@link TcVelocityResponseEngine}.
     */
    private static final String OCTOPUS_RESPONSESTREAM = "octopusResponseStream";
    /**
     * Octopus content-key for the {@link VelocityContext}.
     */
    private static final String OCTOPUS_RESPONSECONTEXT = "octopusResponseContext";

    /**
     * Velocity engine instance
     */
    private VelocityEngine engine;
    /**
     * Velocity script rootpath
     */
    private File rootPath;

    /**
     * Init this response engine.
     */
    public void init(TcModuleConfig moduleConfig, TcCommonConfig commonConfig) {
        engine = new VelocityEngine();
        try {
            rootPath = new File(commonConfig.getTemplateRootPath(moduleConfig.getName()), "velocity");
            logger.debug("Velocity-Root: " + rootPath);
            Properties properties = new Properties();
            properties.setProperty("file.resource.loader.path", rootPath.getAbsolutePath());
            properties.setProperty("velocimacro.library", commonConfig.getConfigData("velocity.macro.library"));
            properties.setProperty("velocimacro.permissions.allow.inline",
              commonConfig.getConfigData("velocity.macro.permissions.allow.inline"));
            properties.setProperty("velocimacro.permissions.allow.inline.to.replace.global",
              commonConfig.getConfigData("velocity.macro.permissions.allow.inline.to.replace.global"));
            properties.setProperty("velocimacro.permissions.allow.inline.local.scope",
              commonConfig.getConfigData("velocity.macro.permissions.allow.inline.local.scope"));
            properties.setProperty("velocimacro.context.localscope",
              commonConfig.getConfigData("velocity.macro.context.localscope"));

            String loggerClass = commonConfig.getConfigData("velocity.log.system.class");
            if (loggerClass != null && loggerClass.trim().length() > 0) {
                properties.setProperty("runtime.log.logsystem.class", loggerClass);
            }

            engine.init(properties);
        } catch (Exception e) {
            logger.error("Fehler beim Init der Velocity Engine.", e);
        }
    }

    /**
     * Return a response.
     */
    public void sendResponse(TcConfig tcConfig, TcResponse tcResponse, TcContent tcContent, TcResponseDescription desc,
      TcRequest tcRequest)
      throws ResponseProcessingException {

        // adding cookies (e.g. set by PersonalConfig)
        Map cookiesSettings = new HashMap(1);
        cookiesSettings.put(CookieMap.CONFIG_MAXAGE,
          tcConfig.getModuleConfig().getParam(CookieMap.PREFIX_CONFIG_MAP + "." + CookieMap.CONFIG_MAXAGE));
        Map cookiesMap = (Map) tcContent.getAsObject(CookieMap.PREFIX_COOKIE_MAP);
        if (cookiesMap != null) {
            Iterator iter = cookiesMap.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                Map cookieMap = (Map) cookiesMap.get(key);
                if (cookieMap.get(CookieMap.COOKIE_MAP_FIELD_COOKIE) != null) {
                    tcResponse.addCookie(cookieMap.get(CookieMap.COOKIE_MAP_FIELD_COOKIE));
                } else {
                    tcResponse.addCookie(key, (String) cookieMap.get(CookieMap.COOKIE_MAP_FIELD_VALUE), cookiesSettings);
                }
            }
        }

        String template = desc.getDescName() + FILE_SUFFIX;
        if (!(new File(rootPath, template)).exists()) {
            throw new ResponseProcessingException("Template '" + template + "' not found.");
        }

        String encoding = (String) tcContent.getAsObject("responseParams.encoding");
        if (encoding == null || encoding.length() == 0) {
            encoding = tcConfig.getDefaultEncoding();
        }

        Writer writer;
        boolean doClose = true;
        try {
            writer = new OutputStreamWriter(tcResponse.getOutputStream(), encoding);
            logger.debug(Resources.getInstance().get("VELOCITYRESPONSE_LOG_ENCODING", tcRequest.getRequestID(), encoding));
        } catch (UnsupportedEncodingException e) {
            logger.warn(
              Resources.getInstance().get("VELOCITYRESPONSE_LOG_ENCODING_UNSUPPORTED", tcRequest.getRequestID(), encoding),
              e);
            writer = tcResponse.getWriter();
            doClose = false;
        }

        try {
            VelocityContext context = new VelocityContext();

            String key;
            for (Iterator it = tcContent.getKeys(); it.hasNext(); ) {
                key = (String) it.next();
                context.put(key, tcContent.get((key)));
            }
            tcContent.setField(OCTOPUS_RESPONSEENGINE, this);
            tcContent.setField(OCTOPUS_RESPONSESTREAM, writer);
            tcContent.setField(OCTOPUS_RESPONSECONTEXT, context);

            context.put(PARAM_NAME_CONFIG, tcConfig);
            context.put(PARAM_NAME_REQUEST, tcRequest);
            context.put(PARAM_NAME_RESPONSE, tcResponse);

            engine.mergeTemplate(template, tcConfig.getDefaultEncoding(), context, writer);

            if (doClose) {
                writer.close();
            }
        } catch (Exception e) {
            logger.error("Fehler beim Erzeugen der Ausgabeseite mit Velocity-Ausgabeseite '" + template + "'.", e);
            throw new ResponseProcessingException("Fehler beim Erzeugen der Velocity-Ausgabeseite '" + template + "'.", e);
        }
    }

    /**
     * Allow additional template merge with a reader.
     *
     * @param cntx
     * @param reader
     * @throws Exception
     */
    static public void mergeTemplate(OctopusContext cntx, Reader reader) throws Exception {
        TcVelocityResponseEngine responseEngine =
          (TcVelocityResponseEngine) cntx.contentAsObject(OCTOPUS_RESPONSEENGINE);
        responseEngine.engine.evaluate(
          (VelocityContext) cntx.contentAsObject(OCTOPUS_RESPONSECONTEXT),
          (Writer) cntx.contentAsObject(OCTOPUS_RESPONSESTREAM),
          reader.toString(), reader);
    }
}
