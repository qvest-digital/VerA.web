/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 21.06.2006
 */

package de.tarent.octopus.extensions;

import org.apache.commons.logging.Log;

import de.tarent.octopus.logging.LogFactory;

/**
 * A simple extension loader that relieves the calling code of extensive
 * error handling on instantiation of the selected extension.
 * TODO: this should be extended to include a simple configurable extension
 * description and automatic loading and running of extensions from
 * a configuration.
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */
public class OctopusExtensionLoader
{
    private static Log logger = LogFactory.getLog(OctopusExtensionLoader.class);
    
    /**
     * Loads, initializes and starts the extension given by classname.
     * On initialization, the given parameter is used.
     * 
     * @param classname Extension class to be initialized and started.
     * @param param Parameter passed to the extension's initialize method.
     * @return Extension instance or null if the extension loading failed.
     */
    public static OctopusExtension load(String classname, Object param)
    {
        logger.info("Enabling octopus extension: " + classname);

        OctopusExtension extension = null;
        try
        {
            extension = (OctopusExtension)Class.forName(classname).newInstance();
        }
        catch (InstantiationException e)
        {
            logger.error("Error getting extension instance: " + classname, e);
        }
        catch (IllegalAccessException e)
        {
            logger.error("Illegal Access getting extension instance: " + classname, e);
        }
        catch (ClassNotFoundException e)
        {
            logger.error("Extension class not found: " + classname, e);
        }

        if (extension!=null)
        {
            extension.initialize(param);
            extension.start();
        }

        return extension;
    }
}
