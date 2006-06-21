/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 19.06.2006
 */

package de.tarent.octopus.jmx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.extensions.OctopusExtension;
import de.tarent.octopus.request.Octopus;

public class OctopusManagement implements OctopusExtension
{
    private static Logger logger = Logger.getLogger(OctopusManagement.class.getName());

    private List modules = null;
    
    public OctopusManagement()
    {
        super();
    }
    
    public void initialize(Object params)
    {
        if (!(params instanceof Map))
            logger.log(Level.SEVERE, "JMX extension parameter is not a map!");
        
        if (!((Map)params).containsKey("octopus") || !((Map)params).containsKey("config"))
            logger.log(Level.SEVERE, "JMX extension needs parameter 'octopus' and parameter 'config'");
            
        Octopus octopus = (Octopus)((Map)params).get("octopus");
        TcCommonConfig commonconfig = (TcCommonConfig)((Map)params).get("config");
        
        modules = new ArrayList();
        
        // initialize octopus core MBean
        try
        {
            modules.add(new OctopusModuleManagement(octopus, commonconfig, "octopus"));
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error initializing JMX for octopus core.", e);
        }
        
        // initialize module specific MBeans
        Iterator iter = commonconfig.getExistingModuleNames();
        String module = null;
        while (iter.hasNext())
            try
            {
                module = (String)iter.next();
                modules.add(new OctopusModuleManagement(octopus, commonconfig, module));
            }
            catch (Exception e)
            {
                logger.log(Level.SEVERE, "Error initializing JMX for module " + module, e);
            }
    }

    /**
     * Starts the management thread by getting a connection to a running
     * JMX server (or creating a new server) and publishing the management
     * information to the server.
     */
    public void start()
    {
        Iterator iter = modules.iterator();
        OctopusModuleManagement module = null;
        while (iter.hasNext())
            try
            {
                module = (OctopusModuleManagement)iter.next();
                module.start();
            }
            catch (Exception e)
            {
                logger.log(Level.SEVERE, "Error starting JMX for module " + module.getMBeanInfo().getClassName(), e);
            }
    }

    /**
     * Shuts down the JMX server by unregistering the MBean from the
     * running server.
     */
    public void stop()
    {
        Iterator iter = modules.iterator();
        OctopusModuleManagement module = null;
        while (iter.hasNext())
            try
            {
                module = (OctopusModuleManagement)iter.next();
                module.stop();
            }
            catch (Exception e)
            {
                logger.log(Level.SEVERE, "Error stopping JMX for module " + module.getMBeanInfo().getClassName(), e);
            }
    }    
}
