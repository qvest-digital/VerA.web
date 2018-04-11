package de.tarent.octopus.request;

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

import de.tarent.octopus.config.*;
import de.tarent.octopus.content.TcAll;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.content.TcContentProzessException;
import de.tarent.octopus.extensions.OctopusExtension;
import de.tarent.octopus.extensions.OctopusExtensionLoader;
import de.tarent.octopus.jndi.OctopusContextJndiFactory;
import de.tarent.octopus.jndi.OctopusInstanceJndiFactory;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.response.ResponseProcessingException;
import de.tarent.octopus.rpctunnel.OctopusRPCTunnel;
import de.tarent.octopus.server.Context;
import de.tarent.octopus.server.OctopusContext;
import org.apache.commons.logging.Log;

import java.io.Serializable;
import java.util.*;

/**
 * Diese Klasse dient als Wrapper für Octopus-Funktionalitäten,
 * der auch außerhalb von Web-Applikationskontexten benutzt werden kann.
 *
 * @author mikel
 */
public class Octopus /*implements Serializable*/ {
    //XXX TODO: TcModuleLookup is not serialisable
    //XXX TODO: OctopusExtension is not serialisable
    private static final long serialVersionUID = 8501961300295813799L;

    /*
     * geschützte Member-Variablen
     */
    private TcRequestDispatcher dispatcher;
    private TcModuleLookup moduleLookup;
    private TcCommonConfig commonConfig;

    private static Log logger = LogFactory.getLog(Octopus.class);

    public static final String TASKNAME_CLEANUP = "cleanup";
    public static final String TASKNAME_AUTOSTART = "autostart";

    // optional JMX extension
    private OctopusExtension jmxManagementServer = null;

    /*
     * Konstruktoren
     */
    public Octopus() {
    }

    /*
     * öffentliche Methoden
     */

    /**
     * Diese Methode initialisiert den Octopus. Dieser Schritt ist notwendig,
     * damit der Octopus Anfragen bearbeiten kann und damit Autostart-Tasks
     * abgearbeitet werden..
     *
     * @param env Eine Sammlung diverser Parameter.
     */
    public void init(TcEnv env) throws TcConfigException, ClassCastException {
        commonConfig = new TcCommonConfig(env, this);
        dispatcher = new TcRequestDispatcher(commonConfig);

        new OctopusInstanceJndiFactory().bind();
        new OctopusContextJndiFactory().bind();
    }

    /**
     * Diese Methode initialisiert den Octopus. Dieser Schritt ist notwendig,
     * damit der Octopus Anfragen bearbeiten kann und damit Autostart-Tasks
     * abgearbeitet werden..
     *
     * @param moduleLookup Lookup context for modules.
     */
    public void init(TcModuleLookup moduleLookup) throws ClassCastException {
        this.moduleLookup = moduleLookup;
        commonConfig.setModuleLookup(moduleLookup);

        preloadModules(commonConfig);

        // Initalizing the optional JMX subsystem
        String jmxEnabledString = commonConfig.getConfigData(TcEnv.KEY_JMX_ENABLED);
        if (Boolean.valueOf(jmxEnabledString).booleanValue()) {
            logger.info("Enabling optional JMX subsystem.");

            Map params = new HashMap();
            params.put("octopus", this);
            params.put("config", commonConfig);

            jmxManagementServer = OctopusExtensionLoader.load("de.tarent.octopus.jmx.OctopusManagement",
                    params);
        } else {
            logger.info("Optional JMX subsystem is disabled.");
        }

        // Initalizing the optional rpc tunnel subsystem
        String rpcTunnelEnabledString = commonConfig.getConfigData(TcEnv.KEY_RPCTUNNEL_ENABLED);
        if (Boolean.valueOf(rpcTunnelEnabledString).booleanValue()) {
            logger.info("Enabling optional RPC-tunnel.");

            OctopusRPCTunnel.createInstance(this, commonConfig);
        } else {
            logger.info("Optional RPC-tunnel is disabled.");
        }
    }

    /**
     * Diese Methode deinitialisert den Octopus. Dieser Schritt ist notwendig,
     * damit Cleanup-Tasks abgearbeitet werden.
     */
    public void deInit()
            throws
            ClassCastException,
            TcTaskProzessingException,
            TcContentProzessException,
            TcConfigException {

        // shutting down the JMX subsystem
        if (jmxManagementServer != null) {
            jmxManagementServer.stop();
        }

        cleanupModules(dispatcher);
    }

