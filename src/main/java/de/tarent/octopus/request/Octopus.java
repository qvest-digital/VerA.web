/* $Id: Octopus.java,v 1.15 2007/03/01 13:54:27 christoph Exp $
 * 
 * Created on 18.09.2003
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
 * by Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */
package de.tarent.octopus.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcConfigException;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcAll;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.content.TcContentProzessException;
import de.tarent.octopus.extensions.OctopusExtension;
import de.tarent.octopus.extensions.OctopusExtensionLoader;
import de.tarent.octopus.jndi.OctopusFactory;
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
    private OctopusConfiguration config;

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
     * @param config Konfigurationsdaten
     * TODO! Parameter spezifizieren. 
     */
    public void init(TcEnv env, OctopusConfiguration config)
        throws
            TcConfigException,
            ClassCastException,
            TcTaskProzessingException,
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            TcContentProzessException {
    	TcCommonConfig commonconfig = new TcCommonConfig(env, config, this);
        dispatcher = new TcRequestDispatcher(commonconfig);
        this.config = config;
        OctopusFactory.tryToBind();
        preloadModules(commonconfig); 
        
        // Initalizing the optional JMX subsystem
        String jmxEnabledString = commonconfig.getConfigData("jmxenabled");
        if (Boolean.valueOf(jmxEnabledString).booleanValue())
        {
            logger.info("Enabling optional JMX subsystem.");

            Map params = new HashMap();
            params.put("octopus", this);
            params.put("config", commonconfig);

            jmxManagementServer = OctopusExtensionLoader.load("de.tarent.octopus.jmx.OctopusManagement", params);
        }
        
        logger.info("Enabling optional RPC-tunnel.");
        OctopusRPCTunnel.createInstance(this, commonconfig);
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
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
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
			TcModuleConfig moduleConfig = config.getModuleConfig(module, getModulePreferences(module));
			
			if (moduleConfig != null)
				commonConfig.registerModule(module, moduleConfig);
			else
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
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
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
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
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
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
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

    private Preferences getOctopusPreferences() {
        return Preferences.systemRoot().node("/de/tarent/octopus");
    }
}