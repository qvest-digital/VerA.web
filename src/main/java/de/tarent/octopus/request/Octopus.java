/**
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2015 tarent solutions GmbH and its contributors
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
package de.tarent.octopus.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;

import de.tarent.octopus.config.TcModuleLookup;
import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcConfigException;
import de.tarent.octopus.config.TcModuleConfig;
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

/**
 * Diese Klasse dient als Wrapper für Octopus-Funktionalitäten,
 * der auch außerhalb von Web-Applikationskontexten benutzt werden kann.
 *
 * @author mikel
 */
public class Octopus {
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
        if (Boolean.valueOf(jmxEnabledString).booleanValue())
        {
            logger.info("Enabling optional JMX subsystem.");

            Map params = new HashMap();
            params.put("octopus", this);
            params.put("config", commonConfig);

            jmxManagementServer = OctopusExtensionLoader.load("de.tarent.octopus.jmx.OctopusManagement", params);
        }
        else
        {
        	logger.info("Optional JMX subsystem is disabled.");
        }

        // Initalizing the optional rpc tunnel subsystem
        String rpcTunnelEnabledString = commonConfig.getConfigData(TcEnv.KEY_RPCTUNNEL_ENABLED);
        if (Boolean.valueOf(rpcTunnelEnabledString).booleanValue())
        {
            logger.info("Enabling optional RPC-tunnel.");

            OctopusRPCTunnel.createInstance(this, commonConfig);
        }
        else
        {
        	logger.info("Optional RPC-tunnel is disabled.");
        }
    }

    /**
     * Diese Methode deinitialisert den Octopus. Dieser Schritt ist notwendig,
     * damit Cleanup-Tasks abgearbeitet werden.
     *
     * @throws ClassCastException
     * @throws TcTaskProzessingException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws TcContentProzessException
     * @throws TcConfigException
     */
    public void deInit()
        throws
            ClassCastException,
            TcTaskProzessingException,
            TcContentProzessException,
            TcConfigException {

            // shutting down the JMX subsystem
            if (jmxManagementServer!=null)
                jmxManagementServer.stop();

            cleanupModules(dispatcher);
    }

    /**
     * Diese Methode führt einen Request aus.
     *
     * @param tcRequest
     * @param tcResponse
     * @param theSession
     * @throws ResponseProcessingException
     */
    public void dispatch(TcRequest tcRequest, TcResponse tcResponse, TcSession theSession)
        throws ResponseProcessingException {
        logger.trace(getClass().getName() +" dispatch " + new Object[] {tcRequest, tcResponse, theSession});
        dispatcher.dispatch(tcRequest, tcResponse, theSession);
        logger.trace(getClass().getName() + " dispatch");
    }

    public Preferences getModulePreferences(String moduleName) {
        Preferences modulePreferences = getOctopusPreferences().node("modules");
        if (moduleName == null)
            return modulePreferences;
        while (moduleName.startsWith("/")) moduleName = moduleName.substring(1);
        while (moduleName.endsWith("/")) moduleName = moduleName.substring(0, moduleName.length()-1);
        return modulePreferences.node(moduleName);
    }

	/*
	 * geschützte Methoden
	 */
	private void preloadModules(TcCommonConfig commonConfig) {
		String preloadString = commonConfig.getConfigData(TcEnv.KEY_PRELOAD_MODULES);
		if (preloadString == null || preloadString.length() == 0)
			return;

		List preloads = Arrays.asList(preloadString.split("[\\ ,\\,,\\;]"));

		for (Iterator it = preloads.iterator(); it.hasNext();) {
			String module = it.next().toString().trim();
			if (module.length() == 0)
				continue;

			logger.debug(Resources.getInstance().get("OCTOPUS_LOG_PRELOAD_MODULE", module));
			TcModuleConfig moduleConfig = commonConfig.getModuleConfig(module);

			if (moduleConfig == null)
				logger.warn(Resources.getInstance().get("OCTOPUS_LOG_PRELOAD_MODULE_ERROR", module));
		}
	}

    private void callTask(String modulename, TcCommonConfig config, String taskname) throws TcContentProzessException, TcTaskProzessingException, TcConfigException {
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
     * @param modulename Name des Moduls, in dem der Autostart durchgeführt werden soll
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

    private Preferences getOctopusPreferences() {
        return Preferences.systemRoot().node("/de/tarent/octopus");
    }
}