    /**
     * Diese Methode führt einen Request aus.
     */
    public void dispatch(TcRequest tcRequest, TcResponse tcResponse, TcSession theSession)
            throws ResponseProcessingException {
        logger.trace(getClass().getName() + " dispatch " + new Object[] { tcRequest, tcResponse, theSession });
        dispatcher.dispatch(tcRequest, tcResponse, theSession);
        logger.trace(getClass().getName() + " dispatch");
    }

    /*
     * geschützte Methoden
     */
    private void preloadModules(TcCommonConfig commonConfig) {
        String preloadString = commonConfig.getConfigData(TcEnv.KEY_PRELOAD_MODULES);
        if (preloadString == null || preloadString.length() == 0) {
            return;
        }

        List preloads = Arrays.asList(preloadString.split("[\\ ,\\,,\\;]"));

        for (Iterator it = preloads.iterator(); it.hasNext(); ) {
            String module = it.next().toString().trim();
            if (module.length() == 0) {
                continue;
            }

            logger.debug(Resources.getInstance().get("OCTOPUS_LOG_PRELOAD_MODULE", module));
            TcModuleConfig moduleConfig = commonConfig.getModuleConfig(module);

            if (moduleConfig == null) {
                logger.warn(Resources.getInstance().get("OCTOPUS_LOG_PRELOAD_MODULE_ERROR", module));
            }
        }
    }

    private void callTask(String modulename, TcCommonConfig config, String taskname)
            throws TcContentProzessException, TcTaskProzessingException, TcConfigException {
        TcRequest tcRequest = new TcRequest(TcRequest.createRequestID());
        tcRequest.setRequestParameters(new HashMap());
        tcRequest.setModule(modulename);
        tcRequest.setTask(taskname);

        OctopusContext context = new TcAll(
                tcRequest,
                new TcContent(),
                new TcConfig(config, config.createNewPersonalConfig(modulename), modulename));

        try {
            Context.addActive(context);

            TcTaskManager taskmanager = new TcTaskManager(context);
            taskmanager.start(modulename, taskname, false);

            while (taskmanager.doNextStep()) { /* Do nothing here */ }

        } finally {
            TcRequestDispatcher.processCleanupCode(tcRequest.getRequestID(), context.getContentObject());
            Context.clear();
        }
    }

    /**
     * @param modulename   Name des Moduls, in dem der Autostart durchgeführt werden soll
     * @param commonConfig die Config
     */
    public void doAutostart(String modulename, TcCommonConfig commonConfig)
            throws
            TcTaskProzessingException,
            TcContentProzessException,
            TcConfigException {
        TcModuleConfig moduleconfig = commonConfig.getModuleConfig(modulename);
        TcTaskList tasklist = moduleconfig.getTaskList();
        TcTask task = tasklist.getTask(TASKNAME_AUTOSTART);
        if (task != null) {
            logger.debug(Resources.getInstance().get("OCTOPUS_LOG_AUTOSTART_MODULE", modulename));
            callTask(modulename, commonConfig, TASKNAME_AUTOSTART);
        }
    }

    protected void cleanupModules(TcRequestDispatcher dispatcher)
            throws
            ClassCastException,
            TcTaskProzessingException,
            TcContentProzessException,
            TcConfigException {
        TcCommonConfig commonConfig = dispatcher.getCommonConfig();
        Iterator it = commonConfig.getExistingModuleNames();
        //Durchlaufe alle Module
        while (it.hasNext()) {
            String modulename = it.next().toString();
            doCleanup(modulename, commonConfig);
        }
    }

    /**
     * @param modulename
     * @param commonConfig
     */
    public void doCleanup(String modulename, TcCommonConfig commonConfig)
            throws
            TcTaskProzessingException,
            ClassCastException,
            TcContentProzessException,
            TcConfigException {
        TcModuleConfig moduleconfig = commonConfig.getModuleConfig(modulename);
        TcTaskList tasklist = moduleconfig.getTaskList();
        TcTask task = tasklist.getTask(TASKNAME_CLEANUP);
        if (task != null) {
            logger.debug(Resources.getInstance().get("OCTOPUS_LOG_CLEANUP_MODULE", modulename));
            callTask(modulename, commonConfig, TASKNAME_CLEANUP);
        }
    }

    public TcModuleLookup getModuleLookup() {
        return moduleLookup;
    }

    public TcCommonConfig getCommonConfig() {
        return commonConfig;
    }
}
