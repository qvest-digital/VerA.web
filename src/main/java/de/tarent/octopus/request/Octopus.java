/* $Id: Octopus.java,v 1.6 2006/05/17 11:41:04 asteban Exp $
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcConfigException;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcAll;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.content.TcContentProzessException;
import de.tarent.octopus.jndi.OctopusFactory;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.response.ResponseProcessingException;

/**
 * Diese Klasse dient als Wrapper für Octopus-Funktionalitäten,
 * der auch außerhalb von Web-Applikationskontexten benutzt werden kann.
 * 
 * @author mikel
 */
public class Octopus {

    public static final String TASKNAME_CLEANUP = "cleanup";
    public static final String TASKNAME_AUTOSTART = "autostart";

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
    public void init(TcEnv env, Configuration config)
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
        logger.entering(getClass().getName(), "dispatch", new Object[] {tcRequest, tcResponse, theSession});
        dispatcher.dispatch(tcRequest, tcResponse, theSession);
        logger.exiting(getClass().getName(), "dispatch");
    }

    public Preferences getModulePreferences(String moduleName) {
        Preferences modulePreferences = getOctopusPreferences().node("modules");
        if (moduleName == null)
            return modulePreferences;
        while (moduleName.startsWith("/")) moduleName = moduleName.substring(1);
        return modulePreferences.node(moduleName);
    }
    
    /*
     * geschachtelte Klassen und Schnittstellen.
     */
    /**
     * Mit dieser Schnittstelle werden dem Octopus Konfigurationsdaten zur
     * Verfügung gestellt.  
     */
    public static interface Configuration {
        /**
         * Diese Methode liefert zu einem Modulnamen die Konfiguration.
         * 
         * @param module der Name des Moduls.
         * @param modulePreferences Preferences zum Modul als Hints.
         * @return Modulkonfiguration zu dem Modul. <code>null</code>
         *  steht hier für ein nicht gefundenes Modul.
         */
        public TcModuleConfig getModuleConfig(String module, Preferences modulePreferences);
    }

    /*
     * geschützte Methoden
     */
    private void preloadModules(TcCommonConfig commonConfig) {
    	String preloadstring = commonConfig.getConfigData(TcEnv.KEY_PRELOAD_MODULES);
    	List preloads=null;
    	if(preloadstring!=null&&preloadstring.length()>0){
    		preloads = Arrays.asList(preloadstring.split(" "));
    	}
        if (preloads == null)
            return;
        Iterator itPreloads = preloads.iterator();
        while (itPreloads.hasNext()) {
            Object oNext = itPreloads.next();
            if (oNext != null) {
                String moduleName = oNext.toString();
                logger.config(Resources.getInstance().get("OCTOPUS_LOG_PRELOAD_MODULE", moduleName));
                TcModuleConfig moduleConfig = config.getModuleConfig(moduleName, getModulePreferences(moduleName));
                if (moduleConfig != null)
                    commonConfig.registerModule(moduleName, moduleConfig);
                else
                    logger.warning(Resources.getInstance().get("OCTOPUS_LOG_PRELOAD_MODULE_ERROR", moduleName));
            }
        }
    }

    private void callTask(String modulename, TcCommonConfig config, String taskname) throws TcContentProzessException, TcTaskProzessingException {
        TcRequest tcRequest = new TcRequest(TcRequest.createRequestID());
        tcRequest.setRequestParameters(new HashMap());
        tcRequest.setModule(modulename);
        tcRequest.setTask(taskname);

        TcTaskManager taskmanager = new TcTaskManager(new TcAll(tcRequest,
                                                                new TcContent(),
                                                                new TcConfig(config, null, modulename)));
        taskmanager.start(modulename, taskname, false);

        while (taskmanager.doNextStep()) { /* Do nothing here */ }
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
            TcContentProzessException {
        TcModuleConfig moduleconfig = commonConfig.getModuleConfig(modulename);
        TcTaskList tasklist = moduleconfig.getTaskList();
        TcTask task = tasklist.getTask(TASKNAME_AUTOSTART);
        if (task != null) {           
            logger.fine(Resources.getInstance().get("OCTOPUS_LOG_AUTOSTART_MODULE", modulename));
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
            TcContentProzessException {
        TcModuleConfig moduleconfig = commonConfig.getModuleConfig(modulename);
        TcTaskList tasklist = moduleconfig.getTaskList();
        TcTask task = tasklist.getTask(TASKNAME_CLEANUP);
        if (task != null) {
            logger.fine(Resources.getInstance().get("OCTOPUS_LOG_CLEANUP_MODULE", modulename));
            callTask(modulename, commonConfig, TASKNAME_CLEANUP);
        }
    }

    private Preferences getOctopusPreferences() {
        return Preferences.systemRoot().node("/de/tarent/octopus");
    }

    /*
     * geschützte Member-Variablen
     */
    private TcRequestDispatcher dispatcher;
    private Configuration config;

    private static Logger logger = Logger.getLogger(Octopus.class.getName());

}
