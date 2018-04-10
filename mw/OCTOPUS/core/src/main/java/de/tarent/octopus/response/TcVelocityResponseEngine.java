package de.tarent.octopus.response;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
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
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.VelocityContext;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.CookieMap;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcResponse;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.server.OctopusContext;

/**
 * This class merge the octopus content with a velocity script.
 *
 * @author <a href="mailto:h.helwich@tarent.de">Hendrik Helwich</a>, <b>tarent GmbH</b>
 */
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
     * Logger instance
     */
    private Log logger;
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
        logger = LogFactory.getLog(TcVelocityResponseEngine.class);
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
