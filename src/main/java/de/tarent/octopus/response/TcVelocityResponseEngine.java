/* $Id: TcVelocityResponseEngine.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
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

package de.tarent.octopus.response;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.VelocityContext;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcResponse;
import de.tarent.octopus.resource.Resources;

/** Diese Klasse repräsentiert die <a href="http://jakarta.apache.org/velocity/" target="_blank">Velocity</a> Template-Engine.
 *
 *  @author <a href="mailto:H.Helwich@tarent.de">Hendrik Helwich</a>, <b>tarent GmbH</b>
 */
public class TcVelocityResponseEngine implements TcResponseEngine {
    /** Der Logger */
    private static Logger logger = Logger.getLogger(TcCommonConfig.class.getName());

    private static final String suffix = ".vm";
    public final static String PARAM_NAME_REQUEST = "octopusRequest";
    public final static String PARAM_NAME_CONFIG = "octopusConfig";
    private VelocityEngine engine = new VelocityEngine();
    private VelocityContext context = null;
    private File rootPath;

    public void init(TcModuleConfig moduleConfig, TcCommonConfig commonConfig) {
        try {
            rootPath = new File(commonConfig.getTemplateRootPath(moduleConfig.getName()), "velocity");
            logger.config("Velocity-Root: " + rootPath);
            Properties p = new Properties();
            p.setProperty("file.resource.loader.path", rootPath.getAbsolutePath());
            p.setProperty("velocimacro.library", commonConfig.getConfigData("velocity.macro.library"));
            p.setProperty(
                "velocimacro.permissions.allow.inline",
                commonConfig.getConfigData("velocity.macro.permissions.allow.inline"));
            p.setProperty(
                "velocimacro.permissions.allow.inline.to.replace.global",
                commonConfig.getConfigData("velocity.macro.permissions.allow.inline.to.replace.global"));
            p.setProperty(
                "velocimacro.permissions.allow.inline.local.scope",
                commonConfig.getConfigData("velocity.macro.permissions.allow.inline.local.scope"));
            p.setProperty(
                "velocimacro.context.localscope",
                commonConfig.getConfigData("velocity.macro.context.localscope"));
            String loggerClass = commonConfig.getConfigData("velocity.log.system.class");
            if (loggerClass != null && loggerClass.trim().length() > 0)
                p.setProperty("runtime.log.logsystem.class", loggerClass);
            engine.init(p);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Fehler beim Init der Velocity Engine.", e);
        }
        context = new VelocityContext();
    }

    public void sendResponse(TcConfig config, TcResponse tcResponse, TcContent theContent, TcResponseDescription desc, TcRequest request)
        throws ResponseProcessingException {

        context = new VelocityContext();

        // die Daten in den Kontext hängen
        String key;
        for (Iterator e = theContent.getKeys(); e.hasNext();) {
            key = (String) e.next();
            context.put(key, theContent.get((key)));
        }
        context.put(PARAM_NAME_CONFIG, config);
        context.put(PARAM_NAME_REQUEST, request);

        String templateFile = desc.getDescName() + suffix;
        if (!(new File(rootPath, templateFile)).exists())
            throw new ResponseProcessingException("Template '" + templateFile + "' kann nicht gefunden werden.");
        
        String encoding = (String) theContent.getAsObject("responseParams.encoding");
        if (encoding == null || encoding.length() == 0)
            encoding = config.getDefaultEncoding();
        Writer out;
        boolean doClose = true;
        try {
            out = new OutputStreamWriter(tcResponse.getOutputStream(), encoding);
            logger.fine(Resources.getInstance().get("VELOCITYRESPONSE_LOG_ENCODING", request.getRequestID(), encoding));
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.WARNING, Resources.getInstance().get("VELOCITYRESPONSE_LOG_ENCODING_UNSUPPORTED", request.getRequestID(), encoding), e);
            out = tcResponse.getWriter();
            doClose = false;
        }
        try {
            engine.mergeTemplate(templateFile, config.getDefaultEncoding(), context, out);
            if (doClose)
                out.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Fehler beim Erzeugen der Ausgabeseite mit Velocity.", e);
            throw new ResponseProcessingException("Fehler beim Erzeugen der Ausgabeseite mit Velocity.", e);
        }
    }
}
